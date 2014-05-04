package com.ezbook.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;


public class EzBookViewImpl extends BaseViewImpl implements EzBookView {
	
	private static EzBookViewImplUiBinder uiBinder = GWT.create(EzBookViewImplUiBinder.class);
	interface EzBookViewImplUiBinder extends UiBinder<Widget, EzBookViewImpl> {}
	
	@SuppressWarnings("unused") private Presenter presenter;
	
	@UiField Anchor connectBtn;
	
	public EzBookViewImpl() { 
		initWidget(uiBinder.createAndBindUi(this));
		connectBtn.getElement().setClassName("btn btn-primary btn-large span6 bs-button");
		connectBtn.getElement().setAttribute("href", "fbLogin?place=EzBook&param=ezbook");
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
