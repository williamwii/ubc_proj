package com.ezbook.client.ui;

import com.ezbook.client.ClientUtil;
import com.ezbook.client.SubscriptionService;
import com.ezbook.client.SubscriptionServiceAsync;
import com.ezbook.shared.UnsubscriptionComponent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SubscriptionItemView extends Composite {
	private static ItemViewImplUiBinder uiBinder = GWT.create(ItemViewImplUiBinder.class);
	interface ItemViewImplUiBinder extends UiBinder<Widget, SubscriptionItemView> {}

	private SubscriptionServiceAsync subscriptionService = GWT.create(SubscriptionService.class);
	private SubscriptionItemView self; // kind of bad.. but whatever
	private SubscriptionsViewImpl parent;
	private String unsubscriptionCode;
	//private HandlerRegistration repostReg;
	
	@UiField
	Anchor email, unsubscribeBtn;
	@UiField
	Image image;
	@UiField
	HTMLPanel infoPanel, listingPanel;
	@UiField
	Label title, author, isbn, subscriptionDate, priceMin, priceMax;
	@UiField HTML errorPanel;

	public SubscriptionItemView(JSONObject item, boolean forListing) {
		initWidget(uiBinder.createAndBindUi(this));
		
		unsubscriptionCode = ClientUtil.isJSONString(item.get("UNSUBSCRIBE_CODE"));
		self = this;
		
		this.getElement().setClassName("row item");
		title.getElement().setClassName("item-title");
		String imageURL = ClientUtil.isJSONString(item.get("IMAGE_URL"));
		if (!imageURL.equals("")) {
			image.setUrl(imageURL);
			image.getElement().setClassName("img-polaroid pull-right");
		} else {
			image.removeFromParent();
		}

		//String lat = ClientUtil.isJSONString(item.get("LATITUDE"));
		//String lng = ClientUtil.isJSONString(item.get("LONGITUDE"));
		//String address = ClientUtil.isJSONString(item.get("ADDRESS"));
		/*if (!lat.equals("") && !lng.equals("") && !lat.equals("0")
				&& !lng.equals("0") && !address.equals("")) {
			location.removeFromParent();
			mapPanelLocation.setHTML("Proposed Meetup Location: " + address);
			mapBtn.getElement().setAttribute("data-toggle", "collapse");
			mapBtn.setHref("#map-" + subscriptionID);
			mapBtn.setText("Show on map");
			map.getElement().setAttribute("id", "map-" + subscriptionID);
			map.getElement().setClassName("accordion-body collapse");
			String url = "";
			if (userLat != null && userLng != null) {
				url = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&markers=icon:http://chart.apis.google.com/chart?chst=d_map_pin_icon%26chld=books%257CFF0000|"
						+ lat
						+ ","
						+ lng
						+ "&markers=icon:http://chart.apis.google.com/chart?chst=d_map_pin_icon%26chld=glyphish_user%257CADDE63|"
						+ userLat + "," + userLng + "&format=jpg&sensor=false";
			} else {
				url = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&zoom=12&markers=icon:http://chart.apis.google.com/chart?chst=d_map_pin_icon%26chld=books%257CFF0000|"
						+ lat + "," + lng + "&format=jpg&sensor=false";
			}
			mapImage.getElement().setClassName("img-rounded");
			mapImage.getElement().setAttribute("src", url);
		} else {
			location.setText("Proposed Meetup Location: " + address);
			mapPanel.removeFromParent();
		}*/

		title.setText(ClientUtil.isJSONString(item.get("TITLE")));
		author.setText("Author: " + ClientUtil.isJSONString(item.get("AUTHOR")));
		isbn.setText("ISBN: " + ClientUtil.isJSONString(item.get("ISBN")));
		//String priceString = ClientUtil.isJSONString(item.get("PRICE"));
//		Double priceNum = priceString.equals("") ? 0 : Double
//				.parseDouble(priceString);
//		price.setText("Price: $"
//				+ NumberFormat.getFormat(".00").format(priceNum));
		subscriptionDate.setText("Subscription Date: "
				+ ClientUtil.isJSONString(item.get("SUBMISSION_DATE")));
		priceMin.setText("Price Min: "
				+ ClientUtil.isJSONString(item.get("PRICE_MIN")));
		priceMax.setText("Price Max: "
				+ ClientUtil.isJSONString(item.get("PRICE_MAX")));
		email.setHref("mailto:" + ClientUtil.isJSONString(item.get("EMAIL")));
		email.setText(ClientUtil.isJSONString(item.get("EMAIL")));

		
		if (forListing) {
			infoPanel.getElement().setClassName("span5");
			listingPanel.getElement().setClassName("span1");
			//repostBtn.getElement().setClassName("btn item-btn");
			//editBtn.getElement().setClassName("btn btn-primary item-btn");
			unsubscribeBtn.getElement().setClassName("btn btn-danger item-btn");
			errorPanel.getElement().setClassName("span9 alert alert-error");
			errorPanel.getElement().setAttribute("style", "display: none;");
			
			if (unsubscriptionCode!=null) {
/*				repostReg = repostBtn.addClickHandler(new ClickHandler() {
	
					@Override
					public void onClick(ClickEvent event) {
						repostBtn.getElement().setAttribute("disabled", "");
						repostBtn.getElement().setInnerHTML("Reposting...");
						subscriptionService.repostListing(subscriptionID, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								errorPanel.getElement().removeAttribute("style");
								errorPanel.getElement().setInnerHTML("Reposting failed, please try again");
							}

							@Override
							public void onSuccess(Boolean result) {
								if (result) {
									repostBtn.getElement().setInnerHTML("Reposted!");
									errorPanel.getElement().setAttribute("style", "display: none;");
									errorPanel.getElement().setInnerHTML("");
									if (repostReg!=null) {
										repostReg.removeHandler();
									}
								}
								else {
									errorPanel.getElement().removeAttribute("style");
									errorPanel.getElement().setInnerHTML("Reposting failed, please try again");
								}
							}
							
						});
					}
					
				});*/

				//editBtn.setHref("#EditListing:" + subscriptionID);
				
				unsubscribeBtn.addClickHandler(new ClickHandler() {
	
					@Override
					public void onClick(ClickEvent event) {
						if (Window.confirm("Are you sure you want to unsubscribe?")) {
							UnsubscriptionComponent unsubscriber = new UnsubscriptionComponent();
							unsubscriber.setUnsubscribeCode(unsubscriptionCode);
							subscriptionService.unsubscribe(unsubscriber, new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									errorPanel.getElement().removeAttribute("style");
									errorPanel.getElement().setInnerHTML("Unsubscribing failed, please try again");
								}

								@Override
								public void onSuccess(Boolean result) {
									if (result) {
										parent.reload(false);
										self.removeFromParent();
									}
									else {
										errorPanel.getElement().removeAttribute("style");
										errorPanel.getElement().setInnerHTML("Unsubscribing failed, please try again");
									}
								}
								
							});
						}
					}
					
				});
			}
		}
		else {
			infoPanel.getElement().setClassName("span6");
			errorPanel.removeFromParent();
			listingPanel.removeFromParent();
		}
	}
	
	public void setParent(SubscriptionsViewImpl subscriptionsView) {
		parent = subscriptionsView;
	}
}
