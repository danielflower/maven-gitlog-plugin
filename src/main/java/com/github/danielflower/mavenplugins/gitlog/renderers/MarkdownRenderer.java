package com.github.danielflower.mavenplugins.gitlog.renderers;

import static com.github.danielflower.mavenplugins.gitlog.renderers.Formatter.NEW_LINE;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	/**
	 * Helper class to convert a HTML link into a markdown link.
	 * 
	 * It searches for a <a href="...URL...">...Text...</a> and the
	 * URL and text is used to build afterwards the markdown link.
	 * The markdown links goes like [...Text...](...URL...).
	 *
	 */
	private class MarkdownLinkConverter {

		private Pattern pattern;
		private final Log log;
		
		public MarkdownLinkConverter(Log log) {
			this.log = log;
			// see http://stackoverflow.com/questions/26323/regex-to-parse-hyperlinks-and-descriptions
			// limited to double quotes (removed the single quote),
			// because the Converters will use double quotes.
			this.pattern = Pattern.compile("<a\\s+href=(\"([^\"]+)\").*?>(.*?)</a>");
		}
		
		public String formatCommitMessage(String original) {
			try {
				Matcher matcher = pattern.matcher(original);
				String result = matcher.replaceAll("[$3]($2)");
				return result;
			} catch (Exception e) {
				// log, but don't let this small setback fail the build
				log.info("Unable to convert a HTML link into markdown link: " + original, e);
			}
			return original;
		}
		
	}
	
	
	public MarkdownRenderer(Log log, File targetFolder, String filename, boolean fullGitMessage, MessageConverter messageConverter) throws IOException {
		super(log, targetFolder, filename);
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
		writer.write(" (" + commit.getCommitterIdent().getName() + ")");
		writer.write("  "); // MD line warp
		writer.write(NEW_LINE);
		previousWasTag = false;
	}


	public void renderFooter() throws IOException {
	}

	protected static String markdownEncode(String input) {
		// Add a backslash in front of [ and ]
		// because (at least) these characters need to be escaped in markdown,
		// else the markdown hyperlink will not work.
		input = input.replaceAll("\\[", "\\\\[");
		input = input.replaceAll("\\]", "\\\\]");
		return input; 
	}
	
}
