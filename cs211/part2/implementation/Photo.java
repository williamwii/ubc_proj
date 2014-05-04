package ca.ubc.cs.cpsc211.photo;

/**
 * @author CPSC 211 Instructor
 */


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.imageio.ImageIO;

import ca.ubc.cs.cpsc211.utility.Thumbnail;
import ca.ubc.cs.cpsc211.utility.ThumbnailDoesNotExistException;

/**
 * A photo has a name. Each photo is stored in a photos
 * subdirectory with the filename <name>.jpg.
 * 
 */
public class Photo {
	// Information about where we keep the photos
	private static final String PICTURES_DIRECTORY = "photos";
	private static final String THUMBNAILS_DIRECTORY = "thumbnails";
	private static final String PHOTO_FILE_TYPE = ".jpg";

	private static final String PROJECT_DIRECTORY_PATH = System.getProperty("user.dir");
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	// Each photo needs a name. The name is the root of the filename.
	private String name;
	
	// The full image and a thumbnail
	private BufferedImage image;
	private Thumbnail thumbnail;

	// ADDITIONAL ATTRIBUTES TO BE DEFINED FOR PART 2
	/**
	 * description of photo
	 */
	private String description;
	
	/**
	 * album containing the photo
	 */
	private Album album;
	
	/**
	 * date of photo loaded in system
	 */
	private Date loadedDate;

	
	

	/**
	 * Create a photo object from the provided file.
	 * 
	 * @pre A file for the photo exists which is photos/[name].jpg
	 * @param name The name (which is the root of the filename)
	 */
	public Photo(String name) {
		this.name = name;
		loadedDate = new Date();
		image = null;
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
	 * Provide the photo image
	 * 
	 * @pre true
	 * @post true
	 * @return The image of the actual photo
	 * @throws PhotoDoesNotExistException
	 *             if the photo cannot be found on the filesystem
	 */
	public Image getImage() throws PhotoDoesNotExistException {
		if (image == null) 
			throw new PhotoDoesNotExistException();
			
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
	
	
	// ADDITIONAL METHODS FOR PART 2
	
	/**
	 *  @return name of the photo
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @pre true
	 * @post @pre.name = newName
	 * @param newName new name of the photo
	 */
	public void setName(String newName){
		name = newName;
	}
	
	/**
	 * @return album containing the photo
	 */
	public Album getAlbum(){
		return album;
	}
	
	/**
	 * @pre newAlbum != album
	 * @post @pre.album = newAlbum
	 * @param newAlbum new album of the photo belongs to
	 */
	public void setAlbum(Album newAlbum){
		album = newAlbum;
	}
	
	/**
	 * @return description of the photo
	 */
	public String getDescription(){
		return description;
	}
	
	/**
	 * @pre newDescription != description
	 * @post @pre.description = newDescription
	 * @param newDescription the new description of photo
	 */
	public void setDescription(String newDescription){
		description = newDescription;
	}
	
	/**
	 * @return the date of photo loaded into system
	 */
	public Date getDate(){
		return loadedDate;
	}
	
}
