package eminijava.ast;

import eminijava.lexer.JSymbol;

public class BooleanType extends Type {
	public BooleanType(JSymbol jSymbol) {
		super(jSymbol);
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

	@Override
	public String toString() {
		return "boolean";
	}
}
