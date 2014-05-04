package com.ezbook.client.ui;

import com.ezbook.client.ClientUtil;
import com.ezbook.client.MyListingsService;
import com.ezbook.client.MyListingsServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MyListingsViewImpl extends BaseViewImpl implements
		MyListingsView {

	private static MyListingsViewImplUiBinder uiBinder = GWT.create(MyListingsViewImplUiBinder.class);
	interface MyListingsViewImplUiBinder extends UiBinder<Widget, MyListingsViewImpl> {}
	
	private static int LISTINGS_PER_PAGE = 10;
	private static MyListingsServiceAsync myListingsService = GWT.create(MyListingsService.class);
	private static MyListingsViewImpl self;
	
	private Presenter presenter;
	private static JSONArray myListings;
	private static int totalPages = 0;
	
	@UiField static HTMLPanel listings;

	public MyListingsViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		exportLoadPage();
		self = this;
	}
	
	public void updateListings() {
		reload(true);
	}
	
	protected void reload(final boolean updateView) {
		myListingsService.searchMyListings(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				myListings = null;
			}

			@Override
			public void onSuccess(String result) {
				myListings = JSONParser.parseLenient(result).isArray();
				if (myListings!=null) {
					totalPages = (int)Math.ceil(myListings.size()/LISTINGS_PER_PAGE) - 1;
				}
				if (updateView) {
					loadPage(0);
				}
			}
			
		});
	}
	
	private static void loadPage(int page) {
		listings.clear();
		HTML pager = ClientUtil.createPager(page, totalPages, "loadMyListingsPage");
		pager.getElement().setAttribute("style", "border-bottom: 1px solid #E5E5E5;");
		listings.add(pager);

		int startIndex = page * LISTINGS_PER_PAGE;
		int endIndex = (page + 1) * LISTINGS_PER_PAGE;
		for (int i=startIndex;i<endIndex && i<myListings.size();i++) {
			JSONObject item = myListings.get(i).isObject();
			if (item!=null) {
				MyListingsItemView itemView = new MyListingsItemView(item);
				itemView.setParent(self);
				listings.add(itemView.asWidget());
			}
		}

		listings.add(ClientUtil.createPager(page, totalPages, "loadMyListingsPage"));
	}
	
	@Override
	protected void checkStatusFailed() {
		listings.clear();
	}
	
	@Override
	protected void unAuthorized() {
		listings.clear();
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	public static native void exportLoadPage() /*-{
		$wnd.loadMyListingsPage = $entry(@com.ezbook.client.ui.MyListingsViewImpl::loadPage(I));
	}-*/;
}
