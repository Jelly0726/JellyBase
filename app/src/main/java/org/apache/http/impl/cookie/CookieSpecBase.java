//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.cookie;

import org.apache.http.HeaderElement;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;

import java.util.List;

/** @deprecated */
@Deprecated
public abstract class CookieSpecBase extends AbstractCookieSpec {
    public CookieSpecBase() {
        throw new RuntimeException("Stub!");
    }

    protected static String getDefaultPath(CookieOrigin origin) {
        throw new RuntimeException("Stub!");
    }

    protected static String getDefaultDomain(CookieOrigin origin) {
        throw new RuntimeException("Stub!");
    }

    protected List<Cookie> parse(HeaderElement[] elems, CookieOrigin origin) throws MalformedCookieException {
        throw new RuntimeException("Stub!");
    }

    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        throw new RuntimeException("Stub!");
    }

    public boolean match(Cookie cookie, CookieOrigin origin) {
        throw new RuntimeException("Stub!");
    }
}
