package com.ezbook.client;

import com.ezbook.shared.PostComponent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MyListingsServiceAsync {
	
    public void searchMyListings(AsyncCallback<String> asyncCallback);
    public void searchItem(String postID, AsyncCallback<String> asyncCallback);
    public void searchMySubscription(AsyncCallback<String> asyncCallback);
    public void repostListing(String postID, AsyncCallback<Boolean> asyncCallback);
    public void editListing(PostComponent component, AsyncCallback<Boolean> asyncCallback);
    public void deleteListing(String postID, AsyncCallback<Boolean> asyncCallback);
    
}
