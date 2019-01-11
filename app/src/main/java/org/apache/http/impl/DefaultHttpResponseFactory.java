//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.ProtocolVersion;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HttpContext;

import java.util.Locale;

/** @deprecated */
@Deprecated
public class DefaultHttpResponseFactory implements HttpResponseFactory {
    protected final ReasonPhraseCatalog reasonCatalog;

    public DefaultHttpResponseFactory(ReasonPhraseCatalog catalog) {
        throw new RuntimeException("Stub!");
    }

    public DefaultHttpResponseFactory() {
        throw new RuntimeException("Stub!");
    }

    public HttpResponse newHttpResponse(ProtocolVersion ver, int status, HttpContext context) {
        throw new RuntimeException("Stub!");
    }

    public HttpResponse newHttpResponse(StatusLine statusline, HttpContext context) {
        throw new RuntimeException("Stub!");
    }

    protected Locale determineLocale(HttpContext context) {
        throw new RuntimeException("Stub!");
    }
}
