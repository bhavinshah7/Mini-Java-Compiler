package eminijava.visitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

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
import eminijava.ast.Tree;
import eminijava.ast.True;
import eminijava.ast.Type;
import eminijava.ast.VarDecl;
import eminijava.ast.Visitor;
import eminijava.ast.While;
import eminijava.lexer.Lexer;
import eminijava.parser.ParseException;
import eminijava.parser.Parser;
import eminijava.semantics.Binding;
import eminijava.symbol.Klass;
import eminijava.symbol.Method;
import eminijava.symbol.SymbolTable;
import eminijava.symbol.Variable;
import jasmin.Main;

public class CodeGenerator2 implements Visitor<String> {

	private Klass currClass;
	private Method currMethod;
	private int slot;
	private int labelCount;
	private final String PATH;
	private int bytecode;
	BranchGenerator bg;
	public static final String LABEL = "Label";

	public CodeGenerator2(String progPath) {
		PATH = progPath;
	}

	public void setBG(BranchGenerator branchG) {
		bg = branchG;
	}

	@Override
	public String visit(Print n) {
		StringBuilder sb = new StringBuilder();
		sb.append("getstatic java/lang/System/out Ljava/io/PrintStream;" + "\n");
		sb.append(n.getExpr().accept(this) + "\n");
		sb.append("invokevirtual java/io/PrintStream/println(");
		sb.append(n.getExpr().type().accept(this));
		sb.append(")V" + "\n");

		bytecode += 2;
		return sb.toString();
	}

	@Override
	public String visit(Assign n) {
		StringBuilder sb = new StringBuilder();

		Binding b = n.getId().getB();
		int lvIndex = getLocalVarIndex(b);
		if (lvIndex == -1) { // Class Variable
			sb.append("aload_0" + "\n");
			sb.append(n.getExpr().accept(this) + "\n");
			sb.append(
					"putfield " + currClass.getId() + "/" + n.getId().getVarID() + " " + b.type().accept(this) + "\n");

			bytecode += 2;
		} else { // Local Variable
			sb.append(n.getExpr().accept(this) + "\n");
			Type t = n.getExpr().type();
			if (t instanceof IntType || t instanceof BooleanType) {
				sb.append("istore " + lvIndex + "\n");
			} else {
				sb.append("astore " + lvIndex + "\n");
			}

			bytecode += 1;
		}
		return sb.toString();
	}

	@Override
	public String visit(Skip n) {
		return "";
	}

	@Override
	public String visit(Block n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n.getStatListSize(); i++) {
			Statement stat = n.getStatAt(i);
			sb.append(stat.accept(this) + "\n");
		}
		return sb.toString();
	}

	@Override
	public String visit(IfThenElse n) {

		StringBuilder sb = new StringBuilder();
		String nTrue = LABEL + getNextLabel();
		String nFalse = LABEL + getNextLabel();
		String nAfter = LABEL + getNextLabel();

		sb.append(n.getExpr().accept(bg, nTrue, nFalse));

		sb.append(nTrue + ":" + "\n");
		sb.append(n.getThen().accept(this) + "\n");
		sb.append("goto " + nAfter + "\n");

		sb.append(nFalse + ":" + "\n");
		sb.append(n.getElze().accept(this) + "\n");
		sb.append(nAfter + ":" + "\n");

		bytecode += 4;
		return sb.toString();
	}

	@Override
	public String visit(While n) {

		StringBuilder sb = new StringBuilder();

		String nTest = LABEL + getNextLabel();
		String nBody = LABEL + getNextLabel();
		String nExit = LABEL + getNextLabel();

		sb.append(nTest + ":" + "\n");
		sb.append(n.getExpr().accept(bg, nBody, nExit) + "\n");

		sb.append(nBody + ":" + "\n");
		sb.append(n.getBody().accept(this) + "\n");
		sb.append("goto " + nTest + "\n");

		sb.append(nExit + ":" + "\n");

		bytecode += 4;
		return sb.toString();
	}

	@Override
	public String visit(IntLiteral n) {
		bytecode += 1;
		return "ldc " + n.getValue() + "\n";
	}

	@Override
	public String visit(Plus n) {

		StringBuilder sb = new StringBuilder();
		if (n.type() != null && n.type() instanceof IntType) {
			sb.append(n.getLhs().accept(this) + "\n");
			sb.append(n.getRhs().accept(this) + "\n");
			sb.append("iadd" + "\n");

			bytecode += 1;
		} else {

			/*
			 * Concatenation is done using java.lang.StringBuilder. The
			 * procedure consists in building a new instance of a StringBuilder,
			 * then appending to it whatever you want to concatenate together,
			 * then calling toString() on the builder. The append method is
			 * overloaded for strings and integers, and you're not asked to be
			 * able to concatenate any other type.
			 */
			sb.append("new java/lang/StringBuilder" + "\n");
			sb.append("dup" + "\n");

			// 1
			sb.append("invokespecial java/lang/StringBuilder/<init>()V" + "\n");

			// 2
			sb.append(n.getLhs().accept(this) + "\n");
			sb.append("invokevirtual java/lang/StringBuilder/append(");
			sb.append(n.getLhs().type().accept(this));
			sb.append(")Ljava/lang/StringBuilder;" + "\n");

			// 3
			sb.append(n.getRhs().accept(this) + "\n");
			sb.append("invokevirtual java/lang/StringBuilder/append(");
			sb.append(n.getRhs().type().accept(this));
			sb.append(")Ljava/lang/StringBuilder;" + "\n");

			// 4
			sb.append("invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;");

			bytecode += 6;
		}
		return sb.toString();
	}

	@Override
	public String visit(Minus n) {
		StringBuilder sb = new StringBuilder();
		sb.append(n.getLhs().accept(this) + "\n");
		sb.append(n.getRhs().accept(this) + "\n");
		sb.append("isub" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(Times n) {
		StringBuilder sb = new StringBuilder();
		sb.append(n.getLhs().accept(this) + "\n");
		sb.append(n.getRhs().accept(this) + "\n");
		sb.append("imul" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(Division n) {
		StringBuilder sb = new StringBuilder();
		sb.append(n.getLhs().accept(this) + "\n");
		sb.append(n.getRhs().accept(this) + "\n");
		sb.append("idiv" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(Equals n) {

		Type t = n.type();
		String nEquals = LABEL + getNextLabel();
		String nAfter = LABEL + getNextLabel();

		StringBuilder sb = new StringBuilder();
		sb.append(n.getLhs().accept(this) + "\n");
		sb.append(n.getRhs().accept(this) + "\n");

		if (t instanceof IntType || t instanceof BooleanType) {
			sb.append("if_icmpeq " + nEquals + "\n");
			sb.append("iconst_0" + "\n");
			sb.append("goto " + nAfter + "\n");

			sb.append(nEquals + ":" + "\n");
			sb.append("iconst_1" + "\n");

			sb.append(nAfter + ":" + "\n");

			bytecode += 6;
		} else if (t instanceof StringType || t instanceof IntArrayType || t instanceof IdentifierType) {
			sb.append("if_acmpeq " + nEquals + "\n");
			sb.append("iconst_0" + "\n");
			sb.append("goto " + nAfter + "\n");

			sb.append(nEquals + ":" + "\n");
			sb.append("iconst_1" + "\n");

			sb.append(nAfter + ":" + "\n");

			bytecode += 6;
		}

		return sb.toString();
	}

	@Override
	public String visit(LessThan n) {
		StringBuilder sb = new StringBuilder();
		String lessThan = LABEL + getNextLabel();
		String after = LABEL + getNextLabel();
		sb.append(n.getLhs().accept(this) + "\n");
		sb.append(n.getRhs().accept(this) + "\n");

		sb.append("if_icmplt " + lessThan + "\n");
		sb.append("iconst_0" + "\n");
		sb.append("goto " + after + "\n");

		sb.append(lessThan + ":" + "\n");
		sb.append("iconst_1" + "\n");

		sb.append(after + ":" + "\n");

		bytecode += 6;
		return sb.toString();
	}

	@Override
	public String visit(And n) {
		StringBuilder sb = new StringBuilder();
		String nElze = LABEL + getNextLabel();
		String nAfter = LABEL + getNextLabel();

		sb.append(n.getLhs().accept(this) + "\n");
		sb.append("ifeq " + nElze + "\n");

		sb.append(n.getRhs().accept(this) + "\n");
		sb.append("goto " + nAfter + "\n");

		sb.append(nElze + ":" + "\n");
		sb.append("iconst_0" + "\n");

		sb.append(nAfter + ":" + "\n");

		bytecode += 5;
		return sb.toString();
	}

	@Override
	public String visit(Or n) {
		StringBuilder sb = new StringBuilder();
		String nElze = LABEL + getNextLabel();
		String nAfter = LABEL + getNextLabel();

		sb.append(n.getLhs().accept(this) + "\n");
		sb.append("ifeq " + nElze + "\n");

		sb.append("iconst_1" + "\n");
		sb.append("goto " + nAfter + "\n");

		sb.append(nElze + ":" + "\n");
		sb.append(n.getRhs().accept(this) + "\n");
		sb.append(nAfter + ":" + "\n");

		bytecode += 5;
		return sb.toString();
	}

	@Override
	public String visit(Not n) {
		StringBuilder sb = new StringBuilder();
		String nElze = LABEL + getNextLabel();
		String nAfter = LABEL + getNextLabel();

		sb.append(n.getExpr().accept(this) + "\n");
		sb.append("ifeq " + nElze + "\n");

		sb.append("iconst_0" + "\n");
		sb.append("goto " + nAfter + "\n");

		sb.append(nElze + ":" + "\n");
		sb.append("iconst_1" + "\n");

		sb.append(nAfter + ":" + "\n");

		bytecode += 6;
		return sb.toString();
	}

	@Override
	public String visit(True true1) {
		bytecode += 1;
		return "iconst_1";
	}

	@Override
	public String visit(False false1) {
		bytecode += 1;
		return "iconst_0";
	}

	@Override
	public String visit(IdentifierExpr id) {

		StringBuilder sb = new StringBuilder();

		Binding b = id.getB();
		int lvIndex = getLocalVarIndex(b);

		if (lvIndex == -1) { // Not a local variable
			sb.append("aload_0" + "\n");
			sb.append("getfield " + currClass.getId() + "/" + id.getVarID() + " " + b.type().accept(this) + "\n");

			bytecode += 2;
		} else {
			if (b.type() instanceof IntType || b.type() instanceof BooleanType) {
				sb.append("iload " + lvIndex + "\n");
			} else {
				sb.append("aload " + lvIndex + "\n");
			}

			bytecode += 1;
		}

		return sb.toString();
	}

	@Override
	public String visit(This this1) {
		bytecode += 1;
		return "aload_0";
	}

	@Override
	public String visit(NewArray na) {
		StringBuilder sb = new StringBuilder();
		sb.append(na.getArrayLength().accept(this) + "\n");
		sb.append("newarray int" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(NewInstance ni) {
		StringBuilder sb = new StringBuilder();
		sb.append("new " + ni.getClassName() + "\n");
		sb.append("dup" + "\n");
		sb.append("invokespecial " + ni.getClassName() + "/<init>()V" + "\n");

		bytecode += 3;
		return sb.toString();
	}

	@Override
	public String visit(CallMethod cm) {
		StringBuilder sb = new StringBuilder();
		sb.append(cm.getInstanceName().accept(this) + "\n");
		Type refT = cm.getInstanceName().type();

		for (int i = 0; i < cm.getArgExprListSize(); i++) {
			sb.append(cm.getArgExprAt(i).accept(this) + "\n");
		}
		sb.append("invokevirtual " + refT.toString() + "/" + cm.getMethodId() + "(");
		Method m = (Method) cm.getMethodId().getB();

		for (int i = 0; i < m.getParamsSize(); i++) {
			sb.append(m.getParamAt(i).type().accept(this));
		}
		sb.append(")" + m.type().accept(this));

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(Length length) {
		StringBuilder sb = new StringBuilder();
		sb.append(length.getArray().accept(this) + "\n");
		sb.append("arraylength" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(Sidef sidef) {
		StringBuilder sb = new StringBuilder();
		sb.append(sidef.getArgument().accept(this) + "\n");
		sb.append("pop");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(IntType intType) {
		return "I";
	}

	@Override
	public String visit(StringType stringType) {
		return "Ljava/lang/String;";
	}

	@Override
	public String visit(BooleanType booleanType) {
		return "Z";
	}

	@Override
	public String visit(IntArrayType intArrayType) {
		return "[I";
	}

	@Override
	public String visit(IdentifierType refT) {
		return "L" + refT.getVarID() + ";";
	}

	@Override
	public String visit(VarDecl vd) {

		if (currMethod != null) {
			Binding b = vd.getId().getB();
			setLocalVarIndex(b);
			return "";
		} else {
			bytecode += 1;
			return ".field public " + vd.getId() + " " + vd.getType().accept(this);
		}
	}

	@Override
	public String visit(ArgDecl ad) {
		Binding b = ad.getId().getB();
		setLocalVarIndex(b);
		return ad.getType().accept(this);
	}

	@Override
	public String visit(MethodDecl md) {
		slot = 0;
		int localvars = 1;
		localvars += md.getArgListSize();
		localvars += md.getVarListSize();

		StringBuilder sb = new StringBuilder();
		currMethod = (Method) md.getMethodName().getB();

		// sb.append(";" + "\n");
		// sb.append("; method " + currMethod.getId() + " declaration" + "\n");
		// sb.append(";" + "\n");
		sb.append(".method public " + currMethod.getId() + "(");

		for (int i = 0; i < md.getArgListSize(); i++) {
			ArgDecl ad = md.getArgDeclAt(i);
			sb.append(ad.accept(this));
		}
		sb.append(")");
		sb.append(currMethod.type().accept(this) + "\n");

		sb.append(".limit locals " + localvars * 4 + "\n");
		sb.append(".limit stack 100" + "\n");

		for (int i = 0; i < md.getVarListSize(); i++) {
			VarDecl vd = md.getVarDeclAt(i);
			sb.append(vd.accept(this));
		}

		for (int i = 0; i < md.getStatListSize(); i++) {
			sb.append(md.getStatAt(i).accept(this) + "\n");
		}

		sb.append(md.getReturnExpr().accept(this) + "\n");
		if (currMethod.type() instanceof IntType || currMethod.type() instanceof BooleanType) {
			sb.append("ireturn" + "\n");
		} else {
			sb.append("areturn" + "\n");
		}
		sb.append(".end method" + "\n");

		currMethod = null;

		bytecode += 5;
		return sb.toString();

	}

	@Override
	public String visit(MainClass mc) {

		StringBuilder sb = new StringBuilder();
		labelCount = 0;

		currClass = (Klass) mc.getClassName().getB();
		sb.append(".class public " + currClass.getId() + "\n");
		sb.append(".super java/lang/Object" + "\n");

		// sb.append(";" + "\n");
		// sb.append("; standard initializer (calls java.lang.Object's
		// initializer)" + "\n");
		// sb.append(";" + "\n");
		sb.append(".method public <init>()V" + "\n");
		sb.append("aload_0" + "\n");
		sb.append("invokenonvirtual java/lang/Object/<init>()V" + "\n");
		sb.append("return" + "\n");
		sb.append(".end method" + "\n");

		// sb.append(";" + "\n");
		// sb.append("; main()" + "\n");
		// sb.append(";" + "\n");

		sb.append(".method public static main([Ljava/lang/String;)V" + "\n");
		sb.append(".limit locals 10" + "\n");
		sb.append(".limit stack 10" + "\n");
		sb.append(mc.getStat().accept(this) + "\n");
		sb.append("return" + "\n");
		sb.append(".end method" + "\n");

		bytecode += 12;
		return sb.toString();
	}

	@Override
	public String visit(Program program) {

		String code = program.mClass.accept(this);
		File f = write(currClass.getId(), code);
		// System.out.println(code);
		execJasmin(f);

		for (int i = 0; i < program.klassList.size(); i++) {
			code = program.klassList.get(i).accept(this);
			f = write(currClass.getId(), code);
			// System.out.println(code);
			execJasmin(f);
		}
		return null;
	}

	private File write(String name, String code) {
		try {
			File f = new File(PATH + name + ".j");
			PrintWriter writer = new PrintWriter(f, "UTF-8");
			writer.println(code);
			writer.close();
			return f;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return null;
	}

	private void execJasmin(File f) {
		if (f != null) {
			try {
				String[] args = new String[3];
				args[0] = "-d";
				args[1] = f.getParent();
				// System.out.println(f.getParent());
				args[2] = f.getPath();
				// System.out.println(f.getPath());
				Main.main(args);

			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}

	@Override
	public String visit(Identifier id) {
		Binding b = id.getB();
		int lvIndex = getLocalVarIndex(b);
		StringBuilder sb = new StringBuilder();
		if (lvIndex == -1) {

			sb.append("aload_0" + "\n");
			sb.append("getfield " + currClass.getId() + "/" + id.getVarID() + " " + b.type().accept(this) + "\n");

			bytecode += 2;
		} else {
			if (b.type() instanceof IntType || b.type() instanceof BooleanType) {
				sb.append("iload " + lvIndex + "\n");
			} else {
				sb.append("aload " + lvIndex + "\n");
			}

			bytecode += 1;
		}
		return sb.toString();
	}

	@Override
	public String visit(IndexArray ia) {
		StringBuilder sb = new StringBuilder();
		sb.append(ia.getArray().accept(this) + "\n");
		sb.append(ia.getIndex().accept(this) + "\n");
		sb.append("iaload" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(ArrayAssign aa) {
		StringBuilder sb = new StringBuilder();
		// sb.append("; array assignment" + "\n");

		sb.append(aa.getIdentifier().accept(this) + "\n");
		sb.append(aa.getE1().accept(this) + "\n");
		sb.append(aa.getE2().accept(this) + "\n");
		sb.append("iastore" + "\n");

		bytecode += 1;
		return sb.toString();
	}

	@Override
	public String visit(StringLiteral stringLiteral) {
		bytecode += 1;
		return "ldc " + "\"" + stringLiteral.getValue() + "\"" + "\n";
	}

	@Override
	public String visit(ClassDeclSimple cd) {

		StringBuilder sb = new StringBuilder();
		labelCount = 0;

		Binding b = cd.getId().getB();
		currClass = (Klass) b;
		sb.append(".class public " + currClass.getId() + "\n");
		sb.append(".super java/lang/Object" + "\n\n");

		for (int i = 0; i < cd.getVarListSize(); i++) {
			VarDecl vd = cd.getVarDeclAt(i);
			sb.append(vd.accept(this) + "\n");
		}

		// sb.append(";" + "\n");
		// sb.append("; standard initializer (calls java.lang.Object's
		// initializer)" + "\n");
		// sb.append(";" + "\n");
		sb.append(".method public <init>()V" + "\n");
		sb.append("aload_0" + "\n");
		sb.append("invokenonvirtual java/lang/Object/<init>()V" + "\n");
		sb.append("return" + "\n");
		sb.append(".end method" + "\n");

		for (int i = 0; i < cd.getMethodListSize(); i++) {
			MethodDecl md = cd.getMethodDeclAt(i);
			sb.append(md.accept(this) + "\n");
		}

		bytecode += 7;
		return sb.toString();
	}

	@Override
	public String visit(ClassDeclExtends cd) {

		StringBuilder sb = new StringBuilder();
		labelCount = 0;

		Binding b = cd.getId().getB();
		currClass = (Klass) b;
		sb.append(".class public " + currClass.getId() + "\n");
		sb.append(".super " + currClass.parent() + "\n\n");

		for (int i = 0; i < cd.getVarListSize(); i++) {
			VarDecl vd = cd.getVarDeclAt(i);
			sb.append(vd.accept(this) + "\n");
		}

		// sb.append(";" + "\n");
		// sb.append("; standard initializer (calls " + currClass.parent() + "'s
		// initializer)" + "\n");
		// sb.append(";" + "\n");
		sb.append(".method public <init>()V" + "\n");
		sb.append("aload_0" + "\n");
		sb.append("invokenonvirtual " + currClass.parent() + "/<init>()V" + "\n");
		sb.append("return" + "\n");
		sb.append(".end method" + "\n");

		for (int i = 0; i < cd.getMethodListSize(); i++) {
			MethodDecl md = cd.getMethodDeclAt(i);
			sb.append(md.accept(this) + "\n");
		}

		bytecode += 7;
		return sb.toString();
	}

	private int getLocalVarIndex(Binding b) {
		if (b != null && b instanceof Variable) {
			return ((Variable) b).getLvIndex();
		}
		return -1;
	}

	private int setLocalVarIndex(Binding b) {
		if (b != null && b instanceof Variable) {
			((Variable) b).setLvIndex(++slot);
			return slot;
		}
		return -1;
	}

	private int getNextLabel() {
		return ++labelCount;
	}

	public int getLen() {
		return bytecode;
	}

	public static void generateCode(File file, String dirPath) throws FileNotFoundException, ParseException {
		Lexer lexer = new Lexer(new FileReader(file));
		Parser p = new Parser(lexer);
		Tree tree = p.parse();
		BuildSymbolTableVisitor b = new BuildSymbolTableVisitor();
		b.visit((Program) tree);
		SymbolTable st = b.getSymTab();
		NameAnalyserTreeVisitor natv = new NameAnalyserTreeVisitor(st);
		natv.visit((Program) tree);
		TypeAnalyser ta = new TypeAnalyser(st);
		ta.visit((Program) tree);
		CodeGenerator2 cg = new CodeGenerator2(dirPath);
		cg.visit((Program) tree);

	}
}
