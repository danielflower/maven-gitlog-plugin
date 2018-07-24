package com.github.danielflower.mavenplugins.gitlog.renderers;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import java.io.File;
import java.io.IOException;

import static com.github.danielflower.mavenplugins.gitlog.renderers.Formatter.NEW_LINE;

public class SimpleHtmlRenderer extends FileRenderer {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private String title;
	private String template;
	protected StringBuilder tableHtml = new StringBuilder();
	protected final MessageConverter messageConverter;
	private final boolean tableOnly;
	private final boolean fullGitMessage;

	public SimpleHtmlRenderer(Log log, File targetFolder, String filename, boolean fullGitMessage, MessageConverter messageConverter, boolean tableOnly) throws IOException {
		super(log, targetFolder, filename, false);
		this.messageConverter = messageConverter;
		this.tableOnly = tableOnly;
		this.fullGitMessage = fullGitMessage;

		if (!tableOnly) {
			this.template = loadResourceToString("/html/SimpleHtmlTemplate.html");
		}

	}

	protected static String htmlEncode(String input) {
		input = StringEscapeUtils.escapeHtml4(input);
		return input.replaceAll(System.getProperty("line.separator"), "<br/>");
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
		tableHtml.append("\t\t<tr class=\"tag\"><td colspan=\"3\">")
				.append(SimpleHtmlRenderer.htmlEncode(tag.getTagName()))
				.append("</td></tr>")
				.append(NEW_LINE);
	}

	@Override
	public void renderCommit(RevCommit commit) throws IOException {
		String date = Formatter.formatDateTime(commit.getCommitTime());
		String message = null;
		if (fullGitMessage){
			message = messageConverter.formatCommitMessage(formatLongMessage(commit.getFullMessage()));
		} else {
			message = messageConverter.formatCommitMessage(htmlEncode(commit.getShortMessage()));
		}

		String author = SimpleHtmlRenderer.htmlEncode(commit.getAuthorIdent().getName());
		String committer = SimpleHtmlRenderer.htmlEncode(commit.getCommitterIdent().getName());
		String authorHtml = "<span class=\"committer\">" + Formatter.formatCommiter(commit.getCommitterIdent()) + "</span>";
		if (!areSame(author, committer)) {
			authorHtml = "<span class=\"author\">" + author + "</span> by " + authorHtml;
		}

		tableHtml.append("\t\t<tr>")
				.append("<td class=\"date\">").append(date).append("</td>")
				.append("<td>").append(authorHtml).append("</td>")
				.append("<td>").append(message).append("</td>")
				.append("</tr>").append(NEW_LINE);
	}

	private String formatLongMessage(String gitMessage) {
		String lines[] = StringEscapeUtils.escapeHtml4(gitMessage).split(LINE_SEPARATOR);
		if (lines.length <=1) {
			return gitMessage;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			if (i == 0) {
				builder.append(lines[i]).append("</br><span class='commitDetails'>");
			} else {
				builder.append(lines[i]).append("</br>");
			}
		}
		return builder.append("</span>").toString();
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
}
