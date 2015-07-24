package test;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import cass.enhanced.mongo.Transaction;
import cass.enhanced.mongo.TxClient;
import cass.enhanced.mongo.TxDatabase;

public class TestEnhance {

	public static void main(String[] args) {
		TxClient client = new TxClient();
		TxDatabase db = client.getDatabase("test_transaction");
		Transaction tx = db.getTransaction();
		db.drop();
		MongoCollection<Document> test1 = db.getCollection("test1");
		MongoCollection<Document> test2 = db.getCollection("test2");

		List<Document> list1 = new ArrayList<Document>();
		List<Document> list2 = new ArrayList<Document>();
		// init data
		list1.add(new Document("name", "java").append("description", "water B very duo").append("count", 1000000));
		list1.add(new Document("name", "c#").append("description", "few people").append("count", 500000));

		list2.add(new Document("name", "js").append("description", "ghost bar").append("count", 10000));
		list2.add(new Document("name", "php").append("description", "best programming language in the world")
				.append("count", 500000));
		list2.add(new Document("name", "node").append("description", "i didn't learn").append("count", 200000));

		test1.insertMany(list1);
		test2.insertMany(list2);

		// now, 50K people move from java to php
		tx.start();
		MongoCollection<Document> txColl = db.getCollection("j_transaction_0");
		test1.updateOne(new BasicDBObject("name", "java"),
				new BasicDBObject("$inc", new BasicDBObject("count", -50000)));
		test2.updateOne(new BasicDBObject("name", "php"), new BasicDBObject("$inc", new BasicDBObject("count", 50000)));

		System.out.println("=====test1=====uncommitted=====");
		for (Document doc : test1.find()) {
			System.out.println(doc.toJson());
		}
		System.out.println("=====test2=====uncommitted=====");
		for (Document doc : test2.find()) {
			System.out.println(doc.toJson());
		}
		System.out.println("=====tx=====uncommitted=====");
		for (Document doc : txColl.find()) {
			System.out.println(doc.toJson());
		}

		tx.commit();
		System.out.println("=====test1=====committed=====");
		for (Document doc : test1.find()) {
			System.out.println(doc.toJson());
		}
		System.out.println("=====test2=====committed=====");
		for (Document doc : test2.find()) {
			System.out.println(doc.toJson());
		}
		client.close();
	}

}
