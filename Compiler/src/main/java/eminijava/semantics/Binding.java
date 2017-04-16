package eminijava.semantics;

import eminijava.symbol.Counter;

public abstract class Binding {

	private Counter counter = Counter.getInstance();
	private int count;

	public Binding() {
		count = counter.getCount();
	}

	@Override
	public String toString() {
		return Integer.toString(count);
	}

}
