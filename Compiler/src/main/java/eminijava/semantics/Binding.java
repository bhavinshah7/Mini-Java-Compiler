package eminijava.semantics;

import eminijava.ast.Type;
import eminijava.symbol.Counter;

public abstract class Binding {

	private Counter counter = Counter.getInstance();
	protected Type type;
	private int count;

	public Binding(Type t) {
		count = counter.getCount();
		type = t;
	}

	public Type type() {
		return type;
	}

	@Override
	public String toString() {
		return Integer.toString(count);
	}

}
