package org.apache.http;


/** @deprecated */
@Deprecated
public interface HttpRequest extends HttpMessage {
    org.apache.http.RequestLine getRequestLine();
}
