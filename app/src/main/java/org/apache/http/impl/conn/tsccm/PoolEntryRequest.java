//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.conn.tsccm;

import org.apache.http.conn.ConnectionPoolTimeoutException;

import java.util.concurrent.TimeUnit;

/** @deprecated */
@Deprecated
public interface PoolEntryRequest {
    BasicPoolEntry getPoolEntry(long var1, TimeUnit var3) throws InterruptedException, ConnectionPoolTimeoutException;

    void abortRequest();
}
