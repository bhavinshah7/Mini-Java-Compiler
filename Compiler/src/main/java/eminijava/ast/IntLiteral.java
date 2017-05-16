package eminijava.ast;

import eminijava.lexer.JSymbol;
import eminijava.visitor.IBranchVisitor;

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

	@Override
	public <R> R accept(IBranchVisitor<R> v, String nTrue, String nFalse) {
		return v.visit(this, nTrue, nFalse);
	}
}
