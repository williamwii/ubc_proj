package com.ezbook.client;

//import static org.junit.Assert.*;

import org.junit.Test;

//import com.ezbook.client.ui.BaseViewImpl;
//import com.ezbook.client.ui.PostViewImpl;
//import com.google.gwt.event.dom.client.BlurEvent;
//import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.junit.client.GWTTestCase;

public class PostViewImplTest extends GWTTestCase{

	@Test
	public void testMarketplaceViewImpl() {
		
/*		PostViewImpl view = new PostViewImpl();
		
		view.amazonBtn.fireEvent(new ClickEvent(){});//empty amazon search
		
		view.amazonQuery.setValue("Evolution");
		view.amazonBtn.fireEvent(new ClickEvent(){});//search Evolution on amazon
		
		view.title.setValue("Evolution");
		view.comment.setValue("comment");
		view.price.setValue("");
		assertNotNull(view.validateField());
		view.price.setValue(" 23 2 ");
		view.title.setValue("");
		view.comment.setValue("");
		assertNotNull(view.validateField());
		view.price.setValue("wefwfe");
		assertNotNull(view.validateField());
		view.price.setValue("-234");
		assertNotNull(view.validateField());
		
		//amazon query with valid input
		BaseViewImpl base = new BaseViewImpl();
		view.setPresenter(null);*/
	}

	@Override
	public String getModuleName() {
		return "com.ezbook.EzBook";
	}

}
