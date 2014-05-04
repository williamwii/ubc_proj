package ca.ubc.cs.cpsc211.photo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * A tag, consisting of a name and an added date.
 */
public class Tag {

	
	//ADD CLASS ATTRIBUTES AND CONSTRUCTOR(S) HERE
	/**
	 * name of the tag
	 */
	private String name;
	
	/**
	 * date of the tag's creation
	 */
	private Date dateAdded;
	
	/**
	 * the set of photos in the tag
	 */
	private Set<Photo> photos;
	
	/**
	 * construct a new tag withe the given name
	 * @param name name of the tag
	 */
	public Tag(String name){
		this.name = name;
		photos = new HashSet<Photo>();
		dateAdded = new Date();
	}
	
	/**
	 * @return The tag name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The date this tag was created
	 */
	public Date getDateAdded() {
		return dateAdded;
	}

	
	/**
	 * Set the tag name. Should only be called by a TagManager.
	 * Clients should use tagManager.renameTag(oldName, newName) to rename tags.
	 */
	void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * Add the tag to a photo.
	 */
	 void addToPhoto(Photo photo){
		if ( !photos.contains(photo) ){
			photo.addTag(this);
			photos.add(photo);
		}
	}
	
	/**
	 * Remove the tag from a photo.
	 */
	 void removeFromPhoto(Photo photo) {
		if ( photos.contains(photo) ){
		 	photo.removeTag(this);
		 	photos.remove(photo);
		}
	}
	
	/**
	 * @return The photos associated with this tag.
	 */
	public Set<Photo> getPhotos(){
		return photos;
	}
	
	
	// MAY ADD METHODS HERE
	/**
	 * indicate whether two tags are equal
	 * @pre true
	 * @post IF  obj==null, return false
	 *       IF obj.getClass()!=getClass(), return false
	 *       ELSE return name.equals(other.name)
	 * @param obj the object being compared
	 * @return true if two tags are equal, false otherwise
	 */
	public boolean equals(Object obj){
		if ( obj==null )
			return false;
		if ( obj.getClass()!=getClass() )
			return false;
		Tag other = (Tag) obj;
		return ( name.equals(other.name) );
	}
	
	/**
	 * compute a has code for the tag
	 */
	public int hashCode(){
		int result = 29;
		int h = 39;
		result = h*result + name.hashCode();
		return result;
	}
	
	/**
	 * return a string representing the tag
	 * @return a string representing the tag
	 */
	public String toString(){
		return "The tag, " + name + ", has " + photos.size() + " photos associated with it.";
	}
}
