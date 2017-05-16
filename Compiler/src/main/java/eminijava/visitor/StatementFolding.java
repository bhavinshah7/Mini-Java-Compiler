package eminijava.visitor;

import java.io.FileNotFoundException;

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
import eminijava.ast.VarDecl;
import eminijava.ast.Visitor;
import eminijava.ast.While;
import eminijava.parser.ParseException;

public class StatementFolding implements Visitor<Statement> {

	@Override
	public Statement visit(Print n) {
		return n;
	}

	@Override
	public Statement visit(Assign n) {
		return n;
	}

	@Override
	public Statement visit(Skip n) {
		return n;
	}

	@Override
	public Statement visit(Block n) {
		for (int i = 0; i < n.getStatListSize(); i++) {
			Statement st = n.getStatAt(i);
			n.setStatAt(i, st.accept(this));
		}
		return n;
	}

	@Override
	public Statement visit(IfThenElse n) {

		if (n.getExpr() instanceof True) {
			return n.then;
		} else if (n.getExpr() instanceof False) {
			return n.elze;
		}

		return n;
	}

	@Override
	public Statement visit(While n) {
		if (n.getExpr() instanceof False) {
			return new Skip(n.getSymbol());
		}

		return n;
	}

	@Override
	public Statement visit(IntLiteral n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(Plus n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(Minus n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(Times n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(Division n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(Equals n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(LessThan n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(And n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(Or n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(Not n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(True true1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(False false1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(IdentifierExpr identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(This this1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(NewArray newArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(NewInstance newInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(CallMethod callMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(Length length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(Sidef sidef) {
		return sidef;
	}

	@Override
	public Statement visit(IntType intType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(StringType stringType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(BooleanType booleanType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(IntArrayType intArrayType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(IdentifierType referenceType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(VarDecl varDeclaration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(ArgDecl argDeclaration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(MethodDecl md) {
		for (int i = 0; i < md.getStatListSize(); i++) {
			Statement st = md.getStatAt(i);
			md.setStatAt(i, st.accept(this));
		}
		return null;
	}

	@Override
	public Statement visit(MainClass mc) {
		mc.setStat(mc.getStat().accept(this));
		return null;
	}

	@Override
	public Statement visit(Program program) {
		program.mClass.accept(this);

		for (int i = 0; i < program.klassList.size(); i++) {
			program.klassList.get(i).accept(this);
		}
		return null;
	}

	@Override
	public Statement visit(Identifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(IndexArray indexArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(ArrayAssign arrayAssign) {
		return arrayAssign;
	}

	@Override
	public Statement visit(StringLiteral stringLiteral) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement visit(ClassDeclSimple cd) {
		for (int i = 0; i < cd.getMethodListSize(); i++) {
			MethodDecl md = cd.getMethodDeclAt(i);
			md.accept(this);
		}
		return null;
	}

	@Override
	public Statement visit(ClassDeclExtends cd) {
		for (int i = 0; i < cd.getMethodListSize(); i++) {
			MethodDecl md = cd.getMethodDeclAt(i);
			md.accept(this);
		}
		return null;
	}

	public static void optimize(Tree prog) throws FileNotFoundException, ParseException {
		ConstantFolding optimizer = new ConstantFolding();
		prog.accept(optimizer);
	}

}
