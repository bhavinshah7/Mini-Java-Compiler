package eminijava.visitor;

import eminijava.ast.And;
import eminijava.ast.ArgDeclaration;
import eminijava.ast.ArrayAssign;
import eminijava.ast.Assign;
import eminijava.ast.Block;
import eminijava.ast.BooleanType;
import eminijava.ast.CallMethod;
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
import eminijava.ast.VarDecl;
import eminijava.ast.Visitor;
import eminijava.ast.While;
import eminijava.semantics.Binding;

public class TreePrinter implements Visitor<String> {
	int level = 0;

	void incLevel() {
		level = level + 1;
	}

	void decLevel() {
		level = level - 1;
	}

	String printInc() {
		char[] chars = new char[4 * level];
		java.util.Arrays.fill(chars, ' ');
		return new String(chars);
	}

	@Override
	public String visit(IntLiteral n) {
		return Integer.toString(n.getValue());
	}

	@Override
	public String visit(Plus n) {
		return n.getLhs().accept(this) + " + " + n.getRhs().accept(this);
	}

	@Override
	public String visit(Minus n) {
		return n.getLhs().accept(this) + " - " + n.getRhs().accept(this);
	}

	@Override
	public String visit(Times n) {
		return n.getLhs().accept(this) + " * " + n.getRhs().accept(this);
	}

	@Override
	public String visit(Division n) {
		return n.getLhs().accept(this) + " / " + n.getRhs().accept(this);
	}

	@Override
	public String visit(Equals n) {
		return n.getLhs().accept(this) + " == " + n.getRhs().accept(this);
	}

	@Override
	public String visit(LessThan n) {
		return n.getLhs().accept(this) + " < " + n.getRhs().accept(this);
	}

	@Override
	public String visit(And n) {
		return n.getLhs().accept(this) + " && " + n.getRhs().accept(this);
	}

	@Override
	public String visit(Or n) {
		return n.getLhs().accept(this) + " || " + n.getRhs().accept(this);
	}

	@Override
	public String visit(Not n) {
		return "!" + n.getExpr().accept(this);
	}

	// ############## Statements ##############

	@Override
	public String visit(IfThenElse n) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(printInc() + "if (" + n.getExpr().accept(this) + ") {\n");
		incLevel();
		strBuilder.append(n.getThen().accept(this));
		strBuilder.append("\n");
		decLevel();
		if (n.getElze().accept(this) != "") {
			strBuilder.append(printInc() + "} else {\n");
			incLevel();
			strBuilder.append(n.getElze().accept(this));
			strBuilder.append("\n");
			decLevel();
		}
		strBuilder.append(printInc() + "}\n");
		return strBuilder.toString();
	}

	@Override
	public String visit(Print n) {
		return printInc() + "System.out.println(" + n.getExpr().accept(this) + ");";
	}

	@Override
	public String visit(Assign n) {
		return printInc() + n.getId().accept(this) + " = " + n.getExpr().accept(this) + ";";
	}

	@Override
	public String visit(While n) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(printInc() + "while (" + n.getExpr().accept(this) + ") {\n");
		incLevel();
		strBuilder.append(n.getBody().accept(this));
		decLevel();
		strBuilder.append(printInc() + "}\n");
		return strBuilder.toString();
	}

	@Override
	public String visit(Block n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n.getStatListSize(); i++) {
			Statement stat = n.getStatAt(i);
			sb.append(stat.accept(this));
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public String visit(Skip n) {
		return "";
	}

	@Override
	public String visit(True true1) {
		return "true";
	}

	@Override
	public String visit(False false1) {
		return "false";
	}

	@Override
	public String visit(IdentifierExpr id) {
		Binding b = id.getB();
		if (b == null) {
			return id.getVarID() + "_#error#_";
		} else {
			return id.getVarID() + "_" + b.toString() + "_";
		}
	}

	@Override
	public String visit(This this1) {
		return "this";
	}

	@Override
	public String visit(NewArray na) {
		return "new int [" + na.getArrayLength().accept(this) + "]";
	}

	@Override
	public String visit(NewInstance ni) {
		return "new " + ni.getClassName().accept(this) + "()";
	}

	@Override
	public String visit(CallMethod cm) {
		StringBuilder sb = new StringBuilder();
		sb.append("(" + cm.getInstanceName().accept(this));
		sb.append(".");
		sb.append(cm.getMethodId().accept(this));
		sb.append("(");

		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			Expression expr = cm.getArgExprAt(i);
			sb.append(expr.accept(this));
			if (i != cm.getArgExprListSize() - 1) {
				sb.append(", ");
			}
		}

		sb.append("))");
		return sb.toString();
	}

	@Override
	public String visit(Length length) {
		return length.getArray().accept(this) + ".length";
	}

	@Override
	public String visit(Sidef sidef) {
		return printInc() + "sidef(" + sidef.getArgument().accept(this) + ");";
	}

	@Override
	public String visit(IntType intType) {
		return "int";
	}

	@Override
	public String visit(StringType stringType) {
		return "String";
	}

	@Override
	public String visit(BooleanType booleanType) {
		return "boolean";
	}

	@Override
	public String visit(IntArrayType intArrayType) {
		return "int[]";
	}

	@Override
	public String visit(IdentifierType id) {
		Binding b = id.getB();
		if (b == null) {
			return id.getVarID() + "_#error#_";
		} else {
			return id.getVarID() + "_" + b.toString() + "_";
		}
	}

	@Override
	public String visit(VarDecl vd) {
		return printInc() + vd.getType().accept(this) + " " + vd.getId().accept(this) + ";";
	}

	@Override
	public String visit(ArgDeclaration ad) {
		return ad.getType().accept(this) + " " + ad.getId().accept(this);
	}

	@Override
	public String visit(MethodDecl md) {
		StringBuilder sb = new StringBuilder();
		sb.append(printInc() + " public " + md.getReturnType().accept(this));
		sb.append(" " + md.getMethodName().accept(this) + "(");

		for (int i = 0; i < md.getArgListSize(); i++) {
			ArgDeclaration ad = md.getArgDeclAt(i);
			sb.append(ad.accept(this));
			if (i != md.getArgListSize() - 1) {
				sb.append(", ");
			}
		}
		sb.append(") {\n");
		incLevel();

		for (int i = 0; i < md.getVarListSize(); i++) {
			VarDecl vd = md.getVarDeclAt(i);
			sb.append(vd.accept(this));
			sb.append("\n");
		}

		for (int i = 0; i < md.getStatListSize(); i++) {
			Statement st = md.getStatAt(i);
			sb.append(st.accept(this));
			sb.append("\n");
		}

		sb.append(printInc() + "return " + md.getReturnExpr().accept(this) + ";");
		sb.append("\n");
		decLevel();
		sb.append(printInc() + "}");
		return sb.toString();
	}

	@Override
	public String visit(MainClass mc) {
		StringBuilder sb = new StringBuilder();
		sb.append("class " + mc.getClassName().accept(this) + " {\n");

		incLevel();
		sb.append(printInc() + "public static void main(String[] " + mc.getStringArrayId().accept(this) + ") {\n");

		incLevel();
		sb.append(mc.getStat().accept(this));
		decLevel();

		sb.append("\n" + printInc() + "}");
		decLevel();

		sb.append("\n}");

		return sb.toString();
	}

	@Override
	public String visit(Program program) {
		StringBuilder sb = new StringBuilder();
		sb.append(program.mClass.accept(this));
		for (int i = 0; i < program.getClassListSize(); i++) {
			sb.append("\n");
			sb.append(program.getClassDeclAt(i).accept(this));
		}
		return sb.toString();
	}

	@Override
	public String visit(Identifier id) {
		Binding b = id.getB();
		if (b == null) {
			return id.getVarID() + "_#error#_";
		} else {
			return id.getVarID() + "_" + b.toString() + "_";
		}
	}

	@Override
	public String visit(IndexArray ia) {
		return ia.getArray().accept(this) + "[" + ia.getIndex().accept(this) + "]";
	}

	@Override
	public String visit(ArrayAssign aa) {
		return printInc() + aa.getIdentifier().accept(this) + "[" + aa.getE1().accept(this) + "] = "
				+ aa.getE2().accept(this);
	}

	@Override
	public String visit(StringLiteral stringLiteral) {
		return stringLiteral.getValue();
	}

	@Override
	public String visit(ClassDeclSimple cd) {
		StringBuilder sb = new StringBuilder();
		sb.append("class " + cd.getId().accept(this) + "{\n");
		incLevel();

		for (int i = 0; i < cd.getVarListSize(); i++) {
			VarDecl vd = cd.getVarDeclAt(i);
			sb.append(vd.accept(this));
			sb.append("\n");
		}

		for (int i = 0; i < cd.getMethodListSize(); i++) {
			MethodDecl md = cd.getMethodDeclAt(i);
			sb.append(md.accept(this));
			sb.append("\n");
		}
		decLevel();
		sb.append("\n");
		sb.append(printInc() + "}");
		return sb.toString();
	}

	@Override
	public String visit(ClassDeclExtends cd) {
		StringBuilder sb = new StringBuilder();
		sb.append("class " + cd.getId().accept(this) + " extends " + cd.getParent().accept(this) + "{\n");
		incLevel();

		for (int i = 0; i < cd.getVarListSize(); i++) {
			VarDecl vd = cd.getVarDeclAt(i);
			sb.append(vd.accept(this));
			sb.append("\n");
		}

		for (int i = 0; i < cd.getMethodListSize(); i++) {
			MethodDecl md = cd.getMethodDeclAt(i);
			sb.append(md.accept(this));
			sb.append("\n");
		}
		decLevel();
		sb.append("\n");
		sb.append(printInc() + "}");
		return sb.toString();
	}
}