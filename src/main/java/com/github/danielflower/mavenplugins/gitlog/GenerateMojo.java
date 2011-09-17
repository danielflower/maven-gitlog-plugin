package com.github.danielflower.mavenplugins.gitlog;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileWriter;
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

		List<ChangeLogRenderer> renderers = createRenderers();

		File touch = new File(f, "changelog.txt");

		FileWriter w = null;
		try {


			w = new FileWriter(touch);


		} catch (IOException e) {
			throw new MojoExecutionException("Error creating file " + touch, e);
		} finally {
			for (ChangeLogRenderer renderer : renderers) {
				renderer.close();
			}
		}


	}

	private List<ChangeLogRenderer> createRenderers() {
		ArrayList<ChangeLogRenderer> renderers = new ArrayList<ChangeLogRenderer>();
		return renderers;
	}


}
