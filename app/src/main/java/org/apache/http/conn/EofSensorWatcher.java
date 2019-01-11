//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;

/** @deprecated */
@Deprecated
public interface EofSensorWatcher {
    boolean eofDetected(InputStream var1) throws IOException;

    boolean streamClosed(InputStream var1) throws IOException;

    boolean streamAbort(InputStream var1) throws IOException;
}
