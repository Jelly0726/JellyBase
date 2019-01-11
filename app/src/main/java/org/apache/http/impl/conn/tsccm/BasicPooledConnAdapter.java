//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.conn.tsccm;

import org.apache.http.HttpException;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.AbstractPoolEntry;
import org.apache.http.impl.conn.AbstractPooledConnAdapter;

import java.io.IOException;

import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;

/** @deprecated */
@Deprecated
public class BasicPooledConnAdapter extends AbstractPooledConnAdapter {
    protected BasicPooledConnAdapter(ThreadSafeClientConnManager tsccm, AbstractPoolEntry entry) {
        super((ClientConnectionManager)null, (AbstractPoolEntry)null);
        throw new RuntimeException("Stub!");
    }

    protected ClientConnectionManager getManager() {
        throw new RuntimeException("Stub!");
    }

    protected AbstractPoolEntry getPoolEntry() {
        throw new RuntimeException("Stub!");
    }

    protected void detach() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void sendRequestHeader(HttpRequest var1) throws HttpException, IOException {

    }

    @Override
    public void receiveResponseEntity(HttpResponse var1) throws HttpException, IOException {

    }
}
