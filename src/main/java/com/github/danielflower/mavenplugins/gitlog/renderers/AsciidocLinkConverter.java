package com.github.danielflower.mavenplugins.gitlog.renderers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

/**
 * Helper class to convert a HTML link into an Asciidoc link.
 * 
 * It searches for a <a href="...URL...">...Text...</a> and the
 * URL and text is used to build afterwards the Asciidoc link.
 * The Asciidoc links goes like ...URL...[...Text...].
 *
 */
public class AsciidocLinkConverter {

	private Pattern pattern;
	private final Log log;

	public AsciidocLinkConverter(Log log) {
		this.log = log;
		// see http://stackoverflow.com/questions/26323/regex-to-parse-hyperlinks-and-descriptions
		// limited to double quotes (removed the single quote),
		// because the Converters will use double quotes.
		this.pattern = Pattern.compile("<a\\s+href=(\"([^\"]+)\").*?>(.*?)</a>");
	}
	
	public String formatCommitMessage(String original) {
		try {
			Matcher matcher = pattern.matcher(original);
			String result = matcher.replaceAll("$2[$3]");
			return result;
		} catch (Exception e) {
			// log, but don't let this small setback fail the build
			log.info("Unable to convert a HTML link into asciidoc link: " + original, e);
		}
		return original;
	}
	
}
