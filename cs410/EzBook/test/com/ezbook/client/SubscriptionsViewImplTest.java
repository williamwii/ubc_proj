package com.ezbook.client;

//import static org.junit.Assert.*;

import org.junit.Test;

//import com.ezbook.client.ui.SubscriptionsViewImpl;
//import com.google.gwt.event.dom.client.BlurEvent;
//import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.junit.client.GWTTestCase;

public class SubscriptionsViewImplTest extends GWTTestCase{

	@Test
	public void testSubscriptionsViewImpl(){
/*
		SubscriptionsViewImpl view = new SubscriptionsViewImpl();
		//execute with empty values
		view.amazonBtn.fireEvent(new ClickEvent(){});
		view.isbn.fireEvent(new BlurEvent(){});
		view.subscriptionBtn.fireEvent(new ClickEvent(){});//invalid values
		
		String email = String.valueOf((int)(Math.random()*1000000));
		view.email.setValue(email);
		view.isbn.setValue("unit-test isbn");
		view.subscriptionBtn.fireEvent(new ClickEvent(){});
		view.subscriptionBtn.fireEvent(new ClickEvent(){});//duplicate will throw error

		//show error message
		view.minPrice.setValue("weifjwe");
		view.email.setValue("");
		view.isbn.setValue("");
		view.subscriptionBtn.fireEvent(new ClickEvent(){});//show error
		view.isbn.setValue("wofij");
		view.subscriptionBtn.fireEvent(new ClickEvent(){});//show different error
		view.isbn.setValue("");
		view.minPrice.setValue("");
		
		view.updateListings();
		
		assertTrue(view.validatePrice());//valid price (since its empty)
		view.minPrice.setValue("10");
		view.maxPrice.setValue("100");
		assertTrue(view.validatePrice());//valid price since its 10 to 100
		view.minPrice.setValue("owfjio");
		assertFalse(view.validatePrice());//invalid price
		assertFalse(view.validateISBN());//invalid isbn
		view.isbn.setValue("1234567890");
		assertTrue(view.validateISBN());//valid isbn
		view.isbn.fireEvent(new BlurEvent(){});
		
		//amazon query with valid input
		view.amazonQuery.setValue("Harry Potter");
		assertEquals("Harry Potter", view.amazonQuery.getValue());
		view.amazonBtn.fireEvent(new ClickEvent(){});
		view.isbn.setValue("9781609803681");
		assertEquals("9781609803681", view.isbn.getValue());
		view.isbn.fireEvent(new BlurEvent(){});
		view.setPresenter(null);*/
	}

	@Override
	public String getModuleName() {
		return "com.ezbook.EzBook";
	}
}
