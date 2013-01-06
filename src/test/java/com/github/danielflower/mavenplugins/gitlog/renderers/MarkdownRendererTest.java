package com.github.danielflower.mavenplugins.gitlog.renderers;

import junit.framework.Assert;

import org.junit.Test;

public class MarkdownRendererTest {

	@Test
	public void emptyMessagesAreUnchanged() {
		Assert.assertEquals("", MarkdownRenderer.markdownEncode(""));
	}

	@Test
	public void normalTestIsUnchanged() {
		Assert.assertEquals("Alles OK", MarkdownRenderer.markdownEncode("Alles OK"));
	}

	@Test
	public void OriginalTextWithCharactersToEscape() {
		// Tests to the escaping of the markdown link
		// already existing [ ] must be escaped
		Assert.assertEquals("\\[Escape1\\]", 
				MarkdownRenderer.markdownEncode("[Escape1]"));
		Assert.assertEquals("\\[Escape1\\]\\[\\[Escape2\\]\\] \\]\\[Escape3\\]\\]",
				MarkdownRenderer.markdownEncode("[Escape1][[Escape2]] ][Escape3]]"));
	}

	

}
