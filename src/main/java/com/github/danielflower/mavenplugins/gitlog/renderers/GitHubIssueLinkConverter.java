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
		this.pattern = Pattern.compile("(.*)(GH-[0-9]+|#[0-9]+)(.*)", Pattern.CASE_INSENSITIVE);
	}

	@Override
	public String formatCommitMessage(String original) {
		try {
			Matcher matcher = pattern.matcher(original);
			if (matcher.matches()) {
				String startText = matcher.group(1);
				String group = matcher.group(2);
				String endText = matcher.group(3);
				String number = getNumberFrom(group);
				String result = matcher.replaceAll("<a href=\"" + urlPrefix + number + "\">" + group + "</a>");
				return startText + result + endText;
			}
		} catch (Exception e) {
			// log, but don't let this small setback fail the build
			log.info("Unable to parse issue tracking URL in commit message: " + original, e);
		}
		return original;
	}

	private String getNumberFrom(String input) {
		return Pattern.compile("[^0-9]").matcher(input).replaceAll("");
	}
}
