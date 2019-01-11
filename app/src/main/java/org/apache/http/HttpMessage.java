package org.apache.http;

import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public interface HttpMessage {
    org.apache.http.ProtocolVersion getProtocolVersion();

    boolean containsHeader(String var1);

    Header[] getHeaders(String var1);

    Header getFirstHeader(String var1);

    Header getLastHeader(String var1);

    Header[] getAllHeaders();

    void addHeader(Header var1);

    void addHeader(String var1, String var2);

    void setHeader(Header var1);

    void setHeader(String var1, String var2);

    void setHeaders(Header[] var1);

    void removeHeader(Header var1);

    void removeHeaders(String var1);

    HeaderIterator headerIterator();

    HeaderIterator headerIterator(String var1);

    HttpParams getParams();

    void setParams(HttpParams var1);
}