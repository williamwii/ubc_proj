package com.ezbook.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ezbook.shared.PostComponent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class SubscriptionEmailServlet extends HttpServlet {
	private static final long serialVersionUID = -8061067566150528176L;
    private static final Logger log = Logger.getLogger(SubscriptionEmailServlet.class.getName());

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Gson gson = new Gson();
		String postBody = request.getParameter("data");
		PostComponent component = gson.fromJson(postBody, PostComponent.class);
		
		String ISBN = component.getISBN();
		if (ISBN!=null && !ISBN.equals("")) {
			JsonArray emails = DBService.searchSubscription(ISBN, component.getPrice());
			if (emails!=null) {
				log.info("Prepare to send emails to: " + emails.toString());
				for (int i=0;i<emails.size();i++) {
					try {
						String email = emails.get(i).getAsJsonObject().get("EMAIL").getAsString();
						EmailService.sendSubscriptionEmail(email, postBody);
					} catch (ClassCastException e) {
						continue;
					} catch (IllegalStateException e) {
						continue;
					}
				}
			}
		}
	}
}
