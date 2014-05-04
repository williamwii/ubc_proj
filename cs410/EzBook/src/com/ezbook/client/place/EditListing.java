package com.ezbook.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class EditListing extends Place {

	private String postID;
	
	public EditListing(String token) {
		postID = token;
	}
	
	public String getToken() {
		return postID;
	}
	
	public static class Tokenizer implements PlaceTokenizer<EditListing> {

		@Override
		public EditListing getPlace(String token) {
			return new EditListing(token);
		}

		@Override
		public String getToken(EditListing place) {
			return place.getToken();
		}
	}
}
