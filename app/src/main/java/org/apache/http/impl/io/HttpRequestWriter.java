//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.HttpMessage;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineFormatter;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public class HttpRequestWriter extends AbstractMessageWriter {
    public HttpRequestWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
        super((SessionOutputBuffer)null, (LineFormatter)null, (HttpParams)null);
        throw new RuntimeException("Stub!");
    }

    protected void writeHeadLine(HttpMessage message) throws IOException {
        throw new RuntimeException("Stub!");
    }
}
