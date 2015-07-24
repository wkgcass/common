package cass.enhanced.mongo;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.CreateCollectionOptions;

public class TxDatabase implements MongoDatabase {
	private final MongoDatabase db;
	private final Transaction tx;

	public TxDatabase(MongoDatabase db) {
		this.db = db;
		this.tx = new Transaction(db);
	}

	@Override
	public void createCollection(String arg0) {
		db.createCollection(arg0);
	}

	@Override
	public void createCollection(String arg0, CreateCollectionOptions arg1) {
		db.createCollection(arg0, arg1);
	}

	@Override
	public void drop() {
		db.drop();
	}

	@Override
	public CodecRegistry getCodecRegistry() {
		return db.getCodecRegistry();
	}

	@Override
	public MongoCollection<Document> getCollection(String arg0) {
		return new TxCollection(db.getCollection(arg0), tx);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <TDocument> MongoCollection<TDocument> getCollection(String arg0, Class<TDocument> arg1) {
		if (arg1 != Document.class) {
			throw new java.lang.UnsupportedOperationException();
		} else {
			return (MongoCollection<TDocument>) getCollection(arg0);
		}
	}

	@Override
	public String getName() {
		return db.getName();
	}

	@Override
	public ReadPreference getReadPreference() {
		return db.getReadPreference();
	}

	@Override
	public WriteConcern getWriteConcern() {
		return db.getWriteConcern();
	}

	@Override
	public MongoIterable<String> listCollectionNames() {
		return db.listCollectionNames();
	}

	@Override
	public ListCollectionsIterable<Document> listCollections() {
		return db.listCollections();
	}

	@Override
	public <TResult> ListCollectionsIterable<TResult> listCollections(Class<TResult> arg0) {
		return db.listCollections(arg0);
	}

	@Override
	public Document runCommand(Bson arg0) {
		return db.runCommand(arg0);
	}

	@Override
	public Document runCommand(Bson arg0, ReadPreference arg1) {
		return db.runCommand(arg0, arg1);
	}

	@Override
	public <TResult> TResult runCommand(Bson arg0, Class<TResult> arg1) {
		return db.runCommand(arg0, arg1);
	}

	@Override
	public <TResult> TResult runCommand(Bson arg0, ReadPreference arg1, Class<TResult> arg2) {
		return db.runCommand(arg0, arg1, arg2);
	}

	@Override
	public MongoDatabase withCodecRegistry(CodecRegistry arg0) {
		return db.withCodecRegistry(arg0);
	}

	@Override
	public MongoDatabase withReadPreference(ReadPreference arg0) {
		return db.withReadPreference(arg0);
	}

	@Override
	public MongoDatabase withWriteConcern(WriteConcern arg0) {
		return db.withWriteConcern(arg0);
	}

	public Transaction getTransaction() {
		return this.tx;
	}

}
