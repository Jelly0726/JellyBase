//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;

/** @deprecated */
@Deprecated
public class BasicHttpEntityEnclosingRequest extends BasicHttpRequest implements HttpEntityEnclosingRequest {
    public BasicHttpEntityEnclosingRequest(String method, String uri) {
        super((RequestLine)null);
        throw new RuntimeException("Stub!");
    }

    public BasicHttpEntityEnclosingRequest(String method, String uri, ProtocolVersion ver) {
        super((RequestLine)null);
        throw new RuntimeException("Stub!");
    }

    public BasicHttpEntityEnclosingRequest(RequestLine requestline) {
        super((RequestLine)null);
        throw new RuntimeException("Stub!");
    }

    public HttpEntity getEntity() {
        throw new RuntimeException("Stub!");
    }

    public void setEntity(HttpEntity entity) {
        throw new RuntimeException("Stub!");
    }

    public boolean expectContinue() {
        throw new RuntimeException("Stub!");
    }
}
