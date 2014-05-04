package com.ezbook.client;

import com.ezbook.client.mvp.AppActivityMapper;

import com.ezbook.client.mvp.AppPlaceHistoryMapper;
import com.ezbook.client.place.EzBook;
import com.ezbook.client.place.Marketplace;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.EventBus;

// UI Components with association with war/EzBook.css
public class EzBookEntry implements EntryPoint {
	
	private AuthServiceAsync authService = GWT.create(AuthService.class);
	
	public void onModuleLoad() {
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		EventBus eventBus = clientFactory.getEventBus();
		final PlaceController placeController = clientFactory.getPlaceController();

		// Creating the mappers to handler history		
		ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		
		SimplePanel contentPanel = new SimplePanel();
		activityManager.setDisplay(contentPanel);
		RootPanel.get("content-panel").add(contentPanel);
		
		final TextBox query = TextBox.wrap(RootPanel.get("navbar-query").getElement());
		query.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					String input = "title=" + query.getText();
					placeController.goTo(new Marketplace(input));
				}
			}
		});
		
		Button searchBtn = Button.wrap(RootPanel.get("navbar-search-btn").getElement());
		searchBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String input = "title=" + query.getText();
				placeController.goTo(new Marketplace(input));
			}
		});
		
		Anchor connectBtn = Anchor.wrap(RootPanel.get("connect-btn").getElement());
		connectBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String place, token;
				String href = Window.Location.getHref();
				String[] splitedStr = href.split("#");
				if (splitedStr.length > 1) {
					String[] placeToken = splitedStr[1].split(":");
					if (placeToken.length > 1) {
						place = placeToken[0];
						token = placeToken[1];
					}
					else {
						place = "EzBook";
						token = "ezbook";
					}
				}
				else {
					place = "EzBook";
					token = "ezbook";
				}
				Window.Location.replace("/fbLogin?place=" + place + "&param=" + token);
			}
		});
	
		Anchor logoutBtn = Anchor.wrap(RootPanel.get("logout-btn").getElement());
		logoutBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				authService.logout(new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						// Do nothing
					}

					public void onSuccess(Boolean result) {
						if (result) {
							Window.Location.replace("#EzBook:ezbook");
							Element connectLi = RootPanel.get("connect-li").getElement();
							Element loggedInLi = RootPanel.get("logged-in-li").getElement();
							Element loggedInBtn = RootPanel.get("logged-in-btn").getElement();
							loggedInBtn.setInnerHTML("");
							connectLi.removeAttribute("style");
							loggedInLi.setAttribute("style", "display:none;");
						}
					}
				});
			}
		});
		
		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, new EzBook());
		historyHandler.handleCurrentHistory();
    }
}
