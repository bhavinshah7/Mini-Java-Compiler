package eminijava.visitor;

import eminijava.ast.And;
import eminijava.ast.CallMethod;
import eminijava.ast.Division;
import eminijava.ast.Equals;
import eminijava.ast.False;
import eminijava.ast.IdentifierExpr;
import eminijava.ast.IndexArray;
import eminijava.ast.IntLiteral;
import eminijava.ast.Length;
import eminijava.ast.LessThan;
import eminijava.ast.Minus;
import eminijava.ast.NewArray;
import eminijava.ast.NewInstance;
import eminijava.ast.Not;
import eminijava.ast.Or;
import eminijava.ast.Plus;
import eminijava.ast.StringLiteral;
import eminijava.ast.This;
import eminijava.ast.Times;
import eminijava.ast.True;

public interface IBranchVisitor<R> {

	// Expression

	public R visit(IntLiteral n, String nTrue, String nFalse);

	public R visit(And and, String nTrue, String nFalse);

	public R visit(CallMethod callMethod, String nTrue, String nFalse);

	public R visit(Division division, String nTrue, String nFalse);

	public R visit(Equals equals, String nTrue, String nFalse);

	public R visit(False false1, String nTrue, String nFalse);

	public R visit(IdentifierExpr identifierExpr, String nTrue, String nFalse);

	public R visit(IndexArray indexArray, String nTrue, String nFalse);

	public R visit(Length length, String nTrue, String nFalse);

	public R visit(LessThan lessThan, String nTrue, String nFalse);

	public R visit(Minus minus, String nTrue, String nFalse);

	public R visit(NewArray newArray, String nTrue, String nFalse);

	public R visit(NewInstance newInstance, String nTrue, String nFalse);

	public R visit(Not not, String nTrue, String nFalse);

	public R visit(Or or, String nTrue, String nFalse);

	public R visit(Plus plus, String nTrue, String nFalse);

	public R visit(StringLiteral stringLiteral, String nTrue, String nFalse);

	public R visit(This this1, String nTrue, String nFalse);

	public R visit(Times times, String nTrue, String nFalse);

	public R visit(True true1, String nTrue, String nFalse);

}
