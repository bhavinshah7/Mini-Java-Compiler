package eminijava.ast;

import eminijava.lexer.JSymbol;

public class Not extends Expression {
	public Expression expr;

	public Not(JSymbol jSymbol, Expression expr) {
		super(jSymbol);
		this.expr = expr;
	}

	public Expression getExpr() {
		return expr;
	}

	public void setExpr(Expression expr) {
		this.expr = expr;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}