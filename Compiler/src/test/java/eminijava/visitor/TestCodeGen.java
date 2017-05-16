package eminijava.visitor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class TestCodeGen {

	private static String getPath(String filename) {
		return "src/test/resources/eminijava/benchmarks/" + filename;
	}

	private static String getExpJasminPath(String filename) {
		return "src/test/resources/eminijava/jasmin/expected/" + filename;
	}

	private static String getJasminPath(String filename) {
		return "src/test/resources/eminijava/jasmin/" + filename;
	}

	@Test
	public void testJasminCode() throws Exception {
		CodeGenerator.generateCode(new File(getPath("QuickSort.emj")), getJasminPath(""));
		final String actual = new String(Files.readAllBytes(Paths.get(getJasminPath("QuickSort.j"))),
				StandardCharsets.UTF_8);
		final String expected = new String(Files.readAllBytes(Paths.get(getExpJasminPath("QuickSort.j"))),
				StandardCharsets.UTF_8);
		Assert.assertEquals(expected, actual);

		final String actual2 = new String(Files.readAllBytes(Paths.get(getJasminPath("QS.j"))), StandardCharsets.UTF_8);
		final String expected2 = new String(Files.readAllBytes(Paths.get(getExpJasminPath("QS.j"))),
				StandardCharsets.UTF_8);
		Assert.assertEquals(expected2, actual2);
	}

	@Test
	public void testProgramOutput() throws Exception {
		//
		// testJasminCode();
		// final String actual = execCode(getJasminPath(""), "QuickSort");
		// final String expected = new
		// String(Files.readAllBytes(Paths.get(getExpJasminPath("QS.j"))),
		// StandardCharsets.UTF_8);
		// Assert.assertEquals(expected2, actual2);
	}

	private String execCode(String path, String main) {
		// try {
		// String[] args = new String[3];
		// args[0] = "-d";
		// args[1] = f.getParent();
		// // System.out.println(f.getParent());
		// args[2] = f.getPath();
		// // System.out.println(f.getPath());
		// Main.main(args);
		//
		// } catch (Exception e) {
		// System.err.println(e.getMessage());
		// }
		return null;
	}

}
