package eminijava.ast;

import eminijava.lexer.JSymbol;

public class While extends Statement {
	public Expression expr;
	public Statement body;

	public While(JSymbol jSymbol, Expression expr, Statement body) {
		super(jSymbol);
		this.expr = expr;
		this.body = body;
	}

	public Expression getExpr() {
		return expr;
	}

	public void setExpr(Expression expr) {
		this.expr = expr;
	}

	public Statement getBody() {
		return body;
	}

	public void setBody(Statement body) {
		this.body = body;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}