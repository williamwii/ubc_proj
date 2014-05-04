package com.ezbook.client.activity;

import com.ezbook.client.ClientFactory;
import com.ezbook.client.place.Feedback;
import com.ezbook.client.ui.FeedbackView;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class FeedbackActivity extends AbstractActivity implements FeedbackView.Presenter {

	private ClientFactory clientFactory;
	
	public FeedbackActivity(Feedback postPlace, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		FeedbackView feedbackView = clientFactory.getFeedbackView();
		feedbackView.setPresenter(this);
		panel.setWidget(feedbackView.asWidget());
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}
}
