package com.ezbook.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class Subscriptions extends Place {

	public Subscriptions() {
		
	}
	
	public static class Tokenizer implements PlaceTokenizer<Subscriptions> {

		@Override
		public Subscriptions getPlace(String token) {
			return new Subscriptions();
		}

		@Override
		public String getToken(Subscriptions place) {
			return "subscriptions";
		}
	}
}
