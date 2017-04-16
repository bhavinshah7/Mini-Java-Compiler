package eminijava.ast;

import eminijava.lexer.JSymbol;

/* Skip represents empty statement.
 * It is useful e.g. as "else" block of and if-then-else statement
 */
public class Skip extends Statement {
	public Skip(JSymbol jSymbol) {
		super(jSymbol);
	};

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}
};