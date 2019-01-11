package org.apache.http;


/** @deprecated */
@Deprecated
public interface HttpEntityEnclosingRequest extends HttpRequest {
    boolean expectContinue();

    void setEntity(HttpEntity var1);

    HttpEntity getEntity();
}
