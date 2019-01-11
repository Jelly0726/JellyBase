//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.protocol;

/** @deprecated */
@Deprecated
public interface HttpContext {
    String RESERVED_PREFIX = "http.";

    Object getAttribute(String var1);

    void setAttribute(String var1, Object var2);

    Object removeAttribute(String var1);
}
