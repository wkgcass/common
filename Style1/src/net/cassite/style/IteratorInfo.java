package net.cassite.style;

public class IteratorInfo {
	public final int previousIndex;
	public final int nextIndex;
	public final boolean hasPrevious;
	public final boolean hasNext;
	public final int currentIndex;

	IteratorInfo(int previousIndex, int nextIndex, boolean hasPrevious, boolean hasNext, int currentIndex) {
		this.previousIndex = previousIndex;
		this.nextIndex = nextIndex;
		this.hasPrevious = hasPrevious;
		this.hasNext = hasNext;
		this.currentIndex = currentIndex;
	}
}