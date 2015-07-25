package cass.enhanced.mongo;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoNamespace;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.RenameCollectionOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class TxCollection implements MongoCollection<Document> {
	private final MongoCollection<Document> coll;
	private final Transaction tx;

	@SuppressWarnings("unchecked")
	private Bson modifyQuery(Bson query) {
		BasicDBObject bdo = new BasicDBObject((Map<String, Object>) query);
		Object _id = bdo.remove("_id");
		if (null != _id) {
			bdo.append("$or", new BasicDBObject(Transaction.TX_REF, _id).append(Transaction.TX_TARGET_ID, _id));
		}
		bdo.append(Transaction.TX_MAP_COLL, coll.getNamespace().getCollectionName());
		return bdo;
	}

	public TxCollection(MongoCollection<Document> coll, Transaction tx) {
		this.coll = coll;
		this.tx = tx;
	}

	@Override
	public AggregateIterable<Document> aggregate(List<? extends Bson> arg0) {
		return coll.aggregate(arg0);
	}

	@Override
	public <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> arg0, Class<TResult> arg1) {
		return coll.aggregate(arg0, arg1);
	}

	@Override
	public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends Document>> arg0) {
		return coll.bulkWrite(arg0);
	}

	@Override
	public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends Document>> arg0, BulkWriteOptions arg1) {
		return coll.bulkWrite(arg0, arg1);
	}

	@Override
	public long count() {
		return coll.count();
	}

	@Override
	public long count(Bson arg0) {
		return coll.count(arg0);
	}

	@Override
	public long count(Bson arg0, CountOptions arg1) {
		return coll.count(arg0, arg1);
	}

	@Override
	public String createIndex(Bson arg0) {
		return coll.createIndex(arg0);
	}

	@Override
	public String createIndex(Bson arg0, IndexOptions arg1) {
		return coll.createIndex(arg0, arg1);
	}

	@Override
	public List<String> createIndexes(List<IndexModel> arg0) {
		return coll.createIndexes(arg0);
	}

	@Override
	public DeleteResult deleteMany(Bson arg0) {
		if (tx.started()) {
			return tx.deleteMany(coll, arg0);
		} else {
			return coll.deleteMany(arg0);
		}
	}

	@Override
	public DeleteResult deleteOne(Bson arg0) {
		if (tx.started()) {
			return tx.deleteOne(coll, arg0);
		} else {
			return coll.deleteOne(arg0);
		}
	}

	@Override
	public <TResult> DistinctIterable<TResult> distinct(String arg0, Class<TResult> arg1) {
		return coll.distinct(arg0, arg1);
	}

	@Override
	public void drop() {
		if (tx.started()) {
			tx.commit();
		}
		coll.drop();
	}

	@Override
	public void dropIndex(String arg0) {
		coll.dropIndex(arg0);
	}

	@Override
	public void dropIndex(Bson arg0) {
		coll.dropIndex(arg0);
	}

	@Override
	public void dropIndexes() {
		coll.dropIndexes();
	}

	@Override
	public FindIterable<Document> find() {
		return coll.find();
	}

	@Override
	public <TResult> FindIterable<TResult> find(Class<TResult> arg0) {
		return coll.find(arg0);
	}

	@Override
	public FindIterable<Document> find(Bson arg0) {
		return coll.find(arg0);
	}

	@Override
	public <TResult> FindIterable<TResult> find(Bson arg0, Class<TResult> arg1) {
		return coll.find(arg0, arg1);
	}

	@Override
	public Document findOneAndDelete(Bson arg0) {
		if (tx.started()) {
			tx.deleteOne(coll, arg0);
			return coll.find(arg0).first();
		} else {
			return coll.findOneAndDelete(arg0);
		}
	}

	@Override
	public Document findOneAndDelete(Bson arg0, FindOneAndDeleteOptions arg1) {
		if (tx.started()) {
			tx.deleteOne(coll, arg0);
			return coll.find(arg0).first();
		} else {
			return coll.findOneAndDelete(arg0, arg1);
		}
	}

	@Override
	public Document findOneAndReplace(Bson arg0, Document arg1) {
		if (tx.started()) {
			tx.beforeUpdateOne(coll, arg0);
			return tx.getTxCollection().findOneAndReplace(modifyQuery(arg0), arg1);
		} else {
			return coll.findOneAndReplace(arg0, arg1);
		}
	}

	@Override
	public Document findOneAndReplace(Bson arg0, Document arg1, FindOneAndReplaceOptions arg2) {
		if (tx.started()) {
			tx.beforeUpdateOne(coll, arg0);
			return tx.getTxCollection().findOneAndReplace(modifyQuery(arg0), arg1, arg2);
		} else {
			return coll.findOneAndReplace(arg0, arg1, arg2);
		}
	}

	@Override
	public Document findOneAndUpdate(Bson arg0, Bson arg1) {
		if (tx.started()) {
			tx.beforeUpdateOne(coll, arg0);
			return tx.getTxCollection().findOneAndUpdate(modifyQuery(arg0), arg1);
		} else {
			return coll.findOneAndUpdate(arg0, arg1);
		}
	}

	@Override
	public Document findOneAndUpdate(Bson arg0, Bson arg1, FindOneAndUpdateOptions arg2) {
		if (tx.started()) {
			tx.beforeUpdateOne(coll, arg0);
			return tx.getTxCollection().findOneAndUpdate(modifyQuery(arg0), arg1, arg2);
		} else {
			return coll.findOneAndUpdate(arg0, arg1, arg2);
		}
	}

	@Override
	public CodecRegistry getCodecRegistry() {
		return coll.getCodecRegistry();
	}

	@Override
	public Class<Document> getDocumentClass() {
		return coll.getDocumentClass();
	}

	@Override
	public MongoNamespace getNamespace() {
		return coll.getNamespace();
	}

	@Override
	public ReadPreference getReadPreference() {
		return coll.getReadPreference();
	}

	@Override
	public WriteConcern getWriteConcern() {
		return coll.getWriteConcern();
	}

	@Override
	public void insertMany(List<? extends Document> arg0) {
		if (tx.started()) {
			tx.beforeInsertMany(coll, arg0);
			tx.getTxCollection().insertMany(arg0);
		} else {
			coll.insertMany(arg0);
		}
	}

	@Override
	public void insertMany(List<? extends Document> arg0, InsertManyOptions arg1) {
		if (tx.started()) {
			tx.beforeInsertMany(coll, arg0);
			tx.getTxCollection().insertMany(arg0, arg1);
		} else {
			coll.insertMany(arg0, arg1);
		}
	}

	@Override
	public void insertOne(Document arg0) {
		if (tx.started()) {
			tx.beforeInsertOne(coll, arg0);
			tx.getTxCollection().insertOne(arg0);
		} else {
			coll.insertOne(arg0);
		}
	}

	@Override
	public ListIndexesIterable<Document> listIndexes() {
		return coll.listIndexes();
	}

	@Override
	public <TResult> ListIndexesIterable<TResult> listIndexes(Class<TResult> arg0) {
		return coll.listIndexes(arg0);
	}

	@Override
	public MapReduceIterable<Document> mapReduce(String arg0, String arg1) {
		return coll.mapReduce(arg0, arg1);
	}

	@Override
	public <TResult> MapReduceIterable<TResult> mapReduce(String arg0, String arg1, Class<TResult> arg2) {
		return coll.mapReduce(arg0, arg1, arg2);
	}

	@Override
	public void renameCollection(MongoNamespace arg0) {
		if (tx.started()) {
			tx.commit();
		}
		coll.renameCollection(arg0);
	}

	@Override
	public void renameCollection(MongoNamespace arg0, RenameCollectionOptions arg1) {
		if (tx.started()) {
			tx.commit();
		}
		coll.renameCollection(arg0, arg1);
	}

	@Override
	public UpdateResult replaceOne(Bson arg0, Document arg1) {
		if (tx.started()) {
			tx.beforeUpdateOne(coll, arg0);
			return tx.getTxCollection().replaceOne(modifyQuery(arg0), arg1);
		} else {
			return coll.replaceOne(arg0, arg1);
		}
	}

	@Override
	public UpdateResult replaceOne(Bson arg0, Document arg1, UpdateOptions arg2) {
		if (tx.started()) {
			tx.beforeUpdateOne(coll, arg0);
			return tx.getTxCollection().replaceOne(modifyQuery(arg0), arg1, arg2);
		} else {
			return coll.replaceOne(arg0, arg1, arg2);
		}
	}

	@Override
	public UpdateResult updateMany(Bson arg0, Bson arg1) {
		if (tx.started()) {
			tx.beforeUpdateMany(coll, arg0);
			return tx.getTxCollection().updateMany(modifyQuery(arg0), arg1);
		} else {
			return coll.updateMany(arg0, arg1);
		}
	}

	@Override
	public UpdateResult updateMany(Bson arg0, Bson arg1, UpdateOptions arg2) {
		if (tx.started()) {
			tx.beforeUpdateMany(coll, arg0);
			return tx.getTxCollection().updateMany(modifyQuery(arg0), arg1, arg2);
		} else {
			return coll.updateMany(arg0, arg1, arg2);
		}
	}

	@Override
	public UpdateResult updateOne(Bson arg0, Bson arg1) {
		if (tx.started()) {
			tx.beforeUpdateMany(coll, arg0);
			return tx.getTxCollection().updateOne(modifyQuery(arg0), arg1);
		} else {
			return coll.updateOne(arg0, arg1);
		}
	}

	@Override
	public UpdateResult updateOne(Bson arg0, Bson arg1, UpdateOptions arg2) {
		if (tx.started()) {
			tx.beforeUpdateMany(coll, arg0);
			return tx.getTxCollection().updateOne(modifyQuery(arg0), arg1, arg2);
		} else {
			return coll.updateOne(arg0, arg1, arg2);
		}
	}

	@Override
	public MongoCollection<Document> withCodecRegistry(CodecRegistry arg0) {
		return coll.withCodecRegistry(arg0);
	}

	@Override
	public <NewDocument> MongoCollection<NewDocument> withDocumentClass(Class<NewDocument> arg0) {
		return coll.withDocumentClass(arg0);
	}

	@Override
	public MongoCollection<Document> withReadPreference(ReadPreference arg0) {
		return coll.withReadPreference(arg0);
	}

	@Override
	public MongoCollection<Document> withWriteConcern(WriteConcern arg0) {
		return coll.withWriteConcern(arg0);
	}
}
