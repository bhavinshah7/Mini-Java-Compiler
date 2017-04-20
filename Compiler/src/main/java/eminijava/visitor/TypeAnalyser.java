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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Assign n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Skip n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Block n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(IfThenElse n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(While n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(IntLiteral n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Plus n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Minus n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Times n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Division n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Equals n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(LessThan n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(And n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Or n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Not n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(True true1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(False false1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(IdentifierExpr identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(This this1) {
		// TODO Auto-generated method stub
		return null;
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
	public Type visit(CallMethod callMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Length length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Sidef sidef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(IntType intType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(StringType stringType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(BooleanType booleanType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(IntArrayType intArrayType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(IdentifierType referenceType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(VarDecl varDeclaration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(ArgDecl argDeclaration) {
		// TODO Auto-generated method stub
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
		currMethod = currClass.getMethod(id);

		checkOverriding(md, currMethod);

		for (int i = 0; i < md.getStatListSize(); i++) {
			Statement st = md.getStatAt(i);
			st.accept(this);
		}

		Type t1 = md.getReturnExpr().accept(this);
		Type t2 = md.getReturnType();
		if (!st.compareTypes(t1, t2)) {
			JSymbol sym = md.getReturnExpr().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "Method " + id + " must return a result of Type " + t2);
		}

		currMethod = null;
		return md.getReturnType();
	}

	@Override
	public Type visit(MainClass mainClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Program program) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(Identifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(IndexArray indexArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(ArrayAssign arrayAssign) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(StringLiteral stringLiteral) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visit(ClassDeclSimple cd) {

		String id = cd.getId().getVarID();
		if (hsKlass.contains(id)) { // Duplicate class
			return null;
		}
		hsKlass.add(id);
		currClass = st.getKlass(id);

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
		currClass = st.getKlass(id);

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
