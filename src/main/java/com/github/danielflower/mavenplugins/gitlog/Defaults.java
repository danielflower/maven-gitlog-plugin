package com.github.danielflower.mavenplugins.gitlog;

import java.util.Arrays;
import java.util.List;

public class Defaults {

	public static final List<CommitFilter> COMMIT_FILTERS = Arrays.asList(
			new MavenReleasePluginMessageFilter(),
			new DuplicateCommitFilter()
	);

}
