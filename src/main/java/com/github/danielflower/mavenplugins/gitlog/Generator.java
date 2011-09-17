package com.github.danielflower.mavenplugins.gitlog;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Generator {

	private final List<ChangeLogRenderer> renderers;
	private RevWalk walk;
	private Map<String, List<RevTag>> commitIDToTagsMap;
	private final Log log;

	public Generator(List<ChangeLogRenderer> renderers, Log log) {
		this.renderers = renderers;
		this.log = log;
	}

	public void openRepository() throws IOException {
		Repository repository = new RepositoryBuilder().findGitDir().build();
		log.debug("Opened " + repository);
		walk = createWalk(repository);
		log.debug("Loaded commits.");
		commitIDToTagsMap = createCommitIDToTagsMap(repository, walk);
		log.debug("Loaded tag map: " + commitIDToTagsMap);
	}

	public void generate() throws IOException {
		for (ChangeLogRenderer renderer : renderers) {
			renderer.renderHeader();
		}

		for (RevCommit commit : walk) {
			List<RevTag> revTags = commitIDToTagsMap.get(commit.name());
			for (ChangeLogRenderer renderer : renderers) {
				if (revTags != null) {
					for (RevTag revTag : revTags) {
						renderer.renderTag(revTag);
					}
				}
				renderer.renderCommit(commit);
			}
		}
		walk.dispose();


		for (ChangeLogRenderer renderer : renderers) {
			renderer.renderFooter();
		}
	}

	private static RevWalk createWalk(Repository repository) throws IOException {
		RevWalk walk = new RevWalk(repository);
		ObjectId head = repository.resolve("HEAD");
		RevCommit mostRecentCommit = walk.parseCommit(head);
		walk.markStart(mostRecentCommit);
		return walk;
	}


	private Map<String, List<RevTag>> createCommitIDToTagsMap(Repository repository, RevWalk revWalk) throws IOException {
		Map<String, Ref> allTags = repository.getTags();

		Map<String, List<RevTag>> revTags = new HashMap<String, List<RevTag>>();

		for (Ref ref : allTags.values()) {
			try {
				RevTag revTag = revWalk.parseTag(ref.getObjectId());
				String commitID = revTag.getObject().getId().getName();
				if (!revTags.containsKey(commitID)) {
					revTags.put(commitID, new ArrayList<RevTag>());
				}
				revTags.get(commitID).add(revTag);
			} catch (IncorrectObjectTypeException e) {
				log.debug("Light-weight tags not supported. Skipping " + ref.getName());
			}
		}

		return revTags;
	}


}
