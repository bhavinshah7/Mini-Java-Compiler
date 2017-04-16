package eminijava.ast;

import eminijava.lexer.JSymbol;

public abstract class Tree {

	private JSymbol symbol;

	public Tree(JSymbol jsymbol) {
		symbol = jsymbol;
	}

	public JSymbol getSymbol() {
		return symbol;
	}

	public void setSymbol(JSymbol symbol) {
		this.symbol = symbol;
	}

	public abstract <R> R accept(Visitor<R> v);
}