package com.ezbook.client.ui;

import com.ezbook.client.ClientUtil;
import com.ezbook.client.MyListingsService;
import com.ezbook.client.MyListingsServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
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
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class MyListingsItemView extends Composite {
	private static MyListingsItemViewImplUiBinder uiBinder = GWT.create(MyListingsItemViewImplUiBinder.class);
	interface MyListingsItemViewImplUiBinder extends UiBinder<Widget, MyListingsItemView> {}

	private MyListingsServiceAsync myListingsService = GWT.create(MyListingsService.class);
	private MyListingsItemView self; // kind of bad.. but whatever
	private MyListingsViewImpl parent;
	private String postID;
	private HandlerRegistration repostReg;
	
	@UiField
	Anchor email, mapBtn, repostBtn, editBtn, deleteBtn;
	@UiField
	Image image, mapImage;
	@UiField
	HTMLPanel mapPanel, map, infoPanel, listingPanel;
	@UiField
	InlineHTML mapPanelLocation;
	@UiField
	Label title, author, isbn, price, postDate, contact, details, location;
	@UiField HTML errorPanel;

	public MyListingsItemView(JSONObject item) {
		initWidget(uiBinder.createAndBindUi(this));
		
		postID = ClientUtil.isJSONString(item.get("POST_ID"));
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

		String lat = ClientUtil.isJSONString(item.get("LATITUDE"));
		String lng = ClientUtil.isJSONString(item.get("LONGITUDE"));
		String address = ClientUtil.isJSONString(item.get("ADDRESS"));
		if (!lat.equals("") && !lng.equals("") && !lat.equals("0")
				&& !lng.equals("0") && !address.equals("")) {
			location.removeFromParent();
			mapPanelLocation.setHTML("Proposed Meetup Location: " + address);
			mapBtn.getElement().setAttribute("data-toggle", "collapse");
			mapBtn.setHref("#map-" + postID);
			mapBtn.setText("Show on map");
			map.getElement().setAttribute("id", "map-" + postID);
			map.getElement().setClassName("accordion-body collapse");
			String url = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&zoom=12&markers=icon:http://chart.apis.google.com/chart?chst=d_map_pin_icon%26chld=books%257CFF0000|"
					+ lat + "," + lng + "&format=jpg&sensor=false";
			mapImage.getElement().setClassName("img-rounded");
			mapImage.getElement().setAttribute("src", url);
		} else {
			location.setText("Proposed Meetup Location: " + address);
			mapPanel.removeFromParent();
		}

		title.setText(ClientUtil.isJSONString(item.get("TITLE")));
		author.setText("Author: " + ClientUtil.isJSONString(item.get("AUTHOR")));
		isbn.setText("ISBN: " + ClientUtil.isJSONString(item.get("ISBN")));
		String priceString = ClientUtil.isJSONString(item.get("PRICE"));
		Double priceNum = priceString.equals("") ? 0 : Double
				.parseDouble(priceString);
		price.setText("Price: $"
				+ NumberFormat.getFormat(".00").format(priceNum));
		postDate.setText("Post Date: "
				+ ClientUtil.isJSONString(item.get("POST_DATE")));
		contact.setText("Contact: "
				+ ClientUtil.isJSONString(item.get("FB_NAME")));
		email.setHref("mailto:" + ClientUtil.isJSONString(item.get("EMAIL")));
		email.setText(ClientUtil.isJSONString(item.get("EMAIL")));
		details.setText("Details: "
				+ ClientUtil.isJSONString(item.get("COMMENT")));

		infoPanel.getElement().setClassName("span5");
		listingPanel.getElement().setClassName("span1");
		repostBtn.getElement().setClassName("btn item-btn");
		editBtn.getElement().setClassName("btn btn-primary item-btn");
		deleteBtn.getElement().setClassName("btn btn-danger item-btn");
		errorPanel.getElement().setClassName("span9 alert alert-error");
		errorPanel.getElement().setAttribute("style", "display: none;");

		if (postID!=null) {
			repostReg = repostBtn.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					repostBtn.getElement().setAttribute("disabled", "");
					repostBtn.getElement().setInnerHTML("Reposting...");
					myListingsService.repostListing(postID, new AsyncCallback<Boolean>() {

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

			});

			editBtn.setHref("#EditListing:" + postID);

			deleteBtn.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (Window.confirm("Are you sure you want to delete this post?")) {
						myListingsService.deleteListing(postID, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								errorPanel.getElement().removeAttribute("style");
								errorPanel.getElement().setInnerHTML("Deleting failed, please try again");
							}

							@Override
							public void onSuccess(Boolean result) {
								if (result) {
									parent.reload(false);
									self.removeFromParent();
								}
								else {
									errorPanel.getElement().removeAttribute("style");
									errorPanel.getElement().setInnerHTML("Deleting failed, please try again");
								}
							}

						});
					}
				}

			});
		}
	}
	
	public void setParent(MyListingsViewImpl myListingsView) {
		parent = myListingsView;
	}
}
