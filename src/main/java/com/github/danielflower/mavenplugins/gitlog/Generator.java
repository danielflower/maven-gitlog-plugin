package com.github.danielflower.mavenplugins.gitlog;

import com.github.danielflower.mavenplugins.gitlog.filters.CommitFilter;
import com.github.danielflower.mavenplugins.gitlog.renderers.ChangeLogRenderer;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.*;

class Generator {

	private final List<ChangeLogRenderer> renderers;
	private RevWalk walk;
	private Map<String, List<RevTag>> commitIDToTagsMap;
	private final List<CommitFilter> commitFilters;
	private final Log log;
	


	public Generator(List<ChangeLogRenderer> renderers, List<CommitFilter> commitFilters, Log log) {
		this.renderers = renderers;
		this.commitFilters = (commitFilters == null) ? new ArrayList<CommitFilter>() : commitFilters;
		this.log = log;
	}

	public Repository openRepository() throws IOException, NoGitRepositoryException {
		return openRepository(null);
	}

	public Repository openRepository(File gitdir) throws IOException, NoGitRepositoryException {
		log.debug("About to open git repository.");
		Repository repository;
		try {
                    if ( gitdir == null ) {
			repository = new RepositoryBuilder().findGitDir().build();
                    } else {
			repository = new RepositoryBuilder().findGitDir(gitdir).build();
                    }
		} catch (IllegalArgumentException iae) {
			throw new NoGitRepositoryException();
		}
		log.debug("Opened " + repository + ". About to load the commits.");
		walk = createWalk(repository);
		log.debug("Loaded commits. about to load the tags.");
		commitIDToTagsMap = createCommitIDToTagsMap(repository, walk);
		log.debug("Loaded tag map: " + commitIDToTagsMap);

		return repository;
	}

	
	public void generate(String reportTitle) throws IOException {
		generate(reportTitle, new Date(0l));
	}

	public void generate(String reportTitle, Date includeCommitsAfter) throws IOException {
		for (ChangeLogRenderer renderer : renderers) {
			renderer.renderHeader(reportTitle);
		}

		long dateInSecondsSinceEpoch = includeCommitsAfter.getTime() / 1000;
		for (RevCommit commit : walk) {
			int commitTimeInSecondsSinceEpoch = commit.getCommitTime();
			if (dateInSecondsSinceEpoch < commitTimeInSecondsSinceEpoch) {
				List<RevTag> revTags = commitIDToTagsMap.get(commit.name());
				for (ChangeLogRenderer renderer : renderers) {
					if (revTags != null) {
						for (RevTag revTag : revTags) {
							renderer.renderTag(revTag);
						}
					}
				}
				if (show(commit)) {
					for (ChangeLogRenderer renderer : renderers) {
						renderer.renderCommit(commit);
					}
				}
			}
		}
		walk.dispose();


		for (ChangeLogRenderer renderer : renderers) {
			renderer.renderFooter();
			renderer.close();
		}
	}
	
	public void generate(String reportTitle , Repository repository ,boolean includeOnlyCommitsAfterRelease , String nameTagRelease) throws IOException {
		generate(reportTitle, repository, new Date(0l) ,includeOnlyCommitsAfterRelease, nameTagRelease);
	}

	public void generate(String reportTitle, Repository repository, Date includeCommitsAfter , boolean includeOnlyCommitsAfterRelease ,String nameTagRelease) throws IOException {
		for (ChangeLogRenderer renderer : renderers) {
			renderer.renderHeader(reportTitle);
		}

		long dateInSecondsSinceEpoch = includeCommitsAfter.getTime() / 1000;

		

			List<RevCommit> listCommitAll = this.getAllCommit(walk);
			
			List<RevCommit> listCommit=new ArrayList <RevCommit>() ;
            if (includeOnlyCommitsAfterRelease)
            {
            	listCommit = getReleaseCommits(walk, listCommitAll, nameTagRelease);	
            }
            else {
            	listCommit=listCommitAll ;
            }
			
			for (RevCommit commit : listCommit) {

				int commitTimeInSecondsSinceEpoch = commit.getCommitTime();
				if (dateInSecondsSinceEpoch < commitTimeInSecondsSinceEpoch) {
					List<RevTag> revTags = commitIDToTagsMap.get(commit.name());
					for (ChangeLogRenderer renderer : renderers) {
						if (revTags != null) {
							for (RevTag revTag : revTags) {
								renderer.renderTag(revTag);
							}
						}
					}
					if (show(commit)) {
						List<DiffEntry> listDiffEntry = getListFileCommit(commit ,repository);

						for (ChangeLogRenderer renderer : renderers) {
							renderer.setListDiffEntry(listDiffEntry);
							renderer.renderCommit(commit);
						}
					}
				}
			}

		walk.dispose();


		for (ChangeLogRenderer renderer : renderers) {
			renderer.renderFooter();
			renderer.close();
		}
	}

	private boolean show(RevCommit commit) {
		for (CommitFilter commitFilter : commitFilters) {
			if (!commitFilter.renderCommit(commit)) {
				log.debug("Commit filtered out by " + commitFilter.getClass().getSimpleName());
				return false;
			}
		}
		return true;
	}

	private static RevWalk createWalk(Repository repository) throws IOException {
		RevWalk walk = new RevWalk(repository);
		ObjectId head = repository.resolve("HEAD");
		if (head != null) {
			// if head is null, it means there are no commits in the repository.  The walk will be empty.
			RevCommit mostRecentCommit = walk.parseCommit(head);
			walk.markStart(mostRecentCommit);
		}
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
   
	private List<RevCommit> getAllCommit(RevWalk i_walk) {
		List<RevCommit> i_listCommit = new ArrayList<RevCommit>();
		for (RevCommit commit : i_walk) {
			i_listCommit.add(commit);
		}

		return i_listCommit;

	}
	
	private List<DiffEntry> getListFileCommit(RevCommit commit , Repository repository )
			throws IOException {
		try {

			RevCommit parent = null;
			if (commit.getParent(0) != null) {
				parent = walk.parseCommit(commit.getParent(0).getId());
			}

			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);

			df.setRepository(repository);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);

			return df.scan(parent.getTree(), commit.getTree());
		} catch (ArrayIndexOutOfBoundsException e)

		{
			return new ArrayList<DiffEntry>();
		}
	}
	 
	public List<RevCommit> getReleaseCommits(RevWalk i_walk , List<RevCommit> listCommit , String includeOnlyCommitsAfterTag) throws MissingObjectException, IncorrectObjectTypeException, IOException
	{
		
		List<RevCommit> commitsRelease = getLastCommitRelease(i_walk , listCommit , includeOnlyCommitsAfterTag) ;
		
		List<RevCommit> i_listcommit = new ArrayList <RevCommit> ();
		
		for (RevCommit commit : listCommit) 
		{  
			
			 for (RevCommit i_commit : commitsRelease) 
	          {
		      if (i_walk.isMergedInto(i_commit , commit)&& i_commit.getCommitTime() <= commit.getCommitTime())
		         {
		    	  i_listcommit.add(commit);
		    	  break ;
		    
		        }
		    
	          } 
			
		}
		
	return i_listcommit;
	}

	
	public List<RevCommit>  getLastCommitRelease(RevWalk i_walk , List<RevCommit> listCommit , String includeOnlyCommitsAfterTag) throws MissingObjectException, IncorrectObjectTypeException, IOException
	{

		
		List<RevCommit> commitsRelease = new ArrayList <RevCommit> ();
		
		for (RevCommit commit : listCommit) 
		{  if (isReleaseCommit(commit)||isCommitTagRelease(commit , includeOnlyCommitsAfterTag))
		     { boolean addcommit=true ;  
			   if (commitsRelease.isEmpty())
			      {
				   addcommit=true ;
			     }
			   else {
				   for (RevCommit i_commit : commitsRelease) 
			          {
				      if (i_walk.isMergedInto(i_commit , commit)&& i_commit.getCommitTime() <= commit.getCommitTime())
				         {
					    commitsRelease.remove(i_commit);
					    addcommit=true;
				        }
				      else if (i_walk.isMergedInto(commit , i_commit)&& i_commit.getCommitTime() > commit.getCommitTime()){
				    	  addcommit=false;
				      }
			          } 
			       }
		 if (addcommit)
		 {
			 commitsRelease.add(commit);
		 }
		}
         
		}
     return commitsRelease;
		
	   
	
	}

	
	public boolean isCommitTagRelease(RevCommit commit , String tag_release)
	{

		List<RevTag> revTags = commitIDToTagsMap.get(commit.name());
		if (revTags != null)
			
		{
			for (RevTag revTag : revTags) {
				if (StringUtils.contains(revTag.getTagName(),tag_release))
				{
					return true ;
				}
			}
		
		}
		return false ;
	}
   
	private boolean isReleaseCommit(RevCommit commit) {
		boolean isRelease =false;
		if (commit.getShortMessage().startsWith(ConfigProperties.getExistingProperty("last.commit.maven.release")))
		{
			isRelease=true;
		}
		else
		{
			isRelease=false;
		}
		return isRelease;
	}
	
}
