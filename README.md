Overview
========

This plugin allows the creation of text and HTML changelogs based on the git log. During the Maven packaging
phase this plugin can generate plaintext and HTML reports showing all the commits (with tags) from the local git
repository.  These text files can then be sent to a web server or included during packaging.

Sample output
=============

Using the default plugin parameters, the changelog for this project looks like the following:

[HTML example](http://danielflower.github.com/maven-gitlog-plugin/samples/changelog.html) /
[Text example](http://danielflower.github.com/maven-gitlog-plugin/samples/changelog.txt)

Configuring the plugin
======================

Add the following to your `<plugins>` section in your pom.xml file to generate changelog.txt and changelog.html
in your target folder:

	<plugin>
		<groupId>com.github.danielflower.mavenplugins</groupId>
		<artifactId>maven-gitlog-plugin</artifactId>
		<version>1.4.7</version>
		<executions>
			<execution>
				<goals>
					<goal>generate</goal>
				</goals>
			</execution>
		</executions>
	</plugin>

HTML reports will render issue codes as links if you have your issue tracking system is defined in your pom file.
Currently only Jira and GitHub issue tracking is supported.

	<issueManagement>
		<system>GitHub</system>
		<url>https://github.com/danielflower/maven-gitlog-plugin/issues</url>
	</issueManagement>

The following example shows all the possible configuration values with default values overridden.

	<plugin>
		<groupId>com.github.danielflower.mavenplugins</groupId>
		<artifactId>maven-gitlog-plugin</artifactId>
		<version>1.4.7</version>
		<configuration>
			<verbose>true</verbose>
			<generatePlainTextChangeLog>true</generatePlainTextChangeLog>
			<plainTextChangeLogFilename>changelog-${project.version}.txt</plainTextChangeLogFilename>
			<outputDirectory>target/docs</outputDirectory>
			<generateSimpleHTMLChangeLog>true</generateSimpleHTMLChangeLog>
			<simpleHTMLChangeLogFilename>changelog-${project.version}.html</simpleHTMLChangeLogFilename>
			<reportTitle>Changelog for ${project.name} version ${project.version}</reportTitle>
			<issueManagementSystem>GitHub issue tracker</issueManagementSystem>
			<issueManagementUrl>https://github.com/danielflower/maven-gitlog-plugin/issues</issueManagementUrl>
		</configuration>
		<executions>
			<execution>
				<goals>
					<goal>generate</goal>
				</goals>
			</execution>
		</executions>
	</plugin>

Including the changelog in your Maven assembly
----------------------------------------------

If you want to include the changelogs in your assembled package, then you will need to configure the Maven
assembly plugin to use a custom descriptor.  The following is an example of a descriptor that creates a zip
file which includes the two changelog reports in the docs folder of the zip file:

	<assembly xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0"
			  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			  xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0 http://maven.apache.org/xsd/assembly-1.1.0.xsd">
		<id>full</id>
		<formats>
			<format>zip</format>
		</formats>
		<includeBaseDirectory>false</includeBaseDirectory>
		<dependencySets>
			<dependencySet>
				<outputDirectory>/libs</outputDirectory>
				<useProjectArtifact>true</useProjectArtifact>
				<scope>runtime</scope>
			</dependencySet>
		</dependencySets>
		<fileSets>
			<fileSet>
				<directory>target</directory>
				<includes>
					<include>changelog.*</include>
				</includes>
				<outputDirectory>docs</outputDirectory>
			</fileSet>
		</fileSets>
	</assembly>

Showing the git changelog for your current project
--------------------------------------------

If your project has a reference to the gitlog plugin already, the following command will print
a changelog to the command line:

	$ mvn gitlog:show

If you want to see a git log without having the plugin defined in your pom, you can run:

	$ mvn com.github.danielflower.mavenplugins:maven-gitlog-plugin:show
