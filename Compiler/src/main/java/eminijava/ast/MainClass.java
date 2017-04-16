package eminijava.ast;

import eminijava.lexer.JSymbol;

public class MainClass extends Tree {

	IdentifierExpr className;
	IdentifierExpr stringArrayId;
	Statement stat;

	public MainClass(JSymbol jSymbol, IdentifierExpr className, IdentifierExpr stringArrayId, Statement stat) {
		super(jSymbol);
		this.className = className;
		this.stringArrayId = stringArrayId;
		this.stat = stat;
	}

	public IdentifierExpr getClassName() {
		return className;
	}

	public void setClassName(IdentifierExpr className) {
		this.className = className;
	}

	public IdentifierExpr getStringArrayId() {
		return stringArrayId;
	}

	public void setStringArrayId(IdentifierExpr stringArrayId) {
		this.stringArrayId = stringArrayId;
	}

	public Statement getStat() {
		return stat;
	}

	public void setStat(Statement stat) {
		this.stat = stat;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}
