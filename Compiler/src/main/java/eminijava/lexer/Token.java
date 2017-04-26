package eminijava.lexer;

public enum Token {
	ID(98), RBRACKET(11), COMMA(15), RBRACE(17), RPAREN(20), LBRACKET(10), LESSTHAN(69), AND(79), OR(80), LBRACE(
			16), LPAREN(19), INTLIT(93), BANG(63), EQSIGN(18), TRUE(95), FALSE(201), CLASS(34), PLUS(60), WHILE(
					48), EXTENDS(35), BOOLEAN(2), VOID(37), DIV(64), PUBLIC(24), RETURN(52), TIMES(14), ELSE(43), DOT(
							12), INT(5), STRINGLIT(97), EQUALS(74), EOF(0), SEMICOLON(13), THIS(39), MINUS(61), IF(
									42), COLON(21), NEW(57), STATIC(27), MAIN(202), STRING(
											203), PRINTLN(204), LENGTH(205), SIDEF(206), BAD(207), SENTINEL(-1);

	private int sym;

	Token(int sym) {
		this.sym = sym;
	}

	public int getSym() {
		return sym;
	}
}
