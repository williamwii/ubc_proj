package com.ezbook.client.activity;

import com.ezbook.client.ClientFactory;
import com.ezbook.client.place.MyListings;
import com.ezbook.client.ui.MyListingsView;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class MyListingsActivity extends AbstractActivity implements MyListingsView.Presenter {

	private ClientFactory clientFactory;
	private String query;
	public MyListingsActivity(MyListings myListing, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		MyListingsView myListingsView = clientFactory.getMyListingsView(query);
		myListingsView.setPresenter(this);
		panel.setWidget(myListingsView.asWidget());
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

}
