package com.ezbook;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.ezbook.server.ServerUtil;

public class ServerUtilTest{

	@Test
	public void testISBN1310() {
		String isbn10 = "1234567890";
		String isbn13 = "9784567890123";
		String isbn13X = "978456789012X";
		String isbnShort = "123123";
		String isbnLong = "1234567890123456789";
		String isbnNotNum = "abcdefghijklm";
		
		assertEquals(isbn10,ServerUtil.ISBN1310(isbn10));//already isbn10
		assertEquals(isbnShort,ServerUtil.ISBN1310(isbnShort));//isbn too short
		assertEquals(10,ServerUtil.ISBN1310(isbn13).length());//successfully converted isbn13 to isbn 10
		assertEquals(10,ServerUtil.ISBN1310(isbn13X).length());//successfully converted isbn13X to isbn 10
		assertEquals(10,ServerUtil.ISBN1310(isbnLong).length());//converts to isbn10 by reading only first 12 digits
		assertTrue(ServerUtil.ISBN1310(isbnNotNum).length()>0);
	}
	
	@Test
	public void testISBN1013(){
		String isbn10 = "1234567890";
		String isbn13 = "9784567890123";
		String isbn10X = "123456789X";
		String isbnShort = "123123";
		String isbnLong = "1234567890123456789";
		String isbnNotNum = "abcdefghij";
		
		assertEquals(13, ServerUtil.ISBN1013(isbn10).length());//successfully converted isbn10 to isbn13
		assertEquals(13, ServerUtil.ISBN1013(isbn10X).length());//successfully converted isbn10X to isbn13
		assertEquals(isbn13,ServerUtil.ISBN1013(isbn13));//already isbn13
		assertEquals(isbnShort,ServerUtil.ISBN1013(isbnShort));//isbn too short
		assertEquals(13,ServerUtil.ISBN1013(isbnLong).length());//converts to isbn13 by reading only first 9 digits
		assertTrue(ServerUtil.ISBN1013(isbnNotNum).length()>0);
		
	}
	
	@Test
	public void testGetCahce(){
		assertNotNull(ServerUtil.getCache());
	}
	
	@Test
	public void testFetchURL() throws MalformedURLException, IOException{
		String retStr = ServerUtil.fetchURL("http://google.ca");
		assertTrue(retStr.length()>0);
	}

	@Test
	public void testFetchURLPost() throws IOException{
		URL url = new URL("http://google.ca");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		String retStr = ServerUtil.fetchURLPost(connection);
		assertTrue(retStr.length()>0);
	}

}
