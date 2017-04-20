package eminijava.ast;

import eminijava.lexer.JSymbol;

public class ArgDecl extends VarDecl {

	// Type type;
	// Identifier id;

	public ArgDecl(JSymbol jSymbol, Type type, Identifier id) {
		super(jSymbol, type, id);
		// this.type = type;
		// this.id = id;
	}

	/*
	 * @Override public Type getType() { return type; }
	 *
	 * @Override public void setType(Type type) { this.type = type; }
	 *
	 * @Override public Identifier getId() { return id; }
	 *
	 * @Override public void setId(Identifier id) { this.id = id; }
	 */

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}
