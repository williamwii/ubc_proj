package com.ezbook.client;

//import static org.junit.Assert.*;

import org.junit.Test;

//import com.ezbook.client.ui.MarketplaceViewImpl;
//import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.junit.client.GWTTestCase;

public class MarketplaceViewImplTest extends GWTTestCase{

	@Test
	public void testMarketplaceViewImpl() {
/*		
		MarketplaceViewImpl view = new MarketplaceViewImpl();
		
		//click buttons while all textboxes are empty
		//replace ClientUtil.ValidateForm("search-form") with (true ||ClientUtil.ValidateForm("search-form"))
		//replace hideForm(); with //hideForm();
		//in MarketPlaceViewImpl
		//replace return doSearch(component, page, true); with return doSearch(component, page, false); 
		//but don't commit them
		view.title.setValue("harry potter");
		view.search.fireEvent(new ClickEvent(){});
		
		//replace showForm(); with //showForm();
		view.title.setValue("wfjiwoefjiwofijwofijwofiwjoij");
		view.search.fireEvent(new ClickEvent(){});
		
		//execute validations
		view.minPrice.setValue("10");
		view.maxPrice.setValue("100");
		assertTrue(view.validatePrice());

		view.maxPrice.setValue("abc");
		view.minPrice.setValue("abc");
		assertFalse(view.validatePrice());

		view.isbn.setValue("1235467890");
		assertTrue(view.validateISBN());
		view.isbn.setValue("abc");
		assertFalse(view.validateISBN());
		view.isbn.setValue("-500");
		assertFalse(view.validateISBN());
		
		view.updateWithQuery("");
		//next two lines will print bunch of stack trace, but it increases coverage
		view.updateWithQuery("post=123");
		view.updateWithQuery("title=title");
		
		MarketplaceViewImpl.loadMarketPlacePage(0);
		MarketplaceViewImpl.sortMarketPlace("Price");
		MarketplaceViewImpl.sortMarketPlace("Date");
		
		
		view.setPresenter(null);*/
	}

	@Override
	public String getModuleName() {
		return "com.ezbook.EzBook";
	}

}
