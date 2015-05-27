package com.github.danielflower.mavenplugins.gitlog.filters;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class CommiterFilter implements CommitFilter {

	private final List<String> commiters;

	public CommiterFilter(List<String> commiters) {
		this.commiters = commiters;
	}

	@Override
	public boolean renderCommit(RevCommit commit) {
		for (String commiter : commiters) {
			if (commiter.equalsIgnoreCase(commit.getCommitterIdent().getName())) {
				return false;
			}
		}
		return true;
	}
}