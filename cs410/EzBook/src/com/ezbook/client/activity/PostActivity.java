package com.ezbook.client.activity;

import com.ezbook.client.ClientFactory;
import com.ezbook.client.place.Post;
import com.ezbook.client.ui.PostView;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PostActivity extends AbstractActivity implements PostView.Presenter {

	private ClientFactory clientFactory;
	
	public PostActivity(Post postPlace, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		PostView postView = clientFactory.getPostView();
		postView.setPresenter(this);
		panel.setWidget(postView.asWidget());
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

}
