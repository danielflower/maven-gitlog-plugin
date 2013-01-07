package com.github.danielflower.mavenplugins.gitlog.renderers;

import junit.framework.Assert;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

public class MarkdownLinkConverterTest {

	private MarkdownLinkConverter converter = new MarkdownLinkConverter(null);

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
	public void HyperlinkAtBeginningIsRendered() {
		test("<a href=\"URL\">Text</a> and some message", "[Text](URL) and some message");
	}

	@Test
	public void multipleHyperlinksAreRendered() {
		test("First a link <a href=\"URL1\">Text1</a> a scond one <a href=\"URL2\">Text2</a> and the end.", 
			 "First a link [Text1](URL1) a scond one [Text2](URL2) and the end.");
	}

	private void test(String input, String expectedOutput) {
		String actual = converter.formatCommitMessage(input);
		Assert.assertEquals("Input: " + input, expectedOutput, actual);
	}

}
