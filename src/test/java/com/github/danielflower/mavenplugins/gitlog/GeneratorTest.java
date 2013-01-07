package com.github.danielflower.mavenplugins.gitlog;

import com.github.danielflower.mavenplugins.gitlog.renderers.*;
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
		ChangeLogRenderer renderer = new PlainTextRenderer(log, new File("target"), "changelog.txt", false);
		generateReport(log, renderer);
	}

	@Test
	public void writePlainTextFullLogToFile() throws Exception {
		Log log = new SystemStreamLog();
		ChangeLogRenderer renderer = new PlainTextRenderer(log, new File("target"), "changelogFull.txt", true);
		generateReport(log, renderer);
	}

	@Test
	public void writeSimpleHtmlLogToFile() throws Exception {
		Log log = new SystemStreamLog();
		GitHubIssueLinkConverter messageConverter = new GitHubIssueLinkConverter(log, "https://github.com/danielflower/maven-gitlog-plugin/issues/");
		ChangeLogRenderer renderer = new SimpleHtmlRenderer(log, new File("target"), "changelog.html", false, messageConverter, false);
		generateReport(log, renderer);
	}

	@Test
	public void writeHtmlTableOnlyLogToFile() throws Exception {
		Log log = new SystemStreamLog();
		GitHubIssueLinkConverter messageConverter = new GitHubIssueLinkConverter(log, "https://github.com/danielflower/maven-gitlog-plugin/issues/");
		ChangeLogRenderer renderer = new SimpleHtmlRenderer(log, new File("target"), "changelogtable.html", false, messageConverter, true);
		generateReport(log, renderer);
	}

	@Test
	public void writeMarkdownLogToFile() throws Exception {
		Log log = new SystemStreamLog();
		GitHubIssueLinkConverter messageConverter = new GitHubIssueLinkConverter(log, "https://github.com/danielflower/maven-gitlog-plugin/issues/");
		ChangeLogRenderer renderer = new MarkdownRenderer(log, new File("target"), "changelog.md", false, messageConverter);
		generateReport(log, renderer);
	}

	private void generateReport(Log log, ChangeLogRenderer renderer) throws IOException, NoGitRepositoryException {
		Generator generator = new Generator(Arrays.asList(renderer), Defaults.COMMIT_FILTERS, log);
		generator.openRepository();
		generator.generate("Maven GitLog Plugin changelog");
	}

}
