package eminijava.visitor;

import org.junit.Test;

public class TestConstantFolding {

	private static String getDirPath() {
		return "src/test/resources/eminijava/ast/";
	}

	private static String getPath(String filename) {
		return "src/test/resources/eminijava/ast/" + filename;
	}

	@Test
	public void testCodeGeneration() throws Exception {

		// Lexer lexer = new Lexer(new FileReader(new
		// File(getPath("InputAstPrinter.emj"))));
		// Parser p = new Parser(lexer);
		// Tree tree = p.parse();
		// BuildSymbolTableVisitor bstv = new BuildSymbolTableVisitor();
		// bstv.visit((Program) tree);
		//
		// SymbolTable st = bstv.getSymTab();
		// NameAnalyserTreeVisitor natv = new NameAnalyserTreeVisitor(st);
		// natv.visit((Program) tree);
		//
		// TypeAnalyser ta = new TypeAnalyser(st);
		// ta.visit((Program) tree);
		//
		// Assert.assertEquals(SemanticErrors.errorList.size(), 0);
		//
		// CodeGenerator cg = new CodeGenerator(getDirPath());
		// cg.visit((Program) tree);
		//
		// final String actual = ConstantFoldingVisitor.optimize(tree);
		// final String expected = new
		// String(Files.readAllBytes(Paths.get(getPath("expectedInputAstPrinter.ast"))),
		// StandardCharsets.UTF_8);
		// Assert.assertEquals(expected, actual);
	}

}
