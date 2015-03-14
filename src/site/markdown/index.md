Overview
--------

This plugin allows the creation of text and HTML changelogs based on the git log. During the Maven packaging
phase this plugin can generate plaintext and HTML reports showing all the commits (with tags) from the local git
repository.  These text files can then be sent to a web server or included during packaging.

Sample output
-------------

Using the default plugin parameters, the changelog for this project looks like the following:

[HTML example](http://danielflower.github.com/maven-gitlog-plugin/samples/changelog.html) /
[Markdown example](http://danielflower.github.com/maven-gitlog-plugin/samples/changelog.md) /
[Text example](http://danielflower.github.com/maven-gitlog-plugin/samples/changelog.txt)
