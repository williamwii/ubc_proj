package ca.ubc.cs.cpsc211.photo;

import ca.ubc.cs.cpsc211.utility.ArraySet;
import ca.ubc.cs.cpsc211.utility.Set;

/**
 * An album contains a set of photos.
 * 
 * @author 
 *
 */
public class Album {
	
//  CODE FOR PART 2
	/**
	 * name of album
	 */
	private String name;
	
	/**
	 * a set containing all photos
	 */
	private ArraySet photoSet;
	
	/**
	 * construct a new album with the given name
	 * @param name name of album
	 */
	public Album(String name){
		this.name = name;
		photoSet = new ArraySet();
	}
	
	/**
	 * 
	 * @return the name of album
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * set the name of album
	 * @pre newName != @pre.name
	 * @post @pre.name = newName
	 * @param newName new name of the album
	 */
	public void setName(String newName){
		name = newName;
	}

	/**
	 * get all photos in the album
	 * @return a set of all photos in album
	 */
	public Set getPhotos(){
		return photoSet;
	}
	
	/**
	 * add a photo to the album
	 * @param photo photo getting added to the album
	 * @return true if photo is added. False otherwise
	 * @throws PhotoDoesNotExistException if photo is not found
	 */
	public boolean addPhoto(Photo photo) throws PhotoDoesNotExistException {
		if ( photo==null )
			throw new PhotoDoesNotExistException();
		return photoSet.add(photo);
	}
	
	/**
	 * remove a photo from album
	 * @param photo photo getting removed from the album
	 * @return true is photo is removed. False otherwise
	 * @throws PhotoDoesNotExistException if photo is not found
	 */
	public boolean removePhoto(Photo photo)throws PhotoDoesNotExistException{
		if ( photo==null )
			throw new PhotoDoesNotExistException();
		return photoSet.remove(photo);
	}

}
