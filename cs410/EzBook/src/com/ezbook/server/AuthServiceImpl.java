package com.ezbook.server;

import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import com.ezbook.client.AuthService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
public class AuthServiceImpl extends RemoteServiceServlet implements AuthService {
	private static final long serialVersionUID = 5958268406612347425L;

    private static final Logger log = Logger.getLogger(AuthServiceImpl.class.getName());
	
    public String status() {
    	if (this.getThreadLocalRequest().isRequestedSessionIdValid()) {
	    	HttpSession session = this.getThreadLocalRequest().getSession();
	    	if (session.getAttribute("fb_token")!=null) {
	    		return session.getAttribute("fb_name")!=null ? session.getAttribute("fb_name").toString() : null;
	    	}
	    	return null;
    	}
    	return null;
    }

	public boolean logout() {
		HttpSession session = this.getThreadLocalRequest().getSession();
		if (this.getThreadLocalRequest().isRequestedSessionIdValid() && session.getAttribute("fb_token")!=null) {
			log.info("Email: " + session.getAttribute("fb_email").toString() + " is logging out.");
    	}
		session.invalidate();
		return true;
	}
}
