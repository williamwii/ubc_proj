package com.ezbook;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ezbook.client.place.EditListing;
import com.ezbook.client.place.EzBook;
import com.ezbook.client.place.Feedback;
import com.ezbook.client.place.Marketplace;
import com.ezbook.client.place.MyListings;
import com.ezbook.client.place.Post;
import com.ezbook.client.place.Subscriptions;

public class ClientPlaceTest {

	@Test
	public void testEditListing() {
		EditListing editListing = new EditListing("token");
		assertEquals("token", editListing.getToken());
		EditListing.Tokenizer tokenizer = new EditListing.Tokenizer();
		assertEquals("token2",tokenizer.getPlace("token2").getToken());
		assertEquals("token3",tokenizer.getToken(new EditListing("token3")));
	}
	
	@Test
	public void testEzBook(){
		EzBook.Tokenizer tokenizer = new EzBook.Tokenizer();
		assertEquals("com.ezbook.client.place.EzBook",tokenizer.getPlace("").getClass().getCanonicalName());
		assertEquals("ezbook",tokenizer.getToken(new EzBook()));
	}
	
	@Test
	public void testFeedback(){
		Feedback.Tokenizer tokenizer = new Feedback.Tokenizer();
		assertEquals("com.ezbook.client.place.Feedback",tokenizer.getPlace("").getClass().getCanonicalName());
		assertEquals("feedback",tokenizer.getToken(new Feedback()));
	}
	
	@Test
	public void testMarketplace() {
		Marketplace marketplace = new Marketplace("token");
		assertEquals("token", marketplace.getQuery());
		Marketplace.Tokenizer tokenizer = new Marketplace.Tokenizer();
		assertEquals("token2",tokenizer.getPlace("token2").getQuery());
		assertEquals("token3",tokenizer.getToken(new Marketplace("token3")));
	}

	@Test
	public void testMyListings() {
		MyListings myListings = new MyListings("token");
		assertEquals("token", myListings.getQuery());
		MyListings.Tokenizer tokenizer = new MyListings.Tokenizer();
		assertEquals("token2",tokenizer.getPlace("token2").getQuery());
		assertEquals("token3",tokenizer.getToken(new MyListings("token3")));
	}
	
	@Test
	public void testPost(){
		Post.Tokenizer tokenizer = new Post.Tokenizer();
		assertEquals("com.ezbook.client.place.Post",tokenizer.getPlace("").getClass().getCanonicalName());
		assertEquals("new",tokenizer.getToken(new Post()));
	}
	
	@Test
	public void testSubscriptions(){
		Subscriptions.Tokenizer tokenizer = new Subscriptions.Tokenizer();
		assertEquals("com.ezbook.client.place.Subscriptions",tokenizer.getPlace("").getClass().getCanonicalName());
		assertEquals("subscriptions",tokenizer.getToken(new Subscriptions()));
	}
}
