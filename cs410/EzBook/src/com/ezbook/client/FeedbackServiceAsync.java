package com.ezbook.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FeedbackServiceAsync {

	public void sendFeedback(String message, AsyncCallback<Boolean> asyncCallback);
	
}
