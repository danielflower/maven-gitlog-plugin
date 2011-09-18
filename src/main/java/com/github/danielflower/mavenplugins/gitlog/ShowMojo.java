package com.github.danielflower.mavenplugins.gitlog;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Displays the git log in the maven build log.  Use the generate goal to generate reports.
 *
 * @goal show
 */
public class ShowMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {

		List<ChangeLogRenderer> renderers = Arrays.<ChangeLogRenderer>asList(new MavenLoggerRenderer(getLog()));
		Generator generator = new Generator(renderers, Defaults.COMMIT_FILTERS, getLog());

		try {
			generator.openRepository();
		} catch (IOException e) {
			throw new MojoExecutionException("Error opening git repository.  Is this Maven project hosted in a git repository? " +
					"No changelog will be generated.", e);
		} catch (NoGitRepositoryException e) {
			throw new MojoExecutionException("This maven project does not appear to be in a git repository, " +
					"therefore no git changelog will be generated.");
		}

		try {
			generator.generate("Git log");
		} catch (IOException e) {
			throw new MojoExecutionException("Error while generating changelog.  Some changelogs may be incomplete or corrupt.", e);
		}
	}


}
