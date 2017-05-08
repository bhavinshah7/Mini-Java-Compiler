package eminijava.symbol;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import eminijava.ast.Type;
import eminijava.semantics.Binding;

public class Method extends Binding {

	String id;
	// Type type;
	Vector<Variable> params;
	Hashtable<String, Variable> vars;

	public Method(String id, Type type) {
		super(type);
		this.id = id;
		// this.type = type;
		vars = new Hashtable<>();
		params = new Vector<>();
	}

	public String getId() {
		return id;
	}

	@Override
	public Type type() {
		return type;
	}

	public boolean addParam(String id, Type type) {
		if (containsParam(id)) {
			return false;
		} else {
			params.addElement(new Variable(id, type));
			return true;
		}
	}

	public Enumeration<Variable> getParams() {
		return params.elements();
	}

	public Variable getParamAt(int i) {
		if (i < params.size()) {
			return params.elementAt(i);
		} else {
			return null;
		}
	}

	public boolean addVar(String id, Type type) {
		if (containsVar(id)) {
			return false;
		} else {
			vars.put(id, new Variable(id, type));
			return true;
		}
	}

	public boolean containsVar(String id) {
		return containsParam(id) || vars.containsKey(id);
	}

	public boolean containsParam(String id) {
		for (int i = 0; i < params.size(); i++) {
			if (params.elementAt(i).id.equals(id)) {
				return true;
			}
		}
		return false;
	}

	public Variable getVar(String id) {
		if (containsVar(id)) {
			return vars.get(id);
		} else {
			return null;
		}
	}

	public Variable getParam(String id) {

		for (int i = 0; i < params.size(); i++) {
			if (params.elementAt(i).id.equals(id)) {
				return (params.elementAt(i));
			}
		}

		return null;
	}

	public int getParamsSize() {
		return params.size();
	}

}
