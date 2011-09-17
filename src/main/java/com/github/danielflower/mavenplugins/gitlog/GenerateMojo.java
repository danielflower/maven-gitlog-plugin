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
		try {
			List<ChangeLogRenderer> renderers = createRenderers();
			Generator generator = new Generator(renderers, Defaults.COMMIT_FILTERS, getLog());
			generator.openRepository();
			generator.generate();
		} catch (IOException e) {
			getLog().error("Error while generating gitlog", e);
		}
	}

	private List<ChangeLogRenderer> createRenderers() throws IOException {
		ArrayList<ChangeLogRenderer> renderers = new ArrayList<ChangeLogRenderer>();
		renderers.add(new PlainTextRenderer(getLog(), outputDirectory, "changelog.txt"));
		renderers.add(new MavenLoggerRenderer(getLog()));
		return renderers;
	}


}
