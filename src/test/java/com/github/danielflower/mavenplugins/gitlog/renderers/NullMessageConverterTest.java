package com.github.danielflower.mavenplugins.gitlog.renderers;

import junit.framework.Assert;
import org.junit.Test;

public class NullMessageConverterTest {

	@Test
	public void thatWhichGoesInIsThatWhichComesOutAgain() {
		NullMessageConverter converter = new NullMessageConverter();
		String someText = "Yeah £1 #1 GH-1 yeah yeah";
		Assert.assertEquals(someText, converter.formatCommitMessage(someText));
	}

}
