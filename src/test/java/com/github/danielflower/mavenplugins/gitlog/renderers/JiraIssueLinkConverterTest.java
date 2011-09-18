package com.github.danielflower.mavenplugins.gitlog.renderers;

import junit.framework.Assert;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

public class JiraIssueLinkConverterTest {

	private static final String PREFIX = "https://jira.atlassian.com/browse/CONF";
	private JiraIssueLinkConverter converter = new JiraIssueLinkConverter(new SystemStreamLog(), PREFIX);

	@Test
	public void emptyMessagesAreUnchanged() {
		test("", "");
	}

	@Test
	public void noItemReferencesReturnsMessagesAreUnchanged() {
		String someMessage = "This is some #1 gh-2 conf-3 CONF- message JIRA-YO";
		test(someMessage, someMessage);
	}

	@Test
	public void jiraCodeAtBeginningIsRendered() {
		test("CONF-10 Some commit message", "<a href=\"" + PREFIX + "-10\">CONF-10</a> Some commit message");
	}

	@Test
	public void multipleJiraCodesAreRendered() {
		test("CONF-10 Some CONF-12 commit message CONF-13",
				"<a href=\"" + PREFIX + "-10\">CONF-10</a> Some CONF-12 commit message CONF-13");
	}

	@Test
	public void lowerCaseJiraCodesAreIgnored() {
		test("conf-10 Some commit message", "conf-10 Some commit message");
	}


	@Test
	public void urlWithTrailingSlashHasItRemovedCorrectly() {
		JiraIssueLinkConverter converter = new JiraIssueLinkConverter(new SystemStreamLog(),
				"https://jira.atlassian.com/browse/CONF/");
		String actual = converter.formatCommitMessage("CONF-10 Some commit message");
		Assert.assertEquals("<a href=\"https://jira.atlassian.com/browse/CONF-10\">CONF-10</a> Some commit message", actual);
	}

	private void test(String input, String expectedOutput) {
		String actual = converter.formatCommitMessage(input);
		Assert.assertEquals("Input: " + input, expectedOutput, actual);
	}

}
