package com.ezbook.client.ui;

import com.ezbook.client.ClientUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SearchItemView extends Composite {
	private static SearchItemItemViewImplUiBinder uiBinder = GWT.create(SearchItemItemViewImplUiBinder.class);
	interface SearchItemItemViewImplUiBinder extends UiBinder<Widget, SearchItemView> {}

	private String postID;
	
	@UiField
	Anchor email, mapBtn;
	@UiField
	Image image, mapImage;
	@UiField
	HTMLPanel mapPanel, map, infoPanel;
	@UiField
	InlineHTML mapPanelLocation;
	@UiField
	Label title, author, isbn, price, postDate, contact, details, location;

	public SearchItemView(JSONObject item, Double userLat, Double userLng) {
		initWidget(uiBinder.createAndBindUi(this));
		
		postID = ClientUtil.isJSONString(item.get("POST_ID"));
		
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
		
		infoPanel.getElement().setClassName("span6");
	}
}
