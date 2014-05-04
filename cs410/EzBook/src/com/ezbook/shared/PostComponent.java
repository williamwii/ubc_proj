package com.ezbook.shared;


public class PostComponent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 95772690235408215L;
	private String postID;
	private String ASIN;//Unique amazon product ID
	private double price;//price of the book that user puts
	private String email;//We will fetch this from the logged in user
	private String fbName;
	private String title;//title of the book
	private String ISBN;
	private String author;
	private String imageURL;//url of the book image
	private String comment;
	private String address;//this is the preferred meetup location
	private double latitude;
	private double longitude;
	private String formattedAmazonPrice;
	
	public String getASIN() {
		if (ASIN==null)
			return "";
		return ASIN;
	}
	public void setASIN(String aSIN) {
		ASIN = aSIN;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFbName() {
		return fbName;
	}
	public void setFbName(String fbName) {
		this.fbName = fbName;
	}
	public String getTitle() {
		if (title==null)
			return "";
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getISBN() {
		if (ISBN==null)
			return "";
		return ISBN;
	}
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
	public String getAuthor() {
		if (author==null)
			return "";
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getImageURL() {
		if (imageURL==null)
			return "";
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getComment() {
		if (comment==null)
			return "";
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getAddress() {
		if (address==null)
			return "";
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setLatitude(double lat){
		latitude = lat;
	}
	public Double getLatitude(){
		return latitude;
	}
	public void setLongitude(double lon){
		longitude = lon;
	}
	public Double getLongitude(){
		return longitude;
	}
	public String getPostID() {
		if (postID==null)
			return "";
		return postID;
	}
	public void setPostID(String postID) {
		this.postID = postID;
	}
	public String getFormattedAmazonPrice() {
		return formattedAmazonPrice;
	}
	public void setFormattedAmazonPrice(String formattedAmazonPrice) {
		this.formattedAmazonPrice = formattedAmazonPrice;
	}
}
