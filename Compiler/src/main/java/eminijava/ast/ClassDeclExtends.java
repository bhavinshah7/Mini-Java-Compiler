package eminijava.ast;

import java.util.List;

import eminijava.lexer.JSymbol;

public class ClassDeclExtends extends ClassDecl {

	IdentifierExpr id;
	IdentifierExpr parent;
	List<VarDecl> varList;
	List<MethodDecl> methodList;

	public ClassDeclExtends(JSymbol jSymbol, IdentifierExpr className, IdentifierExpr parentClassName,
			List<VarDecl> varList, List<MethodDecl> methodList) {
		super(jSymbol);
		this.id = className;
		this.parent = parentClassName;
		this.varList = varList;
		this.methodList = methodList;
	}

	public IdentifierExpr getId() {
		return id;
	}

	public void setId(IdentifierExpr id) {
		this.id = id;
	}

	public IdentifierExpr getParent() {
		return parent;
	}

	public void setParent(IdentifierExpr parent) {
		this.parent = parent;
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
