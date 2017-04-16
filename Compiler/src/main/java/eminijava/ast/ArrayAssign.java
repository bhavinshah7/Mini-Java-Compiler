package eminijava.ast;

import eminijava.lexer.JSymbol;

public class ArrayAssign extends Statement {

	public Identifier identifier;
	public Expression e1;
	public Expression e2;

	public ArrayAssign(JSymbol jSymbol, Identifier identifier, Expression e1, Expression e2) {
		super(jSymbol);
		this.identifier = identifier;
		this.e1 = e1;
		this.e2 = e2;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public Expression getE1() {
		return e1;
	}

	public void setE1(Expression e1) {
		this.e1 = e1;
	}

	public Expression getE2() {
		return e2;
	}

	public void setE2(Expression e2) {
		this.e2 = e2;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}
