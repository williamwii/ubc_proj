package com.ezbook.server;

import com.ezbook.client.SubscriptionService;
import com.ezbook.shared.SubscriptionComponent;
import com.ezbook.shared.UnsubscriptionComponent;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SubscriptionServiceImpl extends RemoteServiceServlet implements SubscriptionService{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7740851355159469653L;

	@Override
	public boolean subscribe(SubscriptionComponent subscriber) {
		return DBService.subscribe(subscriber);
	}

	@Override
	public boolean unsubscribe(UnsubscriptionComponent unsubscriber) {
		return DBService.unsubscribe(unsubscriber);
	}

}
