package com.ezbook.client;

import com.ezbook.shared.PostComponent;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>AmazonServiceAsync</code>.
 */
public interface AmazonServiceAsync {

    public void SearchBook(String keywords, AsyncCallback<PostComponent> asyncCallback);
    public void SearchBookISBN(String ISBN, AsyncCallback<PostComponent> asyncCallback);
}
