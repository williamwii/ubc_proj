package ca.ubc.cs.cpsc211.photo;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that manages a set of tags and enforces that
 * only a single tag with a given name exists in the system.
 */
public class TagManager {

	//Map from tag names to tags
	private Map<String, Tag> tags = new HashMap<String, Tag>();

	/**
	 * Get the tag with the given name, or null if no such tag exists.
	 */
	public Tag findTag(String name){
		return tags.get(name);
	}
	
	/**
	 * Create and return a new tag with the given name.
	 * @pre name != null
	 * @throws DuplicateTagException if there is already a tag with the provided name.
	 */
	public Tag createTag(String name) throws DuplicateTagException {
		assert(name != null);
		
		if(tags.containsKey(name))
			throw new DuplicateTagException();
		
		Tag tag = new Tag(name);
		tags.put(name, tag);
		return tag;
	}
	
	/**
	 * Attempt to rename a tag. newName must not be null.
	 * Returns true/false
	 * @throws DuplicateTagException if there is a different tag with the provided name.
	 */
	public boolean renameTag(String oldName, String newName) throws DuplicateTagException {
		
		if(oldName.equals(newName)) return false;
		
		Tag tag = tags.get(oldName);
		if(tag == null) return false;
		
		if(tags.containsKey(newName))
			throw new DuplicateTagException();
		
		tags.remove(oldName);
		tag.setName(newName);
		tags.put(newName, tag);
		return true;
	}
	
	/**
	 * @return The set of tags 
	 */
	public Set<Tag> getTags(){
		Set<Tag> tagSet = new HashSet<Tag>();
		tagSet.addAll(tags.values());
		return Collections.unmodifiableSet(tagSet);
	}

	/**
	 * Remove a tag from the system.
	 * @return false if there is no tag with the provided name.
	 */
	public boolean removeTag(String name){
		Tag tag = tags.get(name);
		if(tag == null) return false;
		
		Set<Photo> photos = tag.getPhotos();
		for (Photo photo : photos)
			photo.removeTag(tag);

		tags.remove(name);
		return true;
	}
	
}