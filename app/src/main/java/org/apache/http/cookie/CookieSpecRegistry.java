//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.cookie;

import org.apache.http.params.HttpParams;

import java.util.List;
import java.util.Map;

/** @deprecated */
@Deprecated
public final class CookieSpecRegistry {
    public CookieSpecRegistry() {
        throw new RuntimeException("Stub!");
    }

    public synchronized void register(String name, CookieSpecFactory factory) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void unregister(String id) {
        throw new RuntimeException("Stub!");
    }

    public synchronized CookieSpec getCookieSpec(String name, HttpParams params) throws IllegalStateException {
        throw new RuntimeException("Stub!");
    }

    public synchronized CookieSpec getCookieSpec(String name) throws IllegalStateException {
        throw new RuntimeException("Stub!");
    }

    public synchronized List<String> getSpecNames() {
        throw new RuntimeException("Stub!");
    }

    public synchronized void setItems(Map<String, CookieSpecFactory> map) {
        throw new RuntimeException("Stub!");
    }
}
