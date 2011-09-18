package com.github.danielflower.mavenplugins.gitlog;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

// Not unit tests as such, but a way to manually observe the output during maven test phase
public class GeneratorTest {

	@Test
	public void writeLogToStandardOutput() throws Exception {
		Log log = new SystemStreamLog();
		ChangeLogRenderer renderer = new MavenLoggerRenderer(log);
		generateReport(log, renderer);
	}

	@Test
	public void writePlainTextLogToFile() throws Exception {
		Log log = new SystemStreamLog();
		ChangeLogRenderer renderer = new PlainTextRenderer(log, new File("target"), "changelog.txt");
		generateReport(log, renderer);
	}

	@Test
	public void writeSimpleHtmlLogToFile() throws Exception {
		Log log = new SystemStreamLog();
		ChangeLogRenderer renderer = new SimpleHtmlRenderer(log, new File("target"), "changelog.html");
		generateReport(log, renderer);
	}

	private void generateReport(Log log, ChangeLogRenderer renderer) throws IOException, NoGitRepositoryException {
		Generator generator = new Generator(Arrays.asList(renderer), Defaults.COMMIT_FILTERS, log);
		generator.openRepository();
		generator.generate("Maven GitLog Plugin changelog");
	}

}
