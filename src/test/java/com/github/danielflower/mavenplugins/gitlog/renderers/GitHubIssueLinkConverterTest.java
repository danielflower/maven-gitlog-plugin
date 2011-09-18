package com.github.danielflower.mavenplugins.gitlog.renderers;

import junit.framework.Assert;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

public class GitHubIssueLinkConverterTest {

	private static final String PREFIX = "https://github.com/danielflower/maven-gitlog-plugin/issues/";
	private GitHubIssueLinkConverter converter = new GitHubIssueLinkConverter(new SystemStreamLog(), PREFIX);

	@Test
	public void emptyMessagesAreUnchanged() {
		test("", "");
	}

	@Test
	public void noItemReferencesReturnsMessagesAreUnchanged() {
		test("This is some # GH- message", "This is some # GH- message");
	}

	@Test
	public void hashNumberAtBeginningIsRendered() {
		test("#10 Some commit message", "<a href=\"" + PREFIX + "10\">#10</a> Some commit message");
	}

	@Test
	public void capitalGHHyphenNumberAtBeginningIsRendered() {
		test("GH-10 Some commit message", "<a href=\"" + PREFIX + "10\">GH-10</a> Some commit message");
	}

	@Test
	public void lowercaseGHHyphenNumberAtBeginningIsRendered() {
		test("gh-10 Some commit message", "<a href=\"" + PREFIX + "10\">gh-10</a> Some commit message");
	}

	@Test
	public void hashNumberAtEndIsRendered() {
		test("Some commit message. Closes #10", "Some commit message. Closes <a href=\"" + PREFIX + "10\">#10</a>");
	}

	@Test
	public void capitalGHHyphenNumberAtEndIsRendered() {
		test("Some commit message. Closes GH-10", "Some commit message. Closes <a href=\"" + PREFIX + "10\">GH-10</a>");
	}

	@Test
	public void lowercaseGHHyphenNumberAtEndIsRendered() {
		test("Some commit message. Closes gh-10", "Some commit message. Closes <a href=\"" + PREFIX + "10\">gh-10</a>");
	}

	@Test
	public void hashNumberInMiddleIsRendered() {
		test("Some commit message. Closes #10 it's true",
				"Some commit message. Closes <a href=\"" + PREFIX + "10\">#10</a> it's true");
	}

	@Test
	public void capitalGHHyphenNumberInMiddleIsRendered() {
		test("Some commit message. Closes GH-10 it's true",
				"Some commit message. Closes <a href=\"" + PREFIX + "10\">GH-10</a> it's true");
	}

	@Test
	public void lowercaseGHHyphenNumberInMiddleIsRendered() {
		test("Some commit message. Closes gh-10 it's true",
				"Some commit message. Closes <a href=\"" + PREFIX + "10\">gh-10</a> it's true");
	}

	@Test
	public void multipleIssueNumbersAreAllRendered() {
		test("#1 Some commit message. Closes gh-10 it's true, also gh-11 and GH-13",
				"<a href=\"https://github.com/danielflower/maven-gitlog-plugin/issues/1\">#1</a> Some commit message. Closes <a href=\"" + PREFIX + "10\">gh-10</a> it's true, also <a href=\""
						+ PREFIX + "11\">gh-11</a> and <a href=\"" + PREFIX + "13\">GH-13</a>");
	}

	@Test
	public void urlMissingTrailingSlashHasItAppendedCorrectly() {
		GitHubIssueLinkConverter converter = new GitHubIssueLinkConverter(new SystemStreamLog(),
				"https://github.com/danielflower/maven-gitlog-plugin/issues");
		String actual = converter.formatCommitMessage("#10 Some commit message");
		Assert.assertEquals("<a href=\"https://github.com/danielflower/maven-gitlog-plugin/issues/10\">#10</a> Some commit message", actual);
	}

	private void test(String input, String expectedOutput) {
		String actual = converter.formatCommitMessage(input);
		Assert.assertEquals("Input: " + input, expectedOutput, actual);
	}

}
