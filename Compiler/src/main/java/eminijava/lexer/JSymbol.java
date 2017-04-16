package eminijava.lexer;

public class JSymbol {
	private int line;
	private int column;
	private Object value;
	public Token token;

	public JSymbol(Token type, int line, int column) {
		this(type, line, column, null);
	}

	public JSymbol(Token type, int line, int column, Object value) {
		this.token = type;
		this.line = line;
		this.column = column;
		this.value = value;

	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return line + ":" + column + " " + token + (value == null ? "()" : ("(" + value + ")"));
	}
}
