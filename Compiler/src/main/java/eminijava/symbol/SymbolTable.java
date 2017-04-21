package eminijava.symbol;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;

import eminijava.ast.BooleanType;
import eminijava.ast.IdentifierType;
import eminijava.ast.IntArrayType;
import eminijava.ast.IntType;
import eminijava.ast.StringType;
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

	public boolean compareTypes(Type t1, Type t2) {

		if (t1 == null || t2 == null) {
			return false;
		}

		if (t1 instanceof IntType && t2 instanceof IntType) {
			return true;
		}
		if (t1 instanceof BooleanType && t2 instanceof BooleanType) {
			return true;
		}
		if (t1 instanceof StringType && t2 instanceof StringType) {
			return true;
		}
		if (t1 instanceof IntArrayType && t2 instanceof IntArrayType) {
			return true;
		}
		if (t1 instanceof IdentifierType && t2 instanceof IdentifierType) {
			IdentifierType i1 = (IdentifierType) t1;
			IdentifierType i2 = (IdentifierType) t2;

			Klass c = getKlass(i2.varID);
			while (c != null && !rstack.contains(c.id)) {
				rstack.push(c.id);
				if (i1.varID.equals(c.getId())) {
					rstack.clear();
					return true;
				} else {
					if (c.parent() == null) {
						rstack.clear();
						return false;
					}
					c = getKlass(c.parent());
				}
			}
			rstack.clear();
		}
		return false;
	}

	public boolean absCompTypes(Type t1, Type t2) {

		if (t1 == null || t2 == null) {
			return false;
		}

		if (t1 instanceof IntType && t2 instanceof IntType) {
			return true;
		}
		if (t1 instanceof BooleanType && t2 instanceof BooleanType) {
			return true;
		}
		if (t1 instanceof StringType && t2 instanceof StringType) {
			return true;
		}
		if (t1 instanceof IntArrayType && t2 instanceof IntArrayType) {
			return true;
		}
		if (t1 instanceof IdentifierType && t2 instanceof IdentifierType) {
			IdentifierType i1 = (IdentifierType) t1;
			IdentifierType i2 = (IdentifierType) t2;
			if (i1.varID.equals(i2.varID)) {
				return true;
			}
		}

		return false;
	}
}
