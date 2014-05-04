package com.ezbook.client.ui;

import com.ezbook.client.AmazonService;
import com.ezbook.client.AmazonServiceAsync;
import com.ezbook.client.ClientUtil;
import com.ezbook.client.MyListingsService;
import com.ezbook.client.MyListingsServiceAsync;
import com.ezbook.client.SubscriptionService;
import com.ezbook.client.SubscriptionServiceAsync;
import com.ezbook.shared.PostComponent;
import com.ezbook.shared.SubscriptionComponent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SubscriptionsViewImpl extends BaseViewImpl implements SubscriptionsView {

	private static SubscriptionsViewImplUiBinder uiBinder = GWT.create(SubscriptionsViewImplUiBinder.class);
	interface SubscriptionsViewImplUiBinder extends UiBinder<Widget, SubscriptionsViewImpl> {}
	
	private static int LISTINGS_PER_PAGE = 10;
	private static int totalPages = 0;
	private static JSONArray mySubscriptions;
	private static SubscriptionsViewImpl self;
	
	private AmazonServiceAsync amazonService = GWT.create(AmazonService.class);
	private SubscriptionServiceAsync subscriptionService = GWT.create(SubscriptionService.class);
	private MyListingsServiceAsync myListingsService = GWT.create(MyListingsService.class);
	private Presenter presenter;
	
	@UiField HTML errorPanel, formAlert, titlePanel;
	@UiField HTMLPanel priceDiv, isbnDiv;
	@UiField TextBox email, isbn, minPrice, amazonQuery, maxPrice, title, author, imageUrl;
	@UiField Anchor subscriptionBtn, amazonBtn;
	@UiField static HTMLPanel subscriptions;
	
	public SubscriptionsViewImpl() {
		self = this;
		
		exportLoadPage();
		
		initWidget(uiBinder.createAndBindUi(this));
		errorPanel.getElement().setClassName("alert alert-error amazon-info controls");
		errorPanel.getElement().setAttribute("style", "display:none;");
		
		amazonQuery.getElement().setClassName("span4");
		amazonQuery.getElement().setAttribute("placeholder", "Search on Amazon");
		amazonBtn.getElement().setClassName("btn");
		
		email.getElement().setAttribute("placeholder", "example@email.com");
		email.getElement().setAttribute("data-required", "");
		email.getElement().setAttribute("id", "email");
		email.getElement().setAttribute("data-field-type", "email");
		email.getElement().setClassName("span5");
		
		isbn.getElement().setAttribute("placeholder", "9784567890123");
		isbn.getElement().setAttribute("data-required", "");
		isbn.getElement().setAttribute("id", "number");
		isbn.getElement().setClassName("span5");
		isbn.addBlurHandler(new BlurHandler(){
			@Override
			public void onBlur(BlurEvent event) {
				searchAmazonWithISBN();
			}
		});
		isbnDiv.getElement().setClassName("control-group");
		priceDiv.getElement().setClassName("control-group");
		minPrice.getElement().setClassName("price-input");
		minPrice.getElement().setAttribute("placeholder", "0");
		maxPrice.getElement().setClassName("price-input");
		maxPrice.getElement().setAttribute("placeholder", "99999999");
		
		title.getElement().setAttribute("id", "title");
		title.getElement().setClassName("span5");
		title.setReadOnly(true);
		
		author.getElement().setAttribute("id", "author");
		author.getElement().setClassName("span5");
		author.setReadOnly(true);
		
		imageUrl.getElement().setAttribute("id", "url");
		imageUrl.getElement().setClassName("span5");
		imageUrl.getElement().setAttribute("data-field-type", "url");
		imageUrl.setReadOnly(true);
		
		subscriptionBtn.getElement().setClassName("btn btn-primary controls");
	}

	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("amazonBtn")
	public void searchAmazon(ClickEvent e) {
		amazonBtn.getElement().setInnerText("Searching...");
		amazonBtn.getElement().setAttribute("disabled", "");
		
		amazonService.SearchBook(amazonQuery.getValue(), new AsyncCallback<PostComponent>() {
            public void onFailure(final Throwable caught) {
            	titlePanel.setHTML("Search on Amazon to retrieve full informations and auto fill.<br>Upon subscription, you will be notified when the book becomes available.");
				formAlert.getElement().setClassName("alert alert-error form-alert controls");
				formAlert.setHTML("Searching on Amazon failed.<br> Try manually typing ISBN.");
            	amazonBtn.getElement().removeAttribute("disabled");
            	amazonBtn.getElement().setInnerText("Auto Fill");
            	errorPanel.getElement().removeAttribute("style");
            	errorPanel.setHTML("Searching \""+ amazonQuery.getValue() + "\" on Amazon Failed");
            }

			@Override
			public void onSuccess(PostComponent book) {
				if(book==null){
					titlePanel.setHTML("Search on Amazon to retrieve full informations and auto fill.<br>Upon subscription, you will be notified when the book becomes available.");
					formAlert.getElement().setClassName("alert alert-error form-alert controls");
					formAlert.setHTML("No result is found.<br> Try manually typing ISBN.");
	            	amazonBtn.getElement().removeAttribute("disabled");
	            	amazonBtn.getElement().setInnerText("Auto Fill");
	            	errorPanel.getElement().removeAttribute("style");
	            	errorPanel.setHTML("Searching \""+ amazonQuery.getValue() + "\" on Amazon Failed");
				}
				else{
					formAlert.getElement().setClassName("alert alert-success form-alert controls");
					formAlert.setHTML("Rest of the book information has been filled for your convenience.");
					amazonBtn.getElement().removeAttribute("disabled");
	            	amazonBtn.getElement().setInnerText("Auto Fill");
	            	errorPanel.getElement().setAttribute("style", "display:none;");
	            	titlePanel.setHTML("Amazon Price: "+ book.getFormattedAmazonPrice());
					title.setText(book.getTitle());
					author.setText(book.getAuthor());
					imageUrl.setText(book.getImageURL());
	            	isbn.setText(book.getISBN());
				}
			}
        });
	}
	
	@UiHandler("subscriptionBtn")
	public void subscribe(ClickEvent e){
		isbnDiv.getElement().removeClassName("warning");
		if (ClientUtil.ValidateForm("subscription-form") && validatePrice()) {
			subscriptionBtn.getElement().setInnerHTML("Subscribing...");
			subscriptionBtn.getElement().setAttribute("disabled", "");

			SubscriptionComponent subscriber = new SubscriptionComponent();
			subscriber.setEmail(email.getValue());
			subscriber.setISBN(isbn.getValue());
			subscriber.setPriceMin(minPrice.getValue());
			subscriber.setPriceMax(maxPrice.getValue());
			subscriber.setTitle(title.getValue());
			subscriber.setAuthor(author.getValue());
			subscriber.setImageURL(imageUrl.getValue());

			subscribe(subscriber);
		}
		else{
			showErrorMessage();
		}
	}
	
	private void subscribe(SubscriptionComponent subscriber){
		subscriptionService.subscribe(subscriber, new AsyncCallback<Boolean>(){
			@Override
			public void onFailure(Throwable caught) {
				formAlert.getElement().setClassName("alert alert-error form-alert controls");
				formAlert.getElement().setAttribute("style", "");
				formAlert.setHTML("Subscription failed. Please try again.");
				subscriptionBtn.getElement().setInnerHTML("Subscribe");
				subscriptionBtn.getElement().removeAttribute("disabled");
			}

			@Override
			public void onSuccess(Boolean result) {
				formAlert.getElement().setAttribute("style", "");
				if(result){
					formAlert.getElement().setClassName("alert alert-success form-alert controls");
					formAlert.setHTML("You have successfully subscribed to the book");
				}
				else{
					formAlert.getElement().setClassName("alert alert-error form-alert controls");
					formAlert.setHTML("One of more of your field is invalid.<br>Or you have already subscribed to this book.");
				}
				subscriptionBtn.getElement().setInnerHTML("Subscribe");
				subscriptionBtn.getElement().removeAttribute("disabled");
			}
		});
	}
	
	public void searchAmazonWithISBN(){
		isbnDiv.getElement().removeClassName("warning");
		isbnDiv.getElement().removeClassName("error");
		subscriptionBtn.getElement().setInnerHTML("Searching...");
		subscriptionBtn.getElement().setAttribute("disabled", "");
		formAlert.setHTML("");
		
		amazonService.SearchBookISBN(isbn.getValue(), new AsyncCallback<PostComponent>() {
			@Override
			public void onFailure(Throwable caught) {
				formAlert.getElement().setClassName("alert alert-error form-alert controls");
				formAlert.setHTML("ISBN lookup failed.<br>Make sure your ISBN is 10 or 13 digits and consists only of numbers.<br>You may still subscribe.");
				title.setValue("");
				author.setValue("");
				imageUrl.setValue("");
				titlePanel.setHTML("Search on Amazon to retrieve full informations and auto fill.");
				subscriptionBtn.getElement().setInnerHTML("Subscribe");
				subscriptionBtn.getElement().removeAttribute("disabled");
				isbnDiv.getElement().addClassName("warning");
			}
			@Override
			public void onSuccess(PostComponent book) {
				if(book == null){
					//There is no book with matching ISBN
					formAlert.getElement().setClassName("alert alert-error form-alert controls");
					formAlert.setHTML("Sorry, we could not find any book that matches your ISBN.<br>You may still subscribe to the book.");
					title.setValue("");
					author.setValue("");
					imageUrl.setValue("");
					titlePanel.setHTML("Search on Amazon to retrieve full informations and auto fill.");
					isbnDiv.getElement().addClassName("warning");
				}
				else{
					formAlert.getElement().setClassName("alert alert-success form-alert controls");
					formAlert.setHTML("Rest of the book information has been filled for your convenience.");
					title.setText(book.getTitle());
					author.setText(book.getAuthor());
					imageUrl.setText(book.getImageURL());
					isbn.setText(book.getISBN());
					titlePanel.setHTML("Amazon Price: "+ book.getFormattedAmazonPrice());
				}
				subscriptionBtn.getElement().setInnerHTML("Subscribe");
				subscriptionBtn.getElement().removeAttribute("disabled");
			}
		});
	}
	
	public static native void hideForm() /*-{
		$wnd.toggleSubscriptionHide();
	}-*/;
	
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
			valid = false;
		}

		return valid;
	}
	
	public boolean validateISBN() {
		boolean valid = true;

		try {
			Integer.parseInt(isbn.getValue());
			isbnDiv.getElement().removeClassName("error");
		}
		catch (NumberFormatException e) {
			isbnDiv.getElement().addClassName("error");
			valid = false;
		}

		return valid;
	}
	
	private void showErrorMessage(){
		String alertMsg = "";
		if(email.getValue().trim().length()==0){
			alertMsg = alertMsg + "Email field cannot be empty.<br>";
		}
		if(isbn.getValue().trim().length()==0){
			alertMsg = alertMsg + "ISBN field cannot be empty.<br>";
		}
		else if(!validateISBN()){
			alertMsg = alertMsg + "ISBN field can only contain numbers.<br>";
		}
		if(!validatePrice()){
			alertMsg = alertMsg + "Your Price fields must only contain non-negative numbers.<br>";
		}
		if(alertMsg.length()==0){
			alertMsg = "One or more fields contain invalid data.<br>Please check if your email is valid.";
		}
		formAlert.getElement().setClassName("alert alert-error form-alert controls");
		formAlert.getElement().setAttribute("style", "");
		formAlert.setHTML(alertMsg);
	}
	
	protected void checkStatusFailed() {
		subscriptions.clear();
		mySubscriptions = null;
	}
	
	protected void unAuthorized() {
		subscriptions.clear();
		mySubscriptions = null;
	}
	
	protected void authorized() {
		reload(true);
	}
	
	protected void reload(final boolean updateView) {
		myListingsService.searchMySubscription(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				mySubscriptions = null;
			}

			@Override
			public void onSuccess(String result) {
				mySubscriptions = JSONParser.parseLenient(result).isArray();
				if (mySubscriptions!=null) {
					totalPages = (int)Math.ceil(mySubscriptions.size()/LISTINGS_PER_PAGE) - 1;
				}
				if (updateView) {
					loadPage(0);
				}
			}
			
		});
	}
	
	private static void loadPage(int page) {
		subscriptions.clear();
		HTML header = new HTML("<legend>My Subscriptions</legend>");
		HTML pager = ClientUtil.createPager(page, totalPages, "loadMySubscriptionsPage");
		pager.getElement().setAttribute("style", "border-bottom: 1px solid #E5E5E5;");
		subscriptions.add(header);
		subscriptions.add(pager);

		int startIndex = page * LISTINGS_PER_PAGE;
		int endIndex = (page + 1) * LISTINGS_PER_PAGE;
		for (int i=startIndex;i<endIndex && i<mySubscriptions.size();i++) {
			JSONObject item = mySubscriptions.get(i).isObject();
			if (item!=null) {
				SubscriptionItemView subscriptionItemView = new SubscriptionItemView(item, true);
				subscriptionItemView.setParent(self);
				subscriptions.add(subscriptionItemView.asWidget());
			}
		}

		subscriptions.add(ClientUtil.createPager(page, totalPages, "loadMySubscriptionsPage"));
	}
	
	public static native void exportLoadPage() /*-{
		$wnd.loadMySubscriptionsPage = $entry(@com.ezbook.client.ui.SubscriptionsViewImpl::loadPage(I));
	}-*/;
}
