package eminijava.ast;

import eminijava.lexer.JSymbol;

public class StringLiteral extends Expression {
	public String value;

	public StringLiteral(JSymbol jSymbol, String value) {
		super(jSymbol);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}
