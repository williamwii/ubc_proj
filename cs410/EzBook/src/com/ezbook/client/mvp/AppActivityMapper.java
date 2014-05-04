package com.ezbook.client.mvp;

import com.ezbook.client.place.*;
import com.ezbook.client.activity.*;
import com.ezbook.client.ClientFactory;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {

	private ClientFactory clientFactory;
	
	public AppActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}
	
	@Override
	public Activity getActivity(Place place) {
		// Add all places here
		if (place instanceof EzBook) {
			return new EzBookActivity((EzBook)place, clientFactory);
		}
		if (place instanceof Post) {
			return new PostActivity((Post)place, clientFactory);
		}
		if (place instanceof Marketplace) {
			Marketplace marketplace = (Marketplace)place;
			MarketplaceActivity marketplaceActivity = new MarketplaceActivity(marketplace, clientFactory);
			marketplaceActivity.setQuery(marketplace.getQuery());
			return marketplaceActivity;
		}
		if (place instanceof MyListings) {
			MyListings mylistings = (MyListings)place;
			MyListingsActivity mylistingsActivity = new MyListingsActivity(mylistings, clientFactory);
			mylistingsActivity.setQuery(mylistings.getQuery());
			return mylistingsActivity;
		}
		if (place instanceof EditListing) {
			EditListing editListing = (EditListing)place;
			EditListingActivity editListingActivity = new EditListingActivity(editListing, clientFactory);
			editListingActivity.setPostID(editListing.getToken());
			return editListingActivity;
		}
		if (place instanceof Subscriptions) {
			return new SubscriptionsActivity((Subscriptions)place, clientFactory);
		}
		if (place instanceof Feedback) {
			return new FeedbackActivity((Feedback)place, clientFactory);
		}
		return null;
	}

}
