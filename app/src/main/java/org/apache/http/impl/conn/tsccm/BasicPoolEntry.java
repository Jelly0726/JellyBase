//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.conn.tsccm;

import java.lang.ref.ReferenceQueue;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.AbstractPoolEntry;

/** @deprecated */
@Deprecated
public class BasicPoolEntry extends AbstractPoolEntry {
    public BasicPoolEntry(ClientConnectionOperator op, HttpRoute route, ReferenceQueue<Object> queue) {
        super((ClientConnectionOperator)null, (HttpRoute)null);
        throw new RuntimeException("Stub!");
    }

    protected final OperatedClientConnection getConnection() {
        throw new RuntimeException("Stub!");
    }

    protected final HttpRoute getPlannedRoute() {
        throw new RuntimeException("Stub!");
    }

    protected final BasicPoolEntryRef getWeakRef() {
        throw new RuntimeException("Stub!");
    }
}
