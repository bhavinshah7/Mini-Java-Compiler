package eminijava.ast;

import eminijava.lexer.JSymbol;

public class Length extends Expression {

	public Expression array;

	public Length(JSymbol jSymbol, Expression array) {
		super(jSymbol);
		this.array = array;
	}

	public Expression getArray() {
		return array;
	}

	public void setArray(Expression array) {
		this.array = array;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}
