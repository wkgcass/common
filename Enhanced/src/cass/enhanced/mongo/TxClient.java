package cass.enhanced.mongo;

import com.mongodb.MongoClient;

public class TxClient extends MongoClient {

	@Override
	public TxDatabase getDatabase(String databaseName) {
		return new TxDatabase(super.getDatabase(databaseName));
	}
}
