package com.ezbook.client;

import com.ezbook.shared.PostComponent;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("../secure/post")
public interface PostService extends RemoteService {

	    public Boolean newPost(PostComponent post, boolean postOnFB);

}
