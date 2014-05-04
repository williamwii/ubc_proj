package com.ezbook.client;

import com.ezbook.shared.PostComponent;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../secure/mylistings")
public interface MyListingsService extends RemoteService {

	public String searchMyListings();
	public String searchItem(String postID);
	public String searchMySubscription();
	public Boolean repostListing(String postID);
	public Boolean editListing(PostComponent component);
	public Boolean deleteListing(String postID);
	
}
