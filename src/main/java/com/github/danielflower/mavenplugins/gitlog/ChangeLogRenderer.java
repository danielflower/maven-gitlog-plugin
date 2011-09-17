package com.github.danielflower.mavenplugins.gitlog;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import java.io.IOException;

interface ChangeLogRenderer {

	public void renderHeader() throws IOException;

	public void renderTag(RevTag tag) throws IOException;

	public void renderCommit(RevCommit commit) throws IOException;

	public void renderFooter() throws IOException;

	public void close();

}
