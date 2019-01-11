//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.io;

import java.io.IOException;
import org.apache.http.util.CharArrayBuffer;

/** @deprecated */
@Deprecated
public interface SessionInputBuffer {
    int read(byte[] var1, int var2, int var3) throws IOException;

    int read(byte[] var1) throws IOException;

    int read() throws IOException;

    int readLine(CharArrayBuffer var1) throws IOException;

    String readLine() throws IOException;

    boolean isDataAvailable(int var1) throws IOException;

    HttpTransportMetrics getMetrics();
}
