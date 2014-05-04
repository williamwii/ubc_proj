package com.ezbook.server;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

import com.ezbook.shared.PostComponent;
import com.ezbook.shared.SearchComponent;
import com.ezbook.shared.SubscriptionComponent;
import com.ezbook.shared.UnsubscriptionComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


public class DBService {
	private static final Logger log = Logger.getLogger(DBService.class.getName());
	
	public static Boolean subscribe(SubscriptionComponent subscriber) {
		if(subscriber.getEmail().length()==0 || subscriber.getISBN().length() ==0){
			return false;
		}
		try {
			URL url = new URL("http://www.bosungbooks.x10.mx/subscribe.php");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write("EMAIL="+URLEncoder.encode(subscriber.getEmail(), "UTF-8")+
					"&ISBN="+URLEncoder.encode(subscriber.getISBN(), "UTF-8")+
					"&AUTHOR="+URLEncoder.encode(subscriber.getAuthor(), "UTF-8")+
					"&TITLE="+URLEncoder.encode(subscriber.getTitle(), "UTF-8")+
					"&IMAGE_URL="+URLEncoder.encode(subscriber.getImageURL(), "UTF-8")+
					"&PRICE_MIN="+URLEncoder.encode(String.valueOf(subscriber.getPriceMin()), "UTF-8")+
					"&PRICE_MAX="+URLEncoder.encode(String.valueOf(subscriber.getPriceMax()), "UTF-8")+
					"&security_code="+URLEncoder.encode(SecurityKeyGenerator.genSubscriptionKey(subscriber.getEmail()), "UTF-8"));
			writer.close();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
				return true;
			} else if(connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){
				return false;
			}
			else if(connection.getResponseCode() == 500 || connection.getResponseCode() == 412){
				log.warning(String.valueOf(connection.getResponseCode()));
				return false;
			}
			else{
				return subscribe(subscriber);
			}
		}
		catch (Exception e){
			e.printStackTrace();
			//ah dam probably server is down so you went stack overflow I give up
			return false;
		}
	}

	public static Boolean unsubscribe(UnsubscriptionComponent unsubscriber) {
		if((unsubscriber.getEmail().length()==0 || unsubscriber.getISBN().length() ==0)&&unsubscriber.getUnsubscribeCode().length()==0){
			return false;
		}
		try {
			String unsubscribeCode = unsubscriber.getUnsubscribeCode();
			URL url = new URL("http://www.bosungbooks.x10.mx/unsubscribe.php");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			if(unsubscribeCode.length()==0){
				unsubscribeCode = SecurityKeyGenerator.genUnsubcriptionKey(unsubscriber.getEmail(), unsubscriber.getISBN());
			}
			writer.write("UNSUBSCRIBE_CODE="+URLEncoder.encode(unsubscribeCode, "UTF-8"));
			writer.close();

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
				return true;
			} 
			else if(connection.getResponseCode() == 500 || connection.getResponseCode() == 412){
				log.warning(String.valueOf(connection.getResponseCode()));
				return false;
			}
			else {
				log.warning(String.valueOf(connection.getResponseCode()));
				return unsubscribe(unsubscriber);
			}
		}
		catch (Exception e){
			e.printStackTrace();
			//ah dam probably server is down so you went stack overflow I give up
			return false;
		}
	}

	public static String newPost(PostComponent postComponent) {

		if(postComponent.getEmail().length()==0 || postComponent.getTitle().length()==0 || postComponent.getComment().length()==0){
			return null;
		}

		try {
			URL url = new URL("http://www.bosungbooks.x10.mx/newPost.php");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write("EMAIL="+URLEncoder.encode(postComponent.getEmail(), "UTF-8")+
					"&FB_NAME="+URLEncoder.encode(postComponent.getFbName(), "UTF-8")+
					"&TITLE="+URLEncoder.encode(postComponent.getTitle(), "UTF-8")+
					"&ISBN="+URLEncoder.encode(postComponent.getISBN(), "UTF-8")+
					"&AUTHOR="+URLEncoder.encode(postComponent.getAuthor(), "UTF-8")+
					"&IMAGE_URL="+URLEncoder.encode(postComponent.getImageURL(), "UTF-8")+
					"&COMMENT="+URLEncoder.encode(postComponent.getComment(), "UTF-8")+
					"&ADDRESS="+URLEncoder.encode(postComponent.getAddress(), "UTF-8")+
					"&LONGITUDE="+URLEncoder.encode(postComponent.getLongitude().toString(), "UTF-8")+
					"&LATITUDE="+URLEncoder.encode(postComponent.getLatitude().toString(), "UTF-8")+					
					"&PRICE="+URLEncoder.encode(String.valueOf(postComponent.getPrice()), "UTF-8")+
					"&security_code="+URLEncoder.encode(SecurityKeyGenerator.genNewPostKey(postComponent.getEmail()), "UTF-8"));
			writer.close();
			
			String postID = ServerUtil.fetchURLPost(connection);
			log.info("newPost has been made with postID: "+postID);
	        
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// OK
				return postID;
			} else if(connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){ 
				log.warning("newPOST ERROR: HTTP_UNAUTHORIZED");
				//this should never happen (unless there is bug)
				return null;
			}
			else if(connection.getResponseCode() == 500 || connection.getResponseCode() == 412){
				log.warning(String.valueOf(connection.getResponseCode()));
				return null;
			}
			else {
				log.warning(String.valueOf(connection.getResponseCode()));
				// Server returned HTTP error code.
				//keep retrying until you succeed!! fighting!!
				return newPost(postComponent);
			}
		}
		catch (Exception e){
			e.printStackTrace();
			//ah dam probably server is down so you went stack overflow I give up
			return null;
		}
	}
	public static Boolean editPost(PostComponent postComponent) {
		
		if(postComponent.getEmail().length()==0 || postComponent.getTitle().length()==0 || postComponent.getComment().length()==0){
			return false;
		}

		try {
			URL url = new URL("http://www.bosungbooks.x10.mx/editPost.php");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write("POST_ID="+URLEncoder.encode(postComponent.getPostID(), "UTF-8")+
					"&EMAIL="+URLEncoder.encode(postComponent.getEmail(), "UTF-8")+
					"&TITLE="+URLEncoder.encode(postComponent.getTitle(), "UTF-8")+
					"&ISBN="+URLEncoder.encode(postComponent.getISBN(), "UTF-8")+
					"&AUTHOR="+URLEncoder.encode(postComponent.getAuthor(), "UTF-8")+
					"&IMAGE_URL="+URLEncoder.encode(postComponent.getImageURL(), "UTF-8")+
					"&COMMENT="+URLEncoder.encode(postComponent.getComment(), "UTF-8")+
					"&ADDRESS="+URLEncoder.encode(postComponent.getAddress(), "UTF-8")+
					"&LONGITUDE="+URLEncoder.encode(postComponent.getLongitude().toString(), "UTF-8")+
					"&LATITUDE="+URLEncoder.encode(postComponent.getLatitude().toString(), "UTF-8")+
					"&PRICE="+URLEncoder.encode(String.valueOf(postComponent.getPrice()), "UTF-8")+
					"&security_code="+URLEncoder.encode(SecurityKeyGenerator.genEditPostKey(postComponent.getEmail()), "UTF-8"));
			writer.close();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// OK
				return true;
			} else if(connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){ 
				log.warning("editPOST ERROR: HTTP_UNAUTHORIZED");
				//this should never happen (unless there is bug)
				return false;
			}
			else if(connection.getResponseCode() == 500 || connection.getResponseCode() == 412){
				log.warning(String.valueOf(connection.getResponseCode()));
				return false;
			}
			else {
				log.warning(String.valueOf(connection.getResponseCode()));
				// Server returned HTTP error code.
				//keep retrying until you succeed!! fighting!!
				return editPost(postComponent);
			}
		}
		catch (Exception e){
			e.printStackTrace();
			//ah dam probably server is down so you went stack overflow I give up
			return false;
		}
	}
	public static Boolean renewPost(String email, int postID) {
		
		if(email.length()==0){
			return false;
		}

		try {
			URL url = new URL("http://www.bosungbooks.x10.mx/renewPost.php");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write("POST_ID="+URLEncoder.encode(String.valueOf(postID), "UTF-8")+
					"&EMAIL="+URLEncoder.encode(email, "UTF-8")+
					"&security_code="+URLEncoder.encode(SecurityKeyGenerator.genEditPostKey(email), "UTF-8"));
			writer.close();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// OK
				return true;
			} else if(connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){ 
				log.warning("editPOST ERROR: HTTP_UNAUTHORIZED");
				//this should never happen (unless there is bug)
				return false;
			}
			else if(connection.getResponseCode() == 500 || connection.getResponseCode() == HttpURLConnection.HTTP_PRECON_FAILED){
				log.warning(String.valueOf(connection.getResponseCode()));
				return false;
			}
			else {
				log.warning(String.valueOf(connection.getResponseCode()));
				// Server returned HTTP error code.
				//keep retrying until you succeed!! fighting!!
				return renewPost(email,postID);
			}
		}
		catch (Exception e){
			e.printStackTrace();
			//ah dam probably server is down so you went stack overflow I give up
			return false;
		}
	}
	
	public static Boolean deletePost(String email, int postID) {
		if(email==null || email.length()==0){
			return false;
		}
		try {
			URL url = new URL("http://www.bosungbooks.x10.mx/deletePost.php");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write("POST_ID="+URLEncoder.encode(String.valueOf(postID), "UTF-8")+
					"&EMAIL="+URLEncoder.encode(email, "UTF-8")+
					"&security_code="+URLEncoder.encode(SecurityKeyGenerator.genDeletePostKey(String.valueOf(postID)), "UTF-8"));
			writer.close();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// OK
				return true;
			} else if(connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){ 
				log.warning("deletePOST ERROR: HTTP_UNAUTHORIZED");
				//this should never happen (unless there is bug)
				return false;
			}
			else if(connection.getResponseCode() == 500 || connection.getResponseCode() == 412){
				log.warning(String.valueOf(connection.getResponseCode()));
				return false;
			}
			else {
				log.warning(String.valueOf(connection.getResponseCode()));
				// Server returned HTTP error code.
				//keep retrying until you succeed!! fighting!!
				return deletePost(email, postID);
			}
		}
		catch (Exception e){
			e.printStackTrace();
			//ah dam probably server is down so you went stack overflow I give up
			return false;
		}
	}
		
		
	public static JsonArray searchSubscription(String ISBN, double price)  {

		if(ISBN==null || ISBN.length()==0){
			return null;
		}
		try {
			URL url = new URL("http://www.bosungbooks.x10.mx/searchSubscription.php");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write("ISBN="+URLEncoder.encode(ISBN, "UTF-8")+
					"&PRICE="+URLEncoder.encode(String.valueOf(price), "UTF-8")+
					"&security_code="+URLEncoder.encode(SecurityKeyGenerator.genSearchSubscriptionKey(ISBN), "UTF-8"));
			writer.close();	

			String res = ServerUtil.fetchURLPost(connection);
			JsonParser parser = new JsonParser();
			return parser.parse(res).getAsJsonArray();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static JsonArray searchMySubscription(String email)  {

		if(email==null || email.length()==0){
			return null;
		}
		try {
			URL url = new URL("http://www.bosungbooks.x10.mx/searchMySubscription.php");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write("EMAIL="+URLEncoder.encode(email, "UTF-8")+
						 "&security_code="+URLEncoder.encode(SecurityKeyGenerator.genSearchMySubscriptionKey(email), "UTF-8"));
			writer.close();	

			String res = ServerUtil.fetchURLPost(connection);
			JsonParser parser = new JsonParser();
			JsonArray resultJsonElement = parser.parse(res).getAsJsonArray();

			return resultJsonElement;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static JsonArray search(SearchComponent searchComponent)  {
		try {
			URL url = new URL("http://www.bosungbooks.x10.mx/searchMarketPlace.php");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write("POST_ID="+URLEncoder.encode(searchComponent.getPostID(),"UTF-8")+
					"&EMAIL="+URLEncoder.encode(searchComponent.getEmail(),"UTF-8")+
					"&TITLE="+URLEncoder.encode(searchComponent.getTitle(),"UTF-8")+
					"&AUTHOR="+URLEncoder.encode(searchComponent.getAuthor(),"UTF-8")+
					"&ISBN="+URLEncoder.encode(searchComponent.getISBN(),"UTF-8")+
					"&PRICEMIN="+URLEncoder.encode(searchComponent.getPriceMin(),"UTF-8")+
					"&PRICEMAX="+URLEncoder.encode(searchComponent.getPriceMax(),"UTF-8")+
					"&ORDER_BY="+URLEncoder.encode(searchComponent.getOrderBy(),"UTF-8")+
					"&security_code="+URLEncoder.encode(SecurityKeyGenerator.genSearchMarketPlaceKey(),"UTF-8"));
			writer.close();	

			String res = ServerUtil.fetchURLPost(connection);
			JsonParser parser = new JsonParser();
			JsonArray resultJsonElement = parser.parse(res).getAsJsonArray();

			return resultJsonElement;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
