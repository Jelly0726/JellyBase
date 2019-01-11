//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.entity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** @deprecated */
@Deprecated
public class HttpEntityWrapper implements HttpEntity {
    protected HttpEntity wrappedEntity;

    public HttpEntityWrapper(HttpEntity wrapped) {
        throw new RuntimeException("Stub!");
    }

    public boolean isRepeatable() {
        throw new RuntimeException("Stub!");
    }

    public boolean isChunked() {
        throw new RuntimeException("Stub!");
    }

    public long getContentLength() {
        throw new RuntimeException("Stub!");
    }

    public Header getContentType() {
        throw new RuntimeException("Stub!");
    }

    public Header getContentEncoding() {
        throw new RuntimeException("Stub!");
    }

    public InputStream getContent() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void writeTo(OutputStream outstream) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public boolean isStreaming() {
        throw new RuntimeException("Stub!");
    }

    public void consumeContent() throws IOException {
        throw new RuntimeException("Stub!");
    }
}
