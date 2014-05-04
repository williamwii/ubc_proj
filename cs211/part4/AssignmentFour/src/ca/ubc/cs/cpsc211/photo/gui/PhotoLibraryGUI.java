package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import ca.ubc.cs.cpsc211.photo.*;
import ca.ubc.cs.cpsc211.utility.*;

/**
 * The graphic user interface of photo library
 * 
 * @author Wei You
 * Nov 28, 2010
 *
 */
public class PhotoLibraryGUI {
	
	/**
	 * JFrame of photo library
	 */
	private JFrame frame;
	
	
	/**
	 * list of albums that are displaying in a JList
	 */
	private JList albums;
	
	/**
	 * list of tags that are displaying in a JList
	 */
	private JList tags;
	
	/**
	 * a JPanel that is displaying the chosen photo(photoDisplay)
	 */
	private JPanel photoPane;
	
	/**
	 * a JPanel that is displaying buttons with thumbnail images for an album
	 */
	private JPanel albumThumbnailPane;
	
	/**
	 * a JPanel that is displaying buttons wiht thumbnail images for a tag
	 */
	private JPanel tagThumbnailPane;
	
	/**
	 * a JTextArea that is showing the infomation of the chosen photo(photoDisplay)
	 */
	private JTextArea infoPane;
	
	
	/**
	 * the photo that is being displayed
	 */
	private Photo photoDisplay;
	
	
	/**
	 * the photo manager used to manage the photos
	 */
	private PhotoManager pManager;
	
	/**
	 * the tag manager used to manage the tags
	 */
	private TagManager tManager;
	
	/**
	 * construct a new graphic user interface
	 */
	public PhotoLibraryGUI(){
		pManager = new PhotoManager();
		tManager = new TagManager();
	}
	
	/**
	 * show the graphic user interface
	 */
	public void showPhotoLibrary(){
		
		//create a new JFrame
		frame = new JFrame();
		frame.setTitle("Photo Library");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//set the JMenuBar for frame
		frame.setJMenuBar( initializeJMenuBar() );

		//create a panel with albums and tags JList
		JPanel listPanel = new JPanel( new GridLayout(2,1) );
		listPanel.add( initializeAlbumList() );
		listPanel.add( initializeTagList() );
		listPanel.setPreferredSize( new Dimension( 150,150 ) );
		
		//create a panel with photoThumbnailPane and tagThumbnailPane
		JPanel thumbnailPanel = new JPanel( new GridLayout(2,1) );
		thumbnailPanel.add(initializePhotoThumbnailPane());
		thumbnailPanel.add(initializeTagThumbnailPane());
		
		//set all components in place and pack
		frame.getContentPane().add(listPanel, BorderLayout.WEST);
		frame.getContentPane().add(thumbnailPanel, BorderLayout.SOUTH);
		frame.getContentPane().add(initializePhotoPane(), BorderLayout.CENTER);
		frame.getContentPane().add(initializeInfoPane(), BorderLayout.EAST);
		frame.pack();
		
		//set the frame to be visible
		frame.setVisible(true);
	}
	
	/**
	 * initialize the JMenu for frame, using the helper methods of initializeFileMenu(), initializeAlbumMenu(),
	 * initializePhotoMenu(), and initializeTagMenu().
	 * @return a JMenu for frame.
	 */
	private JMenuBar initializeJMenuBar(){
		JMenuBar menu = new JMenuBar();
		
		menu.add(initializeFileMenu());
		menu.add(initializeAlbumMenu());
		menu.add(initializePhotoMenu());
		menu.add(initializeTagMenu());
		
		return menu;
	}
	
	/**
	 * initialize the File menu.
	 * helper method for initializeJMenuBar()
	 * @return the file menu component for JMenu
	 */
	private JMenu initializeFileMenu(){
		JMenu file = new JMenu("File");
		
		//to quit the program
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				frame.setVisible(false);
				frame.dispose();
			}
		});
		file.add(quit);
		
		return file;
	}
	
	/**
	 * initialize the Album menu.
	 * helper method for initializeJMenuBar()
	 * @return the file album component for JMenu
	 */
	private JMenu initializeAlbumMenu(){
		JMenu album = new JMenu("Album");
		
		//to create a new album
		JMenuItem createAlbum = new JMenuItem("New Album");
		createAlbum.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				String albumName = (String) JOptionPane.showInputDialog(null, "Please enter the name of new album."
						, "New Album", JOptionPane.PLAIN_MESSAGE);
				if ( albumName==null )
					return;
				if ( !albumName.equals("") ){
					if ( pManager.findAlbum(albumName)==null ){
						Album album = new Album(albumName);
						pManager.addAlbum(album);
						((DefaultListModel)albums.getModel()).addElement( albumName );
					}
					else{
						JOptionPane.showMessageDialog(null, "The album already exist!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else
					JOptionPane.showMessageDialog(null, "Invalid name!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		//to rename a selected album
		JMenuItem renameAlbum = new JMenuItem("Rename Album");
		renameAlbum.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if ( !albums.isSelectionEmpty() ){
					String newAlbumName = (String) JOptionPane.showInputDialog(null, "Please enter the new name of album."
							, "Rename Album", JOptionPane.PLAIN_MESSAGE);
					if ( newAlbumName!=null ){
						String oldAlbumName = pManager.findAlbum((String) albums.getSelectedValue()).getName();
						if ( !newAlbumName.equals("")&&pManager.renameAlbum(oldAlbumName, newAlbumName)){
							((DefaultListModel)albums.getModel()).setElementAt(newAlbumName, albums.getSelectedIndex());
							displayPhoto(photoDisplay);
						}
						else
							JOptionPane.showMessageDialog(null, "Invalid name!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else
					JOptionPane.showMessageDialog(null, "No album is selected!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		//to remove an existing album
		JMenuItem removeAlbum = new JMenuItem("Remove Album");
		removeAlbum.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				String[] albumNames = getAlbumNames( pManager.getAlbums() );
				String targetString = (String)JOptionPane.showInputDialog(null, "Please choose the album want to remove.",
						"Choose Album",JOptionPane.PLAIN_MESSAGE, null, albumNames ,albumNames[0]);
				if ( targetString==null )
					return;
				Album targetAlbum = pManager.findAlbum(targetString);
				if ( targetAlbum.equals( pManager.findAlbum((String) albums.getSelectedValue())) ){
					int index = albums.getSelectedIndex();
					if ( ((DefaultListModel)albums.getModel()).getSize()==1 )
						albums.setSelectedValue(null, true);
					else if ( index==((DefaultListModel)albums.getModel()).getSize()-1 )
						albums.setSelectedIndex(index-1);
					else
						albums.setSelectedIndex(index+1);
				}
				if ( targetAlbum.getPhotos().contains(photoDisplay) )
					displayPhoto(null);
				((DefaultListModel)albums.getModel()).removeElement(targetString);
				pManager.removeAlbum(targetAlbum);
			}
		});
		
		album.add(createAlbum);
		album.add(renameAlbum);
		album.add(removeAlbum);
		
		return album;
	}
	
	/**
	 * initialize the Photo menu.
	 * helper method for initializeJMenuBar()
	 * @return the file photo component for JMenu
	 */
	private JMenu initializePhotoMenu(){
		JMenu photo = new JMenu("Photo");
		
		//to add a photo and place it into an album immediately
		JMenuItem addPhoto = new JMenuItem("Add Photo");
		addPhoto.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e ){
				if ( ((DefaultListModel)albums.getModel()).getSize()>0 ){
					String photoName = (String) JOptionPane.showInputDialog(null, "Please enter the name of new photo."
							, "New Photo", JOptionPane.PLAIN_MESSAGE);
					if ( photoName==null )
						return;
					try{
						Photo photo = new Photo( photoName );
						if ( pManager.getPhotos().contains(photo) ){
							JOptionPane.showMessageDialog(null, "The photo is already in library!", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						photo.loadPhoto();
						String[] albumNames = getAlbumNames( pManager.getAlbums() );
						String targetString = (String)JOptionPane.showInputDialog(null, "Please choose the destination album of the photo.",
								"Choose Album",JOptionPane.PLAIN_MESSAGE, null, albumNames ,albumNames[0]);
						if ( targetString==null )
							return;
						Album targetAlbum = pManager.findAlbum(targetString);
						targetAlbum.addPhoto(photo);
						photo.setDateAdded( new Date() );
					
						if ( targetAlbum.equals( pManager.findAlbum((String) albums.getSelectedValue())) ){
							JButton button = new JButton( photo.getName(),new ImageIcon(photo.getThumbnailImage()) );
							button.addActionListener( new ThumbnailButtonListener() );
							albumThumbnailPane.add(button);
							albumThumbnailPane.updateUI();
						}
					}
					catch( PhotoDoesNotExistException exp ){
						JOptionPane.showMessageDialog(null, "The photo does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					catch( PhotoAlreadyInAlbumException exp ){
						JOptionPane.showMessageDialog(null, "The photo is already in the album!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					catch( ThumbnailDoesNotExistException exp ){
						JOptionPane.showMessageDialog(null, "Cannot find thumbnail for the photo!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			else
				JOptionPane.showMessageDialog(null, "Please create new album first!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		//to remove a selected photo from the library
		JMenuItem removePhoto = new JMenuItem("Remove Photo");
		removePhoto.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if ( photoDisplay!=null ){
					try{
						int optionChose = JOptionPane.showConfirmDialog(null, "Are you sure to remove the selected photo from library?"
								,"Remove Photo",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
						if ( optionChose==JOptionPane.YES_OPTION ){
							Album targetAlbum = photoDisplay.getAlbum();
							targetAlbum.removePhoto(photoDisplay);
							updatePanes(targetAlbum.getPhotos(),albumThumbnailPane);
						}
					}
					catch ( PhotoDoesNotExistException exp ){
						JOptionPane.showMessageDialog(null, "Photo does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else
					JOptionPane.showMessageDialog(null, "No photo is selected!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		//to move a selected photo to another album
		JMenuItem movePhoto = new JMenuItem("Move Photo");
		movePhoto.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if ( photoDisplay!=null ){
					try{
						String[] albumNames = getAlbumNames( pManager.getAlbums() );
						String targetString = (String)JOptionPane.showInputDialog(null, "Please choose the destination album of the photo.",
								"Choose Album",JOptionPane.PLAIN_MESSAGE, null, albumNames ,albumNames[0]);
						Album targetAlbum = pManager.findAlbum(targetString);
						Album oldAlbum = photoDisplay.getAlbum();
						if ( oldAlbum.equals(targetAlbum) )
							return;
						oldAlbum.removePhoto(photoDisplay);
						targetAlbum.addPhoto(photoDisplay);
						Album selectedAlbum = pManager.findAlbum( (String)albums.getSelectedValue() );
						if ( selectedAlbum.equals(oldAlbum) )
							updatePanes(oldAlbum.getPhotos(),albumThumbnailPane);
						else if ( selectedAlbum.equals(targetAlbum) )
							updatePanes(targetAlbum.getPhotos(),albumThumbnailPane);
					}
					catch ( PhotoDoesNotExistException exp ){
						JOptionPane.showMessageDialog(null, "Photo does not exist in album!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					catch( PhotoAlreadyInAlbumException exp ){
						JOptionPane.showMessageDialog(null, "The photo is already in the album!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else
					JOptionPane.showMessageDialog(null, "No photo is selected!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		//to edit description for a selected photo
		JMenuItem editDescription = new JMenuItem("Edit Description");
		editDescription.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if ( photoDisplay!=null ){
					String newDescription = (String) JOptionPane.showInputDialog( "Edit Description",photoDisplay.getDescription() );
					if ( newDescription!=null ){
						photoDisplay.setDescription(newDescription);
						displayPhoto(photoDisplay);
					}
				}
				else
					JOptionPane.showMessageDialog(null, "No photo is selected!", "Error", JOptionPane.ERROR_MESSAGE);

			}
		});
		
		//to tag a selected photo
		JMenuItem tagPhoto = new JMenuItem("Tag Photo");
		tagPhoto.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if ( photoDisplay!=null ){
					try{
						String targetString = (String) JOptionPane.showInputDialog(null, "Please enter the name of the new tag of the photo."
								, "Tag", JOptionPane.PLAIN_MESSAGE);
						if ( targetString==null )
							return;
						if ( !targetString.equals("") ){
							Tag targetTag = tManager.findTag(targetString);
							if ( targetTag==null ){
								int optionChose = JOptionPane.showConfirmDialog(null, "The tag does not exist, do you want to create it?"
										,"Create Tag",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
								if ( optionChose!=JOptionPane.YES_OPTION )
									return;
								targetTag = tManager.createTag(targetString);
								((DefaultListModel)tags.getModel()).addElement(targetString);
								tags.setSelectedValue(targetString, true);					
							}
							photoDisplay.addTag(targetTag);
							updatePanes(targetTag.getPhotos(),tagThumbnailPane);
							displayPhoto(photoDisplay);
						}
						else
							JOptionPane.showMessageDialog(null, "Invalid name!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					catch( DuplicateTagException exp ){
						JOptionPane.showMessageDialog(null, "The tag already exist!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else
					JOptionPane.showMessageDialog(null, "No photo is selected!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		//to untag a selected photo from a tag
		JMenuItem untagPhoto = new JMenuItem("Untag Photo");
		untagPhoto.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if ( photoDisplay!=null ){
					if ( !photoDisplay.getTags().isEmpty() ){
						String[] tagNames = getTagNames( photoDisplay.getTags() );
						String targetString = (String)JOptionPane.showInputDialog(null, "Please choose the tag you want to remove from photo.",
								"Untag",JOptionPane.PLAIN_MESSAGE, null, tagNames ,tagNames[0]);
						if ( targetString==null )
							return;
						Tag targetTag = tManager.findTag(targetString);
						photoDisplay.removeTag(targetTag);
						updatePanes(tManager.findTag((String)tags.getSelectedValue()).getPhotos(),tagThumbnailPane);
						displayPhoto(photoDisplay);
					}
					else
						JOptionPane.showMessageDialog(null, "The photo has no tags!", "Error", JOptionPane.ERROR_MESSAGE);
				}
				else
					JOptionPane.showMessageDialog(null, "No photo is selected!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		//to show the photos added in the given date range
		JMenuItem photosInDateRange = new JMenuItem("Show Photos In Date Range");
		photosInDateRange.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				try{
					SimpleDateFormat formatter = new SimpleDateFormat("m/H/d/M/yyyy" );
					String startDateString = JOptionPane.showInputDialog(null, "Please enter start date. (min/hr/dd/MM/yyyy)"
							, "Start Date", JOptionPane.PLAIN_MESSAGE);
					if( startDateString==null )
						return;
					String endDateString = JOptionPane.showInputDialog(null, "Please enter end date. (min/hr/dd/MM/yyyy)"
							, "End Date", JOptionPane.PLAIN_MESSAGE);
					if( endDateString==null )
						return;
					Date startDate = formatter.parse( startDateString );
					Date endDate = formatter.parse( endDateString );
					updatePanes( pManager.findPhotosInDateRange(startDate, endDate),albumThumbnailPane );
				}
				catch( ParseException exp ){
					JOptionPane.showMessageDialog(null, "Invalid date format!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		photo.add(addPhoto);
		photo.add(movePhoto);
		photo.add(tagPhoto);
		photo.add(untagPhoto);
		photo.add(editDescription);
		photo.add(photosInDateRange);
		photo.add(removePhoto);
		
		return photo;
	}
	
	/**
	 * initialize the Tag menu.
	 * helper method for initializeJMenuBar()
	 * @return the file tag component for JMenu
	 */
	private JMenu initializeTagMenu(){
		JMenu tag = new JMenu("Tag");
		
		//to create a new tag
		JMenuItem newTag = new JMenuItem("New Tag");
		newTag.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				try{
					String tagName = (String) JOptionPane.showInputDialog(null, "Please enter the name of new tag."
							, "New Tag", JOptionPane.PLAIN_MESSAGE);
					if ( tagName==null )
						return;
					if ( !tagName.equals("") ){
						tManager.createTag(tagName);
						((DefaultListModel)tags.getModel()).addElement( tagName );
					}
					else
						JOptionPane.showMessageDialog(null, "Invalid name!", "Error", JOptionPane.ERROR_MESSAGE);
				}
				catch( DuplicateTagException exp ){
					JOptionPane.showMessageDialog(null, "Tag already exist!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		//to rename a selected tag
		//rename method in tag manager does not work properly
		//, if you tag a photo, then rename the tag, you can not untag the photo from that tag anymore.
		//here i remove the tag with the old name and create one with the new name.
		JMenuItem renameTag = new JMenuItem("Rename Tag");
		renameTag.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if ( !tags.isSelectionEmpty() ){
					try{
						String newTagName = (String) JOptionPane.showInputDialog(null, "Please enter the new name of tag."
								, "Rename Tag", JOptionPane.PLAIN_MESSAGE);
						if ( newTagName==null )
							return;
						String oldTagName = (String)tags.getSelectedValue();
						if ( newTagName.equals("")||oldTagName.equals(newTagName) ){
							JOptionPane.showMessageDialog(null, "Invalid name!", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						Tag newTag = tManager.createTag(newTagName);
						for ( Photo photo : tManager.findTag(oldTagName).getPhotos() ){
							photo.addTag(newTag);
						}
						tManager.removeTag(oldTagName);
						((DefaultListModel)tags.getModel()).setElementAt(newTagName, tags.getSelectedIndex());
						displayPhoto(photoDisplay);
					}
					catch ( DuplicateTagException exp ){
						JOptionPane.showMessageDialog(null, "Invalid name!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else
					JOptionPane.showMessageDialog(null, "Please select a tag first!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		//to remove a tag
		JMenuItem removeTag = new JMenuItem("Remove Tag");
		removeTag.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				String[] tagNames = getTagNames( tManager.getTags() );
				String targetString = (String)JOptionPane.showInputDialog(null, "Please choose the tag want to remove from library.",
						"Remove Tag",JOptionPane.PLAIN_MESSAGE, null, tagNames ,tagNames[0]);
				if ( targetString==null )
					return;
				Tag targetTag = tManager.findTag(targetString);
				if ( targetTag.equals( tManager.findTag((String) tags.getSelectedValue())) ){
					int index = tags.getSelectedIndex();
					if ( index==((DefaultListModel)tags.getModel()).getSize()-1 )
						tags.setSelectedIndex(index-1);
					else
						tags.setSelectedIndex(index+1);
				}
				((DefaultListModel)tags.getModel()).removeElement(targetString);
				if ( photoDisplay.getTags().contains(targetTag) )
					displayPhoto(photoDisplay);
				tManager.removeTag(targetString);
			}
		});
		
		tag.add(newTag);
		tag.add(renameTag);
		tag.add(removeTag);
		
		return tag;
	}
	
	/**
	 * initialize the JScrollPane that contains the list of albums.
	 * helper method for showPhotoLibrary()
	 * @return a JScrollPane that contains the list of albums
	 * 
	 */
	private JScrollPane initializeAlbumList(){
		albums = new JList( new DefaultListModel() );
		JLabel title = new JLabel("Albums");
		albums.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		albums.addListSelectionListener( new ListSelectionListener(){
			public void valueChanged( ListSelectionEvent e ){
				Album targetAlbum = pManager.findAlbum((String) ((JList)e.getSource()).getSelectedValue());
				if ( targetAlbum==null )
					updatePanes(null,albumThumbnailPane);
				else
					updatePanes(targetAlbum.getPhotos(),albumThumbnailPane);
			}
		});
		JScrollPane albumPane = new JScrollPane( albums );
		albumPane.setColumnHeaderView(title);
		
		return albumPane;
	}
	
	/**
	 * initialize the JScrollPane that contains the list of tags.
	 * helper method for showPhotoLibrary()
	 * @return a JScrollPane that contains the list of tags
	 * 
	 */
	private JScrollPane initializeTagList(){
		tags = new JList( new DefaultListModel() );
		JLabel title = new JLabel("Tags");
		tags.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tags.addListSelectionListener( new ListSelectionListener(){
			public void valueChanged( ListSelectionEvent e ){
				Tag targetTag = tManager.findTag((String) ((JList)e.getSource()).getSelectedValue());
				if ( targetTag==null )
					updatePanes(null,tagThumbnailPane);
				else
					updatePanes(targetTag.getPhotos(),tagThumbnailPane);			
			}
		});
		JScrollPane tagPane = new JScrollPane( tags );
		tagPane.setColumnHeaderView(title);
		
		return tagPane;
	}
	
	/**
	 * initialize the JScrollPane that contains the list of thumbnails for the selected album
	 * helper method for showPhotoLibrary()
	 * @return a JScrollPane that contains the list of thumbnails for the selected album
	 */
	private JScrollPane initializePhotoThumbnailPane(){
		albumThumbnailPane = new JPanel();
		JLabel title = new JLabel("Photo Thumbnails");
		JScrollPane thumbnailScrollPane = new JScrollPane(albumThumbnailPane);
		thumbnailScrollPane.setColumnHeaderView(title);
		thumbnailScrollPane.setPreferredSize( new Dimension(100,100) );
		
		return thumbnailScrollPane;
	}
	
	/**
	 * initialize the JScrollPane that contains the list of thumbnails for the selected tag
	 * helper method for showPhotoLibrary()
	 * @return a JScrollPane that contains the list of thumbnails for the selected tag
	 */
	private JScrollPane initializeTagThumbnailPane(){
		tagThumbnailPane = new JPanel();
		JLabel title = new JLabel("Tagged Photos");
		JScrollPane thumbnailScrollPane = new JScrollPane(tagThumbnailPane);
		thumbnailScrollPane.setColumnHeaderView(title);
		thumbnailScrollPane.setPreferredSize( new Dimension(75,75) );
		
		return thumbnailScrollPane;
	}
	
	/**
	 * initialize the JScrollPane that contains the image of the selected photo
	 * helper method for showPhotoLibrary()
	 * @return a JScrollPane that contains the image of the selected photo
	 */
	private JScrollPane initializePhotoPane(){
		photoPane = new JPanel();
		JLabel title = new JLabel("Photo");
		JScrollPane photoScrollPane = new JScrollPane(photoPane);
		photoScrollPane.setColumnHeaderView(title);
		photoScrollPane.setPreferredSize( new Dimension(500,500) );
		
		return photoScrollPane;
	}
	
	/**
	 * initialize the JScrollPane that contains the information of the selected photo
	 * helper method for showPhotoLibrary()
	 * @return a JScrollPane that contains the information of the selected photo
	 */
	private JScrollPane initializeInfoPane(){
		infoPane = new JTextArea("");
		JLabel title = new JLabel("Photo Info");
		infoPane.setEditable(false);
		infoPane.setLineWrap(true);
		JScrollPane infoScrollPane = new JScrollPane(infoPane);
		infoScrollPane.setColumnHeaderView(title);
		infoScrollPane.setPreferredSize( new Dimension(200,200) );
		
		return infoScrollPane;
	}
	
	/**
	 * to get an array of album names
	 * @param set set of albums that needs to get names from
	 * @return an array of String containing the names of the given set of albums
	 */
	private String[] getAlbumNames(Set<Album> set){
		String[] nameArray = new String[set.toArray().length];
		for ( int i=0;i<nameArray.length;i++ ){
			Album temp = (Album) set.toArray()[i];
			nameArray[i] = temp.getName();
		}
		
		return nameArray;
	}
	
	/**
	 * to get an array of tag names
	 * @param set set of tags that needs to get names from
	 * @return an array of String containing the names of the given set of tags
	 */
	private String[] getTagNames(Set<Tag> set){
		String[] nameArray = new String[set.toArray().length];
		for ( int i=0;i<nameArray.length;i++ ){
			Tag temp = (Tag) set.toArray()[i];
			nameArray[i] = temp.getName();
		}
		
		return nameArray;
	}
	
	/**
	 * display the image and information of the photo by updating photoPane and infoPane
	 * @param photo photo getting displayed
	 */
	private void displayPhoto(Photo photo){
		photoDisplay = photo;
		if ( photo==null ){
			photoPane.removeAll();
			infoPane.setText("");
			photoPane.updateUI();
			infoPane.updateUI();
			return;
		}
		try{
			photoPane.removeAll();
			photoPane.add( new JLabel(new ImageIcon(photo.getImage())) );
			infoPane.setText("");
			infoPane.append( "Name: " + photo.getName() + "\n"
							+ "In Album :" + photo.getAlbum().getName() + "\n"
							+ "Description: " + photo.getDescription() + "\n"
							+ "Date Loaded: " + photo.getDateAdded() + "\n"
							+ "Tags: " + photo.getTags().toString() + "\n");
			infoPane.updateUI();
			photoPane.updateUI();
		}
		catch( PhotoDoesNotExistException exp ){
			JOptionPane.showMessageDialog(null, "The photo does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * to update the panels when an item is selected in a JList
	 * @param photoSet the set of photos that needs to be shown in the thumbnail section
	 * @param thumbnailPane the panel showing the thumbnails
	 * 
	 */
	private void updatePanes(Set<Photo> photoSet,JPanel thumbnailPane){
		thumbnailPane.removeAll();
		if ( photoSet==null ){
			thumbnailPane.updateUI();
			return;
		}
		try{
			for( Photo photo : photoSet ){
				JButton button = new JButton( photo.getName(),new ImageIcon(photo.getThumbnailImage()) );
				button.addActionListener( new ThumbnailButtonListener() );
				thumbnailPane.add(button);
			}
		}
		catch( ThumbnailDoesNotExistException exp ){
			JOptionPane.showMessageDialog(null, "Cannot find thumbnail for the photo!", "Error", JOptionPane.ERROR_MESSAGE);
		}
		thumbnailPane.updateUI();
	}
	
	/**
	 * ActionListener for the thumbnail buttons
	 * @author Wei You
	 *
	 */
	private class ThumbnailButtonListener implements ActionListener{
		
		/**
		 * to show the image and information of the photo when a thumbnail is clicked
		 */
		public void actionPerformed( ActionEvent e ){
			Photo targetPhoto = null;
			for ( Photo photo : pManager.getPhotos() ){
				if ( photo.getName().equals(((JButton)e.getSource()).getText()) )
					targetPhoto = photo;
			}
			displayPhoto(targetPhoto);
		}
		
	}

}
