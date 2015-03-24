package com.github.danielflower.mavenplugins.gitlog.renderers;

import org.apache.maven.plugin.logging.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * bugzilla link converter
 * ex.:
 *
 * Bug 1123 Some commit message related to Bug 11230
 * ->
 * <a href="https://bugzilla.mozilla.org/show_bug.cgi?id=1123">Bug 1123</a> Some commit message related
 * to <a href="https://bugzilla.mozilla.org/show_bug.cgi?id=11230">Bug 11230</a>
                                *
 * https://bugzilla.mozilla.org/show_bug.cgi?id=<number>
 *
 * @author hrotkogabor
 */
public class BugzillaIssueLinkConverter implements MessageConverter {

	private Pattern pattern;
	private final Log log;
	private final String urlPrefix;
	private final String urlSufix ="/show_bug.cgi?id=";

	public BugzillaIssueLinkConverter(Log log, String urlPrefix, String bugzillaPattern) {
		this.log = log;
		// strip off trailing slash
		urlPrefix = urlPrefix.endsWith("/") ? urlPrefix.substring(0, urlPrefix.length() - 1) : urlPrefix;
		// strip off jira project code
		this.urlPrefix = !urlPrefix.endsWith(urlSufix) ? (urlPrefix + urlSufix) : urlPrefix;
		this.pattern = Pattern.compile(bugzillaPattern, Pattern.CASE_INSENSITIVE);
	}

	@Override
	public String formatCommitMessage(String original) {
		try {
			Matcher matcher = pattern.matcher(original);
				String result = matcher.replaceAll("<a href=\"" + urlPrefix + "$1\">Bug $1</a>");
				return result;
		} catch (Exception e) {
			// log, but don't let this small setback fail the build
			log.info("Unable to parse issue tracking URL in commit message: " + original, e);
		}
		return original;
	}
}
