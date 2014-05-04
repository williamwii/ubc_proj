package com.ezbook.client.ui;

import com.ezbook.client.ClientUtil;
import com.ezbook.client.MyListingsService;
import com.ezbook.client.MyListingsServiceAsync;
import com.ezbook.shared.PostComponent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditListingViewImpl extends BaseViewImpl implements EditListingView {
	private static EditListingViewImplUiBinder uiBinder = GWT.create(EditListingViewImplUiBinder.class);
	interface EditListingViewImplUiBinder extends UiBinder<Widget, EditListingViewImpl> {}
	@SuppressWarnings("unused") private Presenter presenter;
	
	private MyListingsServiceAsync myListingsService = GWT.create(MyListingsService.class);
	private String postID;
	
	@UiField Anchor postBtn;
	@UiField TextArea comment;
	@UiField HTML errorPanel, formAlert;
	@UiField TextBox title, author, isbn, price, imageUrl, address;
	
	public EditListingViewImpl() {
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
		comment.getElement().setAttribute("id", "comment");
		comment.getElement().setClassName("span5");
		comment.getElement().setAttribute("rows", "10");
		comment.getElement().setAttribute("data-required", "");
		postBtn.getElement().setClassName("btn btn-primary controls");
		formAlert.getElement().setAttribute("id", "form-alert");
		formAlert.getElement().setClassName("alert alert-error form-alert controls");
		formAlert.getElement().setAttribute("style", "display: none;");
	}
	
	public void updateListing(String post) {
		postID = post;
		myListingsService.searchItem(postID, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				postID = null;
				clearForm();
			}

			@Override
			public void onSuccess(String result) {
				if (result!=null) {
					JSONObject json = JSONParser.parseLenient(result).isObject();
					if (json!=null) {
						title.setText(ClientUtil.isJSONString(json.get("TITLE")));
						author.setText(ClientUtil.isJSONString(json.get("AUTHOR")));
						isbn.setText(ClientUtil.isJSONString(json.get("ISBN")));
						imageUrl.setText(ClientUtil.isJSONString(json.get("IMAGE_URL")));
						price.setText(ClientUtil.isJSONString(json.get("PRICE")));
						address.setText(ClientUtil.isJSONString(json.get("ADDRESS")));
						comment.setText(ClientUtil.isJSONString(json.get("COMMENT")));
					}
				}
				else {
					clearForm();
				}
			}
			
		});
	}
	
	@UiHandler("postBtn")
	public void save(ClickEvent e) {
		if (ClientUtil.ValidateForm("edit-form")) {
			postBtn.getElement().setInnerHTML("Saving...");
			postBtn.getElement().setAttribute("disabled", "");
			
			Geocoder geoCoder = new Geocoder();
			geoCoder.getLatLng(address.getValue(), new LatLngCallback() {

				@Override
				public void onFailure() {
					saveListing(bindComponent());
				}

				@Override
				public void onSuccess(LatLng point) {
					PostComponent component = bindComponent();
					component.setLatitude(point.getLatitude());
					component.setLongitude(point.getLongitude());
					saveListing(component);
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
	
	private void saveListing(PostComponent component) {
		component.setPostID(postID);
		myListingsService.editListing(component, new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {
				String message = caught.getLocalizedMessage();
				if (message.contains("Unauthorized user")) {
					formAlert.setHTML("Please log in before editing.");
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
					formAlert.setHTML("Saved");
				}
				else{
					formAlert.getElement().setClassName("alert alert-error form-alert controls");
					formAlert.setHTML("Error while saving, please try again.");
				}
				postBtn.getElement().setInnerHTML("Post");
				postBtn.getElement().removeAttribute("disabled");
			}
			
		});
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
	
	private void clearForm() {
		title.setText("");
		author.setText("");
		isbn.setText("");
		imageUrl.setText("");
		price.setText("");
		address.setText("");
		comment.setText("");
	}
	
	@Override
	protected void checkStatusFailed() {
		clearForm();
	}
	
	@Override
	protected void unAuthorized() {
		clearForm();
	}
	
	public String validateField(){
		String errorMsg="";
		try{
		if(price.getValue().length()==0 || Double.parseDouble(price.getValue())<0){
			errorMsg=errorMsg+"The price field must be not empty and a positive number. <br>";
		}}
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
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}
