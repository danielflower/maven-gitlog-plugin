package com.github.danielflower.mavenplugins.gitlog.renderers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MarkdownRendererTest {

	@Test
	public void emptyMessagesAreUnchanged() {
		assertEquals("", MarkdownRenderer.markdownEncode(""));
	}

	@Test
	public void normalTestIsUnchanged() {
		assertEquals("Alles OK", MarkdownRenderer.markdownEncode("Alles OK"));
	}

	@Test
	public void OriginalTextWithCharactersToEscape() {
		// Tests to the escaping of the markdown link
		// already existing [ ] must be escaped
		assertEquals("\\[Escape1\\]", 
				MarkdownRenderer.markdownEncode("[Escape1]"));
		assertEquals("\\[Escape1\\]\\[\\[Escape2\\]\\] \\]\\[Escape3\\]\\]",
				MarkdownRenderer.markdownEncode("[Escape1][[Escape2]] ][Escape3]]"));
	}
}
