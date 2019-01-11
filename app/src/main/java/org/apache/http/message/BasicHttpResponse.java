//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.StatusLine;

import java.util.Locale;

/** @deprecated */
@Deprecated
public class BasicHttpResponse extends AbstractHttpMessage implements HttpResponse {
    public BasicHttpResponse(StatusLine statusline, ReasonPhraseCatalog catalog, Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public BasicHttpResponse(StatusLine statusline) {
        throw new RuntimeException("Stub!");
    }

    public BasicHttpResponse(ProtocolVersion ver, int code, String reason) {
        throw new RuntimeException("Stub!");
    }

    public ProtocolVersion getProtocolVersion() {
        throw new RuntimeException("Stub!");
    }

    public StatusLine getStatusLine() {
        throw new RuntimeException("Stub!");
    }

    public HttpEntity getEntity() {
        throw new RuntimeException("Stub!");
    }

    public Locale getLocale() {
        throw new RuntimeException("Stub!");
    }

    public void setStatusLine(StatusLine statusline) {
        throw new RuntimeException("Stub!");
    }

    public void setStatusLine(ProtocolVersion ver, int code) {
        throw new RuntimeException("Stub!");
    }

    public void setStatusLine(ProtocolVersion ver, int code, String reason) {
        throw new RuntimeException("Stub!");
    }

    public void setStatusCode(int code) {
        throw new RuntimeException("Stub!");
    }

    public void setReasonPhrase(String reason) {
        throw new RuntimeException("Stub!");
    }

    public void setEntity(HttpEntity entity) {
        throw new RuntimeException("Stub!");
    }

    public void setLocale(Locale loc) {
        throw new RuntimeException("Stub!");
    }

    protected String getReason(int code) {
        throw new RuntimeException("Stub!");
    }
}
