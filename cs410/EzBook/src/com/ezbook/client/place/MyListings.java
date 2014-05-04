package com.ezbook.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class MyListings extends Place {

	private String query;
	
	public MyListings(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
	
	public static class Tokenizer implements PlaceTokenizer<MyListings> {

		@Override
		public MyListings getPlace(String token) {
			return new MyListings(token);
		}

		@Override
		public String getToken(MyListings place) {
			// TODO Auto-generated method stub
			return place.getQuery();
		}
	}
}
