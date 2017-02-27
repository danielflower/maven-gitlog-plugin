package com.github.danielflower.mavenplugins.gitlog;

import java.io.File;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * install the report in the local repository.
 * 
 * @author <a href="mailto:aramirez@apache.org">Allan Ramirez</a>
 */
@Mojo(name = "install", defaultPhase = LifecyclePhase.PACKAGE, aggregator = true)
public class InstallReportMojo extends AbstractMojo {

	/**
	 * GroupId of the artifact to be deployed. Retrieved from POM file if
	 * specified.
	 * 
	 */
	@Parameter(property = "project.groupId")
	private String groupId;

	/**
	 * ArtifactId of the artifact to be deployed. Retrieved from POM file if
	 * specified.
	 */
	@Parameter(property = "project.artifactId")
	private String artifactId;

	/**
	 * Version of the artifact to be deployed. Retrieved from POM file if
	 * specified.
	 * 
	 */
	@Parameter(property = "project.version")
	private String version;

	/**
	 * report type"
	 */
	@Parameter(defaultValue = "html")
	private String packaging;

	/**
	 * Server Id to map on the &lt;id&gt; under &lt;server&gt; section of
	 * settings.xml In most cases, this parameter will be required for
	 * authentication.
	 */
	@Parameter(property = "project.distributionManagementArtifactRepository.id")
	private String repositoryId;

	/**
	 * The type of remote repository layout to deploy to. Try <i>legacy</i> for
	 * a Maven 1.x-style repository layout.
	 */
	@Parameter(defaultValue = "default")
	private String repositoryLayout;

	/**
	 * URL where the artifact will be deployed. <br/>
	 * ie ( file:///C:/m2-repo or scp://host.com/path/to/repo )
	 * 
	 * @parameter 
	 *            expression="${project.distributionManagementArtifactRepository.url}"
	 */
	@Parameter(property = "project.distributionManagementArtifactRepository.url")
	private String url;

	/**
	 * Add classifier to the artifact
	 */
	@Parameter(defaultValue = "report")
	private String classifier;

	/**
	 * Whether to deploy snapshots with a unique version or not.
	 */
	@Parameter(defaultValue = "true")
	private boolean uniqueVersion;

	/**
	 * The directory to put the reports in. Defaults to the project build
	 * directory (normally target).
	 * 
	 */
	@Parameter(property = "project.build.directory")
	private File outputDirectory;

	/**
	 * The filename of the simple HTML changelog, if generated.
	 * 
	 */
	@Parameter(defaultValue = "changelogfull.html")
	private String fullHTMLChangeLogFilename;

	/**
	 * Component used to create an artifact.
	 */
	@Component
	protected ArtifactFactory artifactFactory;

	@Component
	protected ArtifactInstaller installer;

	@Component
	private Map<String, ArtifactRepositoryLayout> repositoryLayouts;

	@Parameter(property = "localRepository")
	private ArtifactRepository localRepository;

	public void execute() throws MojoExecutionException, MojoFailureException {
		File file = new File(this.outputDirectory,
				this.fullHTMLChangeLogFilename);
		if (!file.exists()) {
			throw new MojoExecutionException(file.getPath() + " not found.");
		}

		// Create the artifact
		Artifact artifact = artifactFactory.createArtifactWithClassifier(
				groupId, artifactId, version, packaging, classifier);

		try {
			installer.install(file, artifact, getLocalRepository());
		} catch (ArtifactInstallationException e) {
			// TODO Auto-generated catch block
			throw new MojoExecutionException("Error installing artifact '"
					+ artifact.getDependencyConflictId() + "': "
					+ e.getMessage(), e);
		}

	}

	void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	void setVersion(String version) {
		this.version = version;
	}

	void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	String getGroupId() {
		return groupId;
	}

	String getArtifactId() {
		return artifactId;
	}

	String getVersion() {
		return version;
	}

	String getPackaging() {
		return packaging;
	}

	String getClassifier() {
		return classifier;
	}

	void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public ArtifactRepository getLocalRepository() {
		return localRepository;
	}

	public void setLocalRepository(ArtifactRepository localRepository) {
		this.localRepository = localRepository;
	}

	ArtifactRepositoryLayout getLayout(String id) throws MojoExecutionException {
		ArtifactRepositoryLayout layout = repositoryLayouts.get(id);

		if (layout == null) {
			throw new MojoExecutionException("Invalid repository layout: " + id);
		}

		return layout;
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isUniqueVersion() {
		return uniqueVersion;
	}

	public void setUniqueVersion(boolean uniqueVersion) {
		this.uniqueVersion = uniqueVersion;
	}

	public ArtifactFactory getArtifactFactory() {
		return artifactFactory;
	}

	public void setArtifactFactory(ArtifactFactory artifactFactory) {
		this.artifactFactory = artifactFactory;
	}

	public Map<String, ArtifactRepositoryLayout> getRepositoryLayouts() {
		return repositoryLayouts;
	}

	public void setRepositoryLayouts(
			Map<String, ArtifactRepositoryLayout> repositoryLayouts) {
		this.repositoryLayouts = repositoryLayouts;
	}

	public ArtifactInstaller getInstaller() {
		return installer;
	}

	public void setInstaller(ArtifactInstaller installer) {
		this.installer = installer;
	}
}
