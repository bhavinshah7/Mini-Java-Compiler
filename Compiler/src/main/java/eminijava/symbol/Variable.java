package eminijava.symbol;

import eminijava.ast.Type;
import eminijava.semantics.Binding;

public class Variable extends Binding {

	String id;
	Type type;

	public Variable(String id, Type type) {
		this.id = id;
		this.type = type;
	}

	public String id() {
		return id;
	}

	public Type type() {
		return type;
	}

}
