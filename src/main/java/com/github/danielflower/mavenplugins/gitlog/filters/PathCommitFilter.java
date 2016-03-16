package com.github.danielflower.mavenplugins.gitlog.filters;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Filters out commits not related to files under a given path.
 *
 * @author helpermethod
 */
public class PathCommitFilter implements CommitFilter {
    private final Repository repository;
    private final String relativizedPath;
    private final Log log;

    public PathCommitFilter(Repository repository, String path, Log log) {
        this.repository = repository;
        this.relativizedPath = relativizePath(repository, path);
        this.log = log;
    }

    private String relativizePath(Repository repository, String path) {
        File p = new File(path);

        return p.isAbsolute() ? repository.getDirectory().getParentFile().toURI().relativize(p.toURI()).getPath() : path;
    }

    @Override
    public boolean renderCommit(RevCommit commit) {
        try {
            return isFoundInPath(commit);
        } catch (IOException e) {
            log.warn("Error while diffing commits.  Filter won't be applied.", e);
            return false;
        }
    }

    private boolean isFoundInPath(RevCommit commit) throws IOException {
        for (DiffEntry diff : getDiffs(commit, commit.getParent(0))) {
            if (diff.getNewPath().startsWith(relativizedPath)) {
                return true;
            }
        }

        return false;
    }

    private List<DiffEntry> getDiffs(RevCommit commit, RevCommit parent) throws IOException {
        DiffFormatter formatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
        formatter.setRepository(repository);
        formatter.setDiffComparator(RawTextComparator.DEFAULT);
        formatter.setDetectRenames(true);

        return formatter.scan(parent.getTree(), commit.getTree());
    }
}
