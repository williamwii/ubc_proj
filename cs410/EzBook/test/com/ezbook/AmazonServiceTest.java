package com.ezbook;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ezbook.server.amazon.SearchBook;

public class AmazonServiceTest {

	@Test
	public void searchBook() {
		assertEquals("The Revenge of Geography: What the Map Tells Us About Coming Conflicts and the Battle Against Fate",SearchBook.SearchForBookISBN("9781400069835").getTitle());
		assertNotNull("AmazonSearchForBook Failed",SearchBook.SearchForBook("physics").getTitle());
		assertNotNull("AmazonSearchBookList Failed",SearchBook.SearchBookList("database management systems"));//amazon response contains more than 10 results
		assertNotNull("AmazonSearchBookList Failed",SearchBook.SearchBookList("database internet mysql php bible"));//amazon response contains less than 10 results
		assertEquals("AmazonSearchBookList Failed",SearchBook.SearchBookList("database internet mysql php bible there is no book").size(),0);//amazon response contains no book
	}
}
