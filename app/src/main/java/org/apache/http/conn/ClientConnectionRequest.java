//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.conn;

import java.util.concurrent.TimeUnit;

/** @deprecated */
@Deprecated
public interface ClientConnectionRequest {
    ManagedClientConnection getConnection(long var1, TimeUnit var3) throws InterruptedException, ConnectionPoolTimeoutException;

    void abortRequest();
}
