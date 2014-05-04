package com.ezbook.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>FacebookServiceAsync</code>.
 */
public interface AuthServiceAsync {
    public void status(AsyncCallback<String> asynCallback);
    public void logout(AsyncCallback<Boolean> asyncCallback);
}
