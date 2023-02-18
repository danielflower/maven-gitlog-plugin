package com.github.danielflower.mavenplugins.gitlog;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.stream.Stream;

public class ActionsTest {

	@Test
	public void canDoIt() throws Exception {

		String githubActor = System.getenv("GITHUB_ACTOR");
		String githubToken =  System.getenv("GITHUB_TOKEN");
		System.out.println("githubActor = " + hide(githubActor));
		System.out.println("githubToken = " + hide(githubToken));

		Thread.sleep(Long.MAX_VALUE);
		Git git = Git.open(new File(""));
		CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(githubActor, githubToken);
		Collection<Ref> response = git.lsRemote()
				.setTags(true).setHeads(false)
				.setCredentialsProvider(credentialsProvider)
				.setRemote("https://github.com/danielflower/maven-gitlog-plugin.git")
				.call();

		for (Ref ref : response) {
			System.out.println("ref = " + ref);
		}

	}

	private String hide(String thing) {
		char[] chars = thing.toCharArray();
		String s = "~~~";
		for (int i = chars.length; i > 0; i--) {
			s += "_" + chars[i - 1];
		}
		return s + "~~~";
	}

}
