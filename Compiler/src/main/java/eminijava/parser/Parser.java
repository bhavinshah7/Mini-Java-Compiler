package eminijava.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eminijava.ast.And;
import eminijava.ast.ArgDecl;
import eminijava.ast.ArrayAssign;
import eminijava.ast.Assign;
import eminijava.ast.Block;
import eminijava.ast.BooleanType;
import eminijava.ast.CallMethod;
import eminijava.ast.ClassDecl;
import eminijava.ast.ClassDeclExtends;
import eminijava.ast.ClassDeclSimple;
import eminijava.ast.Division;
import eminijava.ast.Equals;
import eminijava.ast.Expression;
import eminijava.ast.False;
import eminijava.ast.Identifier;
import eminijava.ast.IdentifierExpr;
import eminijava.ast.IdentifierType;
import eminijava.ast.IfThenElse;
import eminijava.ast.IndexArray;
import eminijava.ast.IntArrayType;
import eminijava.ast.IntLiteral;
import eminijava.ast.IntType;
import eminijava.ast.Length;
import eminijava.ast.LessThan;
import eminijava.ast.MainClass;
import eminijava.ast.MethodDecl;
import eminijava.ast.Minus;
import eminijava.ast.NewArray;
import eminijava.ast.NewInstance;
import eminijava.ast.Not;
import eminijava.ast.Or;
import eminijava.ast.Plus;
import eminijava.ast.Print;
import eminijava.ast.Program;
import eminijava.ast.Sidef;
import eminijava.ast.Skip;
import eminijava.ast.Statement;
import eminijava.ast.StringLiteral;
import eminijava.ast.StringType;
import eminijava.ast.This;
import eminijava.ast.Times;
import eminijava.ast.Tree;
import eminijava.ast.True;
import eminijava.ast.Type;
import eminijava.ast.VarDecl;
import eminijava.ast.While;
import eminijava.lexer.JSymbol;
import eminijava.lexer.Lexer;
import eminijava.lexer.Token;

public class Parser {

	private Lexer lexer;
	// private Token token;
	private JSymbol symbol;

	public Parser(Lexer lex) {
		lexer = lex;
	}

	public void advance() {
		try {
			symbol = lexer.yylex();
			// token = symbol.token;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void eat(Token tok) throws ParseException {
		if (tok == symbol.token) {
			advance();
		} else {
			error(symbol.token, tok);

		}
	}

	public Tree parse() throws ParseException {
		Program prog = null;
		try {

			MainClass mClass = null;
			List<ClassDecl> klassList = new ArrayList<>();

			do {

				symbol = lexer.yylex();
				// token = symbol.token;

				switch (symbol.token) {

				case CLASS: {

					mClass = parseMainClass();
					while (symbol.token != Token.EOF) {
						ClassDecl klass = parseClassDecl();
						klassList.add(klass);
					}

				}
					break;
				default:
					throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);

				}
			} while (symbol.token != Token.EOF);
			// TODO : MainClass compulsory
			prog = new Program(mClass.getSymbol(), mClass, klassList);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return prog;
	}

	public MainClass parseMainClass() throws ParseException {
		eat(Token.CLASS);

		IdentifierExpr className = null;
		if (symbol.token == Token.ID) {
			className = new IdentifierExpr(symbol, (String) symbol.getValue());
		}
		eat(Token.ID);

		eat(Token.LBRACE);
		eat(Token.PUBLIC);
		eat(Token.STATIC);
		eat(Token.VOID);
		eat(Token.MAIN);
		eat(Token.LPAREN);
		eat(Token.STRING);
		eat(Token.LBRACKET);
		eat(Token.RBRACKET);

		IdentifierExpr stringArrayId = null;
		if (symbol.token == Token.ID) {
			stringArrayId = new IdentifierExpr(symbol, (String) symbol.getValue());
		}
		eat(Token.ID);

		eat(Token.RPAREN);
		eat(Token.LBRACE);
		Statement stat = parseStatement();
		eat(Token.RBRACE);
		eat(Token.RBRACE);

		MainClass mClass = new MainClass(className.getSymbol(), className, stringArrayId, stat);
		return mClass;
	}

	public ClassDecl parseClassDecl() throws ParseException {
		/*
		 * class Identifier ( extends Identifier )? { ( VarDeclaration )* (
		 * MethodDeclaration )* }
		 */

		eat(Token.CLASS);
		IdentifierExpr className = null;
		if (symbol.token == Token.ID) {
			className = new IdentifierExpr(symbol, (String) symbol.getValue());
		}
		eat(Token.ID);

		IdentifierExpr parent = null;
		if (symbol.token == Token.EXTENDS) {
			eat(Token.EXTENDS);
			if (symbol.token == Token.ID) {
				parent = new IdentifierExpr(symbol, (String) symbol.getValue());
			}
			eat(Token.ID);
		}
		eat(Token.LBRACE);

		List<VarDecl> varList = new ArrayList<>();
		while (symbol.token != Token.RBRACE && symbol.token != Token.PUBLIC) {
			varList.add(parseVariable());
		}

		List<MethodDecl> methodList = new ArrayList<>();
		while (symbol.token != Token.RBRACE && symbol.token == Token.PUBLIC) {
			methodList.add(parseMethodDecl());
		}

		eat(Token.RBRACE);

		if (parent == null) {
			ClassDecl klass = new ClassDeclSimple(className.getSymbol(), className, varList, methodList);
			return klass;
		} else {
			ClassDecl klass = new ClassDeclExtends(className.getSymbol(), className, parent, varList, methodList);
			return klass;
		}
	}

	public MethodDecl parseMethodDecl() throws ParseException {

		/*
		 * public Type Identifier ( ( Type Identifier ( , Type Identifier )* )?
		 * ) { ( VarDeclaration )* ( Statement )* return Expression ; }
		 */

		eat(Token.PUBLIC);
		Type returnType = parseType();
		IdentifierExpr methodName = null;
		if (symbol.token == Token.ID) {
			methodName = new IdentifierExpr(symbol, (String) symbol.getValue());
		}
		eat(Token.ID);
		eat(Token.LPAREN);
		List<ArgDecl> argList = new ArrayList<>();
		if (symbol.token != Token.RPAREN) {
			argList.add(parseArgument());

			while (symbol.token == Token.COMMA) {
				eat(Token.COMMA);
				argList.add(parseArgument());
			}
		}

		eat(Token.RPAREN);
		eat(Token.LBRACE);

		List<VarDecl> varList = new ArrayList<>();
		List<Statement> statList = new ArrayList<>();

		while (symbol.token == Token.INT || symbol.token == Token.BOOLEAN || symbol.token == Token.STRING) {
			VarDecl var = parseVariable();
			varList.add(var);
		}

		// TODO: When Token is ID, Can't decide between variable and statement
		while (symbol.token == Token.ID) {
			IdentifierType idType = new IdentifierType(symbol, (String) symbol.getValue());
			Identifier id1 = new Identifier(symbol, (String) symbol.getValue());
			eat(Token.ID);
			if (symbol.token == Token.ID) { // Still Parsing Variable
				Identifier id2 = new Identifier(symbol, (String) symbol.getValue());
				eat(Token.ID);
				eat(Token.SEMICOLON);
				varList.add(new VarDecl(id2.getSymbol(), idType, id2));

				while (symbol.token == Token.INT || symbol.token == Token.BOOLEAN || symbol.token == Token.STRING) {
					varList.add(parseVariable());
				}

			} else { // Statement Begins
				Statement stat = parseState1(id1);
				statList.add(stat);
				while (symbol.token != Token.RETURN) {
					// TODO: Exit without looping when error occurs
					statList.add(parseStatement());
				}
			}
		}

		while (symbol.token != Token.RETURN) {
			statList.add(parseStatement());
		}
		eat(Token.RETURN);
		Expression returnExpr = parseExpression();
		eat(Token.SEMICOLON);
		eat(Token.RBRACE);

		MethodDecl method = new MethodDecl(methodName.getSymbol(), returnType, methodName, argList, varList, statList,
				returnExpr);
		return method;
	}

	public ArgDecl parseArgument() throws ParseException {
		Type argType = parseType();
		Identifier argId = null;
		if (symbol.token == Token.ID) {
			argId = new Identifier(symbol, (String) symbol.getValue());
		}
		eat(Token.ID);
		ArgDecl var = new ArgDecl(argId.getSymbol(), argType, argId);
		return var;
	}

	public VarDecl parseVariable() throws ParseException {
		Type type = parseType();
		Identifier id = null;
		if (symbol.token == Token.ID) {
			id = new Identifier(symbol, (String) symbol.getValue());

		}

		eat(Token.ID);
		eat(Token.SEMICOLON);
		VarDecl var = new VarDecl(id.getSymbol(), type, id); // TODO:
																// Check
		return var;
	}

	public Type parseType() throws ParseException {
		switch (symbol.token) {

		case INT: {
			JSymbol jSymbol = symbol;
			eat(Token.INT);
			Type type = parseType1();
			if (type == null) {
				return new IntType(jSymbol);
			}
			return type;
		}
		case BOOLEAN: {
			BooleanType bt = new BooleanType(symbol);
			eat(Token.BOOLEAN);
			return bt;
		}
		case STRING: {
			StringType st = new StringType(symbol);
			eat(Token.STRING);
			return st;
		}

		case ID: {
			IdentifierType id = new IdentifierType(symbol, (String) symbol.getValue());
			eat(Token.ID);
			return id;
		}

		default:
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);
		}
	}

	public Type parseType1() throws ParseException {
		switch (symbol.token) {

		case LBRACKET: {
			JSymbol jSymbol = symbol;
			eat(Token.LBRACKET);
			eat(Token.RBRACKET);
			/*
			 * As we know only integer array is allowed, else we need to take
			 * the preceding type as argument input to this method.
			 */
			IntArrayType type = new IntArrayType(jSymbol);
			return type;
		}

		case ID: {
			return null;
			// Case Epsilon
		}
		// break;

		default:
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);
		}
	}

	public Statement parseStatement() throws ParseException {

		switch (symbol.token) {

		case LBRACE: {
			JSymbol jSymbol = symbol;
			eat(Token.LBRACE);
			List<Statement> body = new ArrayList<>();
			while (symbol.token != Token.RBRACE) {
				Statement stat = parseStatement();
				body.add(stat);
			}
			eat(Token.RBRACE);
			Block block = new Block(jSymbol, body);
			return block;
		}
		// break;

		case IF: {
			JSymbol jSymbol = symbol;
			eat(Token.IF);
			eat(Token.LPAREN);
			Expression expr = parseExpression();
			eat(Token.RPAREN);
			Statement then = parseStatement();
			Statement elze = new Skip(then.getSymbol()); // TODO: Check
			if (symbol.token == Token.ELSE) {
				eat(Token.ELSE);
				elze = parseStatement();
			}
			IfThenElse result = new IfThenElse(jSymbol, expr, then, elze);
			return result;
		}
		// break;

		case WHILE: {
			JSymbol jSymbol = symbol;
			eat(Token.WHILE);
			eat(Token.LPAREN);
			Expression expr = parseExpression();
			eat(Token.RPAREN);
			Statement body = parseStatement();
			While result = new While(jSymbol, expr, body);
			return result;
		}
		// break;

		case PRINTLN: {
			JSymbol jSymbol = symbol;
			eat(Token.PRINTLN);
			eat(Token.LPAREN);
			Expression expr = parseExpression();
			eat(Token.RPAREN);
			eat(Token.SEMICOLON);

			Print result = new Print(jSymbol, expr);
			return result;
		}
		// break;

		case ID: {
			Identifier id = new Identifier(symbol, (String) symbol.getValue());
			eat(Token.ID);
			Statement stat = parseState1(id);
			return stat;
		}
		// break;

		case SIDEF: {
			JSymbol jSymbol = symbol;
			eat(Token.SIDEF);
			eat(Token.LPAREN);
			Expression argument = parseExpression();
			eat(Token.RPAREN);
			Sidef sidef = new Sidef(jSymbol, argument);
			return sidef;
		}
		// break;

		default:
			// System.err.println("parseStatement(): " + error());
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);
		}

	}

	public Statement parseState1(Identifier id) throws ParseException {
		switch (symbol.token) {

		case EQSIGN: {
			JSymbol jSymbol = symbol;
			eat(Token.EQSIGN);
			Expression expr = parseExpression();
			eat(Token.SEMICOLON);
			Assign assign = new Assign(jSymbol, id, expr);
			return assign;
		}
		// break;

		case LBRACKET: {
			eat(Token.LBRACKET);
			Expression expr1 = parseExpression();
			eat(Token.RBRACKET);
			eat(Token.EQSIGN);
			Expression expr2 = parseExpression();
			eat(Token.SEMICOLON);
			ArrayAssign assign = new ArrayAssign(id.getSymbol(), id, expr1, expr2);
			return assign;
		}
		// break;

		default:
			// System.err.println("parseState1(): " + error());
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);

		}
	}

	public Expression parseExpression() throws ParseException {
		switch (symbol.token) {

		case INTLIT: {
			IntLiteral lit = new IntLiteral(symbol, (Integer) symbol.getValue());
			eat(Token.INTLIT);
			Expression expr = parseTerm1(lit);
			return expr;

		}
		// break;

		case STRINGLIT: {
			StringLiteral sl = new StringLiteral(symbol, (String) symbol.getValue());
			eat(Token.STRINGLIT);
			Expression expr = parseTerm1(sl);
			return expr;
		}
		// break;

		case TRUE: {
			True true1 = new True(symbol);
			eat(Token.TRUE);
			Expression expr = parseTerm1(true1);
			return expr;
		}
		// break;

		case FALSE: {
			False false1 = new False(symbol);
			eat(Token.FALSE);
			Expression expr = parseTerm1(false1);
			return expr;
		}
		// break;

		case ID: {
			IdentifierExpr id = new IdentifierExpr(symbol, (String) symbol.getValue());
			eat(Token.ID);
			Expression expr = parseTerm1(id);
			return expr;
		}
		// break;

		case THIS: {
			This this1 = new This(symbol);
			eat(Token.THIS);
			Expression expr = parseTerm1(this1);
			return expr;
		}
		// break;

		case NEW: {
			eat(Token.NEW);
			Expression expr = parseTerm2();
			Expression result = parseTerm1(expr);
			return result;

		}
		// break;

		case BANG: {
			JSymbol bangSymbol = symbol;
			eat(Token.BANG);
			Expression expr = parseExpression();
			/*
			 * Bang has the highest precedence! hence first apply bang to
			 * rightmost expression.
			 */
			Not bang = new Not(bangSymbol, expr);
			Expression result = parseTerm1(bang);
			return result;

		}
		// break;

		case LPAREN: {
			eat(Token.LPAREN);
			Expression expr = parseExpression();
			eat(Token.RPAREN);
			/**
			 * Expression within parenthesis is given preference
			 */
			Expression result = parseTerm1(expr);
			return result;
		}
		// break;

		default:
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);

		}
	}

	public Expression parseTerm1(Expression lhs) throws ParseException {
		switch (symbol.token) {

		case AND: {
			JSymbol andSymbol = symbol;
			eat(Token.AND);
			Expression expr = parseExpression();
			Expression rhs = parseTerm1(expr);
			And and = new And(andSymbol, lhs, rhs);
			return and;
		}
		// break;

		case OR: {
			JSymbol orSymbol = symbol;
			eat(Token.OR);

			Expression expr = parseExpression();
			Expression rhs = parseTerm1(expr);
			Or or = new Or(orSymbol, lhs, rhs);
			return or;
		}
		// break;

		case EQUALS: {
			JSymbol eqSymbol = symbol;
			eat(Token.EQUALS);
			Expression expr = parseExpression();
			Expression rhs = parseTerm1(expr);
			Equals equals = new Equals(eqSymbol, lhs, rhs);
			return equals;
		}
		// break;

		case LESSTHAN: {
			JSymbol ltSymbol = symbol;
			eat(Token.LESSTHAN);
			Expression expr = parseExpression();
			Expression rhs = parseTerm1(expr);
			LessThan lessThan = new LessThan(ltSymbol, lhs, rhs);
			return lessThan;
		}
		// break;

		case PLUS: {
			JSymbol plusSymbol = symbol;
			eat(Token.PLUS);
			Expression expr = parseExpression();
			Expression rhs = parseTerm1(expr);
			Plus plus = new Plus(plusSymbol, lhs, rhs);
			return plus;
		}
		// break;

		case MINUS: {
			JSymbol minusSymbol = symbol;
			eat(Token.MINUS);
			Expression expr = parseExpression();
			Expression rhs = parseTerm1(expr);
			Minus minus = new Minus(minusSymbol, lhs, rhs);
			return minus;
		}
		// break;

		case TIMES: {
			JSymbol timesSymbol = symbol;
			eat(Token.TIMES);
			Expression expr = parseExpression();
			Expression rhs = parseTerm1(expr);
			Times times = new Times(timesSymbol, lhs, rhs);
			return times;
		}
		// break;

		case DIV: {
			JSymbol divSymbol = symbol;
			eat(Token.DIV);
			Expression expr = parseExpression();
			Expression rhs = parseTerm1(expr);
			Division div = new Division(divSymbol, lhs, rhs);
			return div;
		}
		// break;

		case LBRACKET: {
			JSymbol lbSymbol = symbol;
			eat(Token.LBRACKET);
			Expression index = parseExpression();
			eat(Token.RBRACKET);

			IndexArray indexArray = new IndexArray(lbSymbol, lhs, index);
			Expression result = parseTerm1(indexArray);

			return result;
		}
		// break;

		case DOT: {
			eat(Token.DOT);
			Expression expr = parseTerm3(lhs);
			// TODO: Check the precedence here
			Expression result = parseTerm1(expr);
			return result;
		}
		// break;

		case RPAREN:
		case SEMICOLON:
		case COMMA:
		case RBRACKET: {
			return lhs;
			// TODO: Epsilon expected, Check 3:41 & 3.42 Quicksort
		}
		// break;

		default:
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);

		}

	}

	public Expression parseTerm2() throws ParseException {
		switch (symbol.token) {

		case INT: {
			eat(Token.INT);
			eat(Token.LBRACKET);
			Expression arrayLength = parseExpression();
			eat(Token.RBRACKET);
			NewArray array = new NewArray(arrayLength.getSymbol(), arrayLength);
			return array;
		}
		// break;

		case ID: {
			IdentifierExpr id = new IdentifierExpr(symbol, (String) symbol.getValue());
			eat(Token.ID);
			eat(Token.LPAREN);
			eat(Token.RPAREN);
			NewInstance instance = new NewInstance(id.getSymbol(), id);
			return instance;
		}
		// break;

		default:
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);

		}
	}

	public Expression parseTerm3(Expression lhs) throws ParseException {
		switch (symbol.token) {

		case LENGTH: {
			Length length = new Length(symbol, lhs);
			eat(Token.LENGTH);
			return length;
		}
		// break;

		case ID: {
			IdentifierExpr methodId = new IdentifierExpr(symbol, (String) symbol.getValue());
			eat(Token.ID);
			eat(Token.LPAREN);
			List<Expression> exprList = new ArrayList<Expression>();
			if (symbol.token != Token.RPAREN) {
				Expression exprArg = parseExpression();
				exprList.add(exprArg);
				while (symbol.token == Token.COMMA) {
					eat(Token.COMMA);
					exprArg = parseExpression();
					exprList.add(exprArg);
				}
			}
			eat(Token.RPAREN);
			CallMethod callMethod = new CallMethod(methodId.getSymbol(), lhs, methodId, exprList);
			return callMethod;
		}
		// break;

		default:
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);

		}
	}

	public void error(Token found, Token expected) throws ParseException {
		throw new ParseException(symbol.getLine(), symbol.getColumn(),
				"Invalid token :" + found + " Expected token :" + expected);

	}

}
