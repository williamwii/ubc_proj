package com.ezbook.client;

import com.ezbook.shared.SearchComponent;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../search")
public interface MarketPlaceService extends RemoteService {

	public String search(SearchComponent search, int page);

}
