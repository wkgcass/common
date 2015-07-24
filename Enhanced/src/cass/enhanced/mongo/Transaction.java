package cass.enhanced.mongo;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	private static final String MAP_REF = "object-ref";
	private static final String MAP_COLL = "collection-name";
	static final String TX_MAP_COLL = "j-tx-map-coll";
	static final String TX_REF = "j-transaction-ref";
	private static final String TX_BKUP = "j-tx-backup";
	private static final String TX_REQUIRE_ROLLBACK_ON_ERROR = "j-tx-req-rolbk-on-err";
	private static final String TX_COLL_PREFIX = "j_transaction_";
	private static List<Integer> transactionId = new LinkedList<Integer>();
	private static Map<Integer, List<Map<String, Object>>> txMap = new HashMap<Integer, List<Map<String, Object>>>();

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

	private boolean requireMapping(MongoCollection<Document> coll, Object ref_id) {
		List<Map<String, Object>> txList;
		synchronized (txMap) {
			if (txMap.containsKey(id)) {
				txList = txMap.get(id);
			} else {
				txList = new ArrayList<Map<String, Object>>();
				txMap.put(id, txList);
			}
		}
		Map<String, Object> tmpMap = null;
		synchronized (txList) {
			for (Map<String, Object> map : txList) {
				if (map.get(MAP_REF).equals(ref_id)) {
					return false;
				}
			}
			tmpMap = new HashMap<String, Object>();
			txList.add(tmpMap);
		}
		synchronized (tmpMap) {
			tmpMap.put(MAP_REF, ref_id);
			tmpMap.put(MAP_COLL, coll.getNamespace().getCollectionName());
		}
		return true;
	}

	private boolean requireDocCopy(MongoCollection<Document> coll, Document doc) {
		String collName = coll.getNamespace().getCollectionName();
		Object _id = doc.remove("_id");
		// check whether already mapped
		if (!requireMapping(coll, _id)) {
			return false;
		}
		// set mapping properties
		doc.put(TX_REF, _id);
		doc.put(TX_MAP_COLL, collName);
		return true;
	}

	void beforeUpdateOne(MongoCollection<Document> coll, Bson query) {
		if (started) {
			Document doc = coll.find(query).first();
			if (null != doc) {
				if (requireDocCopy(coll, doc)) {
					txColl.insertOne(doc);
				}
			}
		}
	}

	void beforeUpdateMany(MongoCollection<Document> coll, Bson query) {
		if (started) {
			FindIterable<Document> res = coll.find(query);
			List<Document> list = new ArrayList<Document>();
			for (Document doc : res) {
				if (!requireDocCopy(coll, doc)) {
					continue;
				}
				list.add(doc);
			}
			// do insert
			txColl.insertMany(list);
		}
	}

	void afterInsertOne(MongoCollection<Document> coll, Object _id) {
		if (started) {
			txColl.updateOne(new BasicDBObject("_id", _id),
					new BasicDBObject("$set", new BasicDBObject(TX_MAP_COLL, coll.getNamespace().getCollectionName())));
		}
	}

	void afterInsertMany(MongoCollection<Document> coll, List<? extends Document> docList) {
		if (started) {
			List<Object> _ids = new ArrayList<Object>(docList.size());
			for (Document doc : docList) {
				Object _id = doc.get("_id");
				_ids.add(_id);
			}
			txColl.updateMany(new BasicDBObject("_id", new BasicDBObject("$in", _ids)),
					new BasicDBObject("$set", new BasicDBObject(TX_MAP_COLL, coll.getNamespace().getCollectionName())));
		}
	}

	private static class MannualDeleteResult extends DeleteResult {
		private long count;
		private boolean acknowledged;

		public MannualDeleteResult(long count, boolean acknowledged) {
			this.count = count;
			this.acknowledged = acknowledged;
		}

		@Override
		public long getDeletedCount() {
			return count;
		}

		@Override
		public boolean wasAcknowledged() {
			return acknowledged;
		}
	}

	DeleteResult deleteOne(MongoCollection<Document> coll, Bson query) {
		Document doc = coll.find(query).first();
		if (null != doc) {
			Object _id = doc.get("_id");
			if (!requireMapping(coll, _id)) {
				txColl.deleteOne(
						new BasicDBObject(TX_REF, _id).append(TX_MAP_COLL, coll.getNamespace().getCollectionName()));
			}
			return new MannualDeleteResult(1l, true);
		}
		return new MannualDeleteResult(0l, true);
	}

	DeleteResult deleteMany(MongoCollection<Document> coll, Bson query) {
		FindIterable<Document> res = coll.find(query);
		Iterator<Document> it = res.iterator();
		long count = 0;
		while (it.hasNext()) {
			++count;
			Document doc = it.next();
			Object _id = doc.get("_id");
			if (!requireMapping(coll, _id)) {
				txColl.deleteOne(
						new BasicDBObject(TX_REF, _id).append(TX_MAP_COLL, coll.getNamespace().getCollectionName()));
			}
		}
		return new MannualDeleteResult(count, true);
	}

	MongoCollection<Document> getTxCollection() {
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
		List<Map<String, Object>> txList = null;
		synchronized (txMap) {
			txList = txMap.get(id);
		}
		if (null != txList) {
			synchronized (txList) {
				FindIterable<Document> toCommit = txColl.find();
				for (Document doc : toCommit) {
					String collName = (String) doc.remove(TX_MAP_COLL);
					MongoCollection<Document> coll = db.getCollection(collName);
					if (doc.containsKey(TX_REF)) {
						// update
						Object _id = doc.remove(TX_REF);
						Object tx_id = doc.put("_id", _id);
						Bson query = new BasicDBObject("_id", _id);
						Iterator<Document> it = coll.find(query).iterator();
						if (it.hasNext()) {
							// no other tx delete operation
							Document target = it.next();
							target.remove("_id");
							txColl.updateOne(new BasicDBObject("_id", tx_id), new BasicDBObject("$set",
									new BasicDBObject(TX_BKUP, target).append(TX_REQUIRE_ROLLBACK_ON_ERROR, true)));

							// do update
							coll.replaceOne(new BasicDBObject("_id", _id), doc);

							// delete map
							Iterator<Map<String, Object>> mapIt = txList.iterator();
							while (mapIt.hasNext()) {
								Map<String, Object> map = mapIt.next();
								if (map.get(MAP_REF).equals(_id) && map.get(MAP_COLL).equals(collName)) {
									mapIt.remove();
									break;
								}
							}
						}
					} else {
						// insert
						Object tx_id = doc.remove("_id");
						coll.insertOne(doc);
						Object _id = doc.get("_id");
						txColl.updateOne(new BasicDBObject("_id", tx_id), new BasicDBObject("$set",
								new BasicDBObject(TX_REQUIRE_ROLLBACK_ON_ERROR, true).append(TX_REF, _id)));
					}
				}
				for (Map<String, Object> map : txList) {
					// delete
					Object _id = map.get(MAP_REF);
					String collName = (String) map.get(MAP_COLL);
					MongoCollection<Document> coll = db.getCollection(collName);
					FindIterable<Document> res = coll.find(new BasicDBObject("_id", _id));
					Iterator<Document> it = res.iterator();
					if (it.hasNext()) {
						Document retrievedDoc = it.next();
						retrievedDoc.remove("_id");
						Document insertedDoc = new Document(TX_REF, _id).append(TX_MAP_COLL, collName).append(TX_BKUP,
								retrievedDoc.append(TX_REQUIRE_ROLLBACK_ON_ERROR, true));
						txColl.insertOne(insertedDoc);
						coll.deleteOne(new BasicDBObject("_id", _id));
					}
				}
			}
		}
		clear();
		state = INITIAL;
	}

	private void clear() {
		txColl.drop();
		synchronized (txMap) {
			txMap.remove(id);
		}
	}

	@SuppressWarnings("unchecked")
	public void rollback() {
		state = ROLLINGBACK;
		if (state == COMMITTING) {
			FindIterable<Document> res = txColl.find();
			for (Document doc : res) {
				boolean rollback = doc.getBoolean(TX_REQUIRE_ROLLBACK_ON_ERROR, false);
				if (rollback) {
					String collName = doc.getString(TX_MAP_COLL);
					MongoCollection<Document> coll = db.getCollection(collName);
					if (doc.containsKey(TX_REF)) {
						// insert or update
						if (doc.containsKey(TX_BKUP)) {
							// update - rollback updated obj
							Document bkup = new Document((Map<String, Object>) doc.get(TX_BKUP));
							bkup.put("_id", doc.get(TX_REF));
							coll.replaceOne(new BasicDBObject("_id", doc.get(TX_REF)), bkup);
						} else {
							// insert - delete inserted obj
							coll.deleteOne(new BasicDBObject("_id", doc.get(TX_REF)));
						}
					} else {
						// delete - insert updated obj
						Document bkup = new Document((Map<String, Object>) doc.get(TX_BKUP));
						bkup.put("_id", doc.get(TX_REF));
						coll.replaceOne(new BasicDBObject("_id", doc.get(TX_REF)), bkup);
					}
				}
			}
		}
		clear();
		txColl = db.getCollection(TX_COLL_PREFIX + id);
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