package eminijava.ast;

import eminijava.lexer.JSymbol;

public class Assign extends Statement {

	public Identifier id;
	public Expression expr;

	public Assign(JSymbol jSymbol, Identifier id, Expression src) {
		super(jSymbol);
		this.id = id;
		this.expr = src;
	}

	public Identifier getId() {
		return id;
	}

	public void setId(Identifier id) {
		this.id = id;
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