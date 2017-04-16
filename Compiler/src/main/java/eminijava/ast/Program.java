package eminijava.ast;

import java.util.List;

import eminijava.lexer.JSymbol;

public class Program extends Tree {

	public MainClass mClass;
	public List<ClassDecl> klassList;

	public Program(JSymbol jSymbol, MainClass mClass, List<ClassDecl> klassList) {
		super(jSymbol);
		this.mClass = mClass;
		this.klassList = klassList;
	}

	public MainClass getmClass() {
		return mClass;
	}

	public void setmClass(MainClass mClass) {
		this.mClass = mClass;
	}

	public List<ClassDecl> getKlassList() {
		return klassList;
	}

	public void setKlassList(List<ClassDecl> klassList) {
		this.klassList = klassList;
	}

	public int getClassListSize() {
		return klassList.size();
	}

	public ClassDecl getClassDeclAt(int index) {
		if (index < klassList.size()) {
			return klassList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

}
