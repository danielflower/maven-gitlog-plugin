package com.github.danielflower.mavenplugins.gitlog;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Goal which touches a timestamp file.
 *
 * @goal generate
 * @phase prepare-package
 */
public class GenerateMojo extends AbstractMojo {

	/**
	 * Location of the file.
	 *
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generating change log in " + outputDirectory.getAbsolutePath());

		Repository repository;
		try {
			repository = new RepositoryBuilder().findGitDir().build();
			getLog().info("Reading repo " + repository.getFullBranch());
		} catch (IOException e) {
			throw new MojoFailureException("Could not load git repository. Is there a .git folder available?");
		}

		File f = outputDirectory;

		if (!f.exists()) {
			f.mkdirs();
		}

		File touch = new File(f, "changelog.txt");

		FileWriter w = null;
		try {
			w = new FileWriter(touch);
			RevWalk walk = new RevWalk(repository);
			ObjectId head = repository.resolve("HEAD");
			RevCommit mostRecentCommit = walk.parseCommit(head);
			walk.markStart(mostRecentCommit);

			for (RevCommit commit : walk) {
				getLog().info("Commit: " + commit.getShortMessage());
				w.write(commit.getShortMessage() + String.format("%n"));
			}
			walk.dispose();
		} catch (IOException e) {
			throw new MojoExecutionException("Error creating file " + touch, e);
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}


	}

}
