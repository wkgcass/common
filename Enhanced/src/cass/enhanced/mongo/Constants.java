package cass.enhanced.mongo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Constants {
	static final int INITIAL = 0;
	static final int COMMITTING = 1;
	static final int ROLLINGBACK = -1;
	static final String TX_MAP_COLL = "j-tx-map-coll";
	static final String TX_REF = "j-transaction-ref";
	static final String TX_BKUP = "j-tx-backup";
	static final String TX_REQUIRE_ROLLBACK_ON_ERROR = "j-tx-req-rolbk-on-err";
	static final String TX_COLL_PREFIX = "j_transaction_";
	static final String TX_OPERATION = "j-transaction-op";
	static final String TX_TARGET_ID = "j-tx-target-id";
	static final String TX_OP_UPDATE = "update";
	static final String TX_OP_INSERT = "insert";
	static final String TX_OP_DELETE = "delete";
	static List<Integer> transactionId = new LinkedList<Integer>();
	/**
	 * cache of id.<br/>
	 * Map&lt;TransactionID, Map&lt;CollectionName, Set&lt;IdObject&gt;&gt;&gt;
	 */
	static Map<Integer, Map<String, Set<Object>>> txIdCache = new HashMap<Integer, Map<String, Set<Object>>>();

	private Constants() {
	}

	/**
	 * Get an id not in use
	 * 
	 * @return an ID for transaction
	 */
	static Integer findId() {
		synchronized (transactionId) {
			int i = 0;
			while (transactionId.contains(i)) {
				++i;
			}
			Integer tmp = new Integer(i);
			transactionId.add(tmp);
			return tmp;
		}
	}
	
}
