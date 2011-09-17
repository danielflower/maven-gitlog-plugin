package com.github.danielflower.mavenplugins.gitlog;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import java.io.IOException;
import java.io.PrintStream;

class StandardOutputRenderer implements ChangeLogRenderer {

	private final PrintStream stream;

	public StandardOutputRenderer(PrintStream stream) {
		this.stream = stream;
	}

	public void renderHeader() throws IOException {
		stream.println("Change log");
	}

	public void renderTag(RevTag tag) throws IOException {
		stream.println();
		stream.println("****** " + tag.getTagName() + " - " + tag.getShortMessage() + " ******");
	}

	public void renderCommit(RevCommit commit) throws IOException {
		stream.println(commit.getName() + " " + Formatter.formatDateTime(commit.getCommitTime()) + " "
				+ commit.getShortMessage() + " (" + commit.getCommitterIdent().getName() + ")");
	}

	public void renderFooter() throws IOException {
	}

	public void close() {
	}
}
