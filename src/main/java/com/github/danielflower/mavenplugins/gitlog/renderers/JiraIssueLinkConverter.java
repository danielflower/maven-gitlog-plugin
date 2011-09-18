package com.github.danielflower.mavenplugins.gitlog.renderers;

import org.apache.maven.plugin.logging.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraIssueLinkConverter implements MessageConverter {

	private Pattern pattern;
	private final Log log;
	private final String urlPrefix;

	public JiraIssueLinkConverter(Log log, String urlPrefix) {
		this.log = log;
		// strip off trailing slash
		urlPrefix = urlPrefix.endsWith("/") ? urlPrefix.substring(0, urlPrefix.length() - 2) : urlPrefix;
		// strip off jira project code
		this.urlPrefix = urlPrefix.substring(0, urlPrefix.lastIndexOf("/") + 1);
		this.pattern = Pattern.compile("[A-Z]+-[0-9]+");
	}

	@Override
	public String formatCommitMessage(String original) {
		try {
			Matcher matcher = pattern.matcher(original);
				String result = matcher.replaceAll("<a href=\"" + urlPrefix + "$0\">$0</a>");
				return result;
		} catch (Exception e) {
			// log, but don't let this small setback fail the build
			log.info("Unable to parse issue tracking URL in commit message: " + original, e);
		}
		return original;
	}
}
