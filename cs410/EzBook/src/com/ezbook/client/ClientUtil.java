package com.ezbook.client;

import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.HTML;

public class ClientUtil {

	public static native boolean ValidateForm(String id) /*-{
		var regExes = {
		    "url":/^https?\:\/\/[a-zA-Z0-9\-\.]+\.[a-zA-Z]{2,3}(\S*)?$/,
		    "number":/^\+?[0-9.]+$/,
		    "email":/^[\w\.=-]+@[\w\.-]+\.[\w]{2,3}$/,
		};
	
		var required = $wnd.$("#" + id + " *[data-required]"),
        formatChecks = $wnd.$("#" + id + " *[data-field-type]"),
        elem,
        field_type,
        valid = true;

	    required.each(function (i) {
	        elem = $wnd.$(this);
	        var stripedElem = elem.val().replace(/(<([^>]+)>)/ig, "");
	        if (stripedElem == undefined || stripedElem.length == 0) {
	            elem.parents('.control-group').addClass("error");
	            $wnd.$("#form-alert").attr("style", "");
	            $wnd.$("#form-alert").html("Please fill in all the required fields marked with a *.");
	            valid = false;
	        } else {
	            elem.parents('.control-group').removeClass("error");
	        }
	    });
	
	    formatChecks.each(function (i) {
	        var elem = $wnd.$(this);
	        field_type = elem.data('field-type');
	        if (elem.val().length != 0 && !regExes[field_type].exec(elem.val())) {
	            elem.parents('.control-group').addClass("warning");
	            valid = false;
	        } else {
	            elem.parents('.control-group').removeClass("warning");
	        }
	    });
	
	    return valid;
	}-*/;
	
	public static String isJSONString(JSONValue v) {
		if (v!=null) {
			JSONString jsonString = v.isString();
			if (jsonString!=null) {
				return jsonString.stringValue();
			}
		}
		return "";
	}
	
	public static HTML createPager(int page, int totalpages, String loadPageMethod) {
		String html = "<div class='pagination pagination-right'>"
					+ "<ul>";
		boolean prev = page > 0;
		html += "<li" + (prev ? "" : " class='disabled' ") + "><a " + (!prev ? "" : "onclick='" + loadPageMethod + "(" + (page-1) + ");'") + "><<</a></li>";
		int count = 0;
		int currentPage = page - 2;
		if (currentPage > (totalpages-4)) {
			currentPage -= currentPage - (totalpages-4);
		}
		while (count < 5) {
			if (currentPage>=0) {
				html += "<li" + (page==currentPage ? " class='active' " : "") + "><a onclick='" + loadPageMethod + "(" + currentPage + ");'" + ">" + (currentPage+1) +"</a></li>";
				count++;
			}
			if (currentPage>=totalpages) break;
			currentPage++;
		}
		boolean next = page < totalpages;
		html += "<li" + (next ? "" : " class='disabled' ") + "><a " + (!next ? "" : "onclick='" + loadPageMethod + "(" + (page+1) + ");'") + ">>></a></li>";
		html += "</ul>"+ "</div>";

		return new HTML(html);
	}
}
