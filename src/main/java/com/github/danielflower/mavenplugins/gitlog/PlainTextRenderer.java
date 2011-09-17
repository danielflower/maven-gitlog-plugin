package com.github.danielflower.mavenplugins.gitlog;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import java.io.File;
import java.io.IOException;

import static com.github.danielflower.mavenplugins.gitlog.Formatter.NEW_LINE;

class PlainTextRenderer extends FileRenderer {

	private boolean previousWasTag = false;

	public PlainTextRenderer(Log log, File targetFolder, String filename) throws IOException {
		super(log, targetFolder, filename);
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
		writer.write(Formatter.formatDateTime(commit.getCommitTime()) + "    " + commit.getShortMessage());
		writer.write(NEW_LINE);
		previousWasTag = false;
	}


	public void renderFooter() throws IOException {
	}

}
