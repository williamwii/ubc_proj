package ca.ubc.cs.cpsc211.photo;


import java.util.HashSet;
import java.util.Set;

import ca.ubc.cs.cpsc211.utility.ThumbnailDoesNotExistException;


/**
 * An album contains a set of photos.
 */
public class Album {

	// ADD CLASS ATTRIBUTES AND CONSTRUCTOR(S)
	/**
	 * name of the album
	 */
	private String name;
	
	/**
	 * the set of photos in the album
	 */
	private Set<Photo> photos;
	
	/**
	 * create an album with the given name
	 * @param name name of album
	 */
	public Album( String name ){
		this.name = name;
		photos = new HashSet<Photo>();
	}

	
	/**
	 * @return The album name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Change the album name.
	 * 
	 * @pre name != null
	 * @post @post.getName().equals( name )
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Add a photo to this album.
	 * 
	 * @pre photos.contains(photo) == false AND photo.getAlbum() == null
	 * @post @post.photos.contains(photo) == true
	 * @throws PhotoAlreadyInAlbumException
	 *             if the precondition is violated
	 */
	public void addPhoto(Photo photo) throws PhotoAlreadyInAlbumException{
		if ( photos.contains(photo)==true || photo.getAlbum() != null )
			throw new PhotoAlreadyInAlbumException();
		photos.add(photo);
		photo.setAlbum(this);
	}

	/**
	 * Remove a photo from this album.
	 * @throws PhotoDoesNotExistException 
	 * 
	 * @pre getPhotos.contains(photo) == true
	 * @post @post.getPhotos.contains(photo) == false
	 * @throws PhotoDoesNotExistException
	 *             if the precondition is violated
	 */
	public void removePhoto(Photo photo) throws PhotoDoesNotExistException {
		if ( photos.contains(photo)== false )
			throw new PhotoDoesNotExistException();
		photos.remove(photo);
		photo.setAlbum(null);
	}
	
	/**
	 * Return access to all photos
	 */
	public Set<Photo> getPhotos() {
		return photos;
	}
	
	// MAY ADD MORE METHODS HERE
	/**
	 * indicate whether two albums are equal
	 * @pre true
	 * @post IF obj==null, return false
	 *       IF obj.getClass()!=getClass(), return false
	 *       ELSE return name.equals(other.name)
	 * @param obj object being compared to the album
	 * @return true if two albums are equal, false otherwise
	 */
	public boolean equals( Object obj ){
		if ( obj==null )
			return false;
		if ( obj.getClass()!=getClass() )
			return false;
		
		Album other = (Album) obj;
		return ( name.equals(other.name) );
	}
	
	/**
	 * compute a hash code for the album
	 */
	public int hashCode(){
		int result = 17;
		int h = 23;
		result = h*result + name.hashCode();
		return result;
	}
	
	/**
	 * return a string representing the album
	 * @return a string representing the album
	 */
	public String toString(){
		return "The album, " + name + ", has " + photos.size() + " photos.";
	}
}
