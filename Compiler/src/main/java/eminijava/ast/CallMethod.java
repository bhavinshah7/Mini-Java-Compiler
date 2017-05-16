package eminijava.ast;

import java.util.List;

import eminijava.lexer.JSymbol;
import eminijava.visitor.IBranchVisitor;

public class CallMethod extends Expression {

	Expression instanceName;
	IdentifierExpr methodId;
	List<Expression> argExprList;

	public CallMethod(JSymbol jSymbol, Expression instanceName, IdentifierExpr methodId, List<Expression> argExprList) {
		super(jSymbol);
		this.instanceName = instanceName;
		this.methodId = methodId;
		this.argExprList = argExprList;
	}

	public Expression getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(Expression instanceName) {
		this.instanceName = instanceName;
	}

	public IdentifierExpr getMethodId() {
		return methodId;
	}

	public void setMethodId(IdentifierExpr methodId) {
		this.methodId = methodId;
	}

	public int getArgExprListSize() {
		return argExprList.size();
	}

	public Expression getArgExprAt(int index) {
		if (index < argExprList.size()) {
			return argExprList.get(index);
		}
		return null;
	}

	@Override
	public <R> R accept(Visitor<R> v) {
		return v.visit(this);
	}

	@Override
	public <R> R accept(IBranchVisitor<R> v, String nTrue, String nFalse) {
		return v.visit(this, nTrue, nFalse);
	}

}
