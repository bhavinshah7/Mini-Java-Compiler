package eminijava.ast;

import eminijava.lexer.JSymbol;

public class Or extends Expression {
	public Expression lhs;
	public Expression rhs;

	public Or(JSymbol jSymbol, Expression lhs, Expression rhs) {
		super(jSymbol);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public Expression getLhs() {
		return lhs;
	}

	public void setLhs(Expression lhs) {
		this.lhs = lhs;
	}

	public Expression getRhs() {
		return rhs;
	}

	public void setRhs(Expression rhs) {
		this.rhs = rhs;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}