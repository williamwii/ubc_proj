package com.ezbook.client;

import com.ezbook.shared.SearchComponent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MarketPlaceServiceAsync {
	
    public void search(SearchComponent search, int page, AsyncCallback<String> asyncCallback);
    
}
