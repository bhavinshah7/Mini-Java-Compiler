package eminijava.ast;

import eminijava.lexer.JSymbol;

public class True extends Expression {

	public True(JSymbol jSymbol) {
		super(jSymbol);
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}
