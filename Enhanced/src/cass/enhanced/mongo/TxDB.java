package cass.enhanced.mongo;

import java.util.List;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class TxDB extends DB {

	public TxDB(Mongo mongo, String name) {
		super(mongo, name);
	}

	@Override
	public DBCollection getCollection(String name) {
		return new TxDBCollection(this, name);
	}
	
	protected void txInsert(List<? extends DBObject> documents){
		
	}
	
	protected void txSave(DBObject document){
		
	}
	
	protected void txUpdate(DBObject query){
		
	}
	
	protected void txRemove(DBObject query){
		
	}

}
