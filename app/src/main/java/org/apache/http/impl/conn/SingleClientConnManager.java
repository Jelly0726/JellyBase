//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.conn;

import org.apache.http.HttpException;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;

/** @deprecated */
@Deprecated
public class SingleClientConnManager implements ClientConnectionManager {
    public static final String MISUSE_MESSAGE = "Invalid use of SingleClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
    protected boolean alwaysShutDown;
    protected ClientConnectionOperator connOperator;
    protected long connectionExpiresTime;
    protected volatile boolean isShutDown;
    protected long lastReleaseTime;
    protected SingleClientConnManager.ConnAdapter managedConn;
    protected SchemeRegistry schemeRegistry;
    protected SingleClientConnManager.PoolEntry uniquePoolEntry;

    public SingleClientConnManager(HttpParams params, SchemeRegistry schreg) {
        throw new RuntimeException("Stub!");
    }

    protected void finalize() throws Throwable {
        throw new RuntimeException("Stub!");
    }

    public SchemeRegistry getSchemeRegistry() {
        throw new RuntimeException("Stub!");
    }

    protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        throw new RuntimeException("Stub!");
    }

    protected final void assertStillUp() throws IllegalStateException {
        throw new RuntimeException("Stub!");
    }

    public final ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
        throw new RuntimeException("Stub!");
    }

    public ManagedClientConnection getConnection(HttpRoute route, Object state) {
        throw new RuntimeException("Stub!");
    }

    public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
        throw new RuntimeException("Stub!");
    }

    public void closeExpiredConnections() {
        throw new RuntimeException("Stub!");
    }

    public void closeIdleConnections(long idletime, TimeUnit tunit) {
        throw new RuntimeException("Stub!");
    }

    public void shutdown() {
        throw new RuntimeException("Stub!");
    }

    protected void revokeConnection() {
        throw new RuntimeException("Stub!");
    }

    protected class ConnAdapter extends AbstractPooledConnAdapter {
        protected ConnAdapter(SingleClientConnManager.PoolEntry entry, HttpRoute route) {
            super((ClientConnectionManager)null, (AbstractPoolEntry)null);
            throw new RuntimeException("Stub!");
        }

        @Override
        public void sendRequestHeader(HttpRequest var1) throws HttpException, IOException {

        }

        @Override
        public void receiveResponseEntity(HttpResponse var1) throws HttpException, IOException {

        }
    }

    protected class PoolEntry extends AbstractPoolEntry {
        protected PoolEntry() {
            super((ClientConnectionOperator)null, (HttpRoute)null);
            throw new RuntimeException("Stub!");
        }

        protected void close() throws IOException {
            throw new RuntimeException("Stub!");
        }

        protected void shutdown() throws IOException {
            throw new RuntimeException("Stub!");
        }
    }
}
