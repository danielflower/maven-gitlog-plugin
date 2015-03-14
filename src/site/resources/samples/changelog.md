# Maven GitLog Plugin changelog

2015-03-12 20:54:01 +0800    Add escapes for tab "\t", backspace "\b", form feed "\f" and carriage return "\r", may create invalid JSON that can't be parsed otherwise. (Kristoffer Lundén)  

**maven-gitlog-plugin-1.7.0**  
2014-05-25 20:53:01 +0800    Updated the version in preparation of release (Daniel Flower)  
2014-04-26 04:54:08 +0800    add json file renderer (Keegan Roth)  
2014-04-26 03:57:28 +0800    refactor resource loading into method in parent for re-use (Keegan Roth)  
2014-04-26 03:28:31 +0800    remove resource leak warning in FileRenderer.convertStreamToString() (Keegan Roth)  
2014-04-26 03:27:11 +0800    move convertStreamToString() to parent class for re-use (Keegan Roth)  
2014-04-10 22:11:18 +0800    Updated version to use in readme (Daniel Flower)  

**maven-gitlog-plugin-1.6.0**  
2014-04-10 22:00:15 +0800    Bumping minor version for new feature (Daniel Flower)  
2014-04-07 23:46:59 +0800    Include example of how to use an artifact that contains additional filters (Ivan Martinez-Ortiz)  
2014-04-07 23:43:13 +0800    Include example of how to override includeCommitsAfter parameter. (Ivan Martinez-Ortiz)  
2014-03-31 21:26:10 +0800    Add showCommitsAfter parameter to include in the changelog commits only with a timestamp after the provided parameter value. (Ivan Martinez-Ortiz)  
2014-03-21 06:25:07 +0800    Add SPI loading mechanism for CommitFilters. (Ivan Martinez-Ortiz)  

**maven-gitlog-plugin-1.5.2**  
2013-10-21 22:30:31 +0800    Bumping readme version in anticipation of next release (Daniel Flower)  

**maven-gitlog-plugin-1.5.1**  
2013-10-21 22:05:20 +0800    Bumped version in readme in anticipation of upcoming release (Daniel Flower)  
2013-10-20 22:39:38 +0800    bump eclipse.jgit version (Artem Koshelev)  
2013-10-20 18:16:10 +0800    update readme (Artem Koshelev)  
2013-10-20 18:08:44 +0800    add hamcrest dependency (Artem Koshelev)  
2013-10-20 17:48:07 +0800    fix - create default date format (Artem Koshelev)  
2013-10-20 17:42:32 +0800    provide ability to setup date format in Formatter add tests for Formatter bump up junit version to 4.11 use junit4 assertions in all tests (Artem Koshelev)  
2013-10-19 19:47:17 +0800    move hardcoded strings to constants (Artem Koshelev)  
2013-01-12 12:59:52 +0800    Removing intellij files from maven repo. Better to be IDE agnostic. (Daniel Flower)  
2013-01-12 12:53:01 +0800    Fixed typo in markdown link... getting there.... (Daniel Flower)  
2013-01-12 12:52:01 +0800    Updated plugin verison to use in the readme to 1.5.0 which is the first version to support markdown generation. (Daniel Flower)  

**maven-gitlog-plugin-1.5.0**  
2013-01-12 12:41:18 +0800    Added link to markdown sample in the readme (Daniel Flower)  
2013-01-12 12:35:26 +0800    Creating markdown sample (Daniel Flower)  
2013-01-12 12:31:10 +0800    Adding travis-ci build status to readme (Daniel Flower)  
2013-01-12 12:24:06 +0800    Adding travis-ci build (Daniel Flower)  
2013-01-07 05:07:48 +0800    Refactored code for easier testing. Added test cases for the markdown renderer and the markdown link converter. OPEN - task 10: Generate markdown formatted gitlog  https://github.com/danielflower/maven-gitlog-plugin/issues/issue/10 (Gerd Zanker)  
2013-01-05 21:12:46 +0800    OPEN - task 10: Generate markdown formatted gitlog  https://github.com/danielflower/maven-gitlog-plugin/issues/issue/10 Implemented markdown renderer and simple tests. Updated readme with new available maven plugin parameters. (Gerd Zanker)  

**maven-gitlog-plugin-1.4.11**  
2012-03-17 14:33:55 +0800    Updated maven version in docs (Daniel Flower)  

**maven-gitlog-plugin-1.4.10**  
2012-03-17 14:06:40 +0800    Updated maven version in readme (Daniel Flower)  
2012-03-17 14:02:48 +0800    Random intellij updates (Daniel Flower)  
2012-03-15 08:21:28 +0800    Added property to configure use of full git message (James Rawlings)  

**maven-gitlog-plugin-1.4.9**  
2011-09-21 21:56:28 +0800    Allow generation of table-only HTML reports. Closes [#7](https://github.com/danielflower/maven-gitlog-plugin/issues/7) (Daniel Flower)  

**maven-gitlog-plugin-1.4.8**  
2011-09-19 22:17:47 +0800    Added links to example reports (Daniel Flower)  

**maven-gitlog-plugin-1.4.7**  
2011-09-19 21:17:29 +0800    Preparing for initial release to OSS repository (Daniel Flower)  
2011-09-18 17:01:05 +0800    [#3](https://github.com/danielflower/maven-gitlog-plugin/issues/3) Adding support for issue management tools. Updated documentation. Closes [#3](https://github.com/danielflower/maven-gitlog-plugin/issues/3) (Daniel Flower)  
2011-09-18 16:54:22 +0800    [#3](https://github.com/danielflower/maven-gitlog-plugin/issues/3) Adding support for issue management tools (Daniel Flower)  
2011-09-18 16:52:47 +0800    [#3](https://github.com/danielflower/maven-gitlog-plugin/issues/3) Adding support for issue management tools and added config as described in [gh-4](https://github.com/danielflower/maven-gitlog-plugin/issues/4) (a.k.a [GH-4](https://github.com/danielflower/maven-gitlog-plugin/issues/4)/[#4](https://github.com/danielflower/maven-gitlog-plugin/issues/4)) (Daniel Flower)  
2011-09-18 16:48:14 +0800    [#3](https://github.com/danielflower/maven-gitlog-plugin/issues/3) Adding support for issue management tools (Daniel Flower)  
2011-09-18 15:17:45 +0800    Added author to plaintext renderer (Daniel Flower)  
2011-09-18 14:50:07 +0800    [#3](https://github.com/danielflower/maven-gitlog-plugin/issues/3) Fix whitespace issues in HTML (Daniel Flower)  
2011-09-18 14:36:21 +0800    [#3](https://github.com/danielflower/maven-gitlog-plugin/issues/3) The HTML renderer should HTML-encode tags like <this>, or < or > signs, or & => &amp;, and also " => &quot; etc (Daniel Flower)  

**maven-gitlog-plugin-1.4.5**  
2011-09-18 14:12:09 +0800    Added instructions on how to run the show goal (Daniel Flower)  
2011-09-18 14:06:08 +0800    Rearranged packages (Daniel Flower)  
2011-09-18 13:58:03 +0800    Filter out merge commits from the changelog.  Closes [#6](https://github.com/danielflower/maven-gitlog-plugin/issues/6) (Daniel Flower)  
2011-09-18 13:49:22 +0800    Fixed comment in unit test (master) (Daniel Flower)  
2011-09-18 13:47:39 +0800    Cleaned up HTML (Daniel Flower)  
2011-09-18 13:45:41 +0800    Preparing for release (Daniel Flower)  
2011-09-18 12:09:54 +0800    Fixed groupid and added new show goal (Daniel Flower)  
2011-09-18 00:21:12 +0800    [#4](https://github.com/danielflower/maven-gitlog-plugin/issues/4) Fixed name of README file (Daniel Flower)  
2011-09-18 00:19:57 +0800    [#4](https://github.com/danielflower/maven-gitlog-plugin/issues/4) Allow configuration of renderers (Daniel Flower)  
2011-09-17 22:21:36 +0800    [#3](https://github.com/danielflower/maven-gitlog-plugin/issues/3) Added Simple HTML formatter (Daniel Flower)  
2011-09-17 21:23:30 +0800    Cleaned up access modifiers (Daniel Flower)  
2011-09-17 18:17:16 +0800    [gh-4](https://github.com/danielflower/maven-gitlog-plugin/issues/4) Fixed bug in filtering when there are multiple renderers (Daniel Flower)  
2011-09-17 17:48:10 +0800    [GH-4](https://github.com/danielflower/maven-gitlog-plugin/issues/4) Improve the error reporting for when no git repository is found (Daniel Flower)  
2011-09-17 17:30:35 +0800    [#4](https://github.com/danielflower/maven-gitlog-plugin/issues/4) Set up the GenerateMojo to execute the plugin (Daniel Flower)  
2011-09-17 17:15:12 +0800    Fixed issue management node (Daniel Flower)  
2011-09-17 17:06:07 +0800    Do not show maven release plugin messages.  Closes [GH-1](https://github.com/danielflower/maven-gitlog-plugin/issues/1) (Daniel Flower)  
2011-09-17 17:00:46 +0800    Do not show duplicate commit messages.  Closes [#2](https://github.com/danielflower/maven-gitlog-plugin/issues/2) (Daniel Flower)  
2011-09-17 16:47:53 +0800    Fixed some project setup issues (Daniel Flower)  

**maven-gitlog-plugin-1.2**  
2011-09-17 16:09:37 +0800    Temporarily adding local repository for deployments (Daniel Flower)  

**maven-gitlog-plugin-1.1**  
2011-09-17 15:54:53 +0800    Fixing maven release issues (Daniel Flower)  
2011-09-17 15:53:21 +0800    Fixed github repo URLs (Daniel Flower)  
2011-09-17 15:42:49 +0800    Got PlainText renderer working (Daniel Flower)  
2011-09-17 15:18:59 +0800    Refactored sout logger to use the maven log interface (Daniel Flower)  
2011-09-17 15:03:09 +0800    Added support for reading annotated tags (Daniel Flower)  
2011-09-17 13:10:35 +0800    Renaming project (Daniel Flower)  
2011-09-17 02:42:36 +0800    Can write the list of commits (Daniel Flower)  
2011-09-17 02:31:45 +0800    Initial attempt to use jgit (Daniel Flower)  
2011-09-17 01:38:24 +0800    Got initial mojo set up to write to a text file (Daniel Flower)  
2011-09-17 00:58:33 +0800    Initial maven setup (Daniel Flower)  
2011-09-17 00:26:19 +0800    first commit (Daniel Flower)  
