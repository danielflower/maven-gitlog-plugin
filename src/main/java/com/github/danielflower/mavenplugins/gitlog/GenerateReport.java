package com.github.danielflower.mavenplugins.gitlog;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.Locale;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;

/**
 * Goal which generates a changelog based on commits made to the current git repo.
 */
@Mojo(
        name = "report"
)
public class GenerateReport extends GenerateMojo implements MavenReport {

    @Override
    public void generate(Sink sink, Locale locale)
        throws MavenReportException
    {
            try {
                execute();
            } catch (MojoExecutionException ex) {
                getLog().error(ex.getMessage(), ex);
            } catch (MojoFailureException ex) {
                getLog().error(ex.getMessage(), ex);
            }
    }

	@Override
    public String getOutputName()
    {
        return "gitlog";
    }

    @Override
    public String getCategoryName()
    {
        return CATEGORY_PROJECT_REPORTS;
    }

    @Override
    public String getName(Locale locale)
    {
        return "GitLog";
    }

    @Override
    public String getDescription(Locale locale)
    {
        return "Generate changelog from git SCM.";
    }

    /**
     * When running as a reporting plugin, the output directory is fixed, set by the reporting cycle.
     */
    @Override
    public void setReportOutputDirectory(File file)
    {
        outputDirectory = file;
    }

    @Override
    public File getReportOutputDirectory()
    {
        return outputDirectory;
    }

    @Override
    public boolean isExternalReport()
    {
        return true;
    }

    @Override
    public boolean canGenerateReport()
    {
        return true;
    }

}
