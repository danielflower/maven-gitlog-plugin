package com.github.danielflower.mavenplugins.gitlog;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import java.io.IOException;

class MavenLoggerRenderer implements ChangeLogRenderer {

	private final Log log;

	public MavenLoggerRenderer(Log log) {
		if (log == null) {
			log = new SystemStreamLog();
		}
		this.log = log;
	}

	public void renderHeader() throws IOException {
		log.info("*********************************************");
		log.info("Change log");
		log.info("*********************************************");
	}

	public void renderTag(RevTag tag) throws IOException {
		log.info("");
		log.info(tag.getTagName() + " - " + tag.getShortMessage());
	}

	public void renderCommit(RevCommit commit) throws IOException {
		log.info(Formatter.formatDateTime(commit.getCommitTime()) + " "
				+ commit.getShortMessage() + " (" + commit.getCommitterIdent().getName() + ")");
	}

	public void renderFooter() throws IOException {
		log.info("");
		log.info("*********************************************");
	}

	public void close() {
	}
}
