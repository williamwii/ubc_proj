package com.ezbook.server;

import com.ezbook.shared.PostComponent;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.gson.Gson;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

public class EmailService {

	public static enum EMAIL_TYPE { DEFAULT, SUBSCRIPTION }
	
	private static Queue getQueue() {
		return QueueFactory.getQueue("email");
	}
	
	public static void sendEmail(String email, String message) {
		sendEmail(email, message, EMAIL_TYPE.DEFAULT);
	}
	
	public static void sendSubscriptionEmail(String email, String json) {
		sendEmail(email, json, EMAIL_TYPE.SUBSCRIPTION);
	}
	
	public static void sendEmail(String email, String message, EMAIL_TYPE type) {
		String emailBackendAddr = BackendServiceFactory.getBackendService().getBackendAddress("email");
		getQueue().add(withUrl("/server/email").method(Method.POST).header("HOST", emailBackendAddr).param("secret", ServerUtil.SERVER_SECRET)
				.param("type", type.toString()).param("to", email).param("data", message));
	}
	
	public static void addSubscriptionEmails(PostComponent component) {
		Gson gson = new Gson();
		String postBody = gson.toJson(component);
		QueueFactory.getDefaultQueue().add(withUrl("/server/email/subscription").method(Method.POST).param("secret", ServerUtil.SERVER_SECRET)
				.param("data", postBody));
	}
}
