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
import eminijava.ast.Tree;
import eminijava.ast.True;
import eminijava.ast.Type;
import eminijava.ast.VarDecl;
import eminijava.ast.Visitor;
import eminijava.ast.While;
import eminijava.parser.ParseException;

public class ConstantFolding implements Visitor<Expression> {

	private int changes;

	@Override
	public Expression visit(Print n) {
		n.setExpr(n.getExpr().accept(this));
		return null;
	}

	@Override
	public Expression visit(Assign n) {
		n.setExpr(n.getExpr().accept(this));
		return null;
	}

	@Override
	public Expression visit(Skip n) {
		return null;
	}

	@Override
	public Expression visit(Block n) {
		for (int i = 0; i < n.getStatListSize(); i++) {
			Statement st = n.getStatAt(i);
			st.accept(this);
		}
		return null;
	}

	@Override
	public Expression visit(IfThenElse n) {
		n.setExpr(n.getExpr().accept(this));
		n.then.accept(this);
		n.elze.accept(this);
		return null;
	}

	@Override
	public Expression visit(While n) {
		n.setExpr(n.getExpr().accept(this));
		n.body.accept(this);
		return null;
	}

	@Override
	public Expression visit(IntLiteral n) {
		return n;
	}

	@Override
	public Expression visit(Plus n) {

		n.setLhs(n.getLhs().accept(this));
		n.setRhs(n.getRhs().accept(this));

		if (n.type() instanceof IntType) {

			if (n.getLhs() instanceof IntLiteral && n.getRhs() instanceof IntLiteral) {
				IntLiteral l = (IntLiteral) n.getLhs();
				IntLiteral r = (IntLiteral) n.getRhs();
				int result = l.getValue() + r.getValue();
				incChanges();
				IntLiteral op = new IntLiteral(l.getSymbol(), result);
				op.setType(n.type());
				return op;
			}

		} else {// StringType

			if (n.getLhs() instanceof StringLiteral && n.getRhs() instanceof StringLiteral) {
				StringLiteral l = (StringLiteral) n.getLhs();
				StringLiteral r = (StringLiteral) n.getRhs();
				StringBuilder sb = new StringBuilder();
				sb.append(l.getValue());
				sb.append(r.getValue());
				incChanges();
				StringLiteral op = new StringLiteral(l.getSymbol(), sb.toString());
				op.setType(new StringType(l.getSymbol()));
				return op;

			} else if (n.getLhs() instanceof IntLiteral && n.getRhs() instanceof StringLiteral) {
				IntLiteral l = (IntLiteral) n.getLhs();
				StringLiteral r = (StringLiteral) n.getRhs();
				StringBuilder sb = new StringBuilder();
				sb.append(l.getValue());
				sb.append(r.getValue());
				incChanges();
				StringLiteral op = new StringLiteral(r.getSymbol(), sb.toString());
				op.setType(new StringType(r.getSymbol()));
				return op;

			} else if (n.getLhs() instanceof StringLiteral && n.getRhs() instanceof IntLiteral) {
				StringLiteral l = (StringLiteral) n.getLhs();
				IntLiteral r = (IntLiteral) n.getRhs();
				StringBuilder sb = new StringBuilder();
				sb.append(l.getValue());
				sb.append(r.getValue());
				incChanges();
				StringLiteral op = new StringLiteral(l.getSymbol(), sb.toString());
				op.setType(new StringType(l.getSymbol()));
				return op;
			}
		}
		return n;
	}

	@Override
	public Expression visit(Minus n) {

		n.setLhs(n.getLhs().accept(this));
		n.setRhs(n.getRhs().accept(this));

		if (n.getLhs() instanceof IntLiteral && n.getRhs() instanceof IntLiteral) {
			IntLiteral l = (IntLiteral) n.getLhs();
			IntLiteral r = (IntLiteral) n.getRhs();
			int result = l.getValue() - r.getValue();
			incChanges();
			IntLiteral op = new IntLiteral(l.getSymbol(), result);
			op.setType(new IntType(l.getSymbol()));
			return op;
		}
		return n;
	}

	@Override
	public Expression visit(Times n) {

		n.setLhs(n.getLhs().accept(this));
		n.setRhs(n.getRhs().accept(this));

		if (n.getLhs() instanceof IntLiteral && n.getRhs() instanceof IntLiteral) {
			IntLiteral l = (IntLiteral) n.getLhs();
			IntLiteral r = (IntLiteral) n.getRhs();
			int result = l.getValue() * r.getValue();
			incChanges();
			IntLiteral op = new IntLiteral(l.getSymbol(), result);
			op.setType(new IntType(l.getSymbol()));
			return op;
		}
		return n;
	}

	@Override
	public Expression visit(Division n) {

		n.setLhs(n.getLhs().accept(this));
		n.setRhs(n.getRhs().accept(this));

		if (n.getLhs() instanceof IntLiteral && n.getRhs() instanceof IntLiteral) {
			IntLiteral l = (IntLiteral) n.getLhs();
			IntLiteral r = (IntLiteral) n.getRhs();
			if (r.getValue() != 0) {
				int result = l.getValue() / r.getValue();
				incChanges();
				IntLiteral op = new IntLiteral(l.getSymbol(), result);
				op.setType(new IntType(l.getSymbol()));
				return op;
			}
		}

		return n;
	}

	@Override
	public Expression visit(Equals n) {

		n.setLhs(n.getLhs().accept(this));
		n.setRhs(n.getRhs().accept(this));

		Type t = n.type();
		if (t instanceof IntType) {
			if (n.getLhs() instanceof IntLiteral && n.getRhs() instanceof IntLiteral) {
				IntLiteral l = (IntLiteral) n.getLhs();
				IntLiteral r = (IntLiteral) n.getRhs();
				if (l.getValue() == r.getValue()) {
					incChanges();
					True op = new True(n.getSymbol());
					op.setType(new BooleanType(n.getSymbol()));
					return op;
				} else {
					incChanges();
					False op = new False(n.getSymbol());
					op.setType(new BooleanType(n.getSymbol()));
					return op;
				}
			}
		} else if (t instanceof BooleanType) {
			if ((n.getLhs() instanceof True && n.getRhs() instanceof True)
					|| (n.getLhs() instanceof False && n.getRhs() instanceof False)) {
				incChanges();
				True op = new True(n.getSymbol());
				op.setType(new BooleanType(n.getSymbol()));
				return op;

			} else if ((n.getLhs() instanceof True && n.getRhs() instanceof False)
					|| (n.getLhs() instanceof False && n.getRhs() instanceof True)) {
				incChanges();
				False op = new False(n.getSymbol());
				op.setType(new BooleanType(n.getSymbol()));
				return op;
			}
		} else if (t instanceof StringType) {
			if (n.getLhs() instanceof StringLiteral && n.getRhs() instanceof StringLiteral) {
				StringLiteral l = (StringLiteral) n.getLhs();
				StringLiteral r = (StringLiteral) n.getRhs();

				if (l.getValue() == null) {
					if (r.getValue() == null) {
						incChanges();
						True op = new True(n.getSymbol());
						op.setType(new BooleanType(n.getSymbol()));
						return op;
					}
				} else if (l.getValue().equals(r.getValue())) {
					incChanges();
					True op = new True(n.getSymbol());
					op.setType(new BooleanType(n.getSymbol()));
					return op;
				} else {
					incChanges();
					False op = new False(n.getSymbol());
					op.setType(new BooleanType(n.getSymbol()));
					return op;
				}
			}
		} else if (t instanceof IdentifierType) {
			if (n.getLhs() instanceof IdentifierExpr && n.getRhs() instanceof IdentifierExpr) {
				IdentifierExpr l = (IdentifierExpr) n.getLhs();
				IdentifierExpr r = (IdentifierExpr) n.getRhs();
				if (l.getVarID().equals(r.getVarID())) {
					incChanges();
					True op = new True(n.getSymbol());
					op.setType(new BooleanType(n.getSymbol()));
					return op;
				}
			}
		}

		return n;
	}

	@Override
	public Expression visit(LessThan n) {
		n.setLhs(n.getLhs().accept(this));
		n.setRhs(n.getRhs().accept(this));

		Type t = n.type();
		if (t instanceof IntType) {
			if (n.getLhs() instanceof IntLiteral && n.getRhs() instanceof IntLiteral) {
				IntLiteral l = (IntLiteral) n.getLhs();
				IntLiteral r = (IntLiteral) n.getRhs();
				if (l.getValue() < r.getValue()) {
					incChanges();
					True op = new True(n.getSymbol());
					op.setType(new BooleanType(n.getSymbol()));
					return op;
				} else {
					incChanges();
					False op = new False(n.getSymbol());
					op.setType(new BooleanType(n.getSymbol()));
					return op;
				}
			}
		}
		return n;
	}

	@Override
	public Expression visit(And n) {
		n.setLhs(n.getLhs().accept(this));
		n.setRhs(n.getRhs().accept(this));

		Type t = n.type();
		if (t instanceof BooleanType) {
			if ((n.getLhs() instanceof True) && (n.getRhs() instanceof True)) {
				incChanges();
				True op = new True(n.getSymbol());
				op.setType(new BooleanType(n.getSymbol()));
				return op;

			} else if ((n.getLhs() instanceof False) || (n.getRhs() instanceof False)) {
				incChanges();
				False op = new False(n.getSymbol());
				op.setType(new BooleanType(n.getSymbol()));
				return op;

			}
		}

		return n;
	}

	@Override
	public Expression visit(Or n) {
		n.setLhs(n.getLhs().accept(this));
		n.setRhs(n.getRhs().accept(this));

		Type t = n.type();
		if (t instanceof BooleanType) {
			if ((n.getLhs() instanceof True) || (n.getRhs() instanceof True)) {
				incChanges();
				True op = new True(n.getSymbol());
				op.setType(new BooleanType(n.getSymbol()));
				return op;

			} else if ((n.getLhs() instanceof False) && (n.getRhs() instanceof False)) {
				incChanges();
				False op = new False(n.getSymbol());
				op.setType(new BooleanType(n.getSymbol()));
				return op;

			}
		}

		return n;
	}

	@Override
	public Expression visit(Not n) {
		n.setExpr(n.getExpr().accept(this));

		Type t = n.type();
		if (t instanceof BooleanType) {
			if (n.getExpr() instanceof True) {
				incChanges();
				False op = new False(n.getSymbol());
				op.setType(t);
				return op;

			} else if (n.getExpr() instanceof False) {
				incChanges();
				True op = new True(n.getSymbol());
				op.setType(t);
				return op;

			}
		}

		return n;
	}

	@Override
	public Expression visit(True true1) {
		return true1;
	}

	@Override
	public Expression visit(False false1) {
		return false1;
	}

	@Override
	public Expression visit(IdentifierExpr identifier) {
		return identifier;
	}

	@Override
	public Expression visit(This this1) {
		return this1;
	}

	@Override
	public Expression visit(NewArray newArray) {
		return newArray;
	}

	@Override
	public Expression visit(NewInstance newInstance) {
		return newInstance;
	}

	@Override
	public Expression visit(CallMethod callMethod) {
		return callMethod;
	}

	@Override
	public Expression visit(Length length) {
		length.setArray(length.getArray().accept(this));
		return length;
	}

	@Override
	public Expression visit(Sidef sidef) {
		sidef.setArgument(sidef.getArgument().accept(this));
		return null;
	}

	@Override
	public Expression visit(IntType intType) {
		return null;
	}

	@Override
	public Expression visit(StringType stringType) {
		return null;
	}

	@Override
	public Expression visit(BooleanType booleanType) {
		return null;
	}

	@Override
	public Expression visit(IntArrayType intArrayType) {
		return null;
	}

	@Override
	public Expression visit(IdentifierType referenceType) {
		return null;
	}

	@Override
	public Expression visit(VarDecl varDeclaration) {
		return null;
	}

	@Override
	public Expression visit(ArgDecl argDeclaration) {
		return null;
	}

	@Override
	public Expression visit(MethodDecl md) {

		for (int i = 0; i < md.getStatListSize(); i++) {
			Statement st = md.getStatAt(i);
			st.accept(this);
		}

		md.setReturnExpr(md.getReturnExpr().accept(this));
		return null;
	}

	@Override
	public Expression visit(MainClass mc) {
		mc.getStat().accept(this);
		return null;
	}

	@Override
	public Expression visit(Program program) {
		program.mClass.accept(this);

		for (int i = 0; i < program.klassList.size(); i++) {
			program.klassList.get(i).accept(this);
		}
		return null;
	}

	@Override
	public Expression visit(Identifier id) {
		return null;
	}

	@Override
	public Expression visit(IndexArray ia) {
		ia.setArray(ia.getArray().accept(this));
		ia.setIndex(ia.getIndex().accept(this));
		return ia;
	}

	@Override
	public Expression visit(ArrayAssign aa) {
		aa.setE1(aa.getE1().accept(this));
		aa.setE2(aa.getE2().accept(this));
		return null;
	}

	@Override
	public Expression visit(StringLiteral stringLiteral) {
		return stringLiteral;
	}

	@Override
	public Expression visit(ClassDeclSimple cd) {

		for (int i = 0; i < cd.getMethodListSize(); i++) {
			MethodDecl md = cd.getMethodDeclAt(i);
			md.accept(this);
		}
		return null;
	}

	@Override
	public Expression visit(ClassDeclExtends cd) {
		for (int i = 0; i < cd.getMethodListSize(); i++) {
			MethodDecl md = cd.getMethodDeclAt(i);
			md.accept(this);
		}
		return null;
	}

	public void incChanges() {
		changes++;
	}

	public static void optimize(Tree prog) throws FileNotFoundException, ParseException {

		// int total = 0;
		ConstantFolding optimizer = new ConstantFolding();
		optimizer.changes = 0;
		prog.accept(optimizer);
		while (optimizer.changes > 0) {
			// total += optimizer.changes;
			optimizer.changes = 0;
			prog.accept(optimizer);
		}

		// System.out.println("Total optimization changes = " + total);
	}

}
