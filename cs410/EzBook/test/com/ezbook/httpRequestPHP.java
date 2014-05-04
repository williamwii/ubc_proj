package com.ezbook;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.ezbook.server.DBService;
//import com.ezbook.server.ServerUtil;
import com.ezbook.server.SubscriptionServiceImpl;
//import com.ezbook.server.amazon.SearchBook;
import com.ezbook.shared.PostComponent;
import com.ezbook.shared.SearchComponent;
import com.ezbook.shared.SubscriptionComponent;
import com.ezbook.shared.UnsubscriptionComponent;
import com.google.gson.JsonArray;

public class httpRequestPHP {
	PostComponent postComponent;
	SearchComponent searchComponent;
	SubscriptionComponent subComponent;
	SubscriptionComponent failSubComp;
	UnsubscriptionComponent unsubComponent;
	UnsubscriptionComponent unsubFail;
	JsonArray jsonArray;
	String postID;
	
	@Before
	public void setUp() throws Exception {
		postComponent = new PostComponent();
		postComponent.setAddress("vancouver");
		postComponent.setAuthor("BosungTest");
		postComponent.setComment("testComment");
		postComponent.setEmail("bosung90@gmail.com");
		postComponent.setFbName("Eric Bosung Kim");
		postComponent.setImageURL("http://imageURL.com/omg.jpg");
		postComponent.setISBN("123123123");
		postComponent.setLatitude(123.123);
		postComponent.setLongitude(321.321);
		postComponent.setPrice(3213.21);
		postComponent.setTitle("testBosung222222");
		
		searchComponent = new SearchComponent();
		searchComponent.setTitle("testBosung222222");
		searchComponent.setAuthor("BosungTest");
		searchComponent.setISBN("123123123");
		searchComponent.setOrderBy("POST_DATE");
		searchComponent.setPriceMax("3213.21");
		searchComponent.setPriceMin("3213.21");
	}

	@Test
	public void testSubscribe() {
		SubscriptionServiceImpl service = new SubscriptionServiceImpl();
		subComponent = new SubscriptionComponent();
		String email = String.valueOf((int)(Math.random()*1000000));
		subComponent.setEmail(email);
		subComponent.setTitle("testing title sub");
		subComponent.setAuthor("testing author sub");
		subComponent.setISBN("123456");
		subComponent.setImageURL("testing image url sub");
		subComponent.setPriceMax("3213.21");
		subComponent.setPriceMin("3213.21");

		failSubComp = new SubscriptionComponent();
		failSubComp.setEmail("");

		assertFalse(service.subscribe(failSubComp));
		
		failSubComp.setEmail("tempEmail@email.com");
		failSubComp.setISBN("");
		assertFalse(service.subscribe(failSubComp));
		
		assertTrue(DBService.subscribe(subComponent));
		UnsubscriptionComponent unsubComponent = new UnsubscriptionComponent();
		assertFalse(service.unsubscribe(unsubComponent));//fails due to no email no unsubcode
		unsubComponent.setEmail("containsEmail");
		assertFalse(service.unsubscribe(unsubComponent));//fails due to yes email no isbn no unsubcode
		unsubComponent.setISBN("containsISBN");
		assertTrue(service.unsubscribe(unsubComponent));//pass due to yes email yes isbn no unsubcode
		unsubComponent.setISBN(null);
		unsubComponent.setUnsubscribeCode("randomCode");
		assertTrue(service.unsubscribe(unsubComponent));//pass due to yes email no isbn yes unsubcode
		
		unsubComponent.setEmail(email);
		unsubComponent.setISBN("123456");
		assertTrue(service.unsubscribe(unsubComponent));
		unsubComponent.setEmail("");
		unsubComponent.setISBN("");
		unsubComponent.setUnsubscribeCode("");
		assertFalse(service.unsubscribe(unsubComponent));

	}

	@Test
	public void searchSubscription() {
		SubscriptionServiceImpl service = new SubscriptionServiceImpl();
		subComponent = new SubscriptionComponent();
		subComponent.setEmail("meleteme@deleteme.com");
		subComponent.setTitle("testing title sub");
		subComponent.setAuthor("testing author sub");
		subComponent.setISBN("deleteme");
		subComponent.setImageURL("testing image url sub");
		subComponent.setPriceMax("3213.21");
		subComponent.setPriceMin("3213.21");
		service.subscribe(subComponent);
		assertNotNull(DBService.searchSubscription("deleteme", 3213.21));
		assertNull(DBService.searchSubscription("",3213.21));
		
		assertNull(DBService.searchSubscription(null, 0.0));//null due to ISBN null
		assertNull(DBService.searchSubscription(null, -1));//null due to ISBN null
		
		UnsubscriptionComponent unsubscriber = new UnsubscriptionComponent();
		unsubscriber.setEmail("meleteme@deleteme.com");
		unsubscriber.setISBN("deleteme");
		service.unsubscribe(unsubscriber);
	
	}

	@Test
	public void searchMySubscription() {
		subComponent = new SubscriptionComponent();
		String email = String.valueOf((int)(Math.random()*1000000));
		subComponent.setEmail(email);
		subComponent.setTitle("testing title sub");
		subComponent.setAuthor("testing author sub");
		subComponent.setISBN("123456");
		subComponent.setImageURL("testing image url sub");
		subComponent.setPriceMax("3213.21");
		subComponent.setPriceMin("3213.21");
		DBService.subscribe(subComponent);
		assertNotNull(DBService.searchMySubscription(email));
		assertNull(DBService.searchMySubscription(""));
		
		assertNull(DBService.searchMySubscription(null));//fail due to null
		
	}
	
	@Test
	public void testPostComp(){
		PostComponent newPost = new PostComponent();
		newPost.setPostID("test");
		newPost.setTitle("Test");
		newPost.setISBN("12345");
		newPost.setPrice(123.0);
		newPost.setEmail("testEmail");
		newPost.setComment("This is a test");
		newPost.setAuthor("Mr.Tester");
		newPost.setAddress("123 Test St.");
		newPost.setImageURL("www.test.com");
		newPost.setFbName("fbName");
		newPost.setASIN("ASIN");
		newPost.setLatitude(50.15);
		newPost.setLongitude(40.15);
		newPost.setFormattedAmazonPrice("CDN $15.15");
		
		assertTrue(newPost.getPostID().equals("test"));
		assertTrue(newPost.getASIN().equals("ASIN"));
		assertTrue(newPost.getTitle().equals("Test"));
		assertTrue(newPost.getISBN().equals("12345"));
		assertTrue(new Double(newPost.getPrice()).equals(123.0));
		assertTrue(newPost.getComment().equals("This is a test"));
		assertTrue(newPost.getAuthor().equals("Mr.Tester"));
		assertTrue(newPost.getAddress().equals("123 Test St."));
		assertTrue(newPost.getImageURL().equals("www.test.com"));
		assertTrue(new Double(newPost.getLatitude()).equals(50.15));
		assertTrue(new Double(newPost.getLongitude()).equals(40.15));
		assertTrue(newPost.getFormattedAmazonPrice().equals("CDN $15.15"));
		assertTrue(newPost.getEmail().equals("testEmail"));
		assertTrue(newPost.getFbName().equals("fbName"));
		
		PostComponent nullPost = new PostComponent();
		assertTrue(nullPost.getTitle().equals(""));
		assertTrue(nullPost.getISBN().equals(""));
		assertTrue(nullPost.getASIN().equals(""));
		assertTrue(nullPost.getAuthor().equals(""));
		assertTrue(nullPost.getImageURL().equals(""));
		assertTrue(nullPost.getComment().equals(""));
		assertTrue(nullPost.getAddress().equals(""));
		assertTrue(nullPost.getPostID().equals(""));
		
		SearchComponent search = new SearchComponent();
		assertTrue(search.getTitle().equals(""));
		assertTrue(search.getISBN().equals(""));
		assertTrue(search.getAuthor().equals(""));
		assertTrue(search.getAuthor().equals(""));
		assertTrue(search.getPriceMin().equals(""));
		assertTrue(search.getPriceMax().equals(""));
		assertTrue(search.getEmail().equals(""));
		search.setPostID("hi");
		assertTrue(search.getPostID().equals("hi"));
		assertTrue(search.getOrderBy().equals(""));
		
		search.setEmail("hi");
		assertTrue(search.getEmail().equals("hi"));
/*		
		PostComponent failPost = new PostComponent();
		failPost.setTitle("");
		failPost.setEmail("");
		failPost.setComment("");
		assertNull(DBService.newPost(failPost));*/
	}
	
	@Test
	public void testDBPost() {
		String randomISBN = String.valueOf((int)(Math.random()*1000000));
		postComponent.setISBN(randomISBN);
		searchComponent.setISBN(randomISBN);
		String randomAuthor = String.valueOf((int)(Math.random()*1000000));
		postComponent.setAuthor(randomAuthor);
		searchComponent.setAuthor(randomAuthor);	
		
		postID = DBService.newPost(postComponent);
		assertNotNull("Error in newPost", postID);
		postComponent.setPostID(postID);
		
		jsonArray = DBService.search(searchComponent);
		//System.out.println(jsonArray.toString());
		//System.out.println(jsonArray.size());
		assertEquals("Search result more than 1 items",jsonArray.size(),1);
		assertTrue("Error in EditPost", DBService.editPost(postComponent));
		postComponent.setComment("");
		assertFalse(DBService.editPost(postComponent));//fail due to no comment
		postComponent.setTitle("");
		assertFalse(DBService.editPost(postComponent));//fail due to no title
		postComponent.setEmail("");
		assertFalse(DBService.editPost(postComponent));//fail due to no email
		assertTrue("Error in renewPost", DBService.renewPost("bosung90@gmail.com", Integer.parseInt(postID)));
		assertFalse(DBService.renewPost("", 123));
		assertFalse("DeletePost precondition check fail",DBService.deletePost(null, Integer.parseInt(postID)));//fail due to email null
		assertFalse("DeletePost precondition check fail",DBService.deletePost("", Integer.parseInt(postID)));//fail due to email empty
		assertTrue("Error in DeletePost",DBService.deletePost("bosung90@gmail.com", Integer.parseInt(postID)));
		postComponent.setPostID(null);
		
		PostComponent nullPost = new PostComponent();
		nullPost.setEmail("");
		assertNull(DBService.newPost(nullPost));//null due to no email
		nullPost.setEmail("hi");
		nullPost.setTitle("");
		assertNull(DBService.newPost(nullPost));//null due to no Title
		nullPost.setTitle("hi");
		nullPost.setComment("");
		assertNull(DBService.newPost(nullPost));//null due to no Comment
	}
	

}
