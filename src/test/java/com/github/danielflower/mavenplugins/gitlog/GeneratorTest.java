package com.github.danielflower.mavenplugins.gitlog;

import com.github.danielflower.mavenplugins.gitlog.filters.CommitFilter;
import com.github.danielflower.mavenplugins.gitlog.filters.RegexpFilter;
import com.github.danielflower.mavenplugins.gitlog.renderers.*;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Not unit tests as such, but a way to manually observe the output during maven test phase
public class GeneratorTest {
    private static final String THIS_PLUGIN_ISSUES = "https://github.com/danielflower/maven-gitlog-plugin/issues/";
    private static final String TARGET_DIR = "target";

    @Test
    public void writeLogToStandardOutput() throws Exception {
        Log log = new SystemStreamLog();
        ChangeLogRenderer renderer = new MavenLoggerRenderer(log);
        generateReport(log, renderer);
    }

    @Test
    public void writeLogToStandardOutputWithRegexpFilter() throws Exception {
        Log log = new SystemStreamLog();
        ChangeLogRenderer renderer = new MavenLoggerRenderer(log);

        String regexp = ".*\\b(without|Upgraded)\\b.*";

        List<CommitFilter> commitFilters = new ArrayList<CommitFilter>(Defaults.COMMIT_FILTERS);
        commitFilters.add(new RegexpFilter(regexp));
        generateReport(log, renderer, null, commitFilters);
    }

    @Test
    public void writePlainTextLogToFile() throws Exception {
        Log log = new SystemStreamLog();
        ChangeLogRenderer renderer = new PlainTextRenderer(log, new File(TARGET_DIR), "changelog.txt", false);
        generateReport(log, renderer);
    }

    @Test
    public void writePlainTextFullLogToFile() throws Exception {
        Log log = new SystemStreamLog();
        ChangeLogRenderer renderer = new PlainTextRenderer(log, new File(TARGET_DIR), "changelogFull.txt", true);
        generateReport(log, renderer);
    }

    @Ignore
    public void writePlainTextFullLogToFileCustomDir() throws Exception {
        Log log = new SystemStreamLog();
        ChangeLogRenderer renderer = new PlainTextRenderer(log, new File(TARGET_DIR), "changelogFullCustomDir.txt", true);
        generateReport(log, renderer, new File("/home/myUser/myRepo"));
    }

    @Test
    public void writeSimpleHtmlLogToFile() throws Exception {
        Log log = new SystemStreamLog();
        GitHubIssueLinkConverter messageConverter = new GitHubIssueLinkConverter(log, THIS_PLUGIN_ISSUES);
        ChangeLogRenderer renderer = new SimpleHtmlRenderer(log, new File(TARGET_DIR), "changelog.html", false, messageConverter, false);
        generateReport(log, renderer);
    }

    @Test
    public void writeSimpleHtmlFullLogToFile() throws Exception {
        Log log = new SystemStreamLog();
        GitHubIssueLinkConverter messageConverter = new GitHubIssueLinkConverter(log, THIS_PLUGIN_ISSUES);
        ChangeLogRenderer renderer = new SimpleHtmlRenderer(log, new File(TARGET_DIR), "changelogFullMessage.html", true, messageConverter, false);
        generateReport(log, renderer);
    }

    @Test
    public void writeHtmlTableOnlyLogToFile() throws Exception {
        Log log = new SystemStreamLog();
        GitHubIssueLinkConverter messageConverter = new GitHubIssueLinkConverter(log, THIS_PLUGIN_ISSUES);
        ChangeLogRenderer renderer = new SimpleHtmlRenderer(log, new File(TARGET_DIR), "changelogtable.html", false, messageConverter, true);
        generateReport(log, renderer);
    }

    @Test
    public void writeMarkdownLogToFile() throws Exception {
        Log log = new SystemStreamLog();
        GitHubIssueLinkConverter messageConverter = new GitHubIssueLinkConverter(log, THIS_PLUGIN_ISSUES);
        ChangeLogRenderer renderer = new MarkdownRenderer(log, new File(TARGET_DIR), "changelog.md", false, messageConverter);
        generateReport(log, renderer);
    }

	@Test
	public void writeAsciidocLogToFile() throws Exception {
		Log log = new SystemStreamLog();
		GitHubIssueLinkConverter messageConverter = new GitHubIssueLinkConverter(log, THIS_PLUGIN_ISSUES);
		ChangeLogRenderer renderer = new AsciidocRenderer(log, new File(TARGET_DIR), "changelog.adoc", false, messageConverter, null, false,null, null);
		generateReport(log, renderer);
	}
	@Test
	public void writeAsciidocLogToFileJiraAsTableView() throws Exception {
		Log log = new SystemStreamLog();
		JiraIssueLinkConverter messageConverter = new JiraIssueLinkConverter(log, THIS_PLUGIN_ISSUES);
		ChangeLogRenderer renderer = new AsciidocRenderer(log, new File(TARGET_DIR), "changelog-jira-asTableview.adoc", false, messageConverter, "===", true,"Date", "Merge");
		generateReport(log, renderer);
	}


	@Test
    public void writeJsonLogToFile() throws Exception {
        Log log = new SystemStreamLog();
        ChangeLogRenderer renderer = new JsonRenderer(log, new File(TARGET_DIR), "changelog.json", false);
        generateReport(log, renderer);
    }

    private void generateReport(Log log, ChangeLogRenderer renderer) throws IOException, NoGitRepositoryException {
        generateReport(log, renderer, null);
    }

    private void generateReport(Log log, ChangeLogRenderer renderer, File gitdir) throws IOException, NoGitRepositoryException {
        generateReport(log, renderer, null, Defaults.COMMIT_FILTERS);
    }

    private void generateReport(Log log, ChangeLogRenderer renderer, File gitdir, List<CommitFilter> filters) throws IOException, NoGitRepositoryException {
        Generator generator = new Generator(Arrays.asList(renderer), filters, log);
        generator.openRepository(gitdir);
        generator.generate("Maven GitLog Plugin changelog");
    }

}
