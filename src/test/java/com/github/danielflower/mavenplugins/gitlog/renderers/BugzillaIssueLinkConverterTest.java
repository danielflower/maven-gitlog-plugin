package com.github.danielflower.mavenplugins.gitlog.renderers;

import static org.junit.Assert.assertEquals;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

public class BugzillaIssueLinkConverterTest {

	private static final String PREFIX = "https://bugzilla.mozilla.org/show_bug.cgi?id=";
	private BugzillaIssueLinkConverter converter = new BugzillaIssueLinkConverter(new SystemStreamLog(), PREFIX);

	@Test
	public void emptyMessagesAreUnchanged() {
		test("", "");
	}

	@Test
	public void noItemReferencesReturnsMessagesAreUnchanged() {
		String someMessage = "This is some #1 gh-2 conf-3 Bug- message Bugzilla-YO";
		test(someMessage, someMessage);
	}

	@Test
	public void bugzillaCodeAtBeginningIsRendered() {
		test("Bug 1123 Some commit message", "<a href=\"" + PREFIX + "1123\">Bug 1123</a> Some commit message");
	}

	@Test
	public void multipleBugzillaCodesAreRendered() {
		test("Bug 1123 Some Bug 11239 commit message Bug 11238",
				"<a href=\"" + PREFIX + "1123\">Bug 1123</a> Some <a href=\"" + PREFIX
						+ "11239\">Bug 11239</a> commit message " +
                                "<a href=\"" + PREFIX + "11238\">Bug 11238</a>");
	}

	@Test
	public void bugzillaCodesInOtherProjectsAreRendered() {
		test("Bug 1123 Some commit message related to Bug 11230",
                     "<a href=\"" + PREFIX + "1123\">Bug 1123</a> Some commit message related to " +
				"<a href=\""+PREFIX+"11230\">Bug 11230</a>");
	}

	@Test
	public void urlWithTrailingSlashHasItRemovedCorrectly() {
		BugzillaIssueLinkConverter converter = new BugzillaIssueLinkConverter(new SystemStreamLog(),
				PREFIX+"/");
		String actual = converter.formatCommitMessage("Bug 1123 Some commit message");
		assertEquals("<a href=\""+PREFIX+"1123\">Bug 1123</a> Some commit message", actual);
	}

	private void test(String input, String expectedOutput) {
		String actual = converter.formatCommitMessage(input);
		assertEquals("Input: " + input, expectedOutput, actual);
	}

}
