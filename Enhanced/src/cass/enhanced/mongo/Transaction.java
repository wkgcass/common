package cass.enhanced.mongo;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

public class Transaction implements Closeable {
	private static final int INITIAL = 0;
	private static final int COMMITTING = 1;
	private static final int ROLLINGBACK = -1;
	static final String TX_MAP_COLL = "j-tx-map-coll";
	static final String TX_REF = "j-transaction-ref";
	private static final String TX_BKUP = "j-tx-backup";
	private static final String TX_REQUIRE_ROLLBACK_ON_ERROR = "j-tx-req-rolbk-on-err";
	private static final String TX_COLL_PREFIX = "j_transaction_";
	static final String TX_OPERATION = "j-transaction-op";
	static final String TX_TARGET_ID = "j-tx-target-id";
	static final String TX_OP_UPDATE = "update";
	static final String TX_OP_INSERT = "insert";
	static final String TX_OP_DELETE = "delete";
	private static List<Integer> transactionId = new LinkedList<Integer>();
	/**
	 * cache of id.<br/>
	 * Map&lt;TransactionID, Map&lt;CollectionName, Set&lt;IdObject&gt;&gt;&gt;
	 */
	private static Map<Integer, Map<String, Set<Object>>> txIdCache = new HashMap<Integer, Map<String, Set<Object>>>();

	/**
	 * Get an id not in use
	 * 
	 * @return an ID for transaction
	 */
	private static Integer findId() {
		synchronized (transactionId) {
			int i = 0;
			while (transactionId.contains(i)) {
				++i;
			}
			Integer tmp = new Integer(i);
			transactionId.add(tmp);
			return tmp;
		}
	}

	private boolean started = false;
	private int state = INITIAL;
	private Integer id;
	private MongoDatabase db;
	private MongoCollection<Document> txColl;

	protected Transaction(MongoDatabase db) {
		this.db = db;
	}

	/**
	 * Require putting id into cache<br/>
	 * If id already cached (which means the corresponding document is already
	 * in transaction),<br/>
	 * this method would return false and do nothing.<br/>
	 * Else, id would be cached and return true
	 * 
	 * @param coll
	 *            collection of id to cache
	 * @param ref_id
	 *            id to cache
	 * @return true if require caching(and caching already done), false
	 *         otherwise.
	 */
	private boolean requireCaching(MongoCollection<Document> coll, Object ref_id) {
		Map<String, Set<Object>> collMap;
		synchronized (txIdCache) {
			if (txIdCache.containsKey(id)) {
				collMap = txIdCache.get(id);
			} else {
				collMap = new HashMap<String, Set<Object>>();
				txIdCache.put(id, collMap);
			}
		}
		Set<Object> idSet;
		String collName = coll.getNamespace().getCollectionName();
		synchronized (collMap) {
			if (collMap.containsKey(collName)) {
				idSet = collMap.get(collName);
			} else {
				idSet = new HashSet<Object>();
				collMap.put(collName, idSet);
			}
		}
		synchronized (idSet) {
			if (idSet.contains(ref_id)) {
				return false;
			} else {
				idSet.add(ref_id);
				return true;
			}
		}
	}

	/**
	 * Require document cloning.<br/>
	 * If id of the document already cached, false would be returned and do
	 * nothing.<br/>
	 * Else, the document would be modified as one to be inserted into
	 * transaction collection, and do caching, then return true.<br/>
	 * <b>Property TX_REF, TX_MAP_COLL, TX_BKUP would be added</b>
	 * 
	 * @param coll
	 *            collection of the document
	 * @param doc
	 *            document to be check
	 * @return true if require cloning(and cloning already done), false
	 *         otherwise.
	 */
	private boolean requireDocCopy(MongoCollection<Document> coll, Document doc) {
		String collName = coll.getNamespace().getCollectionName();
		Object _id = doc.remove("_id");
		// check whether already mapped
		if (!requireCaching(coll, _id)) {
			return false;
		}
		// set mapping properties
		doc.put(TX_BKUP, new Document(doc));
		doc.put(TX_REF, _id);
		doc.put(TX_MAP_COLL, collName);
		return true;
	}

	void beforeUpdateOne(MongoCollection<Document> coll, Bson query) {
		if (started) {
			// get document to back up
			Document doc = coll.find(query).first();
			if (null != doc) {
				if (requireDocCopy(coll, doc)) {
					// back up the document
					txColl.insertOne(doc.append(TX_OPERATION, TX_OP_UPDATE));
				}
			}
		}
	}

	void beforeUpdateMany(MongoCollection<Document> coll, Bson query) {
		if (started) {
			// get documents to back up
			FindIterable<Document> res = coll.find(query);
			List<Document> listToInsert = new ArrayList<Document>();
			for (Document doc : res) {
				if (requireDocCopy(coll, doc)) {
					// add into list( with TX_
					listToInsert.add(doc.append(TX_OPERATION, TX_OP_UPDATE));
				}
			}
			// do insert
			txColl.insertMany(listToInsert);
		}
	}

	void beforeInsertOne(MongoCollection<Document> coll, Document doc) {
		// (document already inserted)
		if (started) {
			// add TX_MAP_COLL and TX_TARGET_ID property
			doc.append(TX_MAP_COLL, coll.getNamespace().getCollectionName());
			Object _id = doc.remove("_id");
			if (null != _id) {
				doc.append(TX_TARGET_ID, _id).append(TX_OPERATION, TX_OP_INSERT);
			}
		}
	}

	void beforeInsertMany(MongoCollection<Document> coll, List<? extends Document> docList) {
		// (document already inserted)
		if (started) {
			for (Document doc : docList) {
				// add TX_MAP_COLL property
				doc.append(TX_MAP_COLL, coll.getNamespace().getCollectionName());
				Object _id = doc.remove("_id");
				if (null != _id) {
					doc.append(TX_TARGET_ID, _id).append(TX_OPERATION, TX_OP_INSERT);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	DeleteResult deleteOne(MongoCollection<Document> coll, Bson query) {
		Document doc = coll.find(query).first();
		if (null == doc) {
			// doc not in collection
			// assume it was insert
			Document sample = new Document((Map<String, Object>) query);
			Object _id = sample.remove("_id");
			if (null != _id) {
				sample.append(TX_TARGET_ID, _id).append(TX_REF, null);
			}
			return txColl.deleteOne(sample);
		} else {
			// doc exist in collection
			if (requireDocCopy(coll, doc)) {
				// in tx collection
				doc.append(TX_OPERATION, TX_OP_DELETE); // mark as delete
				txColl.insertOne(doc);
			} else {
				// not in tx collection
				// if it was update
				txColl.updateOne(
						new BasicDBObject(TX_REF, doc.get("_id")).append(TX_MAP_COLL,
								coll.getNamespace().getCollectionName()),
						new BasicDBObject("$set", new BasicDBObject(TX_OPERATION, TX_OP_DELETE)));
			}
			return new MannualDeleteResult(1L, true);
		}
	}

	@SuppressWarnings("unchecked")
	DeleteResult deleteMany(MongoCollection<Document> coll, Bson query) {
		FindIterable<Document> res = coll.find(query);
		Iterator<Document> it = res.iterator();
		long count = 0;
		List<Document> listToInsert = new ArrayList<Document>();
		List<Object> listToUpdate = new ArrayList<Object>();
		while (it.hasNext()) {
			Document doc = it.next();
			if (requireDocCopy(coll, doc)) {
				doc.append(TX_OPERATION, TX_OP_DELETE); // mark as delete
				listToInsert.add(doc);
				++count;
			} else {
				listToUpdate.add(doc.get("_id")); // update already existed tx
													// documents
			}
		}
		txColl.updateMany(
				new BasicDBObject(TX_MAP_COLL, coll.getNamespace().getCollectionName()).append(TX_REF,
						new BasicDBObject("$in", listToUpdate)),
				new BasicDBObject("$set", new BasicDBObject(TX_OPERATION, TX_OP_DELETE)));
		txColl.insertMany(listToInsert);
		// assume inserted documents would be deleted
		Document sample = new Document((Map<String, Object>) query);
		Object _id = sample.remove("_id");
		if (null != _id) {
			sample.append(TX_TARGET_ID, _id).append(TX_REF, null);
		}
		count += txColl.deleteOne(sample).getDeletedCount();
		return new MannualDeleteResult(count, true);
	}

	public MongoCollection<Document> getTxCollection() {
		return txColl;
	}

	public void start() {
		if (!this.started) {
			id = findId();
			txColl = db.getCollection(TX_COLL_PREFIX + id);
			started = true;
		}
	}

	public boolean started() {
		return started;
	}

	public void commit() {
		state = COMMITTING;
		// get all documents in transaction collection
		FindIterable<Document> toCommit = txColl.find();
		for (Document doc : toCommit) {
			// remove _id, TX_MAP_COLL, TX_OPERATION and TX_BKUP so that the
			// document can be used directly (TX_REF would be removed later if
			// exist)
			String op = (String) doc.remove(TX_OPERATION);
			String collName = (String) doc.remove(TX_MAP_COLL);
			doc.remove(TX_BKUP);
			MongoCollection<Document> coll = db.getCollection(collName);
			Object tx_id = doc.remove("_id");
			if (op.equals(TX_OP_INSERT)) {
				// insert
				Object _id = doc.remove(TX_TARGET_ID);
				if (null != _id) {
					doc.append("_id", _id);
				}
				coll.insertOne(doc);
				if (null == _id) {
					_id = doc.get("_id");
				}
				// change state and set tx_ref
				txColl.updateOne(new BasicDBObject("_id", tx_id), new BasicDBObject("$set",
						new BasicDBObject(TX_REQUIRE_ROLLBACK_ON_ERROR, true).append(TX_REF, _id)));
			} else {
				// remove TX_REF
				Object _id = doc.remove(TX_REF);
				if (op.equals(TX_OP_DELETE)) {
					// delete
					coll.deleteOne(new BasicDBObject("_id", _id));
				} else {
					// update
					coll.replaceOne(new BasicDBObject("_id", _id), doc);
				}
				// change state
				txColl.updateOne(new BasicDBObject("_id", tx_id),
						new BasicDBObject("$set", new BasicDBObject(TX_REQUIRE_ROLLBACK_ON_ERROR, true)));
			}
		}
		clear();
		state = INITIAL;
	}

	private void clear() {
		txColl.drop();
		synchronized (txIdCache) {
			txIdCache.remove(id);
		}
	}

	@SuppressWarnings("unchecked")
	public void rollback() {
		state = ROLLINGBACK;
		if (state == COMMITTING) {
			// get all documents in transaction collection
			FindIterable<Document> res = txColl.find(new BasicDBObject(TX_REQUIRE_ROLLBACK_ON_ERROR, true));
			for (Document doc : res) {
				// state require rolling back
				String collName = doc.getString(TX_MAP_COLL);
				MongoCollection<Document> coll = db.getCollection(collName);
				if (doc.getString(TX_OPERATION).equals(TX_OP_DELETE)) {
					// delete - insert updated obj
					Document bkup = new Document((Map<String, Object>) doc.get(TX_BKUP));
					bkup.put("_id", doc.get(TX_REF));
					coll.replaceOne(new BasicDBObject("_id", doc.get(TX_REF)), bkup);
				} else {
					if (doc.getString(TX_OPERATION).equals(TX_OP_UPDATE)) {
						// update - rollback updated obj
						Document bkup = new Document((Map<String, Object>) doc.get(TX_BKUP));
						bkup.put("_id", doc.get(TX_REF));
						coll.replaceOne(new BasicDBObject("_id", doc.get(TX_REF)), bkup);
					} else {
						// insert - delete inserted obj
						coll.deleteOne(new BasicDBObject("_id", doc.get(TX_REF)));
					}
				}
			}
		}
		clear();
		txColl = db.getCollection(TX_COLL_PREFIX + id);
		state = INITIAL;
	}

	@Override
	public void close() {
		if (started) {
			clear();
			synchronized (transactionId) {
				transactionId.remove(id);
			}
		}
		started = false;
	}
}