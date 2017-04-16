package eminijava.semantics;

import eminijava.ast.ClassDecl;

public class ClassBinding extends Binding {

	private ClassDecl cd;

	public ClassBinding(ClassDecl cd) {
		this.cd = cd;
	}

	public ClassDecl getCd() {
		return cd;
	}

	public void setCd(ClassDecl cd) {
		this.cd = cd;
	}

}
