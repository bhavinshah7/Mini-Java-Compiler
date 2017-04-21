package eminijava.visitor;

import java.util.HashSet;
import java.util.Set;

import eminijava.ast.And;
import eminijava.ast.ArgDecl;
import eminijava.ast.ArrayAssign;
import eminijava.ast.Assign;
import eminijava.ast.Block;
import eminijava.ast.BooleanType;
import eminijava.ast.CallMethod;
import eminijava.ast.ClassDeclExtends;
import eminijava.ast.ClassDeclSimple;
import eminijava.ast.Division;
import eminijava.ast.Equals;
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
import eminijava.ast.True;
import eminijava.ast.Type;
import eminijava.ast.VarDecl;
import eminijava.ast.Visitor;
import eminijava.ast.While;
import eminijava.lexer.JSymbol;
import eminijava.semantics.Binding;
import eminijava.semantics.SemanticErrors;
import eminijava.symbol.Klass;
import eminijava.symbol.Method;
import eminijava.symbol.SymbolTable;
import eminijava.symbol.Variable;

public class TypeAnalyser implements Visitor<Type> {

	SymbolTable st;
	private Klass currClass;
	private Method currMethod;
	private Set<String> hsKlass = new HashSet<>();
	private Set<String> hsMethod = new HashSet<>();

	public TypeAnalyser(SymbolTable table) {
		this.st = table;
	}

	@Override
	public Type visit(Print n) {

		Type texp = n.expr.accept(this);

		if (!((texp instanceof IntType) || (texp instanceof BooleanType) || (texp instanceof StringType))) {
			JSymbol sym = n.expr.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Type " + texp + " invalid in println statement");
		}
		return null;
	}

	@Override
	public Type visit(Assign n) {

		Type rhs = n.expr.accept(this);
		Type lhs = n.id.accept(this);

		if (!st.compareTypes(lhs, rhs)) {
			JSymbol sym = n.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Type mismatch in assignment");
		}

		return null;
	}

	@Override
	public Type visit(Skip n) {
		return null;
	}

	@Override
	public Type visit(Block n) {
		for (int i = 0; i < n.getStatListSize(); i++) {
			Statement st = n.getStatAt(i);
			st.accept(this);
		}
		return null;
	}

	@Override
	public Type visit(IfThenElse n) {

		Type texp = n.expr.accept(this);
		if (!(texp instanceof BooleanType)) {
			JSymbol sym = n.expr.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Expression must be of type boolean");
		}

		n.then.accept(this);
		n.elze.accept(this);

		return null;
	}

	@Override
	public Type visit(While n) {
		Type texp = n.expr.accept(this);
		if (!(texp instanceof BooleanType)) {
			JSymbol sym = n.expr.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Expression must be of type boolean");
		}

		n.body.accept(this);

		return null;
	}

	@Override
	public Type visit(IntLiteral n) {
		return new IntType(n.getSymbol());
	}

	@Override
	public Type visit(Plus n) {
		Type tlhs = n.getLhs().accept(this);
		Type trhs = n.getRhs().accept(this);

		if (tlhs == null || !(tlhs instanceof IntType || tlhs instanceof StringType) || trhs == null
				|| !(trhs instanceof IntType || trhs instanceof StringType)) {
			JSymbol sym = n.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Incompatible Types used with + operator");
			return new IntType(n.getSymbol());
		}

		if (tlhs instanceof IntType) {
			return trhs;
		}

		if (tlhs instanceof StringType) {
			return tlhs;
		}

		return new IntType(n.getSymbol());
	}

	// @Override
	// public Type visit(Plus n) {
	// Type tlhs = n.getLhs().accept(this);
	// Type trhs = n.getRhs().accept(this);
	//
	// if (tlhs == null || trhs == null) {
	// if (tlhs == null) {
	// JSymbol sym = n.getLhs().getSymbol();
	// addError(sym.getLine(), sym.getColumn(), "Improper Type used with +
	// operator");
	// }
	//
	// if (trhs == null) {
	// JSymbol sym = n.getRhs().getSymbol();
	// addError(sym.getLine(), sym.getColumn(), "Improper Type used with +
	// operator");
	// }
	//
	// return new IntType(n.getSymbol());
	// }
	//
	// if (tlhs instanceof IntType) {
	// if (trhs instanceof IntType || trhs instanceof StringType) {
	// return trhs;
	// } else {
	// JSymbol sym = n.getRhs().getSymbol();
	// addError(sym.getLine(), sym.getColumn(), "Type " + trhs + " cannot be
	// used with + operator");
	// }
	// } else if (tlhs instanceof StringType) {
	// if (trhs instanceof IntType || trhs instanceof StringType) {
	// return tlhs;
	// } else {
	// JSymbol sym = n.getRhs().getSymbol();
	// addError(sym.getLine(), sym.getColumn(), "Type " + trhs + " cannot be
	// used with + operator");
	// }
	// } else {
	// JSymbol sym = n.getLhs().getSymbol();
	// addError(sym.getLine(), sym.getColumn(), "Type " + tlhs + " cannot be
	// used with + operator");
	// }
	//
	// return new IntType(null);
	// }

	@Override
	public Type visit(Minus n) {
		Type lhs = n.getLhs().accept(this);
		Type rhs = n.getRhs().accept(this);

		if (lhs == null || rhs == null) { // Null
			if (lhs == null) {
				JSymbol sym = n.getLhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Improper Type used with - operator");
			}

			if (rhs == null) {
				JSymbol sym = n.getRhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Improper Type used with - operator");
			}
			return new IntType(null);
		} else if (lhs instanceof IntType && rhs instanceof IntType) {
			// Correct Types
			return lhs;
		} else { // Incorrect Types

			if (!(lhs instanceof IntType)) {
				JSymbol sym = n.getLhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Type " + lhs + " cannot be used with - operator");
			}

			if (!(rhs instanceof IntType)) {
				JSymbol sym = n.getRhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Type " + rhs + " cannot be used with - operator");
			}

		}
		return new IntType(null);
	}

	@Override
	public Type visit(Times n) {
		Type lhs = n.getLhs().accept(this);
		Type rhs = n.getRhs().accept(this);

		if (lhs == null || rhs == null) { // Null
			if (lhs == null) {
				JSymbol sym = n.getLhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Improper Type used with * operator");
			}

			if (rhs == null) {
				JSymbol sym = n.getRhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Improper Type used with * operator");
			}
			return new IntType(null);
		} else if (lhs instanceof IntType && rhs instanceof IntType) {
			// Correct Types
			return lhs;
		} else { // Incorrect Types

			if (!(lhs instanceof IntType)) {
				JSymbol sym = n.getLhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Type " + lhs + " cannot be used with * operator");
			}

			if (!(rhs instanceof IntType)) {
				JSymbol sym = n.getRhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Type " + rhs + " cannot be used with * operator");
			}

		}
		return new IntType(null);
	}

	@Override
	public Type visit(Division n) {
		Type lhs = n.getLhs().accept(this);
		Type rhs = n.getRhs().accept(this);

		if (lhs == null || rhs == null) { // Null
			if (lhs == null) {
				JSymbol sym = n.getLhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Improper Type used with / operator");
			}

			if (rhs == null) {
				JSymbol sym = n.getRhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Improper Type used with / operator");
			}
			return new IntType(null);
		} else if (lhs instanceof IntType && rhs instanceof IntType) {
			// Correct Types
			return lhs;
		} else { // Incorrect Types

			if (!(lhs instanceof IntType)) {
				JSymbol sym = n.getLhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Type " + lhs + " cannot be used with / operator");
			}

			if (!(rhs instanceof IntType)) {
				JSymbol sym = n.getRhs().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Type " + rhs + " cannot be used with / operator");
			}

		}
		return new IntType(null);
	}

	@Override
	public Type visit(Equals n) {

		Type t1 = n.getLhs().accept(this);
		Type t2 = n.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			JSymbol sym = n.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Incorrect types used with == oprator");
		} else if ((t1 instanceof IntType && t2 instanceof IntType)
				|| (t1 instanceof BooleanType && t2 instanceof BooleanType)
				|| (t1 instanceof StringType && t2 instanceof StringType)
				|| (t1 instanceof IntArrayType && t2 instanceof IntArrayType)
				|| (t1 instanceof IdentifierType && t2 instanceof IdentifierType)) {

		} else {
			JSymbol sym = n.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Incorrect types compared used with == oprator");
		}
		return new BooleanType(null);
	}

	@Override
	public Type visit(LessThan n) {

		Type t1 = n.getLhs().accept(this);
		Type t2 = n.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			JSymbol sym = n.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Incorrect types used with < oprator");
		} else if (!(t1 instanceof IntType)) {
			JSymbol sym = n.getLhs().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Type " + t1 + " used with < oprator");
		} else if (!(t2 instanceof IntType)) {
			JSymbol sym = n.getRhs().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Type " + t2 + " used with < oprator");
		}
		return new BooleanType(null);
	}

	@Override
	public Type visit(And n) {

		Type t1 = n.getLhs().accept(this);
		Type t2 = n.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			JSymbol sym = n.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Incorrect types used with && oprator");
		} else if (!(t1 instanceof BooleanType)) {
			JSymbol sym = n.getLhs().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Type " + t1 + " used with && oprator");
		} else if (!(t2 instanceof BooleanType)) {
			JSymbol sym = n.getRhs().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Type " + t2 + " used with && oprator");
		}
		return new BooleanType(null);
	}

	@Override
	public Type visit(Or n) {

		Type t1 = n.getLhs().accept(this);
		Type t2 = n.getRhs().accept(this);

		if (t1 == null || t2 == null) {
			JSymbol sym = n.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Incorrect types used with || oprator");
		} else if (!(t1 instanceof BooleanType)) {
			JSymbol sym = n.getLhs().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Type " + t1 + " used with || oprator");
		} else if (!(t2 instanceof BooleanType)) {
			JSymbol sym = n.getRhs().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Type " + t2 + " used with || oprator");
		}
		return new BooleanType(null);
	}

	@Override
	public Type visit(Not n) {

		Type t1 = n.getExpr().accept(this);

		if (t1 == null) {
			JSymbol sym = n.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Incorrect type used with ! oprator");
		} else if (!(t1 instanceof BooleanType)) {
			JSymbol sym = n.getExpr().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Type " + t1 + " used with ! oprator");
		}
		return new BooleanType(null);
	}

	@Override
	public Type visit(True true1) {
		return new BooleanType(null);
	}

	@Override
	public Type visit(False false1) {
		return new BooleanType(null);
	}

	@Override
	public Type visit(IdentifierExpr i) {
		Binding b = i.getB();
		if (b != null) {
			return ((Variable) b).type();
		}
		return null;
	}

	@Override
	public Type visit(This this1) {
		return currClass.type();
	}

	@Override
	public Type visit(NewArray newArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(NewInstance newInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(CallMethod cm) {

		// Check Reference Object
		Type ref = cm.getInstanceName().accept(this);
		if (ref == null || !(ref instanceof IdentifierType)) {
			JSymbol sym = cm.getInstanceName().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Dereferenced object must be of an object type");
		}

		// Check if method exists
		IdentifierType tid = (IdentifierType) ref;
		IdentifierExpr mid = cm.getMethodId();
		Method m = st.getMethod(mid.getVarID(), tid.getVarID());
		if (m == null) {
			JSymbol sym = mid.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Method " + mid + " not declared");
		}

		mid.setB(m);

		// Check arguments
		checkCallArguments(cm, m);

		return m.type();
	}

	private void checkCallArguments(CallMethod cm, Method m) {

		// Check number of parameters
		if (cm.getArgExprListSize() != m.getParamsSize()) {
			JSymbol sym = cm.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "The number of parameters must match the number of arguments");
			return;
		}

		// Check argument types
		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			Variable var = m.getParamAt(i);
			Type t1 = var.type();
			Type t2 = cm.getArgExprAt(i).accept(this);

			if (!st.compareTypes(t1, t2)) {
				JSymbol sym = cm.getArgExprAt(i).getSymbol();
				addError(sym.getLine(), sym.getColumn(),
						"Type " + t2 + " of argument does not match type " + t1 + " of parameter");
			}
		}

	}

	@Override
	public Type visit(Length length) {
		Type t = length.getArray().accept(this);

		if (t == null || !(t instanceof IntArrayType)) {
			JSymbol sym = length.getArray().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Identifier must be of Type int[]");
		}
		return new IntType(length.getSymbol());
	}

	@Override
	public Type visit(Sidef sidef) {
		sidef.getArgument().accept(this);
		return null;
	}

	@Override
	public Type visit(IntType intType) {
		return intType;
	}

	@Override
	public Type visit(StringType stringType) {
		return stringType;
	}

	@Override
	public Type visit(BooleanType booleanType) {
		return booleanType;
	}

	@Override
	public Type visit(IntArrayType intArrayType) {
		return intArrayType;
	}

	@Override
	public Type visit(IdentifierType referenceType) {
		return referenceType;
	}

	@Override
	public Type visit(VarDecl varDeclaration) {
		return null;
	}

	@Override
	public Type visit(ArgDecl argDeclaration) {
		return null;
	}

	private void checkOverriding(MethodDecl md, Method m) {
		String p = currClass.parent();
		Method pm = st.getMethod(m.getId(), p);
		if (pm == null || pm.getParamsSize() != m.getParamsSize()) {
			// No method overriding
			return;
		}

		for (int i = 0; i < md.getArgListSize(); i++) {
			ArgDecl ad = md.getArgDeclAt(i);
			Type t1 = ad.accept(this);
			Variable v = pm.getParamAt(i);
			Type t2 = v.type();
			if (!st.absCompTypes(t1, t2)) {
				JSymbol sym = ad.getSymbol();
				addError(sym.getLine(), sym.getColumn(),
						"Type mismatch of parameter in the overriding and overridden methods");
			}
		}

		if (!st.absCompTypes(m.type(), pm.type())) {
			JSymbol sym = md.getReturnType().getSymbol();
			addError(sym.getLine(), sym.getColumn(),
					"Return type " + m.type() + " must match type " + pm.type() + " in overridden method");
		}
	}

	@Override
	public Type visit(MethodDecl md) {

		String id = md.getMethodName().getVarID();
		if (hsMethod.contains(id)) { // Duplicate Method
			return md.getReturnType();
		}
		hsMethod.add(id);
		// currMethod = currClass.getMethod(id);
		currMethod = (Method) md.getMethodName().getB();

		checkOverriding(md, currMethod);

		for (int i = 0; i < md.getStatListSize(); i++) {
			Statement st = md.getStatAt(i);
			st.accept(this);
		}

		Type t1 = md.getReturnType();
		Type t2 = md.getReturnExpr().accept(this);

		if (!st.compareTypes(t1, t2)) {
			JSymbol sym = md.getReturnExpr().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Method " + id + " must return a result of Type " + t1);
		}

		currMethod = null;
		return md.getReturnType();
	}

	@Override
	public Type visit(MainClass mc) {

		// String id = mc.getClassName().getVarID();
		// currClass = st.getKlass(id);
		currClass = (Klass) mc.getClassName().getB();
		mc.getStat().accept(this);
		return null;
	}

	@Override
	public Type visit(Program program) {

		program.mClass.accept(this);

		for (int i = 0; i < program.klassList.size(); i++) {
			program.klassList.get(i).accept(this);
		}
		return null;
	}

	@Override
	public Type visit(Identifier id) {
		Binding b = id.getB();
		if (b != null) {
			return ((Variable) b).type();
		}
		// Type tid = st.getVarType(currMethod, currClass, id.getVarID());
		return null;
	}

	@Override
	public Type visit(IndexArray ia) {
		// Check array type
		Type tid = ia.getArray().accept(this);
		if (tid == null || !(tid instanceof IntArrayType)) {
			JSymbol sym = ia.getArray().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Array expression must evaluate to be of Type int[]");
		}

		// Check index type
		Type tin = ia.getIndex().accept(this);
		if (tin == null || !(tin instanceof IntType)) {
			JSymbol sym = ia.getIndex().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Index expression must evaluate to be of Type int");
		}

		return new IntType(null);
	}

	@Override
	public Type visit(ArrayAssign aa) {
		// Check identifier type
		Type tid = aa.getIdentifier().accept(this);
		if (tid == null || !(tid instanceof IntArrayType)) {
			JSymbol sym = aa.getIdentifier().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Identifier must be of Type int[]");
		}

		// Check expression type
		Type texp1 = aa.getE1().accept(this);
		if (texp1 == null || !(texp1 instanceof IntType)) {
			JSymbol sym = aa.getE1().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Expression must be of Type int");
		}

		// Check assigned expression type
		Type texp2 = aa.getE2().accept(this);
		if (texp2 == null || !(texp2 instanceof IntType)) {
			JSymbol sym = aa.getE2().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Expression must be of Type int");
		}

		return null;
	}

	@Override
	public Type visit(StringLiteral stringLiteral) {
		return new StringType(null);
	}

	@Override
	public Type visit(ClassDeclSimple cd) {

		String id = cd.getId().getVarID();
		if (hsKlass.contains(id)) { // Duplicate class
			return null;
		}
		hsKlass.add(id);
		// currClass = st.getKlass(id);

		Binding b = cd.getId().getB();
		currClass = (Klass) b;

		hsMethod.clear();
		for (int i = 0; i < cd.getMethodListSize(); i++) {
			MethodDecl md = cd.getMethodDeclAt(i);
			md.accept(this);
		}

		return null;
	}

	@Override
	public Type visit(ClassDeclExtends cd) {

		String id = cd.getId().getVarID();
		if (hsKlass.contains(id)) { // Duplicate class
			return null;
		}
		hsKlass.add(id);
		Binding b = cd.getId().getB();
		currClass = (Klass) b;

		hsMethod.clear();
		for (int i = 0; i < cd.getMethodListSize(); i++) {
			MethodDecl md = cd.getMethodDeclAt(i);
			md.accept(this);
		}

		return null;
	}

	public static void addError(int line, int col, String errorText) {
		SemanticErrors.addError(line, col, errorText);
	}

}
