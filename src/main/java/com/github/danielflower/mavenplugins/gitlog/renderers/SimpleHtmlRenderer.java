package com.github.danielflower.mavenplugins.gitlog.renderers;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.github.danielflower.mavenplugins.gitlog.renderers.Formatter.NEW_LINE;

public class SimpleHtmlRenderer extends FileRenderer {

	private String title;
	private StringBuilder tableRows = new StringBuilder();
	private String template;
	private final MessageConverter messageConverter;

	public SimpleHtmlRenderer(Log log, File targetFolder, String filename, MessageConverter messageConverter) throws IOException {
		super(log, targetFolder, filename);
		this.messageConverter = messageConverter;

		InputStream templateStream = getClass().getResourceAsStream("/html/SimpleHtmlTemplate.html");
		this.template = convertStreamToString(templateStream);
		templateStream.close();

	}

	@Override
	public void renderHeader(String reportTitle) throws IOException {
		this.title = reportTitle;
	}

	@Override
	public void renderTag(RevTag tag) throws IOException {
		tableRows.append("\t\t<tr class=\"tag\"><td colspan=3>")
				.append(htmlEncode(tag.getTagName()))
				.append("</td></tr>")
				.append(NEW_LINE);
	}

	private static String htmlEncode(String input) {
		return StringEscapeUtils.escapeHtml4(input);
	}

	@Override
	public void renderCommit(RevCommit commit) throws IOException {
		String date = Formatter.formatDateTime(commit.getCommitTime());
		String message = messageConverter.formatCommitMessage(htmlEncode(commit.getShortMessage()));

		String author = htmlEncode(commit.getCommitterIdent().getName());
		String committer = htmlEncode(commit.getCommitterIdent().getName());
		String authorHtml = "<span class=\"committer\">" + commit.getAuthorIdent().getName() + "</span>";
		if (!areSame(author, committer)) {
			authorHtml += "and <span class=\"author\">" + author + "</span>";
		}

		tableRows.append("\t\t<tr>")
				.append("<td class=\"date\">").append(date).append("</td>")
				.append("<td>").append(message).append("</td>")
				.append("<td>").append(authorHtml).append("</td>")
				.append("</tr>").append(NEW_LINE);
	}

	private boolean areSame(String author, String committer) {
		return ("" + author).toLowerCase().equals("" + committer.toLowerCase());
	}

	@Override
	public void renderFooter() throws IOException {
		String html = template
				.replace("{title}", htmlEncode(title))
				.replace("{rows}", tableRows.toString());
		writer.append(html);
	}

	public String convertStreamToString(InputStream is) {

		return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
	}

}
