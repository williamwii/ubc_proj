package com.ezbook.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface EditListingView extends IsWidget {
	void setPresenter(Presenter presenter);
	void updateNavBar();
	void updateListing(String postID);
	
	public interface Presenter {
		void goTo(Place place);
	}
}
