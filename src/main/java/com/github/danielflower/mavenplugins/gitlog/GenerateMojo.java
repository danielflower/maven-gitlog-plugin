package com.github.danielflower.mavenplugins.gitlog;

import com.github.danielflower.mavenplugins.gitlog.renderers.*;

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
	 * If true, then a markdown changelog will be generated.
	 *
	 * @parameter default-value="false"
	 */
	private boolean generateMarkdownChangeLog;

	/**
	 * The filename of the markdown changelog, if generated.
	 *
	 * @parameter default-value="changelog.md"
	 * @required
	 */
	private String markdownChangeLogFilename;

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
	 * If true, then an HTML changelog which contains only a table element will be generated.
	 * This incomplete HTML page is suitable for inclusion in other webpages, for example you
	 * may want to embed it in a wiki page.
	 *
	 * @parameter default-value="false"
	 */
	private boolean generateHTMLTableOnlyChangeLog;

	/**
	 * The filename of the HTML table changelog, if generated.
	 *
	 * @parameter default-value="changelogtable.html"
	 * @required
	 */
	private String htmlTableOnlyChangeLogFilename;

	/**
	 * If true, the changelog will be printed to the Maven build log during packaging.
	 *
	 * @parameter default-value="false"
	 */
	private boolean verbose;

	/**
	 * Used to create links to your issue tracking system for HTML reports. If unspecified, it will try to use the value
	 * specified in the issueManagement section of your project's POM.  The following values are supported:
	 * a value containing the string "github" for the GitHub Issue tracking software;
	 * a value containing the string "jira" for Jira tracking software.
	 * Any other value will result in no links being made.
	 *
	 * @parameter expression="${project.issueManagement.system}"
	 */
	private String issueManagementSystem;

	/**
	 * Used to create links to your issue tracking system for HTML reports. If unspecified, it will try to use the value
	 * specified in the issueManagement section of your project's POM.
	 *
	 * @parameter expression="${project.issueManagement.url}"
	 */
	private String issueManagementUrl;
	
	/**
	 * Used to set date format in log messages. If unspecified, will be used default format 'yyyy-MM-dd HH:mm:ss Z'.
	 * 
	 * @parameter default-value=""
	 */
	private String dateFormat;

	/**
	 * If true, the changelog will include the full git message rather that the short git message
	 *
	 * @parameter default-value="false"
	 */
	private boolean fullGitMessage;
	
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

		if (!"".equals(dateFormat)) {
			Formatter.setFormat(dateFormat, getLog());
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
			renderers.add(new PlainTextRenderer(getLog(), outputDirectory, plainTextChangeLogFilename, fullGitMessage));
		}

		if (generateSimpleHTMLChangeLog || generateHTMLTableOnlyChangeLog || generateMarkdownChangeLog) {
			MessageConverter messageConverter = getCommitMessageConverter();
			if (generateSimpleHTMLChangeLog) {
				renderers.add(new SimpleHtmlRenderer(getLog(), outputDirectory, simpleHTMLChangeLogFilename, fullGitMessage, messageConverter, false));
			}
			if (generateHTMLTableOnlyChangeLog) {
				renderers.add(new SimpleHtmlRenderer(getLog(), outputDirectory, htmlTableOnlyChangeLogFilename, fullGitMessage, messageConverter, true));
			}
			if (generateMarkdownChangeLog) {
				renderers.add(new MarkdownRenderer(getLog(), outputDirectory, markdownChangeLogFilename, fullGitMessage, messageConverter));
			}
		}

		if (verbose) {
			renderers.add(new MavenLoggerRenderer(getLog()));
		}

		return renderers;
	}

	private MessageConverter getCommitMessageConverter() {
		getLog().debug("Trying to load issue tracking info: " + issueManagementSystem + " / " + issueManagementUrl);
		MessageConverter converter = null;
		try {
			if (issueManagementUrl != null && issueManagementUrl.contains("://")) {
				String system = ("" + issueManagementSystem).toLowerCase();
				if (system.contains("jira")) {
					converter = new JiraIssueLinkConverter(getLog(), issueManagementUrl);
				} else if (system.contains("github")) {
					converter = new GitHubIssueLinkConverter(getLog(), issueManagementUrl);
				}
			}
		} catch (Exception ex) {
			getLog().warn("Could not load issue management system information; no HTML links will be generated.", ex);
		}
		if (converter == null) {
			converter = new NullMessageConverter();
		}
		getLog().debug("Using tracker " + converter.getClass().getSimpleName());
		return converter;
	}

}
