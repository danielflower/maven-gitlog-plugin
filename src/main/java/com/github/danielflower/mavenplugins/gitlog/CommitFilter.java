package com.github.danielflower.mavenplugins.gitlog;

import org.eclipse.jgit.revwalk.RevCommit;

public interface CommitFilter {

	/**
	 * Returns true if the commit should be rendered; otherwise false.
	 */
	public boolean renderCommit(RevCommit commit);

}
