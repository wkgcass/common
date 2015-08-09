package net.cassite.style;

import net.cassite.style.style.function;

public class Async<R> {
	private Container<R> container;

	private static class Container<R> {
		R ret = null;

		Container() {
		}
	}

	private static class AsyncRunnable<R> implements Runnable {
		private final Container<R> container;
		private final function<R> func;
		private final Object[] args;

		AsyncRunnable(Container<R> container, function<R> func, Object... args) {
			this.container = container;
			this.func = func;
			this.args = args;
		}

		@Override
		public void run() {
			synchronized (container) {
				container.ret = func.apply(args);
			}
		}
	}

	Async(function<R> func, Object... args) {
		this.container = new Container<R>();
		Thread t = new Thread(new AsyncRunnable<R>(container, func, args));
		t.start();
	}

	public R await() {
		synchronized (container) {
			return container.ret;
		}
	}
}