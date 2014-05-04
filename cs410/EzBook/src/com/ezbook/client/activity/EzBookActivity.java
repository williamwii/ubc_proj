package com.ezbook.client.activity;

import com.ezbook.client.place.EzBook;
import com.ezbook.client.ui.EzBookView;
import com.ezbook.client.ClientFactory;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class EzBookActivity extends AbstractActivity implements EzBookView.Presenter{

	private ClientFactory clientFactory;

	public EzBookActivity(EzBook ezBookPlace, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		EzBookView ezBookView = clientFactory.getEzBookView();
		ezBookView.setPresenter(this);
		panel.setWidget(ezBookView.asWidget());
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

}
