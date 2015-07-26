package test;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import cass.enhanced.mongo.Transaction;
import cass.enhanced.mongo.TxClient;
import cass.enhanced.mongo.TxDatabase;

public class MongoTransactionDemo {

	public static void main(String[] args) {
		// init
		TxClient client = new TxClient();
		TxDatabase db = client.getDatabase("transaction-test");
		db.drop();
		MongoCollection<Document> bank1 = db.getCollection("bank1");
		MongoCollection<Document> bank2 = db.getCollection("bank2");
		insertInitData(bank1, bank2);
		Transaction tx = db.getTransaction();
		tx.start();
		MongoCollection<Document> txColl = tx.getTxCollection();
		System.out.println("----initial state----");
		printState(bank1, bank2, txColl);

		// insertOne(bank1, bank2);
		// insertMany(bank1, bank2);
		// updateOne(bank1, bank2);
		// updateMany(bank1, bank2);
		// deleteOne(bank1, bank2);
		// deleteMany(bank1, bank2);
		System.out.println("----uncommitted state----");
		printState(bank1, bank2, txColl);

		tx.commit();
		System.out.println("----committed state----");
		printState(bank1, bank2, txColl);
		finalize(db);
		tx.close();
		client.close();
	}

	static void insertInitData(MongoCollection<Document> bank1, MongoCollection<Document> bank2) {
		bank1.insertOne(new Document("account", "ABCD").append("money", 10000).append("_id", 1111));
		bank1.insertOne(new Document("account", "!@#$").append("money", 19000));
		bank2.insertOne(new Document("account", "EFGH").append("money", 5000).append("_id", 2222));
	}

	static void finalize(MongoDatabase db) {
		db.drop();
	}

	static void printState(MongoCollection<Document> bank1, MongoCollection<Document> bank2,
			MongoCollection<Document> txColl) {
		FindIterable<Document> bank1All = bank1.find();
		FindIterable<Document> bank2All = bank2.find();
		FindIterable<Document> txAll = txColl.find();
		System.out.println(">>>>bank1 content");
		for (Document doc : bank1All) {
			System.out.println(doc.toJson());
		}
		System.out.println(">>>>bank2 content");
		for (Document doc : bank2All) {
			System.out.println(doc.toJson());
		}
		System.out.println(">>>>tx content");
		for (Document doc : txAll) {
			System.out.println(doc.toJson());
		}
		System.out.println("<<<<");
	}

	// pass
	static void insertOne(MongoCollection<Document> bank1, MongoCollection<Document> bank2) {
		bank1.insertOne(new Document("account", "IJKL").append("money", 7000).append("_id", 3333));
		bank2.insertOne(new Document("account", "MNOP").append("money", 4000));
	}

	// pass
	static void insertMany(MongoCollection<Document> bank1, MongoCollection<Document> bank2) {
		List<Document> insertList1 = new ArrayList<Document>();
		insertList1.add(new Document("account", "IJKL").append("money", 7000).append("_id", 3333));
		insertList1.add(new Document("account", "QRST").append("money", 1000));

		List<Document> insertList2 = new ArrayList<Document>();
		insertList2.add(new Document("account", "MNOP").append("money", 4000));
		insertList2.add(new Document("account", "UVWX").append("money", 8000));

		bank1.insertMany(insertList1);
		bank2.insertMany(insertList2);
	}

	// pass
	static void updateOne(MongoCollection<Document> bank1, MongoCollection<Document> bank2) {
		insertOne(bank1, bank2);
		// update with id
		bank1.updateOne(new BasicDBObject("_id", 1111), new BasicDBObject("$inc", new BasicDBObject("money", 200)));
		// update with inserted id
		bank1.updateOne(new BasicDBObject("_id", 3333), new BasicDBObject("$inc", new BasicDBObject("money", 300)));
		// update with other
		bank2.updateOne(new BasicDBObject("account", "EFGH"),
				new BasicDBObject("$set", new BasicDBObject("account", "efgh")));
		// update with other inserted
		bank2.updateOne(new BasicDBObject("account", "MNOP"),
				new BasicDBObject("$set", new BasicDBObject("account", "mnop")));
	}

	// pass
	static void updateMany(MongoCollection<Document> bank1, MongoCollection<Document> bank2) {
		insertMany(bank1, bank2);
		// update with id
		bank1.updateMany(new BasicDBObject("_id", 1111), new BasicDBObject("$inc", new BasicDBObject("money", 200)));
		// update with inserted id
		bank1.updateMany(new BasicDBObject("_id", 3333), new BasicDBObject("$inc", new BasicDBObject("money", 300)));
		// update with other
		bank2.updateMany(new BasicDBObject("account", "EFGH"),
				new BasicDBObject("$set", new BasicDBObject("account", "efgh")));
		// update with other inserted
		bank2.updateMany(new BasicDBObject("account", "MNOP"),
				new BasicDBObject("$set", new BasicDBObject("account", "mnop")));
	}

	// pass
	static void deleteOne(MongoCollection<Document> bank1, MongoCollection<Document> bank2) {
		updateMany(bank1, bank2);
		// delete with id
		bank1.deleteOne(new BasicDBObject("_id", 1111));
		// delete with inserted id
		bank1.deleteOne(new BasicDBObject("_id", 3333));
		// delete with other
		bank1.deleteOne(new BasicDBObject("account", "!@#$"));
		// delete with other updated
		bank2.deleteOne(new BasicDBObject("account", "efgh"));
		// delete with other inserted
		bank2.deleteOne(new BasicDBObject("account", "IJKL"));
	}

	// pass
	static void deleteMany(MongoCollection<Document> bank1, MongoCollection<Document> bank2) {
		updateMany(bank1, bank2);
		// delete with id
		bank1.deleteMany(new BasicDBObject("_id", 1111));
		// delete with inserted id
		bank1.deleteMany(new BasicDBObject("_id", 3333));
		// delete with other
		bank1.deleteMany(new BasicDBObject("account", "!@#$"));
		// delete with other updated
		bank2.deleteMany(new BasicDBObject("account", "efgh"));
		// delete with other inserted
		bank2.deleteMany(new BasicDBObject("account", "IJKL"));
	}

}

/*
result would look like this
insert one
----initial state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44bcd75cd9e9d04e1d09e" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
<<<<
----uncommitted state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44bcd75cd9e9d04e1d09e" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
{ "_id" : { "$oid" : "55b44bcd75cd9e9d04e1d09f" }, "account" : "IJKL", "money" : 7000, "j-tx-map-coll" : "bank1", "j-transaction-op" : "insert", "j-tx-target-id" : 3333 }
{ "_id" : { "$oid" : "55b44bcd75cd9e9d04e1d0a0" }, "account" : "MNOP", "money" : 4000, "j-tx-map-coll" : "bank2", "j-transaction-op" : "insert" }
<<<<
----committed state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44bcd75cd9e9d04e1d09e" }, "account" : "!@#$", "money" : 19000 }
{ "_id" : 3333, "account" : "IJKL", "money" : 7000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
{ "_id" : { "$oid" : "55b44bcd75cd9e9d04e1d0a1" }, "account" : "MNOP", "money" : 4000 }
>>>>tx content
<<<<

insert many
----initial state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44be875cd9e9d901c8151" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
<<<<
----uncommitted state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44be875cd9e9d901c8151" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
{ "_id" : { "$oid" : "55b44be875cd9e9d901c8152" }, "account" : "IJKL", "money" : 7000, "j-tx-map-coll" : "bank1", "j-transaction-op" : "insert", "j-tx-target-id" : 3333 }
{ "_id" : { "$oid" : "55b44be875cd9e9d901c8153" }, "account" : "QRST", "money" : 1000, "j-tx-map-coll" : "bank1", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44be875cd9e9d901c8154" }, "account" : "MNOP", "money" : 4000, "j-tx-map-coll" : "bank2", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44be875cd9e9d901c8155" }, "account" : "UVWX", "money" : 8000, "j-tx-map-coll" : "bank2", "j-transaction-op" : "insert" }
<<<<
----committed state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44be875cd9e9d901c8151" }, "account" : "!@#$", "money" : 19000 }
{ "_id" : 3333, "account" : "IJKL", "money" : 7000 }
{ "_id" : { "$oid" : "55b44be875cd9e9d901c8156" }, "account" : "QRST", "money" : 1000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
{ "_id" : { "$oid" : "55b44be875cd9e9d901c8157" }, "account" : "MNOP", "money" : 4000 }
{ "_id" : { "$oid" : "55b44be875cd9e9d901c8158" }, "account" : "UVWX", "money" : 8000 }
>>>>tx content
<<<<

update one
----initial state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44bf975cd9e9e04a31166" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
<<<<
----uncommitted state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44bf975cd9e9e04a31166" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
{ "_id" : { "$oid" : "55b44bf975cd9e9e04a31167" }, "account" : "IJKL", "money" : 7300, "j-tx-map-coll" : "bank1", "j-transaction-op" : "insert", "j-tx-target-id" : 3333 }
{ "_id" : { "$oid" : "55b44bf975cd9e9e04a31168" }, "account" : "mnop", "money" : 4000, "j-tx-map-coll" : "bank2", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44bf975cd9e9e04a31169" }, "account" : "ABCD", "money" : 10200, "j-tx-backup" : { "account" : "ABCD", "money" : 10000 }, "j-transaction-ref" : 1111, "j-tx-map-coll" : "bank1", "j-transaction-op" : "update" }
{ "_id" : { "$oid" : "55b44bf975cd9e9e04a3116a" }, "account" : "efgh", "money" : 5000, "j-tx-backup" : { "account" : "EFGH", "money" : 5000 }, "j-transaction-ref" : 2222, "j-tx-map-coll" : "bank2", "j-transaction-op" : "update" }
<<<<
----committed state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10200 }
{ "_id" : { "$oid" : "55b44bf975cd9e9e04a31166" }, "account" : "!@#$", "money" : 19000 }
{ "_id" : 3333, "account" : "IJKL", "money" : 7300 }
>>>>bank2 content
{ "_id" : 2222, "account" : "efgh", "money" : 5000 }
{ "_id" : { "$oid" : "55b44bf975cd9e9e04a3116b" }, "account" : "mnop", "money" : 4000 }
>>>>tx content
<<<<

update many
----initial state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8590" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
<<<<
----uncommitted state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8590" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8591" }, "account" : "IJKL", "money" : 7300, "j-tx-map-coll" : "bank1", "j-transaction-op" : "insert", "j-tx-target-id" : 3333 }
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8592" }, "account" : "QRST", "money" : 1000, "j-tx-map-coll" : "bank1", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8593" }, "account" : "mnop", "money" : 4000, "j-tx-map-coll" : "bank2", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8594" }, "account" : "UVWX", "money" : 8000, "j-tx-map-coll" : "bank2", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8595" }, "account" : "ABCD", "money" : 10200, "j-tx-backup" : { "account" : "ABCD", "money" : 10000 }, "j-transaction-ref" : 1111, "j-tx-map-coll" : "bank1", "j-transaction-op" : "update" }
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8596" }, "account" : "efgh", "money" : 5000, "j-tx-backup" : { "account" : "EFGH", "money" : 5000 }, "j-transaction-ref" : 2222, "j-tx-map-coll" : "bank2", "j-transaction-op" : "update" }
<<<<
----committed state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10200 }
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8590" }, "account" : "!@#$", "money" : 19000 }
{ "_id" : 3333, "account" : "IJKL", "money" : 7300 }
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8597" }, "account" : "QRST", "money" : 1000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "efgh", "money" : 5000 }
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8598" }, "account" : "mnop", "money" : 4000 }
{ "_id" : { "$oid" : "55b44c3175cd9e9f8c6c8599" }, "account" : "UVWX", "money" : 8000 }
>>>>tx content
<<<<

delete one
----initial state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44c4575cd9ea018f6c537" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
<<<<
----uncommitted state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44c4575cd9ea018f6c537" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
{ "_id" : { "$oid" : "55b44c4575cd9ea018f6c539" }, "account" : "QRST", "money" : 1000, "j-tx-map-coll" : "bank1", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44c4575cd9ea018f6c53a" }, "account" : "mnop", "money" : 4000, "j-tx-map-coll" : "bank2", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44c4575cd9ea018f6c53b" }, "account" : "UVWX", "money" : 8000, "j-tx-map-coll" : "bank2", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44c4575cd9ea018f6c53c" }, "account" : "ABCD", "money" : 10200, "j-tx-backup" : { "account" : "ABCD", "money" : 10000 }, "j-transaction-ref" : 1111, "j-tx-map-coll" : "bank1", "j-transaction-op" : "delete" }
{ "_id" : { "$oid" : "55b44c4575cd9ea018f6c53d" }, "account" : "efgh", "money" : 5000, "j-tx-backup" : { "account" : "EFGH", "money" : 5000 }, "j-transaction-ref" : 2222, "j-tx-map-coll" : "bank2", "j-transaction-op" : "delete" }
{ "_id" : { "$oid" : "55b44c4575cd9ea018f6c53e" }, "account" : "!@#$", "money" : 19000, "j-tx-backup" : { "account" : "!@#$", "money" : 19000 }, "j-transaction-ref" : { "$oid" : "55b44c4575cd9ea018f6c537" }, "j-tx-map-coll" : "bank1", "j-transaction-op" : "delete" }
<<<<
----committed state----
>>>>bank1 content
{ "_id" : { "$oid" : "55b44c4575cd9ea018f6c53f" }, "account" : "QRST", "money" : 1000 }
>>>>bank2 content
{ "_id" : { "$oid" : "55b44c4575cd9ea018f6c540" }, "account" : "mnop", "money" : 4000 }
{ "_id" : { "$oid" : "55b44c4575cd9ea018f6c541" }, "account" : "UVWX", "money" : 8000 }
>>>>tx content
<<<<

delete many
----initial state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44c5575cd9ea08c52334b" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
<<<<
----uncommitted state----
>>>>bank1 content
{ "_id" : 1111, "account" : "ABCD", "money" : 10000 }
{ "_id" : { "$oid" : "55b44c5575cd9ea08c52334b" }, "account" : "!@#$", "money" : 19000 }
>>>>bank2 content
{ "_id" : 2222, "account" : "EFGH", "money" : 5000 }
>>>>tx content
{ "_id" : { "$oid" : "55b44c5575cd9ea08c52334d" }, "account" : "QRST", "money" : 1000, "j-tx-map-coll" : "bank1", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44c5575cd9ea08c52334e" }, "account" : "mnop", "money" : 4000, "j-tx-map-coll" : "bank2", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44c5575cd9ea08c52334f" }, "account" : "UVWX", "money" : 8000, "j-tx-map-coll" : "bank2", "j-transaction-op" : "insert" }
{ "_id" : { "$oid" : "55b44c5575cd9ea08c523350" }, "account" : "ABCD", "money" : 10200, "j-tx-backup" : { "account" : "ABCD", "money" : 10000 }, "j-transaction-ref" : 1111, "j-tx-map-coll" : "bank1", "j-transaction-op" : "delete" }
{ "_id" : { "$oid" : "55b44c5575cd9ea08c523351" }, "account" : "efgh", "money" : 5000, "j-tx-backup" : { "account" : "EFGH", "money" : 5000 }, "j-transaction-ref" : 2222, "j-tx-map-coll" : "bank2", "j-transaction-op" : "delete" }
{ "_id" : { "$oid" : "55b44c5575cd9ea08c523352" }, "account" : "!@#$", "money" : 19000, "j-tx-backup" : { "account" : "!@#$", "money" : 19000 }, "j-transaction-ref" : { "$oid" : "55b44c5575cd9ea08c52334b" }, "j-tx-map-coll" : "bank1", "j-transaction-op" : "delete" }
<<<<
----committed state----
>>>>bank1 content
{ "_id" : { "$oid" : "55b44c5675cd9ea08c523353" }, "account" : "QRST", "money" : 1000 }
>>>>bank2 content
{ "_id" : { "$oid" : "55b44c5675cd9ea08c523354" }, "account" : "mnop", "money" : 4000 }
{ "_id" : { "$oid" : "55b44c5675cd9ea08c523355" }, "account" : "UVWX", "money" : 8000 }
>>>>tx content
<<<<
*/