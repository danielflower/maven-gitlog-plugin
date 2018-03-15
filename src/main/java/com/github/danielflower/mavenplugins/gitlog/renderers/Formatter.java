package com.github.danielflower.mavenplugins.gitlog.renderers;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.lib.PersonIdent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formatter {

	public static String NEW_LINE = String.format("%n");

	private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

	private static boolean showCommiter = true;

	private static DateFormat dateFormat = new SimpleDateFormat(DEFAULT_FORMAT);

	public static String formatDateTime() {
		return formatDateTime(new Date());
	}

	public static String formatDateTime(int secondsSinceEpoch) {
		Date date = new Date(secondsSinceEpoch * 1000L);
		return formatDateTime(date);
	}

	public static String formatDateTime(Date date) {
		return dateFormat.format(date);
	}

	public static void setFormat(String format, Log log) {
		try {
			dateFormat = new SimpleDateFormat(format);
		} catch (NullPointerException npe) {
			log.warn(String.format("Date format should not be null, using default: '%s'", DEFAULT_FORMAT));
			dateFormat = new SimpleDateFormat(DEFAULT_FORMAT);
		} catch (IllegalArgumentException iae) {
			log.warn(String.format("Invalid date format '%s', using default: '%s'", format, DEFAULT_FORMAT));
			dateFormat = new SimpleDateFormat(DEFAULT_FORMAT);
		}
	}

	public static void setCommiter(boolean showCommiter, Log log) {
		log.info(String.format("Setting show commiter to '%b'", showCommiter));
		Formatter.showCommiter = showCommiter;
	}

	public static String formatCommiter(PersonIdent commiter) {
		if (Formatter.showCommiter) {
			return "(" + commiter.getName() + ")";
		} else {
			return "";
		}
	}

	public static boolean showCommiter(){
		return Formatter.showCommiter;
	}
}