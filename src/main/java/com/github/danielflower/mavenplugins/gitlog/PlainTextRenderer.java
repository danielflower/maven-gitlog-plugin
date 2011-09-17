package com.github.danielflower.mavenplugins.gitlog;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static com.github.danielflower.mavenplugins.gitlog.Formatter.NEW_LINE;

class PlainTextRenderer implements ChangeLogRenderer {

	private Writer writer;
	private final Log log;
	private boolean previousWasTag = false;

	public PlainTextRenderer(Log log, File targetFolder, String filename) throws IOException {
		this.log = log;
		File file = new File(targetFolder, filename);
		log.debug("Creating plaintext changelog file at " + file.getAbsolutePath());
		writer = new FileWriter(file);
	}

	public void renderHeader() throws IOException {
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

	public void close() {
		if (writer != null) {
			try {
				writer.flush();
			} catch (IOException e) {
				log.error("Could not flush file to disk", e);
			}
			try {
				writer.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
