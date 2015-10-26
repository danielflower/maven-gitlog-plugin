package com.github.danielflower.mavenplugins.gitlog.filters;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpFilter implements CommitFilter {

    private Pattern pattern;

    public RegexpFilter(String excludeCommitsRegexp) {
        this.pattern = Pattern.compile(excludeCommitsRegexp);
    }

    @Override
    public boolean renderCommit(RevCommit commit) {
        Matcher matcher = pattern.matcher(commit.getShortMessage());
        return !matcher.matches();
    }
}
