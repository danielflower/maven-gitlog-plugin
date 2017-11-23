package com.github.danielflower.mavenplugins.gitlog.renderers;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import java.io.File;
import java.io.IOException;

import static com.github.danielflower.mavenplugins.gitlog.renderers.Formatter.NEW_LINE;

/**
 * Asciidoc Renderer to get a*.md file.
 * <p>
 * The created asciidoc file will have
 * - a document title (level 0),
 * - bold tags
 * - and commits in each line.
 * <p>
 * Asciidoc reference is http://asciidoctor.org/docs/
 * <p>
 * Output Example:
 * = Maven GitLog Plugin changelog
 * *maven-gitlog-plugin-1.4.11*
 * 2012-03-17 07:33:55 +0100    Updated maven version in docs (Daniel Flower)
 * ...
 * <p>
 * Table View example
 * </p>
 * == Maven GitLog Plugin changelog
 * <p>
 * |===
 * |Date | Merge
 * |2017-11-23 07:34:02 +0100 |asciidoc also als table view (Marcel Widmer) +
 * |2017-11-22 12:24:41 +0100 |update README (Marcel Widmer) +
 * |2017-11-22 12:19:21 +0100 |Merge Commits Only (Marcel Widmer) +
 * <p>
 * |===
 * == *gitlog-maven-plugin-1.13.3* +
 * <p>
 * |===
 * |Date | Merge
 * |2016-09-24 08:40:27 +0200 |Bumped release plugin version (Daniel Flower) +
 * |2016-09-24 08:38:49 +0200 |Merge pull request #42 from orevial/asciidoc-support (GitHub) +
 * |2016-09-22 16:33:27 +0200 |Add Asciidoc converter support (Olivier Revial) +
 * <p>
 * |===
 */
public class AsciidocRenderer extends FileRenderer {

	private boolean previousWasTag = false;
	private final boolean fullGitMessage;
	protected final MessageConverter messageConverter;
	private AsciidocLinkConverter asciidocLinkConverter;
	private String asciidocHeading; // `=`
	private boolean asciidocTableView;
	private String asciidocTableViewHeader1; // Date
	private String asciidocTableViewHeader2; // Commit

	public AsciidocRenderer(Log log, File targetFolder, String filename, boolean fullGitMessage, MessageConverter messageConverter,
							String asciidocHeading, boolean asciidocTableView, String asciidocTableViewHeader1, String asciidocTableViewHeader2) throws IOException {
		super(log, targetFolder, filename);
		this.fullGitMessage = fullGitMessage;
		this.messageConverter = messageConverter;
		this.asciidocHeading = ((asciidocHeading == null) ? "=" : asciidocHeading);
		this.asciidocTableView = asciidocTableView;
		this.asciidocTableViewHeader1 = ((asciidocTableViewHeader1 == null) ? "Date" : asciidocTableViewHeader1);
		this.asciidocTableViewHeader2 = ((asciidocTableViewHeader2 == null) ? "Commit" : asciidocTableViewHeader2);
		asciidocLinkConverter = new AsciidocLinkConverter(log);
	}


	public void renderHeader(String reportTitle) throws IOException {
		if (reportTitle != null && reportTitle.length() > 0) {
			writer.write(this.asciidocHeading + " "); // MD Heading 1
			writer.write(reportTitle);
			writer.write(NEW_LINE);
			if (asciidocTableView) {
				renderTableHeader();
			}
		}
	}

	public void renderTag(RevTag tag) throws IOException {
			if (!previousWasTag) {
					writer.write(NEW_LINE);
			}else {
				if (asciidocTableView) {
					renderTableFooter();
					writer.write(asciidocHeading + "= ");
					writer.write(tag.getTagName());
					renderTableHeader();
				}else {
					writer.write("*"); // MD start bold
					writer.write(tag.getTagName());
					writer.write("*"); // MD end bold
					writer.write(" +"); // MD line warp
					writer.write(NEW_LINE);
				}
			}
			previousWasTag = true;
	}

	public void renderCommit(RevCommit commit) throws IOException {
		String message = null;
		// use the message formatter to get a HTML hyperlink
		if (fullGitMessage) {
			message = messageConverter.formatCommitMessage(commit.getFullMessage());
		} else {
			message = messageConverter.formatCommitMessage(commit.getShortMessage());
		}
		// now convert the HTML hyperlink into an Asciidoc link
		message = asciidocLinkConverter.formatCommitMessage(message);

		if (asciidocTableView) {
			renderTableCommit(commit, message);
		}else {
			writer.write(Formatter.formatDateTime(commit.getCommitTime()) + "     " + message);
			writer.write(" (" + commit.getCommitterIdent().getName() + ")");
			writer.write(" +"); // MD line warp
			writer.write(NEW_LINE);
		}
		previousWasTag = false;
	}

	private void renderTableCommit(RevCommit commit, String message) throws IOException {
		if (asciidocTableView) {
			writer.write(NEW_LINE);
			writer.write("|");
			writer.write(Formatter.formatDateTime(commit.getCommitTime()) + " | " + message);
			writer.write(" (" + commit.getCommitterIdent().getName() + ")");
			writer.write(NEW_LINE);
		}
	}

	public void renderTableHeader() throws IOException {
		if (asciidocTableView) {
			writer.write(NEW_LINE);
			writer.write("|===");
			writer.write(NEW_LINE);
			writer.write("|" + asciidocTableViewHeader1 + " | " + asciidocTableViewHeader2);
			writer.write(NEW_LINE);
		}
	}

	public void renderTableFooter() throws IOException {
		if (asciidocTableView) {
			writer.write(NEW_LINE);
			writer.write("|===");
			writer.write(NEW_LINE);
		}
	}


	public void renderFooter() throws IOException {
		if (asciidocTableView) {
			renderTableFooter();
		}
	}
}
