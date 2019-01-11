//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;

/** @deprecated */
@Deprecated
public class BasicEofSensorWatcher implements EofSensorWatcher {
    protected boolean attemptReuse;
    protected ManagedClientConnection managedConn;

    public BasicEofSensorWatcher(ManagedClientConnection conn, boolean reuse) {
        throw new RuntimeException("Stub!");
    }

    public boolean eofDetected(InputStream wrapped) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public boolean streamClosed(InputStream wrapped) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public boolean streamAbort(InputStream wrapped) throws IOException {
        throw new RuntimeException("Stub!");
    }
}
