package ca.ubc.cs.cpsc211.photo;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import ca.ubc.cs.cpsc211.utility.Thumbnail;
import ca.ubc.cs.cpsc211.utility.ThumbnailDoesNotExistException;

/**
 * A photo. Each photo has a name, a date added, and a set of tags.
 */
public class Photo {
	// Information about where we keep the photos
	private static final String PICTURES_DIRECTORY = "photos";
	private static final String THUMBNAILS_DIRECTORY = "thumbnails";
	private static final String PHOTO_FILE_TYPE = ".jpg";
	private static final String PROJECT_DIRECTORY_PATH = System.getProperty("user.dir");
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	/**
	 * name of the photo
	 */
	private String name;
	
	//	ADD MORE CLASS ATTRIBUTES HERE
	/**
	 * description of the photo
	 */
	private String description;
	
	/**
	 * date of the photo's creation
	 */
	private Date dateAdded;
	
	/**
	 * album that contains the photo
	 */
	private Album album;
	
	/**
	 * tags of photo
	 */
	private Set<Tag> tags;
	
	
	private BufferedImage image;
	private Thumbnail thumbnail;


	// ADD CONSTRUCTOR(S) HERE
	/**
	 * construct a photo with the given name
	 * @param name name of photo
	 */
	public Photo(String name){
		this.name = name;
		album = null;
		dateAdded = new Date();
		description = null;
		tags = new HashSet<Tag>();
	}
	
	
	/**
	 * @return The photo's name.
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * @return The album containing this photo, or null if the photo is not in an album.
	 */
	public Album getAlbum() {
		 return album;
	}

	/**
	 * Place this photo in the provided album. Called by photoManager only
	 * @pre true
	 * @post getAlbum == newAlbum
	 */
	void setAlbum(Album newAlbum) {
		 album = newAlbum;
	}
	
	
	/**
	 * @return The date this photo was added to the library
	 */
	public Date getDateAdded() {
		 return dateAdded;
	}

	/**
	 * Set the "added" date for this photo.
	 */
	public void setDateAdded(Date dateAdded) {
		 this.dateAdded = dateAdded;
	}

	/**
	 * Get the photo's description.
	 */
	public String getDescription() {
		 return description;
	}

	/**
	 * Set the photo's description.
	 */
	public void setDescription(String description) {
		 this.description = description;
	}
	
		
	/**
	 * Read the photo in based on its name.
	 * @throws PhotoDoesNotExistException if
	 *  there is an error reading the image file with
	 *  name [name].jpg in the photos directory.
	 */
	public void loadPhoto() throws PhotoDoesNotExistException {

		try {
			image = ImageIO.read(new File(PROJECT_DIRECTORY_PATH
						+ FILE_SEPARATOR + PICTURES_DIRECTORY
						+ FILE_SEPARATOR + name + PHOTO_FILE_TYPE));
		} catch (IOException ioe) {
			throw new PhotoDoesNotExistException();
		}

		
		thumbnail = new Thumbnail(
				PROJECT_DIRECTORY_PATH
				+ FILE_SEPARATOR + THUMBNAILS_DIRECTORY
				+ FILE_SEPARATOR, name, image);
	}


	
	/**
	 * Add a tag to the photo.
	 */
	public void addTag(Tag tag) {
		if ( !tags.contains(tag) ){
				tags.add(tag);
				tag.addToPhoto(this);
		}
	}
	
	/**
	 * Remove a tag from the photo.
	 */
	public void removeTag(Tag tag) {
		if ( tags.contains(tag) ){
			tags.remove(tag);
			tag.removeFromPhoto(this);
		}
	}
	
	/**
	 * @return The tags associated with this photo.
	 */
	public Set<Tag> getTags() {
		return tags;
	}

	
	/**
	 * Provide the photo image
	 * 
	 * @pre true
	 * @post true
	 * @return The image of the actual photo
	 * @throws PhotoDoesNotExistException
	 *             if the photo cannot be found on the filesystem
	 */
	public Image getImage() throws PhotoDoesNotExistException {
		return image;
	}

	/**
	 * Provide the image of the thumbnail of the photo
	 * 
	 * @pre true
	 * @post true
	 * @return The thumbnail image
	 * @throws ThumbnailDoesNotExistException
	 *             if the thumbnail image can't be found
	 */
	public Image getThumbnailImage() throws ThumbnailDoesNotExistException {
		return thumbnail.getThumbnailImage();
	}
	
	
	// MAY ADD MORE METHODS HERE
	/**
	 * indicate whether two photos are equal
	 * @pre true
	 * @post IF obj==null, return false
	 *       IF obj.getClass()!=getClass(), return false
	 *       ELSE return name.equals(other.name)
	 * @param obj object being compared to
	 * @return true if two photos are equal, false otherwise
	 */
	public boolean equals( Object obj ){
		if ( obj==null )
			return false;
		if ( obj.getClass()!=getClass() )
			return false;
		Photo other = (Photo) obj;
		return ( name.equals(other.name) );
	}
	
	/**
	 * compute a hash code for the photo
	 */
	public int hashCode(){
		int result = 37;
		int h = 19;
		result = h*result + name.hashCode();
		return result;
	}
	
	/**
	 * return a string representing the photo
	 * @return a string representing the photo
	 */
	public String toString(){
		return "The photo, " + name + ", is described as: " + description + ".";
	}
}
