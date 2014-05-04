package com.ezbook.client.ui;

import com.ezbook.client.AuthService;
import com.ezbook.client.AuthServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

public class BaseViewImpl extends Composite {

	private AuthServiceAsync authService = GWT.create(AuthService.class);
		
	public void updateNavBar() {
		authService.status(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				// do nothing
				checkStatusFailed();
			}

			@Override
			public void onSuccess(String name) {
				Element connectLi = RootPanel.get("connect-li").getElement();
				Element loggedInLi = RootPanel.get("logged-in-li").getElement();
				if (name!=null) {
					loggedInLi.removeAttribute("style");
					connectLi.setAttribute("style", "display:none;");
					Element loggedInBtn = RootPanel.get("logged-in-btn").getElement();
					loggedInBtn.setInnerHTML(name + " <b class=\"caret\"></b>");
					authorized();
				}
				else {
					connectLi.removeAttribute("style");
					loggedInLi.setAttribute("style", "display:none;");
					unAuthorized();
				}
			}
		});
		
		String[] navItems = {"navbar-marketplace", "navbar-post", "navbar-subscription"};
		int navItemIndex = -1;
		if (this instanceof MarketplaceView) {
			navItemIndex = 0;
		}
		else if (this instanceof PostView) {
			navItemIndex = 1;
		}
		else if (this instanceof SubscriptionsView) {
			navItemIndex = 2;
		}
		for (int i=0;i<navItems.length;i++) {
			if ( i==navItemIndex ) {
				RootPanel.get(navItems[i]).getElement().addClassName("active");
			}
			else {
				RootPanel.get(navItems[i]).getElement().removeClassName("active");
			}
		}
	}
	
	// Over write these functions to have custom behaviors
	// depending on user's log in status
	protected void checkStatusFailed() {

	}
	
	protected void unAuthorized() {
		
	}
	
	protected void authorized() {
		
	}
}
