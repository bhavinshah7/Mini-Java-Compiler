package eminijava.symbol;

import eminijava.ast.Type;
import eminijava.semantics.Binding;

public class Variable extends Binding {

	String id;
	int lvIndex = -1;

	public Variable(String id, Type type) {
		super(type);
		this.id = id;
		// this.type = type;
	}

	public String id() {
		return id;
	}

	@Override
	public Type type() {
		return type;
	}

	public int getLvIndex() {
		return lvIndex;
	}

	public void setLvIndex(int lvIndex) {
		this.lvIndex = lvIndex;
	}

}
