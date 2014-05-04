package com.ezbook.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServerFilter implements Filter {

	@SuppressWarnings("unused") private FilterConfig filterConfig;
    private static final Logger log = Logger.getLogger(ServerFilter.class.getName());

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		String secret = request.getParameter("secret");
		if (secret.equals(ServerUtil.SERVER_SECRET) && filterChain!=null) {
			filterChain.doFilter(req, res);
		}
		else {
			log.warning("Invalid server request");
			response.sendError(500, "Invalid server request.");
		}
	}

	@Override
	public void init(FilterConfig config) {
		this.filterConfig = config;
	}

	@Override
	public void destroy() {
	
	}
}
