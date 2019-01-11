//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.message;

import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;

/** @deprecated */
@Deprecated
public class BasicHttpRequest extends AbstractHttpMessage implements HttpRequest {
    public BasicHttpRequest(String method, String uri) {
        throw new RuntimeException("Stub!");
    }

    public BasicHttpRequest(String method, String uri, ProtocolVersion ver) {
        throw new RuntimeException("Stub!");
    }

    public BasicHttpRequest(RequestLine requestline) {
        throw new RuntimeException("Stub!");
    }

    public ProtocolVersion getProtocolVersion() {
        throw new RuntimeException("Stub!");
    }

    public RequestLine getRequestLine() {
        throw new RuntimeException("Stub!");
    }
}
