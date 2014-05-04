package com.ezbook.client.ui;

import com.ezbook.client.ClientUtil;
import com.ezbook.client.FeedbackService;
import com.ezbook.client.FeedbackServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class FeedbackViewImpl extends BaseViewImpl implements FeedbackView {
	private static FeedbackViewImplUiBinder uiBinder = GWT.create(FeedbackViewImplUiBinder.class);
	interface FeedbackViewImplUiBinder extends UiBinder<Widget, FeedbackViewImpl> {}
	@SuppressWarnings("unused") private Presenter presenter;

	private FeedbackServiceAsync feedbackService = GWT.create(FeedbackService.class);
	
	@UiField TextArea message;
	@UiField Anchor sendBtn;
	@UiField HTML formAlert;
	
	public FeedbackViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		message.getElement().setAttribute("id", "message");
		message.getElement().setClassName("span5");
		message.getElement().setAttribute("rows", "10");
		message.getElement().setAttribute("data-required", "");
		sendBtn.getElement().setClassName("btn btn-primary controls");
		formAlert.getElement().setAttribute("id", "form-alert");
		formAlert.getElement().setClassName("alert alert-error form-alert controls");
		formAlert.getElement().setAttribute("style", "display: none;");
	}
	
	@UiHandler("sendBtn")
	public void send(ClickEvent e) {
		if (ClientUtil.ValidateForm("feedback-form")) {
			sendBtn.getElement().setInnerHTML("Sending...");
			sendBtn.getElement().setAttribute("disabled", "");
			feedbackService.sendFeedback(message.getText(), new AsyncCallback<Boolean>() {
	
				@Override
				public void onFailure(Throwable caught) {
					formAlert.setHTML("Failed to send feedback message.");
					formAlert.getElement().setAttribute("style", "");
					formAlert.getElement().setClassName("alert alert-error form-alert controls");
					sendBtn.getElement().setInnerHTML("Post");
					sendBtn.getElement().removeAttribute("disabled");
				}
	
				@Override
				public void onSuccess(Boolean result) {
					formAlert.getElement().setClassName("alert alert-success form-alert controls");
					formAlert.setHTML("Thank you for your feedback!");
					sendBtn.getElement().setInnerHTML("Post");
					sendBtn.getElement().removeAttribute("disabled");
				}
				
			});
		}
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}
