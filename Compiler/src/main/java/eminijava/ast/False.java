package eminijava.ast;

import eminijava.lexer.JSymbol;

public class False extends Expression {

	public False(JSymbol jSymbol) {
		super(jSymbol);
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}
