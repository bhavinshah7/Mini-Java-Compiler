package eminijava.symbol;

import java.util.HashMap;
import java.util.Map;

public class Symbol {

	private String name;
	private static Map<String, Symbol> hmSymbol = new HashMap<>();

	private Symbol(String n) {
		name = n;
	}

	public static Symbol getSymbol(String n) {
		String u = n.intern();
		Symbol s = hmSymbol.get(u);
		if (s == null) {
			s = new Symbol(u);
			hmSymbol.put(u, s);
		}
		return s;
	}

	@Override
	public String toString() {
		return name;
	}

}
