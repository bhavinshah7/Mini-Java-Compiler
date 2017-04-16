package eminijava.ast;

import eminijava.lexer.JSymbol;

public class IntLiteral extends Expression {
	public int value;

	public IntLiteral(JSymbol jSymbol, int value) {
		super(jSymbol);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}
