package com.github.danielflower.mavenplugins.gitlog.renderers;

public class NullMessageConverter implements MessageConverter {
	@Override
	public String formatCommitMessage(String original) {
		return original;
	}
}
