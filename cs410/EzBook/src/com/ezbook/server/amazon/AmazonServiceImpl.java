package com.ezbook.server.amazon;

import com.ezbook.client.AmazonService;
import com.ezbook.shared.PostComponent;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AmazonServiceImpl extends RemoteServiceServlet implements AmazonService{

	// http://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
	private static final long serialVersionUID = 2424476677394132217L;

	@Override
	public PostComponent SearchBook(String keywords) {

		return SearchBook.SearchForBook(keywords);

	}

	@Override
	public PostComponent SearchBookISBN(String ISBN) {
		return SearchBook.SearchForBookISBN(ISBN);
	}
}
