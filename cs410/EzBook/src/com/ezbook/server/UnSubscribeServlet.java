package com.ezbook.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ezbook.shared.UnsubscriptionComponent;

public class UnSubscribeServlet extends HttpServlet {
	private static final long serialVersionUID = 1965454697314278772L;
	private static final Logger log = Logger.getLogger(UnSubscribeServlet.class.getName());

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String code = request.getParameter("code");
		if (code!=null && !code.equals("")) {
			log.info("Unsubscribing with code: " + code);
			UnsubscriptionComponent component = new UnsubscriptionComponent();
			component.setUnsubscribeCode(code);
			if (DBService.unsubscribe(component)) {
				response.sendRedirect("#Subscriptions:unsubscribed");
				return;
			}
			else {
				response.sendError(500, "An error occurred, please try again");
				return;
			}
		}
		else {
			response.sendError(500, "Invalide code");
			return;
		}
	}
}
