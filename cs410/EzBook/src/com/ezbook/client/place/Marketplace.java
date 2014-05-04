package com.ezbook.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class Marketplace extends Place {

	private String query;
	
	public Marketplace(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
	
	public static class Tokenizer implements PlaceTokenizer<Marketplace> {

		@Override
		public Marketplace getPlace(String token) {
			return new Marketplace(token);
		}

		@Override
		public String getToken(Marketplace place) {
			return place.getQuery();
		}
	}
}
