package com.ezbook.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../secure/feedback")
public interface FeedbackService extends RemoteService {

	public boolean sendFeedback(String message);
	
}
