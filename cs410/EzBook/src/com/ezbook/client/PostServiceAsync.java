package com.ezbook.client;

import com.ezbook.shared.PostComponent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PostServiceAsync {
    void newPost(PostComponent post, boolean postOnFB, AsyncCallback<Boolean> asyncCallback);
}
