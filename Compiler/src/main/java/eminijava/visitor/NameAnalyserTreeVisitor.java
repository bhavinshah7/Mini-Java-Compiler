package eminijava.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class NameAnalyserTreeVisitor implements Visitor<Type> {

	SymbolTable symbolTable;
	private Klass currClass;
	private Method currMethod;
	private Set<String> hsKlass = new HashSet<>();
	private Set<String> hsMethod = new HashSet<>();

	public NameAnalyserTreeVisitor(SymbolTable table) {
		this.symbolTable = table;
	}

	@Override
	public Type visit(Print n) {
		n.getExpr().accept(this);
		return null;
	}

	@Override
	public Type visit(Assign n) {

		n.getId().accept(this);
		n.expr.accept(this);
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
		n.expr.accept(this);
		n.then.accept(this);
		n.elze.accept(this);
		return null;
	}

	@Override
	public Type visit(While n) {
		n.getExpr().accept(this);
		n.getBody().accept(this);
		return null;
	}

	@Override
	public Type visit(IntLiteral n) {
		return null;
	}

	@Override
	public Type visit(Plus n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Minus n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Times n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Division n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Equals n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(LessThan n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(And n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Or n) {
		n.getLhs().accept(this);
		n.getRhs().accept(this);
		return null;
	}

	@Override
	public Type visit(Not n) {
		n.getExpr().accept(this);
		return null;
	}

	@Override
	public Type visit(True true1) {
		return null;
	}

	@Override
	public Type visit(False false1) {
		return null;
	}

	@Override
	public Type visit(This this1) {
		/**
		 * Note: This is a hack! Statement cannot exist outside a method. main
		 * is not a method in this AST. We use this fact to check if this is
		 * used inside main method.
		 */
		if (currMethod == null) {
			JSymbol sym = this1.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "this keyword cannot be used in static methods");
		}

		return null;
	}

	@Override
	public Type visit(NewArray na) {
		na.getArrayLength().accept(this);
		return null;
	}

	@Override
	public Type visit(NewInstance ni) {
		String id = ni.getClassName().getVarID();
		Klass klass = symbolTable.getKlass(id);
		if (klass == null) {
			JSymbol sym = ni.getClassName().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "class " + id + " is not declared");
		}

		ni.getClassName().setB(klass);
		return null;
	}

	@Override
	public Type visit(CallMethod cm) {
		cm.getInstanceName().accept(this); // TODO: Can we check the id? this?

		/*
		 * Note: Check if method exists in type analysis
		 */
		// cm.getMethodId().accept(this);

		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			Expression e = cm.getArgExprAt(i);
			e.accept(this);
		}

		return null;
	}

	@Override
	public Type visit(Length length) {
		length.getArray().accept(this);
		return null;
	}

	@Override
	public Type visit(Sidef sidef) {
		sidef.getArgument().accept(this);
		return null;
	}

	@Override
	public Type visit(IntType intType) {
		return null;
	}

	@Override
	public Type visit(StringType stringType) {
		return null;
	}

	@Override
	public Type visit(BooleanType booleanType) {
		return null;
	}

	@Override
	public Type visit(IntArrayType intArrayType) {
		return null;
	}

	@Override
	public Type visit(Identifier i) {
		String id = i.getVarID();
		Variable var = symbolTable.getVar(currMethod, currClass, id);
		if (var == null) {
			JSymbol sym = i.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "variable " + id + " is not declared");
			// TODO: check method ? check assign, dup code possible
		}

		i.setB(var);
		return null;
	}

	@Override
	public Type visit(IdentifierType ref) {

		String id = ref.getVarID();
		Klass klass = symbolTable.getKlass(id);
		if (klass == null) {
			JSymbol sym = ref.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "class " + id + " is not declared");
		}

		ref.setB(klass);
		return null;
	}

	@Override
	public Type visit(IdentifierExpr i) {
		String id = i.getVarID();
		Variable var = symbolTable.getVar(currMethod, currClass, id);
		if (var == null) {
			JSymbol sym = i.getSymbol();
			addError(sym.getLine(), sym.getColumn(), "variable " + id + " is not declared");
			// TODO: check method ? check assign, dup code possible
		}

		i.setB(var);
		return null;
	}

	@Override
	public Type visit(VarDecl vd) {
		vd.getType().accept(this);
		vd.getId().accept(this);

		String id = vd.getId().getVarID();

		if (currMethod == null) {// Klass Variable

			Klass parent = symbolTable.getKlass(currClass.parent());
			if (symbolTable.containsVar(null, parent, id)) {// overriding
				JSymbol sym = vd.getId().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "Variable " + id + " already defined in parent class");
			}
		}

		return null;
	}

	@Override
	public Type visit(ArgDecl ad) {
		/**
		 * Note: argDeclaration already checked in buildSymbolTable
		 */
		// ad.getType().accept(this);
		ad.getId().accept(this);

		return null;
	}

	@Override
	public Type visit(MethodDecl md) {

		String id = md.getMethodName().getVarID();
		if (hsMethod.contains(id)) { // Duplicate Method
			return null;
		} else {
			hsMethod.add(id);
		}

		md.getReturnType().accept(this);
		currMethod = currClass.getMethod(id);
		md.getMethodName().setB(currMethod);

		String parent = currClass.parent();
		Method supMethod = symbolTable.getMethod(id, parent);
		if (supMethod != null) { // Method overloading
			if (supMethod.getParamsSize() != currMethod.getParamsSize()) {
				JSymbol sym = md.getMethodName().getSymbol();
				addError(sym.getLine(), sym.getColumn(), "method " + id + " overloads parent class method");
			}
		}

		/**
		 * Note: argDeclaration already checked in buildSymbolTable
		 */

		for (int i = 0; i < md.getArgListSize(); i++) {
			ArgDecl ad = md.getArgDeclAt(i);
			ad.accept(this);
		}

		for (int i = 0; i < md.getVarListSize(); i++) {
			VarDecl vd = md.getVarDeclAt(i);
			vd.accept(this);
		}

		for (int i = 0; i < md.getStatListSize(); i++) {
			Statement st = md.getStatAt(i);
			st.accept(this);
		}

		md.getReturnExpr().accept(this);
		currMethod = null;
		return null;
	}

	@Override
	public Type visit(MainClass mc) {

		String id = mc.getClassName().getVarID();
		currClass = symbolTable.getKlass(id);
		mc.getClassName().setB(currClass);

		mc.getStringArrayId().accept(this);
		mc.getStat().accept(this);

		return null;
	}

	@Override
	public Type visit(Program program) {

		checkInheritanceCycle(program);

		program.mClass.accept(this);

		for (int i = 0; i < program.klassList.size(); i++) {
			program.klassList.get(i).accept(this);
		}
		return null;
	}

	private void checkInheritanceCycle(Program p) {

		Map<String, List<String>> adjList = new HashMap<>();

		for (int i = 0; i < p.getClassListSize(); i++) {
			ClassDecl cd = p.getClassDeclAt(i);
			if (cd instanceof ClassDeclExtends) {
				String cid = ((ClassDeclExtends) cd).getId().getVarID();
				String pid = ((ClassDeclExtends) cd).getParent().getVarID();
				List<String> cList = adjList.get(pid);
				if (cList == null) {
					cList = new ArrayList<String>();
					adjList.put(pid, cList);
				}
				cList.add(cid);
			}
		}

		String[] nodes = adjList.keySet().toArray(new String[0]);
		List<String> visited = new ArrayList<>();
		for (int i = 0; i < nodes.length; i++) {
			if (!visited.contains(nodes[i])) {
				List<String> rStack = new ArrayList<>();
				dfs(nodes[i], adjList, visited, rStack, p);
			}
		}
	}

	private void dfs(String src, Map<String, List<String>> adjList, List<String> visited, List<String> rStack,
			Program p) {
		visited.add(src);
		rStack.add(src);
		List<String> cList = adjList.get(src);
		if (cList == null) {
			return;
		}

		for (String cid : cList) {
			if (!visited.contains(cid)) {
				dfs(cid, adjList, visited, rStack, p);
			} else if (rStack.contains(cid)) {

				JSymbol sym = getKlassSymbol(cid, p);
				if (sym != null) {
					addError(sym.getLine(), sym.getColumn(),
							"Inheretence cycle found in class hierarchy class " + cid + " extends class " + src);
				} else {
					addError(0, 0,
							"Inheretence cycle found in class hierarchy class " + cid + " extends parent " + src);
				}

			}
		}
	}

	public JSymbol getKlassSymbol(String id, Program p) {
		// TODO: This is a hack!
		for (int i = 0; i < p.getClassListSize(); i++) {
			ClassDecl cd = p.getClassDeclAt(i);
			if (cd instanceof ClassDeclSimple) {
				if (id.equals(((ClassDeclSimple) cd).getId().getVarID())) {
					return cd.getSymbol();
				}
			}

			if (cd instanceof ClassDeclExtends) {
				if (id.equals(((ClassDeclExtends) cd).getId().getVarID())) {
					return cd.getSymbol();
				}
			}
		}
		return null;
	}

	@Override
	public Type visit(IndexArray ia) {
		ia.getArray().accept(this);
		ia.getIndex().accept(this);
		return null;
	}

	@Override
	public Type visit(ArrayAssign aa) {
		aa.getIdentifier().accept(this);
		aa.getE1().accept(this);
		aa.getE2().accept(this);
		return null;
	}

	@Override
	public Type visit(StringLiteral stringLiteral) {
		return null;
	}

	@Override
	public Type visit(ClassDeclSimple cd) {

		String id = cd.getId().getVarID();
		if (hsKlass.contains(id)) { // Duplicate Klass
			return null;
		} else {
			hsKlass.add(id);
		}

		currClass = symbolTable.getKlass(id);
		cd.getId().setB(currClass);

		for (int i = 0; i < cd.getVarListSize(); i++) {
			VarDecl vd = cd.getVarDeclAt(i);
			vd.accept(this);
		}

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
		} else {
			hsKlass.add(id);
		}

		currClass = symbolTable.getKlass(id);
		cd.getId().setB(currClass);

		String parent = currClass.parent();
		Klass parentKlass = symbolTable.getKlass(parent);
		if (parentKlass == null) {
			JSymbol sym = cd.getParent().getSymbol();
			addError(sym.getLine(), sym.getColumn(), "parent class " + parent + " not declared");
		} else {
			cd.getParent().setB(parentKlass);
		}

		for (int i = 0; i < cd.getVarListSize(); i++) {
			VarDecl vd = cd.getVarDeclAt(i);
			vd.accept(this);
		}

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
