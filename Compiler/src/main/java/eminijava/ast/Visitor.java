package eminijava.ast;

public interface Visitor<R> {
	// Statement
	// public R visit(Statement statement);

	public R visit(Print n);

	public R visit(Assign n);

	public R visit(Skip n);

	public R visit(Block n);

	public R visit(IfThenElse n);

	public R visit(While n);

	// Expression

	public R visit(IntLiteral n);

	public R visit(Plus n);

	public R visit(Minus n);

	public R visit(Times n);

	public R visit(Division n);

	public R visit(Equals n);

	public R visit(LessThan n);

	public R visit(And n);

	public R visit(Or n);

	public R visit(Not n);

	public R visit(True true1);

	public R visit(False false1);

	public R visit(IdentifierExpr identifier);

	public R visit(This this1);

	public R visit(NewArray newArray);

	public R visit(NewInstance newInstance);

	public R visit(CallMethod callMethod);

	public R visit(Length length);

	public R visit(Sidef sidef);

	public R visit(IntType intType);

	public R visit(StringType stringType);

	public R visit(BooleanType booleanType);

	public R visit(IntArrayType intArrayType);

	public R visit(IdentifierType referenceType);

	public R visit(VarDecl varDeclaration);

	public R visit(ArgDeclaration argDeclaration);

	public R visit(MethodDecl methodDeclaration);

	public R visit(MainClass mainClass);

	public R visit(Program program);

	public R visit(Identifier identifier);

	public R visit(IndexArray indexArray);

	public R visit(ArrayAssign arrayAssign);

	public R visit(StringLiteral stringLiteral);

	public R visit(ClassDeclSimple classDeclSimple);

	public R visit(ClassDeclExtends classDeclExtends);

}