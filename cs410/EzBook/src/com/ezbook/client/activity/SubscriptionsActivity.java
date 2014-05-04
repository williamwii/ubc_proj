package com.ezbook.client.activity;

import com.ezbook.client.ClientFactory;
import com.ezbook.client.place.Subscriptions;
import com.ezbook.client.ui.SubscriptionsView;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class SubscriptionsActivity extends AbstractActivity implements SubscriptionsView.Presenter {

	private ClientFactory clientFactory;
	
	public SubscriptionsActivity(Subscriptions subscriptionsPlace, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		SubscriptionsView subscriptionsView = clientFactory.getSubscriptionsView();
		subscriptionsView.setPresenter(this);
		panel.setWidget(subscriptionsView.asWidget());
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

}
