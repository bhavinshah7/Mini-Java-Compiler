package eminijava.parser;

public class ParseException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 3536839189171096308L;

	public ParseException(int line, int column, String errorText) {
		super(line + ":" + column + " " + errorText);
	}

}
