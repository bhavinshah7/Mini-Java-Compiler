package eminijava.ast;

import java.util.List;

import eminijava.lexer.JSymbol;

public class Block extends Statement {
	public List<Statement> body;

	public Block(JSymbol jSymbol, List<Statement> body) {
		super(jSymbol);
		this.body = body;
	}

	public int getStatListSize() {
		return body.size();
	}

	public Statement getStatAt(int index) {
		if (index < body.size()) {
			return body.get(index);
		}
		return null;
	}

	public void setStatAt(int index, Statement stat) {
		if (index < body.size()) {
			body.set(index, stat);
		}
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}