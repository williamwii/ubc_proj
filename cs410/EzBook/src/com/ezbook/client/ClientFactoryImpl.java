package com.ezbook.client;

import com.ezbook.client.ui.EditListingView;
import com.ezbook.client.ui.EditListingViewImpl;
import com.ezbook.client.ui.EzBookView;
import com.ezbook.client.ui.EzBookViewImpl;
import com.ezbook.client.ui.FeedbackView;
import com.ezbook.client.ui.FeedbackViewImpl;
import com.ezbook.client.ui.MarketplaceView;
import com.ezbook.client.ui.MarketplaceViewImpl;
import com.ezbook.client.ui.MyListingsView;
import com.ezbook.client.ui.MyListingsViewImpl;
import com.ezbook.client.ui.PostView;
import com.ezbook.client.ui.PostViewImpl;
import com.ezbook.client.ui.SubscriptionsView;
import com.ezbook.client.ui.SubscriptionsViewImpl;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class ClientFactoryImpl implements ClientFactory  {

	private final EventBus eventBus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(eventBus);
	
	private EzBookView ezBookView = null;
	private PostView postView = null;
	private MyListingsView myListingsView = null;
	private EditListingView editListingView = null;
	private SubscriptionsView subscriptionsView = null;
	private MarketplaceView marketplaceView = null;
	private FeedbackView feedbackView = null;
	
	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	@Override
	public EzBookView getEzBookView() {
		if (ezBookView == null) {
			ezBookView = new EzBookViewImpl();
		}
		ezBookView.updateNavBar();
		return ezBookView;
	}

	@Override
	public PostView getPostView() {
		if (postView == null) {
			postView = new PostViewImpl();
		}
		postView.updateNavBar();
		return postView;
	}
	
	public MyListingsView getMyListingsView(String query) {
		if (myListingsView == null) {
			myListingsView = new MyListingsViewImpl();
		}
		myListingsView.updateNavBar();
		myListingsView.updateListings();
		return myListingsView;
	}
	
	public EditListingView getEditListingView(String postID) {
		if (editListingView == null) {
			editListingView = new EditListingViewImpl();
		}
		editListingView.updateNavBar();
		editListingView.updateListing(postID);
		return editListingView;
	}
	
	public SubscriptionsView getSubscriptionsView() {
		if (subscriptionsView == null) {
			subscriptionsView = new SubscriptionsViewImpl();
		}
		subscriptionsView.updateNavBar();
		return subscriptionsView;
	}
	
	public MarketplaceView getMarketplaceView(String query) {
		if (marketplaceView == null) {
			marketplaceView = new MarketplaceViewImpl();
		}
		marketplaceView.updateNavBar();
		marketplaceView.updateWithQuery(query);
		return marketplaceView;
	}
	
	public FeedbackView getFeedbackView() {
		if (feedbackView == null) {
			feedbackView = new FeedbackViewImpl();
		}
		feedbackView.updateNavBar();
		return feedbackView;
	}
	
}
