package com.github.danielflower.mavenplugins.gitlog;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

import java.util.Arrays;

public class GeneratorTest {


	// Not a unit test, but a way to manually observe the output during maven test phase
	@Test
	public void writeLogToStandardOutput() throws Exception {
		Log log = new SystemStreamLog();
		ChangeLogRenderer soutRender = new MavenLoggerRenderer(log);
		Generator generator = new Generator(Arrays.asList(soutRender), log);
		generator.openRepository();
		generator.generate();
	}

}
