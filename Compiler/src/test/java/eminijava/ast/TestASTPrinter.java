package eminijava.ast;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class TestASTPrinter {

	private static String getPath(String filename) {
		return "src/test/resources/eminijava/ast/" + filename;
	}

	@Test
	public void testParseFile() throws Exception {
		final String actual = ASTPrinter.printFileAst(new File(getPath("InputAstPrinter.emj")));
		final String expected = new String(Files.readAllBytes(Paths.get(getPath("expectedInputAstPrinter.ast"))),
				StandardCharsets.UTF_8);
		Assert.assertEquals(expected, actual);
	}

}
