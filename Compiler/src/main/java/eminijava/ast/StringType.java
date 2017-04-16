package eminijava.ast;

import eminijava.lexer.JSymbol;

public class StringType extends Type {
	public StringType(JSymbol jSymbol) {
		super(jSymbol);
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}
