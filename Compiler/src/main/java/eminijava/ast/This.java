package eminijava.ast;

import eminijava.lexer.JSymbol;
import eminijava.visitor.IBranchVisitor;

public class This extends Expression {
	public This(JSymbol jSymbol) {
		super(jSymbol);
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
