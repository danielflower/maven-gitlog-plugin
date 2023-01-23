package com.github.danielflower.mavenplugins.gitlog.filters;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class CommitterFilter implements CommitFilter {

	private final List<String> committers;

	public CommitterFilter(List<String> committers) {
		this.committers = committers;
	}

	@Override
	public boolean renderCommit(RevCommit commit) {
		for (String committer : committers) {
			if (committer.equalsIgnoreCase(commit.getCommitterIdent().getName())) {
				return false;
			}
		}
		return true;
	}
}