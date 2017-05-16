package eminijava.visitor;

import eminijava.ast.And;
import eminijava.ast.BooleanType;
import eminijava.ast.CallMethod;
import eminijava.ast.Division;
import eminijava.ast.Equals;
import eminijava.ast.False;
import eminijava.ast.IdentifierExpr;
import eminijava.ast.IdentifierType;
import eminijava.ast.IndexArray;
import eminijava.ast.IntArrayType;
import eminijava.ast.IntLiteral;
import eminijava.ast.IntType;
import eminijava.ast.Length;
import eminijava.ast.LessThan;
import eminijava.ast.Minus;
import eminijava.ast.NewArray;
import eminijava.ast.NewInstance;
import eminijava.ast.Not;
import eminijava.ast.Or;
import eminijava.ast.Plus;
import eminijava.ast.StringLiteral;
import eminijava.ast.StringType;
import eminijava.ast.This;
import eminijava.ast.Times;
import eminijava.ast.True;
import eminijava.ast.Type;

public class BranchGenerator implements IBranchVisitor<String> {

	private int labelCount;
	private int bytecode;
	private CodeGenerator2 cg;
	public static final String PRE = "BG";

	public BranchGenerator(CodeGenerator2 cg2) {
		cg = cg2;
	}

	private String getNextLabel() {
		return PRE + (++labelCount);
	}

	public int getLen() {
		return bytecode;
	}

	@Override
	public String visit(IntLiteral n, String nTrue, String nFalse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(And and, String nTrue, String nFalse) {

		String nNext = getNextLabel();

		StringBuilder sb = new StringBuilder();
		sb.append(and.getLhs().accept(this, nNext, nFalse) + "\n");
		sb.append(nNext + ":" + "\n");
		sb.append(and.getRhs().accept(this, nTrue, nFalse) + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(CallMethod cm, String nTrue, String nFalse) {

		StringBuilder sb = new StringBuilder();
		sb.append(cm.accept(cg) + "\n");
		sb.append("ifeq " + nFalse + "\n");
		sb.append("goto " + nTrue + "\n");

		bytecode += 2;
		return sb.toString();
	}

	@Override
	public String visit(Division division, String nTrue, String nFalse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(Equals n, String nTrue, String nFalse) {

		Type t = n.type();

		StringBuilder sb = new StringBuilder();
		sb.append(n.getLhs().accept(cg) + "\n");
		sb.append(n.getRhs().accept(cg) + "\n");

		if (t instanceof IntType || t instanceof BooleanType) {
			sb.append("if_icmpeq " + nTrue + "\n");
			sb.append("goto " + nFalse + "\n");

		} else if (t instanceof StringType || t instanceof IntArrayType || t instanceof IdentifierType) {
			sb.append("if_acmpeq " + nTrue + "\n");
			sb.append("goto " + nFalse + "\n");
		}

		bytecode += 2;
		return sb.toString();
	}

	@Override
	public String visit(False false1, String nTrue, String nFalse) {
		bytecode += 1;
		return "goto " + nFalse + "\n";
	}

	@Override
	public String visit(IdentifierExpr ie, String nTrue, String nFalse) {

		StringBuilder sb = new StringBuilder();
		sb.append(ie.accept(cg) + "\n");
		sb.append("ifeq " + nFalse + "\n");
		sb.append("goto " + nTrue + "\n");

		bytecode += 2;
		return sb.toString();
	}

	@Override
	public String visit(IndexArray indexArray, String nTrue, String nFalse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(Length length, String nTrue, String nFalse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(LessThan n, String nTrue, String nFalse) {
		StringBuilder sb = new StringBuilder();

		sb.append(n.getLhs().accept(cg) + "\n");
		sb.append(n.getRhs().accept(cg) + "\n");

		sb.append("if_icmplt " + nTrue + "\n");
		sb.append("goto " + nFalse + "\n");

		bytecode += 2;
		return sb.toString();
	}

	@Override
	public String visit(Minus minus, String nTrue, String nFalse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(NewArray newArray, String nTrue, String nFalse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(NewInstance newInstance, String nTrue, String nFalse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(Not not, String nTrue, String nFalse) {
		StringBuilder sb = new StringBuilder();
		sb.append(not.getExpr().accept(this, nFalse, nTrue));
		return sb.toString();
	}

	@Override
	public String visit(Or or, String nTrue, String nFalse) {
		String nNext = getNextLabel();

		StringBuilder sb = new StringBuilder();
		sb.append(or.getLhs().accept(this, nTrue, nNext) + "\n");
		sb.append(nNext + ":" + "\n");
		sb.append(or.getRhs().accept(this, nTrue, nFalse) + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(Plus plus, String nTrue, String nFalse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(StringLiteral stringLiteral, String nTrue, String nFalse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(This this1, String nTrue, String nFalse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(Times times, String nTrue, String nFalse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visit(True true1, String nTrue, String nFalse) {
		bytecode += 1;
		return "goto " + nTrue + "\n";
	}

}
