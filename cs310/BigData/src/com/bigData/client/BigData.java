package com.bigData.client;

import java.util.Set;
import java.util.Iterator;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray; 
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONObject;

public class BigData implements EntryPoint {

	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel addPanel = new HorizontalPanel();
	
	private TextBox newURLTextBox = new TextBox();
	
	private Button inputURLButton = new Button("Input URL");
	private FileUpload upload = new FileUpload();
	private int jsonRequestId = 0;
	private static String JSON_URL = "";
	private Label errorMsgLabel = new Label();
	private DataSet currentDataSet;
	
	private int datasetcounter = 0;
	private VerticalPanel displayPanel = new VerticalPanel();
	private HorizontalPanel buttonPanel = new HorizontalPanel();

	/**
	 * Entry point method.
	 */
	public void onModuleLoad() {
		// Create table for scientific data.
		addPanel.add(newURLTextBox);
		addPanel.add(inputURLButton);
		upload.setName("Upload Data File");
		addPanel.add(upload);
		addPanel.addStyleName("addPanel");

		// Assemble Main panel.
		buttonPanel.add(addPanel);
		mainPanel.add(buttonPanel);
		
		displayPanel.setSpacing(50);
		mainPanel.add(displayPanel);

		// Associate the Main panel with the HTML host page.
		RootPanel.get("statisticaldatalist").add(mainPanel);

		// Move cursor focus to the input box.
		newURLTextBox.setFocus(true);

		// Listen for mouse events on the Add button.
		inputURLButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				inputURL();
			}
		});

		// Listen for keyboard events in the input box.
		newURLTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					inputURL();
				}
			}
		});
	}

	private void inputURL() {
		JSON_URL = newURLTextBox.getText().trim();
		JSON_JSNI();
	}

	private void JSON_JSNI() {
		String url = JSON_URL;
		System.out.println(url);
		// Append the name of the callback function to the JSON URL.
		url = URL.encode(url) + "&callback=";
		System.out.println(url);
		// Send request to server by replacing RequestBuilder code with a call to a JSNI method.
		getJson(jsonRequestId++, url, this);
	}

	/**
	 * Make call to remote server.
	 */
	public native static void getJson(int requestId, String url,
			BigData handler) /*-{
   var callback = "callback" + requestId;

   // [1] Create a script element.
   var script = document.createElement("script");
   script.setAttribute("src", url+callback);
   script.setAttribute("type", "text/javascript");

   // [2] Define the callback function on the window object.
   window[callback] = function(jsonObj) {
   // [3]
     handler.@com.bigData.client.BigData::handleJsonResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
     window[callback + "done"] = true;
   }

   // [4] JSON download has 1-second timeout.
   setTimeout(function() {
     if (!window[callback + "done"]) {
       handler.@com.bigData.client.BigData::handleJsonResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(null);
     }

     // [5] Cleanup. Remove script and callback elements.
     document.body.removeChild(script);
     delete window[callback];
     delete window[callback + "done"];
   }, 1000);

   // [6] Attach the script element to the document body.
   document.body.appendChild(script);
  }-*/;

	/**
	 * Handle the response to the request for stock data from a remote server.
	 */
	public void handleJsonResponse(JavaScriptObject jso) {
		if (jso == null) {
			displayError("Couldn't retrieve JSON");
			return;
		}
		//DataSet 
		currentDataSet = asDataSet(jso);
		Visualization viz = new Visualization(currentDataSet);
		viz.visualize(displayPanel);
	}


	/**
	 * Cast JavaScriptObject as JsArray of Data.
	 */
	private final DataSet asDataSet(JavaScriptObject jso) {

		DataSet dataset = new DataSet("DataSet "+datasetcounter);
		JSONArray arr = new JSONArray(jso);
		for(int i=0; i<arr.size(); i++){

			JSONObject obj = (JSONObject) arr.get(i);

			Set<String> set = obj.keySet();

			Data data = new Data();
			Iterator<String> itr = set.iterator();
			while(itr.hasNext()){
				String key = itr.next();
				JSONValue val = (JSONValue) obj.get(key);

				if(val.isNumber()!=null) { data.addData(key, val.isNumber().doubleValue()); }
				else if(val.isString()!=null && !val.isString().equals("")) { data.addData(key, val.isString().stringValue()); }
			}

			dataset.addDataToSet(data);
		}
		datasetcounter++;
		return dataset;
	}

	/**
	 * If can't get JSON, display error message.
	 * @param error
	 */
	private void displayError(String error) {
		errorMsgLabel.setText("Error: " + error);
		errorMsgLabel.setVisible(true);
	}
}