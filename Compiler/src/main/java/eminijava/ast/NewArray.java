package eminijava.ast;

import eminijava.lexer.JSymbol;
import eminijava.visitor.IBranchVisitor;

public class NewArray extends Expression {

	Expression arrayLength;

	public NewArray(JSymbol jSymbol, Expression arrayLength) {
		super(jSymbol);
		this.arrayLength = arrayLength;
	}

	public Expression getArrayLength() {
		return arrayLength;
	}

	public void setArrayLength(Expression arrayLength) {
		this.arrayLength = arrayLength;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

	@Override
	public <R> R accept(IBranchVisitor<R> v, String nTrue, String nFalse) {
		return v.visit(this, nTrue, nFalse);
	}

}
