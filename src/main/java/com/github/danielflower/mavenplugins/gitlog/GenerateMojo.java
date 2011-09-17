package com.github.danielflower.mavenplugins.gitlog;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal which generates a changelog based on commits made to the current git repo.
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

		File f = outputDirectory;
		if (!f.exists()) {
			f.mkdirs();
		}

		List<ChangeLogRenderer> renderers;
		try {
			renderers = createRenderers();
		} catch (IOException e) {
			getLog().warn("Error while setting up gitlog renderers.  No changelog will be generated.", e);
			return;
		}

		Generator generator = new Generator(renderers, Defaults.COMMIT_FILTERS, getLog());

		try {
			generator.openRepository();
		} catch (IOException e) {
			getLog().warn("Error opening git repository.  Is this Maven project hosted in a git repository? " +
					"No changelog will be generated.", e);
			return;
		} catch (NoGitRepositoryException e) {
			getLog().warn("This maven project does not appear to be in a git repository, " +
					"therefore no git changelog will be generated.");
			return;
		}

		try {
			generator.generate();
		} catch (IOException e) {
			getLog().warn("Error while generating changelog.  Some changelogs may be incomplete or corrupt.", e);
		}
	}

	private List<ChangeLogRenderer> createRenderers() throws IOException {
		ArrayList<ChangeLogRenderer> renderers = new ArrayList<ChangeLogRenderer>();
		renderers.add(new PlainTextRenderer(getLog(), outputDirectory, "changelog.txt"));
		renderers.add(new MavenLoggerRenderer(getLog()));
		return renderers;
	}

}
