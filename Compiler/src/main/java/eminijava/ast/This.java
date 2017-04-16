package eminijava.ast;

import eminijava.lexer.JSymbol;

public class This extends Expression {
	public This(JSymbol jSymbol) {
		super(jSymbol);
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}
