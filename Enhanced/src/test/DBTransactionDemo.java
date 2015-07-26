package test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;

import cass.enhanced.mongo.TxClient;

public class DBTransactionDemo {

	public static void main(String[] args) {
		TxClient client=new TxClient();
		DB db=client.getDB("transaction-test");
		db.dropDatabase();
		db.getCollection("bank1");
		db.getCollectionFromString("bank1");
		db.createCollection("bank1", new BasicDBObject());
		db.dropDatabase();
	}

}
