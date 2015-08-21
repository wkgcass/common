package net.cassite.style;

public class IteratorInfo<R> {
	public int previousIndex;
	public int nextIndex;
	public boolean hasPrevious;
	public boolean hasNext;
	public int currentIndex;
	public R lastRes;

	IteratorInfo() {
	}

	IteratorInfo<R> setValues(int previousIndex, int nextIndex, boolean hasPrevious, boolean hasNext, int currentIndex,
			R res) {
		this.previousIndex = previousIndex;
		this.nextIndex = nextIndex;
		this.hasPrevious = hasPrevious;
		this.hasNext = hasNext;
		this.currentIndex = currentIndex;
		this.lastRes = res;
		return this;
	}
}