package com.ezbook.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class Feedback extends Place {

	public Feedback() {
		
	}
	
	public static class Tokenizer implements PlaceTokenizer<Feedback> {

		@Override
		public Feedback getPlace(String token) {
			return new Feedback();
		}

		@Override
		public String getToken(Feedback place) {
			return "feedback";
		}

	}
}
