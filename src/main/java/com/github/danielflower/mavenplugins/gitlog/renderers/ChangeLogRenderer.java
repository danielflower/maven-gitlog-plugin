package com.github.danielflower.mavenplugins.gitlog.renderers;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import java.io.IOException;

public interface ChangeLogRenderer {

	public void renderHeader(String reportTitle) throws IOException;

	public void renderTag(RevTag tag) throws IOException;

	public void renderCommit(RevCommit commit) throws IOException;

	public void renderFooter() throws IOException;

	public void close();

}
