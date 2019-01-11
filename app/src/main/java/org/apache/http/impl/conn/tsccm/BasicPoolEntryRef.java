//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.conn.tsccm;

import org.apache.http.conn.routing.HttpRoute;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/** @deprecated */
@Deprecated
public class BasicPoolEntryRef extends WeakReference<BasicPoolEntry> {
    public BasicPoolEntryRef(BasicPoolEntry entry, ReferenceQueue<Object> queue) {
        super((BasicPoolEntry) null, (ReferenceQueue)null);
        throw new RuntimeException("Stub!");
    }

    public final HttpRoute getRoute() {
        throw new RuntimeException("Stub!");
    }
}
