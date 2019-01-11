package org.apache.http.client;

import org.apache.http.HttpResponse;

import java.io.IOException;

/** @deprecated */
@Deprecated
public interface ResponseHandler<T> {
    T handleResponse(HttpResponse var1) throws ClientProtocolException, IOException;
}
