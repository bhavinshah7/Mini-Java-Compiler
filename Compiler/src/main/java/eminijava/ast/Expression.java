package eminijava.ast;

import eminijava.lexer.JSymbol;

public abstract class Expression extends Tree {

	public Expression(JSymbol jsymbol) {
		super(jsymbol);
	}
};