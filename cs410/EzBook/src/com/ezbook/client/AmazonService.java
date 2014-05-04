package com.ezbook.client;

import com.ezbook.shared.PostComponent;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("../amazon")
public interface AmazonService extends RemoteService {

    public PostComponent SearchBook(String keywords);
    public PostComponent SearchBookISBN(String ISBN);
}
