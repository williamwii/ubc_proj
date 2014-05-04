package com.ezbook.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class FBLoginServlet extends HttpServlet {
	private static final long serialVersionUID = -4384616709419867799L;

	private static final String HOST = "http://bosungbooks.appspot.com/";
	private static final String APP_ID = "365202260235570";
	private static final String SECRET = "d165e97312607e576eb350f689b0cde5";

	// Dev
//	private static final String HOST = "http://bosungdev.appspot.com:8888/";
//	private static final String APP_ID = "390711634340010";
//	private static final String SECRET = "c9bcb9de9dcc0281ab1b777063a59599";

    private static final String FB_DIALOG_URL = "https://www.facebook.com/dialog/oauth";
    private static final String FB_OAUTH_URL = "https://graph.facebook.com/oauth/access_token";

    private static final Logger log = Logger.getLogger(FBLoginServlet.class.getName());
    
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String param = request.getParameter("param");
		if (param==null) {
			param = "ezbook";
		}
		String place = request.getParameter("place");
		if (place==null) {
			place = "EzBook";
			param = "ezbook";
		}

		String redirectUrl = HOST + "fbLogin?place=" + URLEncoder.encode(place, "UTF-8") + "&param=" + URLEncoder.encode(param, "UTF-8");

		HttpSession session = request.getSession();
		String fbToken = (String)session.getAttribute("fb_token");
		if (fbToken!=null) {
			JsonObject res = checkFBToken(fbToken);
			if (res!=null) {
				String fbEmail = res.get("email").getAsString();
				String name = res.get("name").getAsString();
				session.setAttribute("fb_token", fbToken);
				session.setAttribute("fb_email", fbEmail);
				session.setAttribute("fb_name", name);
				log.info("Email: " + fbEmail + " is logged in.");
			}
			else {
				session.invalidate();
			}
			response.sendRedirect("#" + place + ":" + param); return;
		}
		
		if (request.getParameter("code")!=null) {
			JsonObject json = new JsonObject();
			BufferedReader reader = null;
			try {
				String authUrl = getAuthorizeUrl(true, redirectUrl)
						+ "&" + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(request.getParameter("code"), "UTF-8")
						+ "&" + URLEncoder.encode("client_secret", "UTF-8") + "=" + URLEncoder.encode(SECRET, "UTF-8");

				String res = ServerUtil.fetchURL(authUrl);
				
				if (res.contains("error") || res.contains("error_code")) {
					log.warning("Error while retrieving access_token: " + res + " with authUrl: " + authUrl);
				}
				else {
					String[] splitedRes = res.split("&");
					for (int i=0;i<splitedRes.length;i++) {
						String[] keyVal = splitedRes[i].split("=");
						json.add(keyVal[0], new JsonPrimitive(keyVal[1]));
					}
				}
			} catch (MalformedURLException e) {
	            
	        } catch (IOException e) {
	           
	        } finally {
				if (reader!=null) {
					reader.close();
				}
			}
			
			if (json.get("access_token")!=null) {
				fbToken = json.get("access_token").getAsString();
				JsonObject res = checkFBToken(fbToken);
				if (res!=null) {
					String fbEmail = res.get("email").getAsString();
					String name = res.get("name").getAsString();
					session.setAttribute("fb_token", fbToken);
					session.setAttribute("fb_email", fbEmail);
					session.setAttribute("fb_name", name);
					log.info("Email: " + fbEmail + " is logged in.");
				}
				else {
					session.invalidate();
				}
			}
			response.sendRedirect("#" + place + ":" + param); return;
		}
		else {
			response.sendRedirect(getAuthorizeUrl(false, redirectUrl)); return;
		}
	}
	
    private String getAuthorizeUrl(boolean hasCode, String redirectUrl) {
        try {
			return (hasCode ? FB_OAUTH_URL : FB_DIALOG_URL) + "?"
					+ URLEncoder.encode("client_id", "UTF-8") + "=" + URLEncoder.encode(APP_ID, "UTF-8")
					+ "&" + URLEncoder.encode("redirect_uri", "UTF-8") + "=" + URLEncoder.encode(redirectUrl, "UTF-8")
					+ "&" + URLEncoder.encode("scope", "UTF-8") + "=" + URLEncoder.encode("email,publish_actions,publish_stream", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return HOST;
		}
    }
    
    private JsonObject checkFBToken(String accessToken) {
    	JsonObject json = new JsonObject();
    	try {
			String res = ServerUtil.fetchURL("https://graph.facebook.com/me?access_token=" + accessToken);
			json = new JsonParser().parse(res).getAsJsonObject();
		} catch (MalformedURLException e) {
    		log.warning("Failed to retrieve info with access_token: " + accessToken);
			return null;
		} catch (IOException e) {
    		log.warning("Failed to retrieve info with access_token: " + accessToken);
			return null;
		}
    	if (json.get("error_code")!=null || json.get("error")!=null) {
    		log.warning("Failed to retrieve info with access_token: " + accessToken);
    		return null;
    	}
    	return json;
    }
}
