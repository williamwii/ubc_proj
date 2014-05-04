package com.ezbook.client.ui;

import com.ezbook.client.AmazonService;
import com.ezbook.client.AmazonServiceAsync;
import com.ezbook.client.ClientUtil;
import com.ezbook.client.PostService;
import com.ezbook.client.PostServiceAsync;
import com.ezbook.shared.PostComponent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PostViewImpl extends BaseViewImpl implements PostView {
	private static PostViewImplUiBinder uiBinder = GWT.create(PostViewImplUiBinder.class);
	interface PostViewImplUiBinder extends UiBinder<Widget, PostViewImpl> {}
	@SuppressWarnings("unused") private Presenter presenter;
	
	private AmazonServiceAsync amazonService = GWT.create(AmazonService.class);
	private PostServiceAsync postService = GWT.create(PostService.class);

	@UiField CheckBox fbPost;
	@UiField TextArea comment;
	@UiField InlineHTML amazonPrice;
	@UiField Anchor postBtn, amazonBtn;
	@UiField HTML errorPanel, formAlert;
	@UiField TextBox title, author, isbn, price, amazonQuery, imageUrl, address;
	
	public PostViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		errorPanel.getElement().setClassName("alert alert-error amazon-info controls");
		errorPanel.getElement().setAttribute("style", "display:none;");
		title.getElement().setAttribute("id", "title");
		title.getElement().setClassName("span5");
		title.getElement().setAttribute("data-required", "");
		author.getElement().setAttribute("id", "author");
		author.getElement().setClassName("span5");
		isbn.getElement().setAttribute("id", "isbn");
		isbn.getElement().setClassName("span5");
		imageUrl.getElement().setAttribute("id", "imageUrl");
		imageUrl.getElement().setClassName("span5");
		imageUrl.getElement().setAttribute("placeholder", "http://");
		imageUrl.getElement().setAttribute("data-field-type", "url");
		address.getElement().setAttribute("id", "address");
		address.getElement().setClassName("span5");
		price.getElement().setClassName("span2");
		price.getElement().setAttribute("style", "float:left;");
		price.getElement().setAttribute("data-required", "");
		price.getElement().setAttribute("data-field-type", "number");
		amazonPrice.getElement().setClassName("alert alert-block span3 hidden");
		comment.getElement().setAttribute("id", "comment");
		comment.getElement().setClassName("span5");
		comment.getElement().setAttribute("rows", "10");
		comment.getElement().setAttribute("data-required", "");
		postBtn.getElement().setClassName("btn btn-primary controls");
		amazonQuery.getElement().setClassName("span4");
		amazonQuery.getElement().setAttribute("placeholder", "Search on Amazon");
		amazonBtn.getElement().setClassName("btn");
		formAlert.getElement().setAttribute("id", "form-alert");
		formAlert.getElement().setClassName("alert alert-error form-alert controls");
		formAlert.getElement().setAttribute("style", "display: none;");
	}
	
	@UiHandler("postBtn")
	public void post(ClickEvent e){
		if (ClientUtil.ValidateForm("post-form")) {
			postBtn.getElement().setInnerHTML("Posting...");
			postBtn.getElement().setAttribute("disabled", "");
			
			Geocoder geoCoder = new Geocoder();
			geoCoder.getLatLng(address.getValue(), new LatLngCallback() {

				@Override
				public void onFailure() {
					doPost(bindComponent());
				}

				@Override
				public void onSuccess(LatLng point) {
					PostComponent component = bindComponent();
					component.setLatitude(point.getLatitude());
					component.setLongitude(point.getLongitude());
					doPost(component);
				}
				
			});
		}
		else{
			String alertErrorMsg = validateField();
			if(alertErrorMsg.length()!=0){
				formAlert.getElement().setAttribute("style", "");
				formAlert.getElement().setClassName("alert alert-error form-alert controls");
				formAlert.setHTML(alertErrorMsg);
			}
		}
	}
	
	private PostComponent bindComponent() {
		PostComponent component = new PostComponent();
		component.setTitle(title.getValue());
		component.setISBN(isbn.getValue());
		component.setPrice(Double.parseDouble(price.getValue()));
		component.setComment(comment.getValue());
		component.setAuthor(author.getValue());
		component.setAddress(address.getValue());
		component.setImageURL(imageUrl.getValue());
		
		return component;
	}
	
	private void doPost(PostComponent component) {
		postService.newPost(component, fbPost.getValue(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				String message = caught.getLocalizedMessage();
				if (message.contains("Unauthorized user")) {
					formAlert.setHTML("Please log in before doing post.");
				}
				else {
					formAlert.setHTML("Error Occurred, please try again later");
				}
				formAlert.getElement().setAttribute("style", "");
				formAlert.getElement().setClassName("alert alert-error form-alert controls");
				postBtn.getElement().setInnerHTML("Post");
				postBtn.getElement().removeAttribute("disabled");
			}
			@Override
			public void onSuccess(Boolean result) {
				formAlert.getElement().setAttribute("style", "");
				if(result){
					formAlert.getElement().setClassName("alert alert-success form-alert controls");
					formAlert.setHTML("Your book has been successfully posted!");
				}
				else{
					formAlert.getElement().setClassName("alert alert-error form-alert controls");
					formAlert.setHTML("Error while posting, please try again.");
				}
				postBtn.getElement().setInnerHTML("Post");
				postBtn.getElement().removeAttribute("disabled");
			}
		});
	}
		
	@UiHandler("amazonBtn")
	public void searchAmazon(ClickEvent e) {
		amazonBtn.getElement().setInnerText("Searching...");
		amazonBtn.getElement().setAttribute("disabled", "");
		
		amazonService.SearchBook(amazonQuery.getValue(), new AsyncCallback<PostComponent>() {
            public void onFailure(final Throwable caught) {
            	amazonBtn.getElement().removeAttribute("disabled");
            	amazonBtn.getElement().setInnerText("Auto Fill");
            	errorPanel.getElement().removeAttribute("style");
            	errorPanel.setHTML("Searching \""+ amazonQuery.getValue() + "\" on Amazon Failed");
            }
			@Override
			public void onSuccess(PostComponent book) {
				if(book==null){
	            	amazonBtn.getElement().removeAttribute("disabled");
	            	amazonBtn.getElement().setInnerText("Auto Fill");
	            	errorPanel.getElement().removeAttribute("style");
	            	errorPanel.setHTML("Searching \""+ amazonQuery.getValue() + "\" on Amazon Failed");
				}
				else{
					amazonBtn.getElement().removeAttribute("disabled");
	            	amazonBtn.getElement().setInnerText("Auto Fill");
	            	errorPanel.getElement().setAttribute("style", "display:none;");
					title.setText(book.getTitle());
					author.setText(book.getAuthor());
					isbn.setText(book.getISBN());
					imageUrl.setText(book.getImageURL());
					amazonPrice.getElement().removeClassName(book.getFormattedAmazonPrice().equals("") ? "" : "hidden");
					amazonPrice.getElement().setInnerHTML("Price on Amazon is " + book.getFormattedAmazonPrice());
				}
			}
        });
	}
	
	@Override
	protected void checkStatusFailed() {
		enableTextBoxes();
		errorPanel.getElement().setAttribute("style", "display:none;");
		errorPanel.setHTML("");
	}
	
	@Override
	protected void authorized() {
		enableTextBoxes();
		errorPanel.getElement().setAttribute("style", "display:none;");
		errorPanel.setHTML("");
	}
	
	@Override
	protected void unAuthorized() {
		disableTextBoxes();
    	errorPanel.getElement().removeAttribute("style");
    	errorPanel.setHTML("Please login before you can post.");
	}
	
	private void disableTextBoxes(){
		amazonQuery.setEnabled(false);
		title.setEnabled(false);
		author.setEnabled(false);
		isbn.setEnabled(false);
		imageUrl.setEnabled(false);
		price.setEnabled(false);
		address.setEnabled(false);
		comment.setEnabled(false);
		postBtn.getElement().setAttribute("disabled", "");
	}
	
	private void enableTextBoxes(){
		amazonQuery.setEnabled(true);
		title.setEnabled(true);
		author.setEnabled(true);
		isbn.setEnabled(true);
		imageUrl.setEnabled(true);
		price.setEnabled(true);
		address.setEnabled(true);
		comment.setEnabled(true);
		postBtn.getElement().removeAttribute("disabled");
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	public String validateField(){
		String errorMsg="";
		try{
		if(price.getValue().length()==0 || Double.parseDouble(price.getValue())<0){
			errorMsg=errorMsg+"The price field must be not empty and a positive number. <br>";
		}
		else if(price.getValue().matches("\\S")){
			errorMsg=errorMsg+"The price field cannot contain any white spaces.<br>";
		}
		}
		catch(NumberFormatException e){
			errorMsg=errorMsg+"The price field must be a number. <br>";
		}

		if(title.getValue().length()==0){
			errorMsg=errorMsg+"The title field cannot be empty. <br>";
		}
		
		if(comment.getValue().length()==0){
			errorMsg=errorMsg+"The additional details field cannot be empty.";
		}
		
		return errorMsg;
	}
}
