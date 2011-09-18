package com.github.danielflower.mavenplugins.gitlog;

import com.github.danielflower.mavenplugins.gitlog.filters.CommitFilter;
import com.github.danielflower.mavenplugins.gitlog.filters.DuplicateCommitMessageFilter;
import com.github.danielflower.mavenplugins.gitlog.filters.MavenReleasePluginMessageFilter;
import com.github.danielflower.mavenplugins.gitlog.filters.MergeCommitFilter;

import java.util.Arrays;
import java.util.List;

class Defaults {

	public static final List<CommitFilter> COMMIT_FILTERS = Arrays.asList(
			new MavenReleasePluginMessageFilter(),
			new MergeCommitFilter(),
			new DuplicateCommitMessageFilter()
	);

}
