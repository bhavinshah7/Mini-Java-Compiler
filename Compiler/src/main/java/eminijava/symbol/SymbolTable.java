package eminijava.symbol;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;

import eminijava.ast.Type;

public class SymbolTable {
	private Hashtable<String, Klass> hashtable;
	private Deque<String> rstack = new ArrayDeque<String>();

	public SymbolTable() {
		hashtable = new Hashtable<>();
	}

	public boolean addKlass(String id, String parent) {
		if (containsKlass(id)) {
			return false;
		} else {
			hashtable.put(id, new Klass(id, parent));
		}
		return true;
	}

	public Klass getKlass(String id) {
		if (containsKlass(id)) {
			return hashtable.get(id);
		} else {
			return null;
		}
	}

	public boolean containsKlass(String id) {
		if (id != null) {
			return hashtable.containsKey(id);
		}
		return false;
	}

	public Variable getVar(Method m, Klass c, String id) {
		if (m != null) {
			if (m.getVar(id) != null) {
				return m.getVar(id);
			}
			if (m.getParam(id) != null) {
				return m.getParam(id);
			}
		}

		while (c != null && !rstack.contains(c.id)) {
			rstack.push(c.id);
			if (c.getVar(id) != null) {
				rstack.clear();
				return c.getVar(id);
			} else {
				if (c.parent() == null) {
					c = null;
				} else {
					c = getKlass(c.parent());
				}
			}
		}
		rstack.clear();
		return null;
	}

	public boolean containsVar(Method m, Klass c, String id) {
		Variable var = getVar(m, c, id);
		if (var != null) {
			return true;
		}
		return false;
	}

	public Type getVarType(Method m, Klass c, String id) {
		Variable var = getVar(m, c, id);
		if (var != null) {
			return var.type();
		}
		return null;
	}

	public Method getMethod(String id, String classScope) {
		if (getKlass(classScope) == null) {
			return null;
		}

		Klass c = getKlass(classScope);
		while (c != null && !rstack.contains(c.id)) {
			rstack.push(c.id);
			if (c.getMethod(id) != null) {
				rstack.clear();
				return c.getMethod(id);
			} else {
				if (c.parent() == null) {
					c = null;
				} else {
					c = getKlass(c.parent());
				}
			}
		}
		rstack.clear();
		return null;
	}

	public Type getMethodType(String id, String classScope) {
		Method m = getMethod(id, classScope);
		if (m == null) {
			return null;
		} else {
			return m.type();
		}
	}
}
