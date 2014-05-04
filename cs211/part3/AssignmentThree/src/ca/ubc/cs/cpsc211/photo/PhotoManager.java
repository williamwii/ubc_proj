package ca.ubc.cs.cpsc211.photo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * Class that manages a collection of albums, and thereby also a collection of photos.
 * To add/remove photos in this collection, simply add/remove them in the albums.
 */
public class PhotoManager {
	
	// ADD CLASS ATTRIBUTES AND CONSTRUCTOR(S) HERE
	/**
	 * the set of albums in the manager
	 */
	private Set<Album> albums;
	
	/**
	 * construct a new photo manager
	 */
	public PhotoManager(){
		albums = new HashSet<Album>();
	}
	
	/**
	 * @return The set of albums currently in the system.
	 */
	public Set<Album> getAlbums(){
		return albums;
	}

	/**
	 * Add an album.
	 */
	public void addAlbum(Album album){
		albums.add(album);
	}

	/**
	 * Remove an album.
	 */
	public void removeAlbum(Album album) {
		albums.remove(album);
	}
	
	/**
	 * Return a particular album
	 */
	public Album findAlbum(String name) {
		Album targetAlbum = null;
		for ( Album a : albums ){
			if ( a.getName().equals(name) )
				targetAlbum = a;
		}
		return targetAlbum;
	}

	
	/**
	 * @return The set of photos currently in the system.
	 */
	public Set<Photo> getPhotos(){
		Set<Photo> allPhotos = new HashSet<Photo>();
		for ( Album a : albums ){
			allPhotos.addAll(a.getPhotos());
		}
		return allPhotos;
	}
	
	
	/**
	 * Finds and returns the photos in the library that were added to the library 
	 * within the given range of dates (including the given dates).
	 * 
	 * To compare two dates you can use the Date methods before() and after().
	 * For instance if d1 and d2 are two dates then:
	 * d1.before(d2) will return true if d1 is strictly an earlier date than d2, and 
	 * d1.after(d2) will return true if d1 is strictly a later date than d2.
	 * 
	 * @return The photos in the system that were added in the provided date range (inclusive).
	 * 
	 */
	
	public Set<Photo> findPhotosInDateRange(Date start, Date end){
		Set<Photo> PhotosInDateRange = new HashSet<Photo>();
		for ( Photo p : getPhotos() ){
			if ( (p.getDateAdded().after(start)&&p.getDateAdded().before(end))
					|| (p.getDateAdded().equals(start))
					|| (p.getDateAdded().equals(end)) )
				PhotosInDateRange.add(p);
		}
		return PhotosInDateRange;
	}
	
	// MAY ADD METHODS HERE
	/**
	 * add a photo and place it in an album
	 * @pre albums.contains(album)
	 * @post getPhotos().contains(photo)
	 * @param photo photo getting added to the system
	 * @param album photo is placing into this album
	 * @throws PhotoAlreadyInAlbumException 
	 * 								if the photo is already in album
	 */
	public void addPhoto(Photo photo, Album album)throws PhotoAlreadyInAlbumException{
		album.addPhoto(photo);
	}
	
	/**
	 * move a photo to another album
	 * @pre true
	 * @post !@pre.photo.getAlbum().getPhotos().contains(photo)
	 * 			newAlbum.getPhotos().contains(photo)
	 * @param photo photo getting moved
	 * @param newAlbum new album that will contain the photo
	 * @throws PhotoAlreadyInAlbumException
	 * 								if photo is already in newAlbum
	 * @throws PhotoDoesNotExistException
	 * 								if photo is not in the system
	 */
	public void movePhoto(Photo photo, Album newAlbum)throws PhotoAlreadyInAlbumException, 
																PhotoDoesNotExistException{
		removePhoto(photo);
		newAlbum.addPhoto(photo);
	}
	
	/**
	 * remove a photo from library
	 * @pre true
	 * @post @post.getPhotos().contains(photo)==false
	 * @param photo photo getting removed
	 * @throws PhotoDoesNotExistException
	 * 								if photo is not in the system
	 */
	public void removePhoto(Photo photo)throws PhotoDoesNotExistException{
		photo.getAlbum().removePhoto(photo);
	}
}
