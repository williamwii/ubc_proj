package com.ezbook.shared;

public class SearchComponent implements java.io.Serializable{
	private static final long serialVersionUID = 7056912954535215740L;

	private String postID;
	private String title;//title of the book
	private String ISBN;
	private String author;
	private String priceMin;
	private String priceMax;
	private String email;
	private String orderBy;


	public String getTitle() {
		if(title==null)
			return "";
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getISBN() {
		if(ISBN==null)
			return "";
		return ISBN;
	}
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
	public String getAuthor() {
		if(author==null)
			return "";
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	public void setPriceMin(String priceMin) {
		this.priceMin = priceMin;
	}
	public void setPriceMax(String priceMax) {
		this.priceMax = priceMax;
	}
	public String getPriceMin() {
		if (priceMin==null)
			return "";
		return priceMin;
	}
	public String getPriceMax() {	
		if (priceMax==null)
			return "";
		return priceMax;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail(){
		if(email==null)
			return "";
		return email;
	}
	public String getOrderBy() {
		if(orderBy==null)
			return "";
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getPostID() {
		if(postID==null)
			return "";		
		return postID;
	}
	public void setPostID(String postID) {
		this.postID = postID;
	}

}
