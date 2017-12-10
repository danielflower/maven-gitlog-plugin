package com.github.danielflower.mavenplugins.gitlog.filters;

import org.eclipse.jgit.revwalk.RevCommit;

public interface CommitFilter {

	/**
	 * Returns true if the commit should be rendered; otherwise false.
	 * @param commit The commit to render
	 * @return Returns true if the commit should be rendered; false if it should be skipped
	 */
	boolean renderCommit(RevCommit commit);

}
