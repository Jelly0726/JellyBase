//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
public class NoConnectionReuseStrategy implements ConnectionReuseStrategy {
    public NoConnectionReuseStrategy() {
        throw new RuntimeException("Stub!");
    }

    public boolean keepAlive(HttpResponse response, HttpContext context) {
        throw new RuntimeException("Stub!");
    }
}
