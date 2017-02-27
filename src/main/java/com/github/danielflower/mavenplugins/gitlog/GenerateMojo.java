package com.github.danielflower.mavenplugins.gitlog;

import com.github.danielflower.mavenplugins.gitlog.filters.CommitFilter;
import com.github.danielflower.mavenplugins.gitlog.filters.CommiterFilter;
import com.github.danielflower.mavenplugins.gitlog.filters.PathCommitFilter;
import com.github.danielflower.mavenplugins.gitlog.filters.RegexpFilter;
import com.github.danielflower.mavenplugins.gitlog.renderers.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Goal which generates a changelog based on commits made to the current git repo.
 */
@Mojo(
		name = "generate",
		defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
		aggregator = true // the plugin should only run once against the aggregator pom
)
public class GenerateMojo extends AbstractMojo {

	/**
	 * The directory to put the reports in.  Defaults to the project build directory (normally target).
         *
         * When running as a reporting plugin, the output directory is fixed, set by the reporting cycle.
	 */
	@Parameter(property = "project.build.directory")
	protected File outputDirectory;

	/**
	 * The title of the reports. Defaults to: ${project.name} v${project.version} changelog
	 */
	@Parameter(defaultValue = "${project.name} v${project.version} git changelog")
	private String reportTitle;

	/**
	 * If true, then a plain text changelog will be generated.
	 */
	@Parameter(defaultValue = "true")
	private boolean generatePlainTextChangeLog;

	/**
	 * The filename of the plain text changelog, if generated.
	 */
	@Parameter(defaultValue = "changelog.txt")
	private String plainTextChangeLogFilename;


	/**
	 * If true, then a markdown changelog will be generated.
	 */
	@Parameter(defaultValue = "false")
	private boolean generateMarkdownChangeLog;

	/**
	 * If true, then an Asciidoc changelog will be generated.
	 */
	@Parameter(defaultValue = "false")
	private boolean generatAsciidocChangeLog;

	/**
	 * The filename of the markdown changelog, if generated.
	 */
	@Parameter(defaultValue = "changelog.md")
	private String markdownChangeLogFilename;

	/**
	 * The filename of the Asciidoc changelog, if generated.
	 */
	@Parameter(defaultValue = "changelog.adoc")
	private String asciidocChangeLogFilename;

	/**
	 * If true, then a simple HTML changelog will be generated.
	 */
	@Parameter(defaultValue = "false")
	private boolean generateSimpleHTMLChangeLog;
	
	/**
	 * If true, then a full HTML changelog will be generated.
	 */
	@Parameter(defaultValue = "true")
	private boolean generateFullHTMLChangeLog;

	/**
	 * The filename of the simple HTML changelog, if generated.
	 */
	@Parameter(defaultValue = "changelog.html")
	private String simpleHTMLChangeLogFilename;
	
	/**
	 * The filename of the simple HTML changelog, if generated.
	 */
	@Parameter(defaultValue = "changelogfull.html")
	private String fullHTMLChangeLogFilename;

	/**
	 * If true, then an HTML changelog which contains only a table element will be generated.
	 * This incomplete HTML page is suitable for inclusion in other webpages, for example you
	 * may want to embed it in a wiki page.
	 */
	@Parameter(defaultValue = "false")
	private boolean generateHTMLTableOnlyChangeLog;

	/**
	 * The filename of the HTML table changelog, if generated.
	 */
	@Parameter(defaultValue = "changelogtable.html")
	private String htmlTableOnlyChangeLogFilename;

	/**
	 * If true, then a JSON changelog will be generated.
	 */
	@Parameter(defaultValue = "true")
	private boolean generateJSONChangeLog;

	/**
	 * The filename of the JSON changelog, if generated.
	 */
	@Parameter(defaultValue = "changelog.json")
	private String jsonChangeLogFilename;

	/**
	 * If true, the changelog will be printed to the Maven build log during packaging.
	 */
	@Parameter(defaultValue = "false")
	private boolean verbose;

	/**
	 * Used to create links to your issue tracking system for HTML reports. If unspecified, it will try to use the value
	 * specified in the issueManagement section of your project's POM.  The following values are supported:
	 * a value containing the string "github" for the GitHub Issue tracking software;
	 * a value containing the string "jira" for Jira tracking software.
	 * Any other value will result in no links being made.
	 */
	@Parameter(property = "project.issueManagement.system")
	private String issueManagementSystem;

	/**
	 * Used to create links to your issue tracking system for HTML reports. If unspecified, it will try to use the value
	 * specified in the issueManagement section of your project's POM.
	 */
	@Parameter(property = "project.issueManagement.url")
	private String issueManagementUrl;

	/**
	 * Regexp pattern to extract the number from the message.
	 * <p>
	 * The default is: "Bug (\\d+)".
	 * Group 1 (the number) is used in links, so "?:" must be used any non relevant group,
	 * ex.:
	 * (?:Bug|UPDATE|FIX|ADD|NEW|#) ?#?(\d+)
	 */
	@Parameter(defaultValue = "Bug (\\d+)")
	private String bugzillaPattern;

	/**
	 * Used to set date format in log messages.
	 */
	@Parameter(defaultValue = "yyyy-MM-dd HH:mm:ss Z")
	private String dateFormat;

	/**
	 * If true, the changelog will include the full git message rather that the short git message
	 */
	@Parameter(defaultValue = "false")
	private boolean fullGitMessage;

	/**
	 * Include in the changelog the commits after this parameter value.
	 */
	@Parameter(defaultValue = "1970-01-01 00:00:00.0 AM")
	private Date includeCommitsAfter;

	/**
	 * Exclude in the changelog all commits by a given commiter
	 */
	@Parameter
	private List<String> excludeCommiters;

	/**
	 * Regexp pattern to filter out commits (using Matcher.matches())
	 */
	@Parameter
	private String excludeCommitsPattern;

	/**
	 * If set only displays commits related to files found under this path.
	 */
	@Parameter
	private String path;
	
	/**
	 * Include in the changelog the commits after the last release
	 */
	@Parameter(defaultValue = "false")
	private boolean includeOnlyCommitsAfterRelease;
	
	/**
	 * the tag name of release
	 */
	@Parameter(defaultValue = "releaseTag_")
	private String nameTagRelease;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generating gitlog in " + outputDirectory.getAbsolutePath()
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

		List<CommitFilter> commitFilters = new ArrayList<CommitFilter>(Defaults.COMMIT_FILTERS);

		if (excludeCommiters != null && !excludeCommiters.isEmpty()) {
			commitFilters.add(new CommiterFilter(excludeCommiters));
		}
		if (excludeCommitsPattern != null) {
			commitFilters.add(new RegexpFilter(excludeCommitsPattern));
		}

		Generator generator = new Generator(renderers, commitFilters, getLog());
		Repository repository;

		try {
			 repository = generator.openRepository();
		} catch (IOException e) {
			getLog().warn("Error opening git repository.  Is this Maven project hosted in a git repository? " +
					"No changelog will be generated.", e);
			return;
		} catch (NoGitRepositoryException e) {
			getLog().warn("This maven project does not appear to be in a git repository, " +
					"therefore no git changelog will be generated.");
			return;
		}

		if (path != null) {
			commitFilters.add(new PathCommitFilter(repository, path, getLog()));
		}

		if (!"".equals(dateFormat)) {
			Formatter.setFormat(dateFormat, getLog());
		}

		try {
		
			generator.generate(reportTitle, repository ,includeOnlyCommitsAfterRelease ,nameTagRelease);
			
		} catch (IOException e) {
			getLog().warn("Error while generating gitlog.  Some changelogs may be incomplete or corrupt.", e);
		}
	}

	private List<ChangeLogRenderer> createRenderers() throws IOException {
		ArrayList<ChangeLogRenderer> renderers = new ArrayList<ChangeLogRenderer>();

		if (generatePlainTextChangeLog) {
			renderers.add(new PlainTextRenderer(getLog(), outputDirectory, plainTextChangeLogFilename, fullGitMessage));
		}

		if (generateFullHTMLChangeLog || generateSimpleHTMLChangeLog || generateHTMLTableOnlyChangeLog || generateMarkdownChangeLog || generatAsciidocChangeLog) {
			MessageConverter messageConverter = getCommitMessageConverter();
			if (generateFullHTMLChangeLog) {
				renderers.add(new FullHtmlRenderer(getLog(), outputDirectory, fullHTMLChangeLogFilename, fullGitMessage, messageConverter, false));
			}
			if (generateSimpleHTMLChangeLog) {
				renderers.add(new SimpleHtmlRenderer(getLog(), outputDirectory, simpleHTMLChangeLogFilename, fullGitMessage, messageConverter, false));
			}
			if (generateHTMLTableOnlyChangeLog) {
				renderers.add(new SimpleHtmlRenderer(getLog(), outputDirectory, htmlTableOnlyChangeLogFilename, fullGitMessage, messageConverter, true));
			}
			if (generateMarkdownChangeLog) {
				renderers.add(new MarkdownRenderer(getLog(), outputDirectory, markdownChangeLogFilename, fullGitMessage, messageConverter));
			}
			if (generatAsciidocChangeLog) {
				renderers.add(new AsciidocRenderer(getLog(), outputDirectory, asciidocChangeLogFilename, fullGitMessage, messageConverter));
			}
		}

		if (generateJSONChangeLog) {
			renderers.add(new JsonRenderer(getLog(), outputDirectory, jsonChangeLogFilename, fullGitMessage));
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
				if (system.toLowerCase().contains("jira")) {
					converter = new JiraIssueLinkConverter(getLog(), issueManagementUrl);
				} else if (system.toLowerCase().contains("github")) {
					converter = new GitHubIssueLinkConverter(getLog(), issueManagementUrl);
				} else if (system.toLowerCase().contains("bugzilla")) {
					converter = new BugzillaIssueLinkConverter(getLog(), issueManagementUrl,
							bugzillaPattern);
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
