package eminijava.ast;

import eminijava.lexer.JSymbol;

public class IntArrayType extends Type {

	public IntArrayType(JSymbol jSymbol) {
		super(jSymbol);
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}
