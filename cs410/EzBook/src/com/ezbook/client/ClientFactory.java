package com.ezbook.client;

import com.ezbook.client.ui.EditListingView;
import com.ezbook.client.ui.EzBookView;
import com.ezbook.client.ui.FeedbackView;
import com.ezbook.client.ui.MarketplaceView;
import com.ezbook.client.ui.MyListingsView;
import com.ezbook.client.ui.PostView;
import com.ezbook.client.ui.SubscriptionsView;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {
	EventBus getEventBus();
	PlaceController getPlaceController();
	EzBookView getEzBookView();
	PostView getPostView();
	MyListingsView getMyListingsView(String query);
	EditListingView getEditListingView(String postID);
	SubscriptionsView getSubscriptionsView();
	MarketplaceView getMarketplaceView(String query);
	FeedbackView getFeedbackView();
}
