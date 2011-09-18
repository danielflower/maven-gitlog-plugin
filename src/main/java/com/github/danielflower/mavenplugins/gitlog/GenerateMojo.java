package com.github.danielflower.mavenplugins.gitlog;

import com.github.danielflower.mavenplugins.gitlog.renderers.ChangeLogRenderer;
import com.github.danielflower.mavenplugins.gitlog.renderers.MavenLoggerRenderer;
import com.github.danielflower.mavenplugins.gitlog.renderers.PlainTextRenderer;
import com.github.danielflower.mavenplugins.gitlog.renderers.SimpleHtmlRenderer;
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
	 * The directory to put the reports in.  Defaults to the project build directory (normally target).
	 *
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * The title of the reports. Defaults to: ${project.name} v${project.version} changelog
	 *
	 * @parameter expression="${project.name} v${project.version} changelog"
	 */
	private String reportTitle;

	/**
	 * If true, then a plain text changelog will be generated.
	 *
	 * @parameter default-value="true"
	 */
	private boolean generatePlainTextChangeLog;

	/**
	 * The filename of the plain text changelog, if generated.
	 *
	 * @parameter default-value="changelog.txt"
	 * @required
	 */
	private String plainTextChangeLogFilename;

	/**
	 * If true, then a simple HTML changelog will be generated.
	 *
	 * @parameter default-value="true"
	 */
	private boolean generateSimpleHTMLChangeLog;

	/**
	 * The filename of the simple HTML changelog, if generated.
	 *
	 * @parameter default-value="changelog.html"
	 * @required
	 */
	private String simpleHTMLChangeLogFilename;

	/**
	 * If true, the changelog will be printed to the Maven build log during packaging.
	 *
	 * @parameter default-value="false"
	 */
	private boolean verbose;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generating changelog in " + outputDirectory.getAbsolutePath()
				+ " with title " + reportTitle);

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
			generator.generate(reportTitle);
		} catch (IOException e) {
			getLog().warn("Error while generating changelog.  Some changelogs may be incomplete or corrupt.", e);
		}
	}

	private List<ChangeLogRenderer> createRenderers() throws IOException {
		ArrayList<ChangeLogRenderer> renderers = new ArrayList<ChangeLogRenderer>();

		if (generatePlainTextChangeLog) {
			renderers.add(new PlainTextRenderer(getLog(), outputDirectory, plainTextChangeLogFilename));
		}

		if (generateSimpleHTMLChangeLog) {
			renderers.add(new SimpleHtmlRenderer(getLog(), outputDirectory, simpleHTMLChangeLogFilename));
		}

		if (verbose) {
			renderers.add(new MavenLoggerRenderer(getLog()));
		}

		return renderers;
	}

}
