package eminijava.ast;

import eminijava.lexer.JSymbol;

public class IntType extends Type {
	public IntType(JSymbol jSymbol) {
		super(jSymbol);
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}
