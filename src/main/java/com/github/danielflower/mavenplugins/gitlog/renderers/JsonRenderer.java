package com.github.danielflower.mavenplugins.gitlog.renderers;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

public class JsonRenderer extends FileRenderer {

	private String template;
	protected StringBuilder json = new StringBuilder();
	private final boolean fullGitMessage;
	private boolean firstCommit = true;
	private Collection<RevTag> tags = new ArrayList<RevTag>();

	public JsonRenderer(Log log, File targetFolder, String filename, boolean fullGitMessage)
			throws IOException {
		super(log, targetFolder, filename, false);
		this.fullGitMessage = fullGitMessage;

		this.template = loadResourceToString("/json/JsonItemTemplate.html");
	}

	@Override
	public void renderHeader(String reportTitle) throws IOException {
		json.append("[\n");
	}

	@Override
	public void renderTag(RevTag tag) throws IOException {
		this.tags.add(tag);
	}

	@Override
	public void renderCommit(RevCommit commit) throws IOException {
		String date = Formatter.formatDateTime(commit.getCommitTime());
		String message = null;
		if (fullGitMessage) {
			message = commit.getFullMessage();
		} else {
			message = commit.getShortMessage();
		}
		StringBuffer tagsJson = new StringBuffer();
		boolean firstTag = true;
		for (RevTag tag : this.tags) {
			if(firstTag) {
				firstTag = false;
				tagsJson.append(" ");
			} else {
				tagsJson.append(", ");
			}
			tagsJson.append("{ \"name\":\"").append(encode(tag.getTagName())).append("\" }");
		}
		if(!firstTag) {
			tagsJson.append(" ");
		}
		this.tags.clear(); //reset for next commit's tags

		if(firstCommit) {
			json.append("    ");
			firstCommit = false;
		} else {
			json.append("  , ");
		}
		String jsonItem;
		jsonItem = template
				.replace("{id}", encode(commit.getName()))
				.replace("{message}", encode(message))
				.replace("{tagItems}", tagsJson.toString())
				.replace("{date}", encode(date));
		if (Formatter.showCommitter()){
			jsonItem = jsonItem.replace("{authorName}", encode(commit.getAuthorIdent().getName()))
					.replace("{authorEmail}", encode(commit.getAuthorIdent().getEmailAddress()))
					.replace("{committerName}", encode(commit.getCommitterIdent().getName()))
					.replace("{committerEmail}", encode(commit.getCommitterIdent().getEmailAddress()));
		}
		else {
			jsonItem = jsonItem.replace("{authorName}", "")
					.replace("{authorEmail}", "")
					.replace("{committerName}", "")
					.replace("{committerEmail}", "");
		}
		json.append(jsonItem);
		json.append("\n");
	}

	@Override
	public void renderFooter() throws IOException {
		json.append("]\n");
		writer.append(json);
	}

	protected static String encode(String input) {
		if((input == null) || (input.length() == 0)) {
			return input;
		}
		if(input.lastIndexOf("\n") == input.length()-1) {
			input = input.substring(0, input.length()-1);
		}
		input = input.replace("\\", "\\\\");
		input = input.replace("\n", "\\n");
		input = input.replace("\t", "\\t");
		input = input.replace("\b", "\\b");
		input = input.replace("\f", "\\f");
		input = input.replace("\r", "\\r");
		input = input.replace("\"", "\\\"");
		return input;
	}

}
