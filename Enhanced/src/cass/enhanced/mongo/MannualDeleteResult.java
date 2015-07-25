package cass.enhanced.mongo;

import com.mongodb.client.result.DeleteResult;

class MannualDeleteResult extends DeleteResult {
	private long count;
	private boolean acknowledged;

	public MannualDeleteResult(long count, boolean acknowledged) {
		this.count = count;
		this.acknowledged = acknowledged;
	}

	@Override
	public long getDeletedCount() {
		return count;
	}

	@Override
	public boolean wasAcknowledged() {
		return acknowledged;
	}
}