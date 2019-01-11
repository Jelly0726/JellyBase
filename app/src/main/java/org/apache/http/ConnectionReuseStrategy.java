package org.apache.http;


import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
public interface ConnectionReuseStrategy {
    boolean keepAlive(HttpResponse var1, HttpContext var2);
}
