package eminijava.ast;

import java.util.List;

import eminijava.lexer.JSymbol;

public class ClassDeclSimple extends ClassDecl {

	IdentifierExpr id;
	List<VarDecl> varList;
	List<MethodDecl> methodList;

	public ClassDeclSimple(JSymbol jSymbol, IdentifierExpr className, List<VarDecl> varList,
			List<MethodDecl> methodList) {
		super(jSymbol);
		this.id = className;
		this.varList = varList;
		this.methodList = methodList;
	}

	public IdentifierExpr getId() {
		return id;
	}

	public void setId(IdentifierExpr id) {
		this.id = id;
	}

	public int getMethodListSize() {
		return methodList.size();
	}

	public MethodDecl getMethodDeclAt(int index) {
		if (index < methodList.size()) {
			return methodList.get(index);
		}
		return null;
	}

	public int getVarListSize() {
		return varList.size();
	}

	public VarDecl getVarDeclAt(int index) {
		if (index < varList.size()) {
			return varList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}
