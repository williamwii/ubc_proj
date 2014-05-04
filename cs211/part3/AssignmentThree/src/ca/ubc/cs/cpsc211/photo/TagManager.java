package ca.ubc.cs.cpsc211.photo;

import java.util.HashSet;
import java.util.Set;


/**
 * Class that manages a set of tags and enforces that
 * only a single tag with a given name exists in the system.
 */
public class TagManager {

	// ADD CLASS ATTRIBUTES AND CONSTRUCTOR(S) HERE
	/**
	 * set of tags in tag manager
	 */
	private Set<Tag> tags;
	
	/**
	 * construct a new tag manager
	 */
	public TagManager(){
		tags = new HashSet<Tag>();
	}

	/**
	 * Get the tag with the given name, or null if no such tag exists.
	 */
	public Tag findTag(String name){
		Tag targetTag = null;
		for ( Tag t : tags ){
			if ( t.getName().equals(name) )
				targetTag = t;
		}
		return targetTag;
	}
	
	/**
	 * Create and return a new tag with the given name.
	 * @pre name != null
	 * @throws DuplicateTagException if there is already a tag with the provided name.
	 */
	public Tag createTag(String name)throws DuplicateTagException {
		if ( findTag(name)!=null )
			throw new DuplicateTagException();
		Tag newTag = new Tag(name);
		tags.add(newTag);
		return newTag;
	}
	
	/**
	 * Attempt to rename a tag. newName must not be null.
	 * Returns true/false
	 * @throws DuplicateTagException if there is a different tag with the provided name.
	 */
	public boolean renameTag(String oldName, String newName)throws DuplicateTagException {
		if ( findTag(newName)!=null )
			throw new DuplicateTagException();
		if ( newName==null )
			return false;
		else{
			findTag(oldName).setName(newName);
			return true;
		}
	}
	
	/**
	 * @return The set of tags that are in the library 
	 */
	public Set<Tag> getTags(){
		return tags;
	}

	/**
	 * Remove a tag from the system.
	 * Does nothing if there is no tag with the provided name.
	 */
	public boolean removeTag(String name){
		if ( findTag(name)==null )
			return false;
		else{
			for ( Photo p : findTag(name).getPhotos() ){
				p.removeTag(findTag(name));
			}
			tags.remove(findTag(name));
			return true;
		}
	}
	
	//MAY ADD METHODS HERE
	
}
