package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
public class RequestProxyAuthentication implements HttpRequestInterceptor {
    public RequestProxyAuthentication() {
        throw new RuntimeException("Stub!");
    }

    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        throw new RuntimeException("Stub!");
    }
}