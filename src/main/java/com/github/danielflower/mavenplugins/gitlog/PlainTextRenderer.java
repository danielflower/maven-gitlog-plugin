package com.github.danielflower.mavenplugins.gitlog;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

class PlainTextRenderer implements ChangeLogRenderer {

	private Writer writer;

	public PlainTextRenderer(File targetFolder, String filename) throws IOException {
		File file = new File(targetFolder, filename);
		writer = new FileWriter(file);
	}

	public void renderHeader() throws IOException {
	}

	public void renderTag(RevTag tag) throws IOException {
	}

	public void renderCommit(RevCommit commit) throws IOException {
	}

	public void renderFooter() throws IOException {
	}

	public void close() {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
