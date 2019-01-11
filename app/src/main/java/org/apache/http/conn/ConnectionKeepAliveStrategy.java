//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.conn;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
public interface ConnectionKeepAliveStrategy {
    long getKeepAliveDuration(HttpResponse var1, HttpContext var2);
}
