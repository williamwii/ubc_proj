package com.ezbook.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class SecurityFilter implements Filter {
	
	@SuppressWarnings("unused") private FilterConfig filterConfig;
    private static final Logger log = Logger.getLogger(SecurityFilter.class.getName());
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest)request).getSession();
 		if (session!=null && session.getAttribute("fb_token")!=null
 				&& session.getAttribute("fb_name")!=null
 				&& session.getAttribute("fb_email")!=null) {
 			if (filterChain!=null) {
 				filterChain.doFilter(request, response);
 			}
		}
 		else {
 			HttpServletResponse res = (HttpServletResponse)response;
 			log.warning("Unauthorized user");
 			res.sendError(HttpURLConnection.HTTP_UNAUTHORIZED, "Unauthorized user");
 		}
	}

	@Override
	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}
	
	@Override
	public void destroy() {

	}
}
