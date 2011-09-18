package com.github.danielflower.mavenplugins.gitlog.renderers;

public interface MessageConverter {

	public String formatCommitMessage(String original);

}
