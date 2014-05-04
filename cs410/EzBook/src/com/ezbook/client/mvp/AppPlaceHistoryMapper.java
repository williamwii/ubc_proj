package com.ezbook.client.mvp;

import com.ezbook.client.place.EditListing;
import com.ezbook.client.place.EzBook;
import com.ezbook.client.place.Feedback;
import com.ezbook.client.place.Marketplace;
import com.ezbook.client.place.MyListings;
import com.ezbook.client.place.Post;
import com.ezbook.client.place.Subscriptions;
import com.google.gwt.place.shared.WithTokenizers;
import com.google.gwt.place.shared.PlaceHistoryMapper;

// Add all tokenizers of places here
@WithTokenizers( {EzBook.Tokenizer.class, Post.Tokenizer.class, Marketplace.Tokenizer.class,
	MyListings.Tokenizer.class,EditListing.Tokenizer.class, Subscriptions.Tokenizer.class,
	Feedback.Tokenizer.class} )
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
