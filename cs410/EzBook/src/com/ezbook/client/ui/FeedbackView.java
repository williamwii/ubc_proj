package com.ezbook.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface FeedbackView extends IsWidget {
	void setPresenter(Presenter presenter);
	void updateNavBar();

	public interface Presenter {
		void goTo(Place place);
	}
}
