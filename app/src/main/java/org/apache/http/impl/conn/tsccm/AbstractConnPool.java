//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.conn.tsccm;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.IdleConnectionHandler;

/** @deprecated */
@Deprecated
public abstract class AbstractConnPool implements RefQueueHandler {
    protected IdleConnectionHandler idleConnHandler;
    protected volatile boolean isShutDown;
    protected Set<BasicPoolEntryRef> issuedConnections;
    protected int numConnections;
    protected final Lock poolLock;
    protected ReferenceQueue<Object> refQueue;

    protected AbstractConnPool() {
        throw new RuntimeException("Stub!");
    }

    public void enableConnectionGC() throws IllegalStateException {
        throw new RuntimeException("Stub!");
    }

    public final BasicPoolEntry getEntry(HttpRoute route, Object state, long timeout, TimeUnit tunit) throws ConnectionPoolTimeoutException, InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public abstract PoolEntryRequest requestPoolEntry(HttpRoute var1, Object var2);

    public abstract void freeEntry(BasicPoolEntry var1, boolean var2, long var3, TimeUnit var5);

    public void handleReference(Reference ref) {
        throw new RuntimeException("Stub!");
    }

    protected abstract void handleLostEntry(HttpRoute var1);

    public void closeIdleConnections(long idletime, TimeUnit tunit) {
        throw new RuntimeException("Stub!");
    }

    public void closeExpiredConnections() {
        throw new RuntimeException("Stub!");
    }

    public abstract void deleteClosedConnections();

    public void shutdown() {
        throw new RuntimeException("Stub!");
    }

    protected void closeConnection(OperatedClientConnection conn) {
        throw new RuntimeException("Stub!");
    }
}
