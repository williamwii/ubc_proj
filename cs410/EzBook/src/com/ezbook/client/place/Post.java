package com.ezbook.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class Post extends Place {

	public Post() {
		
	}
	
	public static class Tokenizer implements PlaceTokenizer<Post> {

		@Override
		public Post getPlace(String token) {
			return new Post();
		}

		@Override
		public String getToken(Post place) {
			return "new";
		}

	}
}
