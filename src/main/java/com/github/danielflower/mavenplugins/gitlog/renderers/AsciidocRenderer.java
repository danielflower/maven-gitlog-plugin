package com.github.danielflower.mavenplugins.gitlog.renderers;

import static com.github.danielflower.mavenplugins.gitlog.renderers.Formatter.NEW_LINE;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

/**
 * Asciidoc Renderer to get a*.md file.
 * 
 * The created asciidoc file will have
 * - a document title (level 0),
 * - bold tags 
 * - and commits in each line.
 * 
 * Asciidoc reference is http://asciidoctor.org/docs/
 *  
 * Output Example:
 * 		= Maven GitLog Plugin changelog
 * 		*maven-gitlog-plugin-1.4.11*
 * 		2012-03-17 07:33:55 +0100    Updated maven version in docs (Daniel Flower)  
 * 		...
 *  
 */
public class AsciidocRenderer extends FileRenderer {

	private boolean previousWasTag = false;
	private final boolean fullGitMessage;
	protected final MessageConverter messageConverter;
	private AsciidocLinkConverter asciidocLinkConverter;

	public AsciidocRenderer(Log log, File targetFolder, String filename, boolean fullGitMessage, MessageConverter messageConverter) throws IOException {
		super(log, targetFolder, filename);
		this.fullGitMessage = fullGitMessage;
		this.messageConverter = messageConverter;
		asciidocLinkConverter = new AsciidocLinkConverter(log);
	}

	public void renderHeader(String reportTitle) throws IOException {
		if (reportTitle != null && reportTitle.length() > 0) {
			writer.write("= "); // MD Heading 1
			writer.write(reportTitle);
			writer.write(NEW_LINE);
			writer.write(NEW_LINE);
		}
	}

	public void renderTag(RevTag tag) throws IOException {
		if (!previousWasTag) {
			writer.write(NEW_LINE);
		}
		writer.write("*"); // MD start bold
		writer.write(tag.getTagName());
		writer.write("*"); // MD end bold
		writer.write(" +"); // MD line warp
		writer.write(NEW_LINE);
		previousWasTag = true;
	}

	public void renderCommit(RevCommit commit) throws IOException {
		String message = null;
		// use the message formatter to get a HTML hyperlink
		if (fullGitMessage){
			message = messageConverter.formatCommitMessage(commit.getFullMessage());
		} else {
			message = messageConverter.formatCommitMessage(commit.getShortMessage());
		}
		// now convert the HTML hyperlink into an Asciidoc link
		message = asciidocLinkConverter.formatCommitMessage(message);
		writer.write(Formatter.formatDateTime(commit.getCommitTime()) + "    " + message);
		writer.write(" (" + commit.getCommitterIdent().getName() + ")");
		writer.write(" +"); // MD line warp
		writer.write(NEW_LINE);
		previousWasTag = false;
	}


	public void renderFooter() throws IOException {
	}
}
