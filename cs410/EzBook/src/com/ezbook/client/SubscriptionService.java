package com.ezbook.client;

import com.ezbook.shared.SubscriptionComponent;
import com.ezbook.shared.UnsubscriptionComponent;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("../subscription")
public interface SubscriptionService extends RemoteService {
    public boolean subscribe(SubscriptionComponent subscriber);
    public boolean unsubscribe(UnsubscriptionComponent unsubscriber);
}
