package eminijava.ast;

import eminijava.lexer.JSymbol;
import eminijava.semantics.Binding;

public class Identifier extends Tree {

	public String varID;
	public Binding b;

	public Identifier(JSymbol jSymbol, String varID) {
		super(jSymbol);
		this.varID = varID;
	}

	public String getVarID() {
		return varID;
	}

	public void setVarID(String varID) {
		this.varID = varID;
	}

	public Binding getB() {
		return b;
	}

	public void setB(Binding b) {
		this.b = b;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
}
