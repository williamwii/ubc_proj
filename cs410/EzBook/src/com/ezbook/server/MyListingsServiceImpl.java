package com.ezbook.server;

import com.ezbook.client.MyListingsService;
import com.ezbook.shared.PostComponent;
import com.ezbook.shared.SearchComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MyListingsServiceImpl extends RemoteServiceServlet implements
		MyListingsService {
	private static final long serialVersionUID = 715518912882289053L;

	@Override
	public String searchMyListings() {
		String email = (String)this.getThreadLocalRequest().getSession().getAttribute("fb_email");
		SearchComponent component = new SearchComponent();
		component.setEmail(email);
		
		JsonArray result = DBService.search(component);
		if (result!=null) {
			return result.toString();
		}
		
		return null;
	}

	@Override
	public String searchItem(String postID) {
		try {
			Integer.parseInt(postID);
		} catch (NumberFormatException e) {
			return null;
		}
		
		String email = (String)this.getThreadLocalRequest().getSession().getAttribute("fb_email");
		SearchComponent component = new SearchComponent();
		component.setPostID(postID);
		JsonArray results = DBService.search(component);
		
		if (results!=null && results.size()>0) {
			JsonObject result = results.get(0).getAsJsonObject();
			if (result!=null) {
				if (result.get("EMAIL")!=null) {
					String resultEmail = result.get("EMAIL").getAsString();
					if (email.equalsIgnoreCase(resultEmail)) {
						return result.toString();
					}
				}
			}
		}
		
		return null;
	}
	
	@Override
	public String searchMySubscription() {
		String email = (String)this.getThreadLocalRequest().getSession().getAttribute("fb_email");
		return DBService.searchMySubscription(email).toString();
	}
	
	@Override
	public Boolean repostListing(String postID) {
		String email = (String)this.getThreadLocalRequest().getSession().getAttribute("fb_email");
		try {
			int post = Integer.parseInt(postID);
			return DBService.renewPost(email, post);
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	@Override
	public Boolean editListing(PostComponent component) {
		String email = (String)this.getThreadLocalRequest().getSession().getAttribute("fb_email");
		component.setEmail(email);
		return DBService.editPost(component);
	}

	@Override
	public Boolean deleteListing(String postID) {
		String email = (String)this.getThreadLocalRequest().getSession().getAttribute("fb_email");
		try {
			int post = Integer.parseInt(postID);
			return DBService.deletePost(email, post);
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
