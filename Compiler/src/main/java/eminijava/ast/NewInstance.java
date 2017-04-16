package eminijava.ast;

import eminijava.lexer.JSymbol;

public class NewInstance extends Expression {

	IdentifierExpr className;

	public NewInstance(JSymbol jSymbol, IdentifierExpr className) {
		super(jSymbol);
		this.className = className;
	}

	public IdentifierExpr getClassName() {
		return className;
	}

	public void setClassName(IdentifierExpr className) {
		this.className = className;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}
