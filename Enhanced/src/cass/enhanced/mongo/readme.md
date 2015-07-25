Enhanced Mongo
=========
This subproject give Mongodb capability of transaction between collections.

How to Use ?
=========
	TxClient client=new TxClient();
	TxDatabase db=client.getDatabase("db_name");
	Transaction tx=db.getTransaction();
	tx.start(); // begin the transaction
	MongoCollection c1=db.getCollection("c1_name");
	MongoCollection c2=db.getCollection("c2_name");
	// do some insert/update/delete
	tx.commit(); // or tx.rollback();

Way of doing this
=========
Actually the method is quite simple.

First, let's see what 2pc is.

Here's a [link](http://docs.mongodb.org/manual/tutorial/perform-two-phase-commits/) to mongodb official documentation with the way of doing 'transaction' with 2PC.

Second, let's see how this library work.

Let's create two documents in **different** collections (eg. people and accounts):

	{
		"_id":111,
		"name":"cass",
		"money":10
	}
	{
		"_id":222,
		"name":"cass's bank account",
		"money":15
	}

CRUD
=====
Now i want to save all my money to my bank account.

$inc -10 where \_id=111, and $inc +10 where \_id=111;

This library would create a collection, and write in documents like these:

	{
		"_id":asfljf3,
		"name":"cass",
		"money":10,
		"j-transaction-ref":111,
		"j-tx-map-coll": "people",
		"j-tx-bkup":{
			"name":"cass",
			"money":10
		}
	}
	{
		"_id":fwlejlf,
		"name":"cass's bank account",
		"money":15,
		"j-transaction-ref":222,
		"j-tx-map-coll":"accounts",
		"j-tx-backup":{
			"name":"cass's bank account",
			"money":15
		}
	}

The documents which were modified are cloned in the collection for transaction. And put("j-transaction-ref", remove("\_id")), record its original collection name, and create back up.

To reduce query amount, the library create a Map to cache information such as reference ids and collection names.

In case of *insert*, the library insert documents to transaction collection instead of the target collection you choose.

In case of *delete*, the Map would record the document you deleted.(Some documents in transaction collection may be changed to delete state).

Commit & Rollback
=======
If commit was called,
 
 * in case of *insert*, insert the document to target collection.
 * in case of *update*, replace the target document.
 * in case of *delete*, remove document in target collection with ref-id.

Then mark the document to state which requires roll back when doing rolling back.

If **rollback** was called,
simply drop the transaction collection and clear recording Cache, because no actual changes were committed to original documents.
If state of document in transaction collection require roll back, 
 * in case of *insert*, delete inserted document.
 * in case of *update*, replace with back up.
 * in case of *delete*, insert back up doc.