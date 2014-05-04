package com.ezbook.client;

import com.ezbook.shared.SubscriptionComponent;
import com.ezbook.shared.UnsubscriptionComponent;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>AmazonServiceAsync</code>.
 */
public interface SubscriptionServiceAsync {
    public void subscribe(SubscriptionComponent subscriber,AsyncCallback<Boolean> asyncCallback);
    public void unsubscribe(UnsubscriptionComponent subscriber,AsyncCallback<Boolean> asyncCallback);
}
