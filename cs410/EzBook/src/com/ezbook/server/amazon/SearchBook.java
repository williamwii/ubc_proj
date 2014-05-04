package com.ezbook.server.amazon;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.ezbook.server.ServerUtil;
import com.ezbook.shared.PostComponent;


public class SearchBook {
	private static final Logger log = Logger.getLogger(SearchBook.class.getName());
	/*
	 * Your AWS Access Key ID, as taken from the AWS Your Account page.
	 * get it from https://portal.aws.amazon.com/gp/aws/securityCredentials
	 */
	private static final String AWS_ACCESS_KEY_ID = "AKIAIIVSAIBPM5CAMB4Q";

	/*
	 * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
	 * Your Account page.
	 */
	private static final String AWS_SECRET_KEY = "6VfiPd1qHrz/exMoS+EquFXAgm5fYUCAd/rpwuEv";

	private static final String SERVICE = "AWSECommerceService";
	private static final String VERSION = "2011-08-01";
	private static final String ASSOCIATE_TAG = "bosubook-20";
	private static final int SEARCH_NUM = 10;

	/*
	 * Use one of the following end-points, according to the region you are
	 * interested in:
	 * 
	 *      US: ecs.amazonaws.com 
	 *      CA: ecs.amazonaws.ca 
	 *      UK: ecs.amazonaws.co.uk 
	 *      DE: ecs.amazonaws.de 
	 *      FR: ecs.amazonaws.fr 
	 *      JP: ecs.amazonaws.jp
	 * 
	 */
	private static final String ENDPOINT = "ecs.amazonaws.ca";
	private static SignedRequestsHelper helper;
	
	public SearchBook(){
	}
	
	public static PostComponent SearchForBookISBN(String ISBN) {
		try {
			helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
		} catch (Exception e) {
			//e.printStackTrace();
			log.warning("Error in PostComponent SearchForBook(String keywords) \n " +
					"its possible that amazon server is down or AWS key is no longer valid ");
		}
		Map<String, String> params = new HashMap<String, String>();		
		params.put("Service", SERVICE);
		params.put("Version", VERSION);
		params.put("AssociateTag", ASSOCIATE_TAG);
		params.put("Operation", "ItemLookup");
		//params.put("SearchIndex", "Books");
		params.put("IdTpye", "ASIN");
		params.put("ItemId", ServerUtil.ISBN1310(ISBN));
		params.put("ResponseGroup", "Large");
		
		String requestUrl = helper.sign(params);
		log.info("Searching ISBN \"" + ISBN + "\" on Amazon");
		
		PostComponent firstBook = fetchBook(requestUrl);
		return firstBook;
	}
	
	//in contrast to SearchBookList, this only searches the first book.
	public static PostComponent SearchForBook(String keywords){
		try {
			helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
		} catch (Exception e) {
			//e.printStackTrace();
			log.warning("Error in PostComponent SearchForBook(String keywords) \n " +
					"its possible that amazon server is down or AWS key is no longer valid ");
		}
		
		Map<String, String> params = new HashMap<String, String>();		
		params.put("Service", SERVICE);
		params.put("Version", VERSION);
		params.put("AssociateTag", ASSOCIATE_TAG);
		params.put("SearchIndex", "Books");
		params.put("Keywords", keywords);
		params.put("Operation", "ItemSearch");
		params.put("ResponseGroup", "Large");
		
		String requestUrl = helper.sign(params);
		log.info("Searching for \"" + keywords + "\" on Amazon");
		
		log.info("Request URL: " + requestUrl);
		PostComponent firstBook = fetchBook(requestUrl);
		return firstBook;
	}

	//in contrast to fetchBookList, this will return just first book.
	private static  PostComponent fetchBook(String requestUrl) {
		PostComponent firstBook = new PostComponent();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(requestUrl);
			//doc contains xml response from amazon

			firstBook.setTitle(doc.getElementsByTagName("Title").item(0).getTextContent());
			firstBook.setASIN(doc.getElementsByTagName("ASIN").item(0).getTextContent());
			firstBook.setAuthor(doc.getElementsByTagName("Author").item(0).getTextContent());
			firstBook.setImageURL(doc.getElementsByTagName("MediumImage").item(0).getFirstChild().getTextContent());
			firstBook.setISBN(doc.getElementsByTagName("EAN").item(0).getTextContent());
			firstBook.setFormattedAmazonPrice(doc.getElementsByTagName("Price").item(0).getLastChild().getTextContent());

		}
		catch (NullPointerException e){
			log.info("book does not exist on amazon");
			return null;
		}
		catch (Exception e) {
			log.warning("Error while searching with url: " + requestUrl);
			throw new RuntimeException(e);
		}
		
		return firstBook;

	}
	
	public static LinkedList<PostComponent> SearchBookList(String keywords){
		try {
			helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
		} catch (Exception e) {
			//e.printStackTrace();
			log.warning("Error in LinkedList<PostComponent> SearchBookList(String keywords)" +
					"its possible that amazon server is down or AWS key is no longer valid ");
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("Service", SERVICE);
		params.put("Version", VERSION);
		params.put("AssociateTag", ASSOCIATE_TAG);
		params.put("SearchIndex", "Books");
		params.put("Keywords", keywords);
		params.put("Operation", "ItemSearch");
		//if you need different response, go to link and scroll down to response group->valid values.
		//http://docs.amazonwebservices.com/AWSECommerceService/latest/DG/ItemSearch.html
		params.put("ResponseGroup", "Large");

		String requestUrl = helper.sign(params);
		log.info("Searching for book list \"" + keywords + "\" on Amazon");

		LinkedList<PostComponent> bookList = fetchBookList(requestUrl);
		return bookList;
	}

	//requestUrl will give a xml response.
	//by putting requestUrl directly into browser, you can see the xml in the browser.
	private static  LinkedList<PostComponent> fetchBookList(String requestUrl) {
		LinkedList<PostComponent> bookList = new LinkedList<PostComponent>();  
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(requestUrl);
			int num = SEARCH_NUM;
			if(SEARCH_NUM>doc.getElementsByTagName("ASIN").getLength()){
				num = doc.getElementsByTagName("ASIN").getLength();
			}	
			for(int i=0; i<num; i++){
				PostComponent book = new PostComponent();
				book.setTitle(doc.getElementsByTagName("Title").item(i).getTextContent());
				book.setASIN(doc.getElementsByTagName("ASIN").item(i).getTextContent());
				book.setAuthor(doc.getElementsByTagName("Author").item(i).getTextContent());
				book.setImageURL(doc.getElementsByTagName("MediumImage").item(i).getTextContent());
				book.setISBN(doc.getElementsByTagName("EAN").item(i).getTextContent());
				//book.setPriceNew(priceNew);
				//NodeList nl = docMedium.getElementsByTagName("Price");
				//System.out.println(nl.item(0).getTextContent());
				//Node node = nl.item(i);
				//Node n = node.getFirstChild();
				//book.setPriceUsed(Integer.parseInt(n.getTextContent()));
				bookList.add(book);
			}
		}
		catch (Exception e) {
			log.warning("Error while searching with url: " + requestUrl);
			throw new RuntimeException(e);
		}
		return bookList;
	}
}
