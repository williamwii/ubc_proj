package com.ezbook.server;

import com.ezbook.client.MarketPlaceService;
import com.ezbook.shared.SearchComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;

public class MarketPlaceServiceImpl extends RemoteServiceServlet implements
		MarketPlaceService {
	private static final long serialVersionUID = 2676496503187274573L;
	private static final Logger log = Logger.getLogger(MarketPlaceServiceImpl.class.getName());

	private static final int RESULT_PER_PAGE = 10;
	private static final String[] ORDERS = { "", "date", "price" };
	
	@Override
	public String search(SearchComponent component, int page) {
		return doSearch(component, page, true);
	}
    
	private String doSearch(SearchComponent search, int page, boolean useCache) {
		// Check postID, priceMin and priceMax are numbers
		try {
			if (search.getPostID().length() > 0) {
				Integer.parseInt(search.getPostID());
			}
			if (search.getPriceMin().length() > 0) {
				Double.parseDouble(search.getPriceMin());
			}
			if (search.getPriceMax().length() > 0) {
				Double.parseDouble(search.getPriceMax());
			}
		}
		catch (NumberFormatException e) {
			log.warning("Invalid number format");
			try {
				HttpServletResponse response = this.getThreadLocalResponse();
				response.sendError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal server error");
				return null;
			} catch (IOException e1) {
				return null;
			}
		}
		
		if (page < 0) {
			try {
				HttpServletResponse response = this.getThreadLocalResponse();
				response.sendError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal server error");
				return null;
			} catch (IOException e1) {
				return null;
			}
		}
		
		String orderBy = search.getOrderBy();
		boolean contains = false;
		for (int i=0;i<ORDERS.length;i++) {
			if (ORDERS[i].equalsIgnoreCase(orderBy)) {
				if (i==1) {
					search.setOrderBy("POST_DATE");
				}
				contains = true;
				break;
			}
		}
		if (!contains) {
			search.setOrderBy("POST_DATE");
		}
		
		// The result set
		JsonArray result = null;
		
		if(useCache){
			Cache cache = ServerUtil.getCache();
			if (cache!=null) {
				// Check in cache
				String jsonStr = (String)cache.get(search);
				if (jsonStr!=null) {	
					// Cache has the result, get it
					log.info("Reusing cached results");
					result = new JsonParser().parse(jsonStr).getAsJsonArray();
				}
				else {
					// Cache does not contain the key, now do search and add the result to cache
					result = DBService.search(search);
					if (result!=null) {
						cache.put(search, result.toString());
					}
				}
			}
		}
		else {
			result = DBService.search(search);
		}
		
		JsonArray subList = new JsonArray();
		if (result!=null) {
			int start = RESULT_PER_PAGE * page;
			int end = start + RESULT_PER_PAGE;
			for (int i=start;i<end && i<result.size();i++) {
				subList.add(result.get(i)); 
			}
		}
		JsonObject data = new JsonObject();
		data.add("per_page", new JsonPrimitive(RESULT_PER_PAGE));
		data.add("count", new JsonPrimitive(result!=null ? result.size() : 0));
		data.add("result", subList);
		
		return data.toString();
	}
}