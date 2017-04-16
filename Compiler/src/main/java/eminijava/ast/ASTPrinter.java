package eminijava.ast;

public class ASTPrinter implements Visitor<String> {

	int level = 0;

	void incLevel() {
		level = level + 1;
	}

	void decLevel() {
		level = level - 1;
	}

	public String printInc() {
		char[] chars = new char[level];
		java.util.Arrays.fill(chars, '\t');
		return new String(chars);
	}

	@Override
	public String visit(Print n) {
		return printInc() + "(PRINTLN " + n.expr.accept(this) + ")";
	}

	@Override
	public String visit(Assign n) {
		return printInc() + "(EQSIGN " + n.id.accept(this) + " " + n.expr.accept(this) + ")";
	}

	@Override
	public String visit(Skip n) {
		return "";
	}

	@Override
	public String visit(Block n) {
		StringBuilder strBuilder = new StringBuilder();
		for (Statement stat : n.body) {
			strBuilder.append(stat.accept(this) + "\n");
		}
		return strBuilder.toString();
	}

	@Override
	public String visit(IfThenElse n) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(printInc() + "(IF " + n.expr.accept(this) + "\n");

		incLevel();
		strBuilder.append(n.then.accept(this) + "\n");
		String elze = n.elze.accept(this);
		if (elze != null && elze.trim().length() > 0) {
			strBuilder.append(elze + "\n");
		}
		decLevel();

		strBuilder.append(printInc() + ")\n");
		return strBuilder.toString();
	}

	@Override
	public String visit(While n) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(printInc() + "(WHILE " + n.expr.accept(this) + "\n");

		incLevel();
		strBuilder.append(n.body.accept(this));
		decLevel();

		strBuilder.append(printInc() + ")\n");
		return strBuilder.toString();
	}

	@Override
	public String visit(IntLiteral n) {
		return "(INTLIT " + n.value + ")";
	}

	@Override
	public String visit(Plus n) {
		return "(PLUS " + n.lhs.accept(this) + " " + n.rhs.accept(this) + ")";
	}

	@Override
	public String visit(Minus n) {
		return "(- " + n.lhs.accept(this) + " " + n.rhs.accept(this) + ")";
	}

	@Override
	public String visit(Times n) {
		return "(* " + n.lhs.accept(this) + " " + n.rhs.accept(this) + ")";
	}

	@Override
	public String visit(Division n) {
		return "(DIV " + n.lhs.accept(this) + " " + n.rhs.accept(this) + ")";
	}

	@Override
	public String visit(Equals n) {
		return "(EQUALS " + n.lhs.accept(this) + " " + n.rhs.accept(this) + ")";
	}

	@Override
	public String visit(LessThan n) {
		return "(< " + n.lhs.accept(this) + " " + n.rhs.accept(this) + ")";
	}

	@Override
	public String visit(And n) {
		return "(&& " + n.lhs.accept(this) + " " + n.rhs.accept(this) + ")";
	}

	@Override
	public String visit(Or n) {
		return "(|| " + n.lhs.accept(this) + " " + n.rhs.accept(this) + ")";
	}

	@Override
	public String visit(Not n) {
		return "(! " + n.expr.accept(this) + ")";
	}

	@Override
	public String visit(True true1) {
		return "TRUE";
	}

	@Override
	public String visit(False false1) {
		return "FALSE";
	}

	@Override
	public String visit(IdentifierExpr identifier) {
		return "(ID " + identifier.varID + ")";
	}

	@Override
	public String visit(This this1) {
		return "THIS";
	}

	@Override
	public String visit(NewArray newArray) {
		return "(NEW-INT-ARRAY " + newArray.arrayLength.accept(this) + ")";
	}

	@Override
	public String visit(NewInstance newInstance) {
		return "(NEW-INSTANCE " + newInstance.className.accept(this) + ")";
	}

	@Override
	public String visit(CallMethod callMethod) {

		StringBuilder sb = new StringBuilder();
		sb.append("(DOT " + callMethod.instanceName.accept(this) + " (FUN-CALL " + callMethod.methodId.accept(this));
		for (Expression expr : callMethod.argExprList) {
			sb.append(expr.accept(this));
		}
		sb.append("))");

		return sb.toString();
	}

	@Override
	public String visit(Length length) {
		return "(DOT " + length.array.accept(this) + " LENGTH)";
	}

	@Override
	public String visit(Sidef sidef) {
		return "(SIDEF " + sidef.argument.accept(this) + ")";
	}

	@Override
	public String visit(IntType intType) {
		return "INT";
	}

	@Override
	public String visit(StringType stringType) {
		return "STRING";
	}

	@Override
	public String visit(BooleanType booleanType) {
		return "BOOLEAN";
	}

	@Override
	public String visit(IntArrayType intArrayType) {
		return "INT-ARRAY";
	}

	@Override
	public String visit(IdentifierType identifierType) {
		return "(ID " + identifierType.varID + ")";
	}

	@Override
	public String visit(VarDecl varDeclaration) {
		return printInc() + "(VAR-DECL " + varDeclaration.type.accept(this) + " " + varDeclaration.id.accept(this)
				+ ")";
	}

	@Override
	public String visit(ArgDeclaration argDecl) {
		return "(" + argDecl.type.accept(this) + " " + argDecl.id.accept(this) + ")";
	}

	@Override
	public String visit(MethodDecl methodDecl) {
		StringBuilder sb = new StringBuilder();
		sb.append(printInc() + "(MTD-DECL " + methodDecl.returnType.accept(this) + " "
				+ methodDecl.methodName.accept(this) + " ");

		sb.append("(TY-ID-LIST ");
		for (ArgDeclaration arg : methodDecl.argList) {
			sb.append(arg.accept(this));
		}
		sb.append(")\n");
		sb.append(printInc() + "(BLOCK\n");

		incLevel();
		for (VarDecl var : methodDecl.varList) {
			sb.append(var.accept(this) + "\n");
		}
		for (Statement stat : methodDecl.statList) {
			sb.append(stat.accept(this) + "\n");
		}
		sb.append(printInc() + "(RETURN " + methodDecl.returnExpr.accept(this) + ")\n");
		decLevel();

		sb.append(printInc() + ")\n");
		sb.append(printInc() + ")");
		return sb.toString();
	}

	@Override
	public String visit(MainClass mainClass) {
		StringBuilder sb = new StringBuilder();
		sb.append("(MAIN-CLASS-DECL " + mainClass.className.accept(this) + "\n");
		incLevel();
		sb.append(printInc() + "(MAIN-FUN-CALL (STRING-ARRAY " + mainClass.stringArrayId.accept(this) + ")\n");
		incLevel();
		sb.append(mainClass.stat.accept(this) + "\n");
		decLevel();
		sb.append(printInc() + ")\n");

		decLevel();
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String visit(Program program) {
		StringBuilder sb = new StringBuilder();
		sb.append(program.mClass.accept(this) + "\n");

		for (ClassDecl klass : program.klassList) {
			sb.append(klass.accept(this) + "\n");
		}

		return sb.toString();
	}

	@Override
	public String visit(Identifier identifier) {
		return "(ID " + identifier.varID + ")";
	}

	@Override
	public String visit(IndexArray indexArray) {
		return "(ARRAY-LOOKUP " + indexArray.array.accept(this) + indexArray.index.accept(this) + ")";
	}

	@Override
	public String visit(ArrayAssign arrayAssign) {
		return printInc() + "(EQSIGN " + "(ARRAY-ASSIGN " + arrayAssign.identifier.accept(this)
				+ arrayAssign.e1.accept(this) + ") " + arrayAssign.e2.accept(this) + ")";
	}

	@Override
	public String visit(StringLiteral stringLiteral) {
		return "(STRINGLIT " + stringLiteral.value + ")";
	}

	// @Override
	// public String visit(ClassDecl classDecl) {
	// StringBuilder sb = new StringBuilder();
	// sb.append("(CLASS-DECL " + classDecl.id.accept(this));
	// if (classDecl.parent != null) {
	// sb.append(" EXTENDS " + classDecl.parent.accept(this));
	// }
	// sb.append("\n");
	// incLevel();
	// for (VarDecl var : classDecl.varList) {
	// sb.append(var.accept(this) + "\n");
	// }
	// for (MethodDecl method : classDecl.methodList) { //
	// sb.append(method.accept(this) + "\n");
	// }
	// decLevel();
	// sb.append(")");
	// return sb.toString();
	// }

	@Override
	public String visit(ClassDeclSimple classDeclSimple) {
		StringBuilder sb = new StringBuilder();
		sb.append("(CLASS-DECL " + classDeclSimple.id.accept(this));
		sb.append("\n");
		incLevel();
		for (VarDecl var : classDeclSimple.varList) {
			sb.append(var.accept(this) + "\n");
		}
		for (MethodDecl method : classDeclSimple.methodList) { //
			sb.append(method.accept(this) + "\n");
		}
		decLevel();
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String visit(ClassDeclExtends classDeclExtends) {
		StringBuilder sb = new StringBuilder();
		sb.append("(CLASS-DECL " + classDeclExtends.id.accept(this));
		sb.append(" EXTENDS " + classDeclExtends.parent.accept(this));
		sb.append("\n");
		incLevel();
		for (VarDecl var : classDeclExtends.varList) {
			sb.append(var.accept(this) + "\n");
		}
		for (MethodDecl method : classDeclExtends.methodList) { //
			sb.append(method.accept(this) + "\n");
		}
		decLevel();
		sb.append(")");
		return sb.toString();
	}

}
