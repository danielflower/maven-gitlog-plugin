package com.github.danielflower.mavenplugins.gitlog.renderers;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import com.github.danielflower.mavenplugins.gitlog.ConfigProperties;

import java.io.File;
import java.io.IOException;

import static com.github.danielflower.mavenplugins.gitlog.renderers.Formatter.NEW_LINE;

public class FullHtmlRenderer extends FileRenderer {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private String title;
	private String template;
	protected StringBuilder tableHtml = new StringBuilder();
	protected final MessageConverter messageConverter;
	private final boolean tableOnly;
	private final boolean fullGitMessage;

	public FullHtmlRenderer(Log log, File targetFolder, String filename, boolean fullGitMessage, MessageConverter messageConverter, boolean tableOnly) throws IOException {
		super(log, targetFolder, filename);
		this.messageConverter = messageConverter;
		this.tableOnly = tableOnly;
		this.fullGitMessage = fullGitMessage;

		if (!tableOnly) {
			FileUtils.copyURLToFile(getClass().getResource("/html/css/gitlog.css"), new File(targetFolder, "/css/gitlog.css"));
			this.template = loadResourceToString("/html/FullHtmlTemplate.html");
		}

	}

	protected static String htmlEncode(String input) {
		input = StringEscapeUtils.escapeHtml4(input);
		return input.replaceAll(System.getProperty("line.separator"), "<br/>");
	}

	@Override
	public void renderHeader(String reportTitle) throws IOException {
		this.title = reportTitle;
		tableHtml.append("\t<table >")
				.append(NEW_LINE)
				.append("\t\t<tbody>")
				.append(NEW_LINE)
				.append("<thead>")
				.append("\t\t<tr>")
				.append("<th>").append(ConfigProperties.getExistingProperty("colonne.date")).append("</th>")
				.append("<th>").append(ConfigProperties.getExistingProperty("colonne.author")).append("</th>")
				.append("<th>").append(ConfigProperties.getExistingProperty("colonne.message")).append("</th>")	
				.append("<th>").append(ConfigProperties.getExistingProperty("colonne.files")).append("</th>")
				.append("</tr>").append("</thead>").append(NEW_LINE);;
	}

	@Override
	public void renderTag(RevTag tag) throws IOException {
		tableHtml.append("\t\t<tr class=\"tag\"><td colspan=\"4\">")
				.append(SimpleHtmlRenderer.htmlEncode("Tag : " +tag.getTagName()))
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

		String author = SimpleHtmlRenderer.htmlEncode(commit.getCommitterIdent().getName());
		String committer = SimpleHtmlRenderer.htmlEncode(commit.getCommitterIdent().getName());
		String authorHtml = "<span class=\"committer\">" + commit.getAuthorIdent().getName() + "</span>";
		if (!areSame(author, committer)) {
			authorHtml += "and <span class=\"author\">" + author + "</span>";
		}

		tableHtml.append("\t\t<tr>")
				.append("<td class=\"date\">").append(date).append("</td>")
				.append("<td>").append(authorHtml).append("</td>")
				.append("<td>").append(message).append("</td>")
				.append("<td>").append(convertListToLinges ()).append("</td>")
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
			
			int index = title.indexOf("Version");
			String titre1=title;
			String titre2="";
			if (index != -1)
			{
				 titre1 = title.substring(0, index);

				titre2 = title.substring(index, title.length());
			}
			String html = template
					.replace("{title}", htmlEncode(titre1))
					.replace("{title2}", htmlEncode(titre2))
					.replace("{table}", tableHtml.toString());
			writer.append(html);
		}
	}

	private boolean areSame(String author, String committer) {
		return ("" + author).toLowerCase().equals("" + committer.toLowerCase());
	}

	private String convertListToLinges() {
		StringBuilder lignesFiles = new StringBuilder();
		if (this.getListDiffEntry()!=null &&this.getListDiffEntry().size() == 1) {

			return StringUtils.substring(this.getListDiffEntry().get(0)
					.toString(), 9);
		} else if (this.getListDiffEntry()!=null && this.getListDiffEntry().size() > 1) {
			lignesFiles.append(StringUtils.substring(this.getListDiffEntry().get(0).toString(), 9))
			.append("</br>");
			int idex=0;
			lignesFiles.append("<details> <summary>"
					+ ConfigProperties.getExistingProperty("bouton.detail")
					+ ": </summary>");

			for (DiffEntry diff : this.getListDiffEntry()) {
			    if(idex!=0)
			    {
				lignesFiles.append(StringUtils.substring(diff.toString(), 9))
						.append("</br>");
			    }
			    idex++;
			}
			lignesFiles.append("</details>");
			return lignesFiles.toString();
		}
		return "";
	}
}
