package com.github.danielflower.mavenplugins.gitlog.renderers;

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

	public SimpleHtmlRenderer(Log log, File targetFolder, String filename) throws IOException {
		super(log, targetFolder, filename);

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
				.append(tag.getTagName())
				.append("</td></tr>")
				.append(NEW_LINE);
	}

	@Override
	public void renderCommit(RevCommit commit) throws IOException {
		String date = Formatter.formatDateTime(commit.getCommitTime());
		String message = commit.getShortMessage();
		String author = commit.getCommitterIdent().getName();
		if (commit.getAuthorIdent() != null && !commit.getAuthorIdent().getName().equals(author)) {
			author += " and " + commit.getAuthorIdent().getName();
		}
		tableRows.append("\t\t<tr>")
				.append("<td>").append(date).append("</td>")
				.append("<td>").append(message).append("</td>")
				.append("<td>").append(author).append("</td>")
				.append("</tr>").append(NEW_LINE);
	}

	@Override
	public void renderFooter() throws IOException {
		String html = template
				.replace("{title}", title)
				.replace("{rows}", tableRows.toString());
		writer.append(html);
	}

	public String convertStreamToString(InputStream is) {
		return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
	}

}
