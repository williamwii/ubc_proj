package com.ezbook.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("../authorize")
public interface AuthService extends RemoteService {
    public String status();
    public boolean logout();
}