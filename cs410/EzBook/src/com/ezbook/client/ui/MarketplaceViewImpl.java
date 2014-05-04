package com.ezbook.client.ui;

import com.ezbook.client.ClientUtil;
import com.ezbook.client.MarketPlaceService;
import com.ezbook.client.MarketPlaceServiceAsync;
import com.ezbook.shared.SearchComponent;
import com.google.code.gwt.geolocation.client.Coordinates;
import com.google.code.gwt.geolocation.client.Geolocation;
import com.google.code.gwt.geolocation.client.Position;
import com.google.code.gwt.geolocation.client.PositionCallback;
import com.google.code.gwt.geolocation.client.PositionError;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MarketplaceViewImpl extends BaseViewImpl implements
		MarketplaceView {

	private static MarketplaceViewImplUiBinder uiBinder = GWT.create(MarketplaceViewImplUiBinder.class);
	interface MarketplaceViewImplUiBinder extends UiBinder<Widget, MarketplaceViewImpl> {}

	private static MarketPlaceServiceAsync marketPlaceService = GWT.create(MarketPlaceService.class);

	private Presenter presenter;
	private JSONValue json;
	private static SearchComponent currentComponent;
	private static String currentOrder;
	private static Double lat = null; private static Double lng = null;

	@UiField HTML errorPanel;
	@UiField static HTML formAlert;
	@UiField HTMLPanel priceDiv, isbnDiv;
	@UiField static HTMLPanel results;
	@UiField Anchor search;
	@UiField TextBox title, author, isbn, minPrice, maxPrice;

	public MarketplaceViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		exportLoadPage();
		errorPanel.getElement().setClassName("alert alert-error span4 controls");
		errorPanel.getElement().setAttribute("style", "display:none;");
		priceDiv.getElement().setClassName("control-group");
		isbnDiv.getElement().setClassName("control-group");
		title.getElement().setAttribute("id", "title");
		title.getElement().setClassName("span5");
		author.getElement().setAttribute("id", "author");
		author.getElement().setClassName("span5");
		isbn.getElement().setAttribute("id", "isbn");
		isbn.getElement().setClassName("span5");
		minPrice.getElement().setClassName("price-input");
		minPrice.getElement().setAttribute("placeholder", "Min. Price");
		maxPrice.getElement().setClassName("price-input");
		maxPrice.getElement().setAttribute("placeholder", "Max. Price");
		search.getElement().setClassName("btn btn-primary controls");
		formAlert.getElement().setAttribute("style", "display:none;");
		formAlert.getElement().setClassName("alert alert-error form-alert controls");

		// get your Geo location...
		if (Geolocation.isSupported()) {
			Geolocation geo = Geolocation.getGeolocation();
			geo.getCurrentPosition(new PositionCallback() {
				public void onFailure(PositionError error) {

				}
				public void onSuccess(Position position) {
					Coordinates coords = position.getCoords();
					lat = coords.getLatitude();
					lng = coords.getLongitude();
				}
			});
		}
	}

	@UiHandler("search")
	public void search(ClickEvent e){
		formAlert.getElement().setAttribute("style", "display:none;");
		boolean isPriceValid = validatePrice();
		if (ClientUtil.ValidateForm("search-form") && isPriceValid) {
			HTML spin = new HTML("<img src='resource/img_processing.gif'/>");
			spin.getElement().setAttribute("style", "text-align: center");
			results.clear();
			results.add(spin);

			SearchComponent component = new SearchComponent();
			component.setTitle(title.getValue());
			component.setISBN(isbn.getValue());
			component.setPriceMin(minPrice.getValue());
			component.setPriceMax(maxPrice.getValue());
			component.setTitle(title.getValue());
			component.setAuthor(author.getValue());
			component.setISBN(isbn.getValue());

			currentComponent = component;
			currentOrder = null;
			doSearch(component, 0, false);
			hideForm();
		}
	}

	private static void doSearch(SearchComponent component, final int page, final boolean hideForm) {
		marketPlaceService.search(component, page, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				// Clear UI
				results.clear();
			}

			@Override
			public void onSuccess(String result) {
				if (hideForm) {
					hideForm();
				}

				results.clear();
				JSONObject json = JSONParser.parseLenient(result).isObject();
				if (json!=null) {
					JSONNumber countValue = json.get("count").isNumber();
					JSONNumber perPageValue = json.get("per_page").isNumber();
					JSONArray data = json.get("result").isArray();

					//show alert when there is no result
					if(countValue.doubleValue()==0){
						formAlert.getElement().setAttribute("style", "");
						formAlert.setHTML("Your search did not match any books.<br>Subscribe to a book if you wish to be notified when the book is available again.");
						showForm();
					}
					else {
						formAlert.getElement().setAttribute("style", "display: none;");
						formAlert.setHTML("");
						if (data!=null) {
							Double count = countValue!=null ? countValue.doubleValue() : null;
							Double perPage = perPageValue!=null ? perPageValue.doubleValue() : null;
							int totalPages = 0;
							if (count!=null && perPage!=null) {
								totalPages = (int)Math.ceil(count/perPage) - 1;
							}
	
							HTML pager = createPager(page, totalPages);
							pager.getElement().setAttribute("style", "border-bottom: 1px solid #E5E5E5;");
							results.add(pager);
	
							for (int i=0;i<data.size();i++) {
								JSONObject item = data.get(i).isObject();
								if (item!=null) {
									results.add(new SearchItemView(item, lat, lng).asWidget());
								}
							}
	
							results.add(createPager(page, totalPages));
						}
					}
				}
			}

		});
	}

	public static void loadMarketPlacePage(int page) {
		doSearch(currentComponent, page, false);
	}
	
	public static void sortMarketPlace(String value) {
		if (value.equals("Price")) {
			currentOrder = "Price";
			currentComponent.setOrderBy("price");
		}
		else {
			currentOrder = "Date";
			currentComponent.setOrderBy("Date");
		}
		doSearch(currentComponent, 0, true);
	}

	// Can not use ClientUtil.validateForm
	// because they are in same control-group
	public boolean validatePrice() {
		boolean valid = true;

		try {
			if (minPrice.getValue().length() > 0) {
				Double.parseDouble(minPrice.getValue());
			}
			if (maxPrice.getValue().length() > 0) {
				Double.parseDouble(maxPrice.getValue());
			}
			priceDiv.getElement().removeClassName("error");
		}
		catch (NumberFormatException e) {
			priceDiv.getElement().addClassName("error");
			formAlert.getElement().setAttribute("style", "");
			formAlert.setHTML("Your price field must be non-negative and only contain numbers.");
			valid = false;
		}

		return valid;
	}
	
	public void updateWithQuery(String query) {
		if (!query.equalsIgnoreCase("marketplace")) {
			String[] splitedQuery = query.split("=");
			if (splitedQuery.length > 0) {
				String param = splitedQuery[0];
				SearchComponent component = new SearchComponent();
				if (param.equalsIgnoreCase("title")) {
					if (splitedQuery.length > 1) {
						component.setTitle(splitedQuery[1]);
					}
				}
				else if (param.equalsIgnoreCase("post")) {
					if (splitedQuery.length > 1) {
						component.setPostID(splitedQuery[1]);
					}
				}
				HTML spin = new HTML("<img src='resource/img_processing.gif'/>");
				spin.getElement().setAttribute("style", "text-align: center");
				results.clear();
				results.add(spin);
				currentOrder = null;
				currentComponent = component;
				doSearch(component, 0, false);
				hideForm();
			}
		}
	}

	private static HTML createPager(int page, int totalpages) {
		String html = "<div class='pagination pagination-right'>"
					+ "<select onchange='sortMarketPlace($(this).val())' style='vertical-align: top; margin-right: 10px;'>"
					+ "<option" + (currentOrder==null ? " selected='selected'" : "") + ">--Sort By--</option>"
					+ "<option" + (currentOrder!=null && !currentOrder.equalsIgnoreCase("price") ? " selected='selected'" : "") + ">Date</option>"
					+ "<option" + (currentOrder!=null && currentOrder.equalsIgnoreCase("price") ? " selected='selected'" : "") + ">Price</option>"
					+ "</select>"
					+ "<ul>";
		boolean prev = page > 0;
		html += "<li" + (prev ? "" : " class='disabled' ") + "><a " + (!prev ? "" : "onclick=loadMarketPlacePage(" + (page-1) + ");'") + "><<</a></li>";
		int count = 0;
		int currentPage = page - 2;
		if (currentPage > (totalpages-4)) {
			currentPage -= currentPage - (totalpages-4);
		}
		while (count < 5) {
			if (currentPage>=0) {
				html += "<li" + (page==currentPage ? " class='active' " : "") + "><a onclick='loadMarketPlacePage(" + currentPage + ");'" + ">" + (currentPage+1) +"</a></li>";
				count++;
			}
			if (currentPage>=totalpages) break;
			currentPage++;
		}
		boolean next = page < totalpages;
		html += "<li" + (next ? "" : " class='disabled' ") + "><a " + (!next ? "" : "onclick='loadMarketPlacePage(" + (page+1) + ");'") + ">>></a></li>";
		html += "</ul>"
				+ "</div>";

		return new HTML(html);
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public static native void hideForm() /*-{
		$wnd.hideSearchForm();
	}-*/;
	
	public static native void showForm() /*-{
		$wnd.showSearchForm();
	}-*/;

	public static native void exportLoadPage() /*-{
		$wnd.sortMarketPlace = $entry(@com.ezbook.client.ui.MarketplaceViewImpl::sortMarketPlace(Ljava/lang/String;));
		$wnd.loadMarketPlacePage = $entry(@com.ezbook.client.ui.MarketplaceViewImpl::loadMarketPlacePage(I));
	}-*/;

}