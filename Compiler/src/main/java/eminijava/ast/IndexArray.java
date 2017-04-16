package eminijava.ast;

import eminijava.lexer.JSymbol;

public class IndexArray extends Expression {
	public Expression array;
	public Expression index;

	public IndexArray(JSymbol jSymbol, Expression array, Expression index) {
		super(jSymbol);
		this.array = array;
		this.index = index;
	}

	public Expression getArray() {
		return array;
	}

	public void setArray(Expression array) {
		this.array = array;
	}

	public Expression getIndex() {
		return index;
	}

	public void setIndex(Expression index) {
		this.index = index;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}
