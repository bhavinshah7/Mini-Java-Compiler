package eminijava.ast;

import eminijava.lexer.JSymbol;

public class Print extends Statement {
	/*
	 * public String msg; public String varID;
	 *
	 * public Print(String msg, String varID) { this.msg = msg; this.varID =
	 * varID; }
	 */
	public Expression expr;

	public Print(JSymbol jSymbol, Expression expr) {
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