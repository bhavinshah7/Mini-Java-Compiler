package eminijava.semantics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SemanticErrors {

	public static List<NameError> errorList = new ArrayList<>();

	public static void addError(int line, int col, String errorText) {
		NameError error = new NameError(line, col, errorText);
		errorList.add(error);
	}

	public static void sort() {
		Collections.sort(errorList);
	}

}
