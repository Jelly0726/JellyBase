//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineFormatter;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;

/** @deprecated */
@Deprecated
public abstract class AbstractMessageWriter implements HttpMessageWriter {
    protected final CharArrayBuffer lineBuf;
    protected final LineFormatter lineFormatter;
    protected final SessionOutputBuffer sessionBuffer;

    public AbstractMessageWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    protected abstract void writeHeadLine(HttpMessage var1) throws IOException;

    public void write(HttpMessage message) throws IOException, HttpException {
        throw new RuntimeException("Stub!");
    }
}
