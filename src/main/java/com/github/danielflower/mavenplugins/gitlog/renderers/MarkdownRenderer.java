package com.github.danielflower.mavenplugins.gitlog.renderers;

import static com.github.danielflower.mavenplugins.gitlog.renderers.Formatter.NEW_LINE;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

/**
 * Markdown Renderer to get a*.md file.
 * 
 * The created markdown file will have 
 * - a h1 heading, 
 * - bold tags 
 * - and commits in each line.
 * 
 * Markdown reference is http://daringfireball.net/projects/markdown/
 *  
 * Output Example:
 * 		# Maven GitLog Plugin changelog
 * 		**maven-gitlog-plugin-1.4.11**  
 * 		2012-03-17 07:33:55 +0100    Updated maven version in docs (Daniel Flower)  
 * 		...
 *  
 */
public class MarkdownRenderer extends FileRenderer {

	private boolean previousWasTag = false;
	private final boolean fullGitMessage;
	protected final MessageConverter messageConverter;
	private MarkdownLinkConverter markdownLinkConverter;
	
	public MarkdownRenderer(Log log, File targetFolder, String filename, boolean fullGitMessage, MessageConverter messageConverter, boolean append) throws IOException {
		super(log, targetFolder, filename, append);
		this.fullGitMessage = fullGitMessage;
		this.messageConverter = messageConverter;
		markdownLinkConverter = new MarkdownLinkConverter(log);
	}

	public void renderHeader(String reportTitle) throws IOException {
		if (reportTitle != null && reportTitle.length() > 0) {
			writer.write("# "); // MD Heading 1
			writer.write(reportTitle);
			writer.write(NEW_LINE);
			writer.write(NEW_LINE);
		}
	}

	public void renderTag(RevTag tag) throws IOException {
		if (!previousWasTag) {
			writer.write(NEW_LINE);
		}
		writer.write("**"); // MD start bold
		writer.write(tag.getTagName());
		writer.write("**"); // MD end bold
		writer.write("  "); // MD line warp
		writer.write(NEW_LINE);
		previousWasTag = true;
	}

	public void renderCommit(RevCommit commit) throws IOException {
		String message = null;
		// use the message formatter to get a HTML hyperlink
		if (fullGitMessage){
			message = messageConverter.formatCommitMessage(MarkdownRenderer.markdownEncode(commit.getFullMessage()));
		} else {
			message = messageConverter.formatCommitMessage(MarkdownRenderer.markdownEncode(commit.getShortMessage()));	
		}
		// now convert the HTML hyperlink into a markdown link
		message = markdownLinkConverter.formatCommitMessage(message);	
		writer.write(Formatter.formatDateTime(commit.getCommitTime()) + "    " + message);
		writer.write(" " + Formatter.formatCommitter(commit.getCommitterIdent()));
		writer.write("  "); // MD line warp
		writer.write(NEW_LINE);
		previousWasTag = false;
	}


	public void renderFooter() throws IOException {
	}

	
	/**
	 * Helper function to escape some important characters for the
	 * markdown hyperlink.
	 * A backslash will be added in front of [ and ].
	 * At least these characters need to be escaped in markdown,
	 * else the markdown hyperlink will not work.
	 * 
	 * The function is package public for easy unit tests.
	 * 
	 * @param input		string to escape
	 * @return			escaped string
	 */
	static String markdownEncode(String input) {
		// Add a backslash in front of [ and ]
		input = input.replaceAll("\\[", "\\\\[");
		input = input.replaceAll("\\]", "\\\\]");
		return input; 
	}
	
}
