package cass.enhanced.mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class TxClient extends MongoClient {

	@Override
	public TxDatabase getDatabase(String databaseName) {
		return new TxDatabase(super.getDatabase(databaseName));
	}

	public DB getDB(String name) {
		return new TxDB(this, name);
	}
}
