package com.ezbook.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class EzBook extends Place {
	
	public EzBook() {
		
	}
	
	public static class Tokenizer implements PlaceTokenizer<EzBook> {

		@Override
		public EzBook getPlace(String token) {
			return new EzBook();
		}

		@Override
		public String getToken(EzBook place) {
			return "ezbook";
		}
	}
}
