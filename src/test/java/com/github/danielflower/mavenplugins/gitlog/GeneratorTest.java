package com.github.danielflower.mavenplugins.gitlog;

import org.junit.Test;

import java.util.Arrays;

public class GeneratorTest {

	// Not a unit test, but a way to manually observe the output during maven test phase
	@Test
	public void writeLogToStandardOutput() throws Exception {
		ChangeLogRenderer soutRender = new StandardOutputRenderer(System.out);
		Generator generator = new Generator(Arrays.asList(soutRender));
		generator.openRepository();
		generator.generate();
	}

}
