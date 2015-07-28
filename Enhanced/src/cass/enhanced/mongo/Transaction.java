package cass.enhanced.mongo;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	private static Map<Thread, Transaction> lastTxMap = new HashMap<Thread, Transaction>();

	public static Transaction getLastTransaction() {
		synchronized (lastTxMap) {
			return lastTxMap.get(Thread.currentThread());
		}
	}

	private boolean started = false;
	private int state = Constants.INITIAL;
	private Integer id;
	private MongoDatabase db;
	private MongoCollection<Document> txColl;

	protected Transaction(MongoDatabase db) {
		this.db = db;
	}

	Transaction() {
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
		synchronized (Constants.txIdCache) {
			if (Constants.txIdCache.containsKey(id)) {
				collMap = Constants.txIdCache.get(id);
			} else {
				collMap = new HashMap<String, Set<Object>>();
				Constants.txIdCache.put(id, collMap);
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
		doc.put(Constants.TX_BKUP, new Document(doc));
		doc.put(Constants.TX_REF, _id);
		doc.put(Constants.TX_MAP_COLL, collName);
		return true;
	}

	void beforeUpdateOne(MongoCollection<Document> coll, Bson query) {
		if (started) {
			// get document to back up
			Document doc = coll.find(query).first();
			if (null != doc) {
				if (requireDocCopy(coll, doc)) {
					// back up the document
					txColl.insertOne(doc.append(Constants.TX_OPERATION, Constants.TX_OP_UPDATE));
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
					listToInsert.add(doc.append(Constants.TX_OPERATION, Constants.TX_OP_UPDATE));
				}
			}
			// do insert
			if (!listToInsert.isEmpty())
				txColl.insertMany(listToInsert);
		}
	}

	void beforeInsertOne(MongoCollection<Document> coll, Document doc) {
		// (document already inserted)
		if (started) {
			// add TX_MAP_COLL and TX_TARGET_ID property
			doc.append(Constants.TX_MAP_COLL, coll.getNamespace().getCollectionName()).append(Constants.TX_OPERATION,
					Constants.TX_OP_INSERT);
			Object _id = doc.remove("_id");
			if (null != _id) {
				doc.append(Constants.TX_TARGET_ID, _id);
			}
		}
	}

	void beforeInsertMany(MongoCollection<Document> coll, List<? extends Document> docList) {
		// (document already inserted)
		if (started) {
			for (Document doc : docList) {
				// add TX_MAP_COLL property
				doc.append(Constants.TX_MAP_COLL, coll.getNamespace().getCollectionName())
						.append(Constants.TX_OPERATION, Constants.TX_OP_INSERT);
				Object _id = doc.remove("_id");
				if (null != _id) {
					doc.append(Constants.TX_TARGET_ID, _id);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	DeleteResult deleteOne(MongoCollection<Document> coll, Bson query) {
		Document doc = coll.find(query).first();
		if (null == doc) {
			// doc not in collection
			// for insert
			Document sample = new Document((Map<String, Object>) query);
			Object _id = sample.remove("_id");
			if (null != _id) {
				sample.append(Constants.TX_TARGET_ID, _id);
			}
			sample.append(Constants.TX_REF, null).append(Constants.TX_MAP_COLL,
					coll.getNamespace().getCollectionName());
			if (txColl.deleteOne(sample).getDeletedCount() == 0) {
				// for update
				sample.remove(Constants.TX_TARGET_ID);
				if (null == _id) {
					sample.put(Constants.TX_REF, new BasicDBObject("$ne", null));
				} else {
					sample.put(Constants.TX_REF, _id);
				}
				return new MannualDeleteResult(txColl
						.updateOne(sample,
								new BasicDBObject("$set",
										new BasicDBObject(Constants.TX_OPERATION, Constants.TX_OP_DELETE)))
						.getModifiedCount(), true);
			} else {
				return new MannualDeleteResult(1L, true);
			}
		} else {
			// doc exist in collection
			Object _id = doc.get("_id");
			if (requireDocCopy(coll, doc)) {
				// not in tx collection
				doc.append(Constants.TX_OPERATION, Constants.TX_OP_DELETE); // mark
																			// as
																			// delete
				txColl.insertOne(doc);
				return new MannualDeleteResult(1L, true);
			} else {
				// in tx collection
				return new MannualDeleteResult(txColl
						.updateOne(
								new BasicDBObject(Constants.TX_REF, _id).append(Constants.TX_MAP_COLL,
										coll.getNamespace().getCollectionName()),
						new BasicDBObject("$set", new BasicDBObject(Constants.TX_OPERATION, Constants.TX_OP_DELETE)))
						.getModifiedCount(), true);
			}
		}
	}

	@SuppressWarnings("unchecked")
	DeleteResult deleteMany(MongoCollection<Document> coll, Bson query) {
		FindIterable<Document> res = coll.find(query);
		Iterator<Document> it = res.iterator();
		List<Document> listToInsert = new ArrayList<Document>();
		List<Object> listToUpdate = new ArrayList<Object>();
		while (it.hasNext()) {
			Document doc = it.next();
			Object _id = doc.get("_id");
			if (requireDocCopy(coll, doc)) {
				doc.append(Constants.TX_OPERATION, Constants.TX_OP_DELETE); // mark
																			// as
																			// delete
				listToInsert.add(doc);
			} else {
				listToUpdate.add(_id); // update already existed tx
										// documents
			}
		}
		long count = 0;
		if (!listToUpdate.isEmpty()) {
			count += txColl
					.updateMany(
							new BasicDBObject(Constants.TX_MAP_COLL, coll.getNamespace().getCollectionName())
									.append(Constants.TX_REF, new BasicDBObject("$in", listToUpdate)),
					new BasicDBObject("$set", new BasicDBObject(Constants.TX_OPERATION, Constants.TX_OP_DELETE)))
					.getModifiedCount();
		}
		if (!listToInsert.isEmpty()) {
			txColl.insertMany(listToInsert);
			count += listToInsert.size();
		}
		// for inserted documents, which would be deleted
		Document sample = new Document((Map<String, Object>) query);
		Object _id = sample.remove("_id");
		if (null != _id) {
			sample.append(Constants.TX_TARGET_ID, _id);
		}
		sample.append(Constants.TX_REF, null).append(Constants.TX_MAP_COLL, coll.getNamespace().getCollectionName());
		count += txColl.deleteMany(sample).getDeletedCount();
		// for updated documents
		sample.remove(Constants.TX_TARGET_ID);
		if (null == _id) {
			sample.put(Constants.TX_REF, new BasicDBObject("$ne", null));
		} else {
			sample.put(Constants.TX_REF, _id);
		}
		count += txColl
				.updateMany(sample,
						new BasicDBObject("$set", new BasicDBObject(Constants.TX_OPERATION, Constants.TX_OP_DELETE)))
				.getModifiedCount();
		return new MannualDeleteResult(count, true);
	}

	public MongoCollection<Document> getTxCollection() {
		return txColl;
	}

	public void start() {
		if (!this.started) {
			id = Constants.findId();
			txColl = db.getCollection(Constants.TX_COLL_PREFIX + id);
			started = true;
		}
	}

	public boolean started() {
		return started;
	}

	public void commit() {
		state = Constants.COMMITTING;
		// get all documents in transaction collection
		FindIterable<Document> toCommit = txColl.find();
		for (Document doc : toCommit) {
			// remove _id, TX_MAP_COLL, TX_OPERATION and TX_BKUP so that the
			// document can be used directly (TX_REF would be removed later if
			// exist)
			String op = (String) doc.remove(Constants.TX_OPERATION);
			String collName = (String) doc.remove(Constants.TX_MAP_COLL);
			doc.remove(Constants.TX_BKUP);
			MongoCollection<Document> coll = db.getCollection(collName);
			Object tx_id = doc.remove("_id");
			if (op.equals(Constants.TX_OP_INSERT)) {
				// insert
				Object _id = doc.remove(Constants.TX_TARGET_ID);
				if (null == _id) {
					doc.append("_id", tx_id);
				} else {
					doc.append("_id", _id);
				}
				coll.insertOne(doc);
				if (null == _id) {
					_id = doc.get("_id");
				}
				// change state and set tx_ref
				txColl.updateOne(new BasicDBObject("_id", tx_id), new BasicDBObject("$set",
						new BasicDBObject(Constants.TX_REQUIRE_ROLLBACK_ON_ERROR, true).append(Constants.TX_REF, _id)));
			} else {
				// remove TX_REF
				Object _id = doc.remove(Constants.TX_REF);
				if (op.equals(Constants.TX_OP_DELETE)) {
					// delete
					coll.deleteOne(new BasicDBObject("_id", _id));
				} else {
					// update
					coll.replaceOne(new BasicDBObject("_id", _id), doc);
				}
				// change state
				txColl.updateOne(new BasicDBObject("_id", tx_id),
						new BasicDBObject("$set", new BasicDBObject(Constants.TX_REQUIRE_ROLLBACK_ON_ERROR, true)));
			}
		}
		clear();
		state = Constants.INITIAL;
	}

	private void clear() {
		txColl.drop();
		synchronized (Constants.txIdCache) {
			Constants.txIdCache.remove(id);
		}
	}

	@SuppressWarnings("unchecked")
	public void rollback() {
		state = Constants.ROLLINGBACK;
		if (state == Constants.COMMITTING) {
			// get all documents in transaction collection
			FindIterable<Document> res = txColl.find(new BasicDBObject(Constants.TX_REQUIRE_ROLLBACK_ON_ERROR, true));
			for (Document doc : res) {
				// state require rolling back
				String collName = doc.getString(Constants.TX_MAP_COLL);
				MongoCollection<Document> coll = db.getCollection(collName);
				if (doc.getString(Constants.TX_OPERATION).equals(Constants.TX_OP_DELETE)) {
					// delete - insert updated obj
					Document bkup = new Document((Map<String, Object>) doc.get(Constants.TX_BKUP));
					bkup.put("_id", doc.get(Constants.TX_REF));
					coll.replaceOne(new BasicDBObject("_id", doc.get(Constants.TX_REF)), bkup);
				} else {
					if (doc.getString(Constants.TX_OPERATION).equals(Constants.TX_OP_UPDATE)) {
						// update - rollback updated obj
						Document bkup = new Document((Map<String, Object>) doc.get(Constants.TX_BKUP));
						bkup.put("_id", doc.get(Constants.TX_REF));
						coll.replaceOne(new BasicDBObject("_id", doc.get(Constants.TX_REF)), bkup);
					} else {
						// insert - delete inserted obj
						coll.deleteOne(new BasicDBObject("_id", doc.get(Constants.TX_REF)));
					}
				}
			}
		}
		clear();
		state = Constants.INITIAL;
	}

	@Override
	public void close() {
		if (started) {
			clear();
			synchronized (Constants.transactionId) {
				Constants.transactionId.remove(id);
			}
		}
		started = false;
	}
}