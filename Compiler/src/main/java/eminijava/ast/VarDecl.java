package eminijava.ast;

import eminijava.lexer.JSymbol;

public class VarDecl extends Tree {

	Type type;
	Identifier id;

	public VarDecl(JSymbol jSymbol, Type type, Identifier id) {
		super(jSymbol);
		this.type = type;
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Identifier getId() {
		return id;
	}

	public void setId(Identifier id) {
		this.id = id;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}
