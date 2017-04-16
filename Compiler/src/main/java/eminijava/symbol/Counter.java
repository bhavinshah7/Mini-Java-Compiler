package eminijava.symbol;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {

	private static Counter instance;
	private static AtomicInteger count = new AtomicInteger(0);

	private Counter() {

	}

	public static Counter getInstance() {
		if (instance == null) {
			synchronized (Counter.class) {
				if (instance == null) {
					instance = new Counter();
					return instance;
				}
			}
		}
		return instance;
	}

	public int getCount() {
		return count.getAndIncrement();
	}

}
