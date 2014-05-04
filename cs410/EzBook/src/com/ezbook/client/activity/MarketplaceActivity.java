package com.ezbook.client.activity;

import com.ezbook.client.ClientFactory;
import com.ezbook.client.place.Marketplace;
import com.ezbook.client.ui.MarketplaceView;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class MarketplaceActivity extends AbstractActivity implements MarketplaceView.Presenter {

	private ClientFactory clientFactory;
	private String query;
	
	public MarketplaceActivity(Marketplace marketplace, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		MarketplaceView marketplaceView = clientFactory.getMarketplaceView(query);
		marketplaceView.setPresenter(this);
		panel.setWidget(marketplaceView.asWidget());
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

}
