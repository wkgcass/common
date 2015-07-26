package cass.enhanced.mongo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mongodb.DBCollection;
import com.mongodb.DBEncoder;
import com.mongodb.DBObject;
import com.mongodb.InsertOptions;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class TxDBCollection extends DBCollection {

	private TxDB txdb;

	protected TxDBCollection(TxDB database, String name) {
		super(database, name);
		txdb = database;
	}

	public WriteResult insert(List<? extends DBObject> documents, InsertOptions options) {
		return super.insert(documents, options);
	}

	public WriteResult save(DBObject document, WriteConcern writeConcern) {
		return super.save(document, writeConcern);
	}

	public WriteResult update(DBObject query, DBObject update, boolean upsert, boolean multi,
			WriteConcern aWriteConcern, DBEncoder encoder) {
		return super.update(query, update, upsert, multi, aWriteConcern, encoder);
	}

	public WriteResult remove(DBObject query, WriteConcern writeConcern) {
		return super.remove(query, writeConcern);
	}

	public WriteResult remove(DBObject query, WriteConcern writeConcern, DBEncoder encoder) {
		return super.remove(query, writeConcern, encoder);
	}

	public DBObject findAndModify(DBObject query, DBObject fields, DBObject sort, boolean remove, DBObject update,
			boolean returnNew, boolean upsert, long l, TimeUnit t) {
		return super.findAndModify(query, fields, sort, remove, update, returnNew, upsert, l, t);
	}

}
