package com.ezbook.shared;


public class UnsubscriptionComponent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3771472609782897614L;

	private String email;
	private String ISBN;
	private String unsubscribeCode;
	
	public String getISBN() {
	if(ISBN==null)
			return "";
		return ISBN;
	}
	
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
	
	public String getEmail() {
		if(email==null)
			return "";
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUnsubscribeCode() {
		if(unsubscribeCode==null)
			return "";
		return unsubscribeCode;
	}
	
	public void setUnsubscribeCode(String unsubscribeCode) {
		this.unsubscribeCode = unsubscribeCode;
	}

}
