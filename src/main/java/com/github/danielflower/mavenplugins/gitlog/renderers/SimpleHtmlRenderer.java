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
	private String template;
	protected StringBuilder tableHtml = new StringBuilder();
	protected final MessageConverter messageConverter;
	private final boolean tableOnly;
	private final boolean fullGitMessage;

	public SimpleHtmlRenderer(Log log, File targetFolder, String filename, boolean fullGitMessage, MessageConverter messageConverter, boolean tableOnly) throws IOException {
		super(log, targetFolder, filename);
		this.messageConverter = messageConverter;
		this.tableOnly = tableOnly;
		this.fullGitMessage = fullGitMessage;

		if (!tableOnly) {
			InputStream templateStream = getClass().getResourceAsStream("/html/SimpleHtmlTemplate.html");
			this.template = convertStreamToString(templateStream);
			templateStream.close();
		}

	}

	protected static String htmlEncode(String input) {
		input = StringEscapeUtils.escapeHtml4(input);
		return input.replaceAll("\n", "<br/>");
	}

	@Override
	public void renderHeader(String reportTitle) throws IOException {
		this.title = reportTitle;
		tableHtml.append("\t<table class=\"changelog\">")
				.append(NEW_LINE)
				.append("\t\t<tbody>")
				.append(NEW_LINE);
	}

	@Override
	public void renderTag(RevTag tag) throws IOException {
		tableHtml.append("\t\t<tr class=\"tag\"><td colspan=3>")
				.append(SimpleHtmlRenderer.htmlEncode(tag.getTagName()))
				.append("</td></tr>")
				.append(NEW_LINE);
	}

	@Override
	public void renderCommit(RevCommit commit) throws IOException {
		String date = Formatter.formatDateTime(commit.getCommitTime());
		String message = null;
		if (fullGitMessage){
			message = messageConverter.formatCommitMessage(SimpleHtmlRenderer.htmlEncode(commit.getFullMessage()));
		} else {
			message = messageConverter.formatCommitMessage(SimpleHtmlRenderer.htmlEncode(commit.getShortMessage()));	
		}

		String author = SimpleHtmlRenderer.htmlEncode(commit.getCommitterIdent().getName());
		String committer = SimpleHtmlRenderer.htmlEncode(commit.getCommitterIdent().getName());
		String authorHtml = "<span class=\"committer\">" + commit.getAuthorIdent().getName() + "</span>";
		if (!areSame(author, committer)) {
			authorHtml += "and <span class=\"author\">" + author + "</span>";
		}

		tableHtml.append("\t\t<tr>")
				.append("<td class=\"date\">").append(date).append("</td>")
				.append("<td>").append(message).append("</td>")
				.append("<td>").append(authorHtml).append("</td>")
				.append("</tr>").append(NEW_LINE);
	}

	@Override
	public void renderFooter() throws IOException {
		tableHtml.append("\t\t</tbody>")
				.append(NEW_LINE)
				.append("\t</table>")
				.append(NEW_LINE);

		if (tableOnly) {
			writer.append(tableHtml.toString());
		} else {
			String html = template
					.replace("{title}", htmlEncode(title))
					.replace("{table}", tableHtml.toString());
			writer.append(html);
		}
	}

	private boolean areSame(String author, String committer) {
		return ("" + author).toLowerCase().equals("" + committer.toLowerCase());
	}

	private String convertStreamToString(InputStream is) {
		return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
	}
}
