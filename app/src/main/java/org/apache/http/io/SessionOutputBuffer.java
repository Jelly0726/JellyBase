//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.io;

import java.io.IOException;
import org.apache.http.util.CharArrayBuffer;

/** @deprecated */
@Deprecated
public interface SessionOutputBuffer {
    void write(byte[] var1, int var2, int var3) throws IOException;

    void write(byte[] var1) throws IOException;

    void write(int var1) throws IOException;

    void writeLine(String var1) throws IOException;

    void writeLine(CharArrayBuffer var1) throws IOException;

    void flush() throws IOException;

    HttpTransportMetrics getMetrics();
}
