package eminijava.parser;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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
	private static final JSymbol SENTINEL = new JSymbol(Token.SENTINEL, 0, 0);
	private Deque<JSymbol> stOperator = new ArrayDeque<JSymbol>();
	private Deque<Expression> stOperand = new ArrayDeque<Expression>();

	public Parser(Lexer lex) {
		lexer = lex;
		stOperator.push(SENTINEL);
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

		case ID: {
			Identifier id = new Identifier(symbol, (String) symbol.getValue());
			eat(Token.ID);
			Statement stat = parseState1(id);
			return stat;
		}

		case SIDEF: {
			JSymbol jSymbol = symbol;
			eat(Token.SIDEF);
			eat(Token.LPAREN);
			Expression argument = parseExpression();
			eat(Token.RPAREN);
			eat(Token.SEMICOLON);
			Sidef sidef = new Sidef(jSymbol, argument);
			return sidef;
		}

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

		try {

			parseExpr();
			JSymbol top = stOperator.peek();
			while (top.token != Token.SENTINEL) {
				popOperator();
				top = stOperator.peek();
			}
			return stOperand.pop();

		} catch (ParseException pe) {
			throw pe;
		} catch (Exception e) {
			System.err.println("Parser Error " + symbol.getLine() + ":" + symbol.getColumn());
			throw e;
		}

	}

	private void parseExpr() throws ParseException {
		switch (symbol.token) {

		case INTLIT: {
			IntLiteral lit = new IntLiteral(symbol, (Integer) symbol.getValue());
			eat(Token.INTLIT);
			stOperand.push(lit);
			parseTerm1();
		}
			break;

		case STRINGLIT: {
			StringLiteral sl = new StringLiteral(symbol, (String) symbol.getValue());
			eat(Token.STRINGLIT);
			stOperand.push(sl);
			parseTerm1();
		}
			break;

		case TRUE: {
			True true1 = new True(symbol);
			eat(Token.TRUE);
			stOperand.push(true1);
			parseTerm1();
		}
			break;

		case FALSE: {
			False false1 = new False(symbol);
			eat(Token.FALSE);
			stOperand.push(false1);
			parseTerm1();
		}
			break;

		case ID: {
			IdentifierExpr id = new IdentifierExpr(symbol, (String) symbol.getValue());
			eat(Token.ID);
			stOperand.push(id);
			parseTerm1();
		}
			break;

		case THIS: {
			This this1 = new This(symbol);
			eat(Token.THIS);
			stOperand.push(this1);
			parseTerm1();
		}
			break;

		case NEW: {
			pushOperator(symbol);
			eat(Token.NEW);
			parseTerm2();
			parseTerm1();
		}
			break;

		case BANG: {
			pushOperator(symbol);
			eat(Token.BANG);
			parseExpr();

			// Not bang = new Not(bangSymbol, expr);
			parseTerm1();

		}
			break;

		case LPAREN: {
			eat(Token.LPAREN);
			stOperator.push(SENTINEL);
			Expression expr = parseExpression();
			eat(Token.RPAREN);
			stOperand.push(expr);
			stOperator.pop();
			parseTerm1();
		}
			break;

		default:
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);

		}
	}

	private void pushOperator(JSymbol current) {
		JSymbol top = stOperator.peek();
		while (getPriority(top.token) >= getPriority(current.token)) {
			popOperator();
			top = stOperator.peek();
		}
		stOperator.push(current);
	}

	private void popOperator() {

		JSymbol top = stOperator.pop();
		if (isBinary(top.token)) {
			parseBinary(top);
		} else {
			parseUnary(top);
		}

	}

	private void parseUnary(JSymbol sym) {
		switch (sym.token) {
		case BANG: {
			Expression expr = stOperand.pop();
			Not bang = new Not(sym, expr);
			stOperand.push(bang);
		}
			break;
		case NEW: {
			Expression expr = stOperand.pop();
			IdentifierExpr idExpr = (IdentifierExpr) expr;
			NewInstance instance = new NewInstance(sym, idExpr);
			stOperand.push(instance);
		}
			break;

		default: {
			System.err.println("parseUnary(): Error in parsing");
		}
			break;

		}
	}

	private void parseBinary(JSymbol sym) {

		switch (sym.token) {
		case OR: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Or or = new Or(sym, lhs, rhs);
			stOperand.push(or);
		}
			break;
		case AND: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			And and = new And(sym, lhs, rhs);
			stOperand.push(and);
		}
			break;
		case EQUALS: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Equals equals = new Equals(sym, lhs, rhs);
			stOperand.push(equals);
		}
			break;
		case LESSTHAN: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			LessThan lessThan = new LessThan(sym, lhs, rhs);
			stOperand.push(lessThan);
		}
			break;
		case PLUS: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Plus plus = new Plus(sym, lhs, rhs);
			stOperand.push(plus);
		}
			break;
		case MINUS: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Minus minus = new Minus(sym, lhs, rhs);
			stOperand.push(minus);
		}
			break;
		case TIMES: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Times times = new Times(sym, lhs, rhs);
			stOperand.push(times);
		}
			break;
		case DIV: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Division div = new Division(sym, lhs, rhs);
			stOperand.push(div);
		}
			break;
		case DOT: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			if (rhs != null && rhs instanceof Length) {
				Length length = (Length) rhs;
				length.setArray(lhs);
				stOperand.push(length);
			} else if (rhs != null && rhs instanceof CallMethod) {
				// method call
				CallMethod cm = (CallMethod) rhs;
				cm.setInstanceName(lhs);
				stOperand.push(cm);
			}

		}
			break;

		case LBRACKET: {
			Expression indexExpr = stOperand.pop();
			Expression ArrayExpr = stOperand.pop();
			IndexArray indexArray = new IndexArray(ArrayExpr.getSymbol(), ArrayExpr, indexExpr);
			stOperand.push(indexArray);
		}
			break;
		default:
			System.err.println("parseBinary(): Error in parsing");
			// throw new ParseException(symbol.getLine(), symbol.getColumn(),
			// "Invalid token :" + symbol.token);
		}

	}

	public void parseTerm1() throws ParseException {
		switch (symbol.token) {

		case AND: {
			pushOperator(symbol);
			eat(Token.AND);
			parseExpr();
			parseTerm1();

		}
			break;

		case OR: {
			pushOperator(symbol);
			eat(Token.OR);
			parseExpr();
			parseTerm1();

		}
			break;

		case EQUALS: {
			pushOperator(symbol);
			eat(Token.EQUALS);
			parseExpr();
			parseTerm1();

		}
			break;

		case LESSTHAN: {
			pushOperator(symbol);
			eat(Token.LESSTHAN);
			parseExpr();
			parseTerm1();

		}
			break;

		case PLUS: {
			pushOperator(symbol);
			eat(Token.PLUS);
			parseExpr();
			parseTerm1();

		}
			break;

		case MINUS: {
			pushOperator(symbol);
			eat(Token.MINUS);
			parseExpr();
			parseTerm1();

		}
			break;

		case TIMES: {
			pushOperator(symbol);
			eat(Token.TIMES);
			parseExpr();
			parseTerm1();

		}
			break;

		case DIV: {
			pushOperator(symbol);
			eat(Token.DIV);
			parseExpr();
			parseTerm1();

		}
			break;

		case LBRACKET: {
			pushOperator(symbol);
			eat(Token.LBRACKET);
			stOperator.push(SENTINEL);
			Expression indexExpr = parseExpression();
			eat(Token.RBRACKET);
			stOperator.pop(); // Pop SENTINEAL

			// Expression ArrayExpr = stOperand.pop();
			// IndexArray indexArray = new IndexArray(ArrayExpr.getSymbol(),
			// ArrayExpr, indexExpr);
			// stOperand.push(indexArray);
			stOperand.push(indexExpr);

			parseTerm1();

		}
			break;

		case DOT: {
			pushOperator(symbol);
			eat(Token.DOT);
			parseTerm3();
			parseTerm1();
		}
			break;

		case RPAREN:
		case SEMICOLON:
		case COMMA:
		case RBRACKET: {
			// Epsilon expected
		}
			break;

		default:
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);

		}

	}

	private boolean isBinary(Token operator) {
		switch (operator) {

		case OR:
		case AND:
		case EQUALS:
		case LESSTHAN:
		case PLUS:
		case MINUS:
		case TIMES:
		case DIV:
		case DOT:
		case LBRACKET: {
			return true;
		}

		case BANG:
		case NEW: {
			return false;
		}

		default:
			return false;

		}
	}

	private int getPriority(Token operator) {
		switch (operator) {

		case SENTINEL: {
			return -1;
		}

		case OR: {
			return 1;
		}

		case AND: {
			return 2;
		}

		case EQUALS: {
			return 3;
		}

		case LESSTHAN: {
			return 3;
		}

		case PLUS: {
			return 4;
		}

		case MINUS: {
			return 4;
		}

		case TIMES: {
			return 5;
		}

		case DIV: {
			return 5;
		}

		case BANG: {
			return 6;
		}

		case LBRACKET: {
			return 7;
		}

		case DOT: {
			return 8;
		}

		case NEW: {
			return 9;
		}

		default:
			return 0;

		}

	}

	public void parseTerm2() throws ParseException {
		switch (symbol.token) {

		case INT: {
			eat(Token.INT);
			eat(Token.LBRACKET);
			stOperator.push(SENTINEL);
			Expression arrayLength = parseExpression();
			eat(Token.RBRACKET);
			stOperator.pop(); // pop SENTINEAL
			/*
			 * Note: New keyword has highest priority, and We cannot decide
			 * between new instance and new Array using new operator and
			 * IdentifierExpr from respective stack.
			 */

			stOperator.pop(); // pop NEW
			NewArray array = new NewArray(arrayLength.getSymbol(), arrayLength);
			stOperand.push(array);
		}
			break;

		case ID: {
			IdentifierExpr idExpr = new IdentifierExpr(symbol, (String) symbol.getValue());
			eat(Token.ID);
			eat(Token.LPAREN);
			eat(Token.RPAREN);
			// stOperator.pop(); // pop NEW
			// NewInstance instance = new NewInstance(id.getSymbol(), id);
			stOperand.push(idExpr);
		}
			break;

		default:
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);

		}
	}

	public void parseTerm3() throws ParseException {
		switch (symbol.token) {

		case LENGTH: {
			Length length = new Length(symbol, null);
			eat(Token.LENGTH);
			stOperand.push(length);
		}
			break;

		case ID: {
			IdentifierExpr methodId = new IdentifierExpr(symbol, (String) symbol.getValue());
			eat(Token.ID);
			eat(Token.LPAREN);

			stOperator.push(SENTINEL);
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
			stOperator.pop(); // Pop SENTINEL
			CallMethod callMethod = new CallMethod(methodId.getSymbol(), null, methodId, exprList);
			stOperand.push(callMethod);
		}
			break;

		default:
			throw new ParseException(symbol.getLine(), symbol.getColumn(), "Invalid token :" + symbol.token);

		}
	}

	public void error(Token found, Token expected) throws ParseException {
		throw new ParseException(symbol.getLine(), symbol.getColumn(),
				"Invalid token :" + found + " Expected token :" + expected);

	}

}
