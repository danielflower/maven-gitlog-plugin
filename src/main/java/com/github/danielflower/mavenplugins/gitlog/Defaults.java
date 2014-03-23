package com.github.danielflower.mavenplugins.gitlog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import com.github.danielflower.mavenplugins.gitlog.filters.CommitFilter;
import com.github.danielflower.mavenplugins.gitlog.filters.DuplicateCommitMessageFilter;
import com.github.danielflower.mavenplugins.gitlog.filters.MavenReleasePluginMessageFilter;
import com.github.danielflower.mavenplugins.gitlog.filters.MergeCommitFilter;

class Defaults {
	public static final List<CommitFilter> DEFAULT_COMMIT_FILTERS = Arrays.asList(
			new MavenReleasePluginMessageFilter(),
			new MergeCommitFilter(),
			new DuplicateCommitMessageFilter()
	);
	
	public static final List<CommitFilter> COMMIT_FILTERS;
	
	static {
		COMMIT_FILTERS = new ArrayList<CommitFilter>();
		COMMIT_FILTERS.addAll(DEFAULT_COMMIT_FILTERS);
		
		Iterator<CommitFilter> it = ServiceLoader.load(CommitFilter.class).iterator();
		while (it.hasNext()){
			COMMIT_FILTERS.add(it.next());
		}
	}
}
