package com.github.danielflower.mavenplugins.gitlog.renderers;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import java.io.File;
import java.io.IOException;

import static com.github.danielflower.mavenplugins.gitlog.renderers.Formatter.NEW_LINE;

public class PlainTextRenderer extends FileRenderer {

	private boolean previousWasTag = false;
	private final boolean fullGitMessage;

	public PlainTextRenderer(Log log, File targetFolder, String filename, boolean fullGitMessage) throws IOException {
		super(log, targetFolder, filename);
		this.fullGitMessage = fullGitMessage;
	}

	public void renderHeader(String reportTitle) throws IOException {
		if (reportTitle != null && reportTitle.length() > 0) {
			writer.write(reportTitle);
			writer.write(NEW_LINE);
			writer.write(NEW_LINE);
		}
	}

	public void renderTag(RevTag tag) throws IOException {
		if (!previousWasTag) {
			writer.write(NEW_LINE);
		}
		writer.write(tag.getTagName());
		writer.write(NEW_LINE);
		previousWasTag = true;
	}

	public void renderCommit(RevCommit commit) throws IOException {
		String message = null;
		if (fullGitMessage){
			message = commit.getFullMessage();
		} else {
			message = commit.getShortMessage();
		}
		writer.write(Formatter.formatDateTime(commit.getCommitTime()) + "    " + message);
		writer.write(" (" + commit.getCommitterIdent().getName() + ")");
		writer.write(NEW_LINE);
		previousWasTag = false;
	}


	public void renderFooter() throws IOException {
	}

}
