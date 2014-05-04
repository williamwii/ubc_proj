package com.ezbook.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import com.ezbook.client.PostService;
import com.ezbook.shared.PostComponent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PostServiceImpl extends RemoteServiceServlet implements PostService {
	private static final long serialVersionUID = 2676496503187274573L;
	private static final Logger log = Logger.getLogger(PostServiceImpl.class.getName());

	@Override
	public Boolean newPost(PostComponent post, boolean postOnFb) {
		// Retrieve email from session
		String email = (String)this.getThreadLocalRequest().getSession().getAttribute("fb_email");
		String fbName = (String)this.getThreadLocalRequest().getSession().getAttribute("fb_name");
		
		Double latitude = post.getLatitude();
		Double longitude = post.getLongitude();
		String address = post.getAddress();
		if (!(address==null || address.equals("")) && (latitude==null || longitude==null)) {
			// Seems like client side failed to resolve the address into geolocation.
			// Try again here
			try {
				String locationResolveURL = "https://maps.googleapis.com/maps/api/geocode/json?address="
											+ URLEncoder.encode(address, "UTF-8")
											+ "&sensor=false";
				String res = ServerUtil.fetchURL(locationResolveURL);
				log.info(res);
				JsonObject json = new JsonParser().parse(res).getAsJsonObject();
				if (json.get("status").getAsString().equals("OK")) {
					JsonObject result = json.get("results").getAsJsonArray().get(0).getAsJsonObject();
					JsonObject location = result.get("geometry").getAsJsonObject().get("location").getAsJsonObject();
					latitude = location.get("lat").getAsDouble();
					longitude = location.get("lng").getAsDouble();
				}
			} catch (UnsupportedEncodingException e1) {
				
			} catch (MalformedURLException e) {
				
			} catch (IOException e) {
				
			} catch (IllegalStateException e) {
				latitude = null;
				longitude = null;
			}
		}
		
		if (latitude!=null && longitude!=null) {
			post.setLatitude(latitude);
			post.setLongitude(longitude);
		}
		post.setEmail(email);
		post.setFbName(fbName);
		String postID = DBService.newPost(post);
		post.setPostID(postID);
		
		// Add check and send subscription email task to queue
		EmailService.addSubscriptionEmails(post);
		
		if(postOnFb && postID!=null){
			if(postOnFb){
				try{
					String title = post.getTitle();
					String author = post.getAuthor();
					String isbn = post.getISBN();
					String price = String.format("%.2f", post.getPrice());
					String comment = post.getComment();
					String imageUrl = post.getImageURL();

					URL url = new URL("https://graph.facebook.com/me/feed");
					String accessToken = (String) this.getThreadLocalRequest().getSession().getAttribute("fb_token");
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setRequestMethod("POST");
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					String postBody = "access_token=" + accessToken
							+ "&name=" + URLEncoder.encode(title, "UTF-8")
							+ "&link=" + URLEncoder.encode("http://bosungbooks.appspot.com/#Marketplace:post=" + postID, "UTF-8")
							+ "&caption=" + URLEncoder.encode("Posted on Bosung Books", "UTF-8")
							+ "&description=" + URLEncoder.encode((author==null||author.equals("") ? "" : "Author: " + author) + ";  "
									+ (isbn==null||isbn.equals("") ? "" : "ISBN: " + isbn) + ";  "
									+ "Price: $" + price + ";  "
									+ "Details: " + comment, "UTF-8")
									+ "&message=I'm selling this book on Bosung Books!"
									+ (imageUrl==null||imageUrl.equals("") ? "" : "&picture="+URLEncoder.encode(imageUrl, "UTF-8"));
					writer.write(postBody);
					writer.close();
					int code = connection.getResponseCode();
					if (code != HttpURLConnection.HTTP_OK) {
						log.warning("Posting on Facebook failed: " + connection.getResponseMessage());
					}
				} catch (MalformedURLException e) {
					log.warning("Posting on Facebook failed: " + e.getLocalizedMessage());
				} catch (IOException e) {
					log.warning("Posting on Facebook failed: " + e.getLocalizedMessage());
				}
			}
		}

		return postID!=null;
	}
}
