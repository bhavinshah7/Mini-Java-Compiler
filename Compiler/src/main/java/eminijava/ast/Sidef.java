package eminijava.ast;

import eminijava.lexer.JSymbol;

public class Sidef extends Statement {

	Expression argument;

	public Sidef(JSymbol jSymbol, Expression argument) {
		super(jSymbol);
		this.argument = argument;
	}

	public Expression getArgument() {
		return argument;
	}

	public void setArgument(Expression argument) {
		this.argument = argument;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}
