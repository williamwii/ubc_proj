package com.ezbook.client.activity;

import com.ezbook.client.ClientFactory;
import com.ezbook.client.place.EditListing;
import com.ezbook.client.ui.EditListingView;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class EditListingActivity extends AbstractActivity implements EditListingView.Presenter {

	private ClientFactory clientFactory;
	private String postID;
	
	public EditListingActivity(EditListing editListing, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	public void setPostID(String postID) {
		this.postID = postID;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		EditListingView editListingView = clientFactory.getEditListingView(postID);
		editListingView.setPresenter(this);
		panel.setWidget(editListingView.asWidget());
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

}
