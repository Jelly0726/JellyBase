//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.cookie;

import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieSpec;

import java.util.Collection;

/** @deprecated */
@Deprecated
public abstract class AbstractCookieSpec implements CookieSpec {
    public AbstractCookieSpec() {
        throw new RuntimeException("Stub!");
    }

    public void registerAttribHandler(String name, CookieAttributeHandler handler) {
        throw new RuntimeException("Stub!");
    }

    protected CookieAttributeHandler findAttribHandler(String name) {
        throw new RuntimeException("Stub!");
    }

    protected CookieAttributeHandler getAttribHandler(String name) {
        throw new RuntimeException("Stub!");
    }

    protected Collection<CookieAttributeHandler> getAttribHandlers() {
        throw new RuntimeException("Stub!");
    }
}
