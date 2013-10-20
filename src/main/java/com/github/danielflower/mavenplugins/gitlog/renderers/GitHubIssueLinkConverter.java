package com.github.danielflower.mavenplugins.gitlog.renderers;

import org.apache.maven.plugin.logging.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubIssueLinkConverter implements MessageConverter {

	private Pattern pattern;
	private final Log log;
	private final String urlPrefix;

	public GitHubIssueLinkConverter(Log log, String urlPrefix) {
		this.log = log;
		this.urlPrefix = urlPrefix.endsWith("/") ? urlPrefix : urlPrefix + "/";
		this.pattern = Pattern.compile("(GH-|#)([0-9]+)", Pattern.CASE_INSENSITIVE);
	}

	@Override
	public String formatCommitMessage(String original) {
		try {
			Matcher matcher = pattern.matcher(original);
			String result = matcher.replaceAll("<a href=\"" + urlPrefix + "$2\">$0</a>");
			return result;
		} catch (Exception e) {
			// log, but don't let this small setback fail the build
			log.info("Unable to parse issue tracking URL in commit message: " + original, e);
		}
		return original;
	}
}
