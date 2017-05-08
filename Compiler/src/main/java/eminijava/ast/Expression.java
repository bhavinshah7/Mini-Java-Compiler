package eminijava.ast;

import eminijava.lexer.JSymbol;

public abstract class Expression extends Tree {

	public Type type;

	public Expression(JSymbol jsymbol) {
		super(jsymbol);
	}

	public Type type() {
		return type;
	}

	public void setType(Type t) {
		type = t;
	}
};