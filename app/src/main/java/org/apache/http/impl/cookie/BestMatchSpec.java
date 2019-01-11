//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.cookie;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;

import java.util.List;

/** @deprecated */
@Deprecated
public class BestMatchSpec implements CookieSpec {
    public BestMatchSpec(String[] datepatterns, boolean oneHeader) {
        throw new RuntimeException("Stub!");
    }

    public BestMatchSpec() {
        throw new RuntimeException("Stub!");
    }

    public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
        throw new RuntimeException("Stub!");
    }

    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        throw new RuntimeException("Stub!");
    }

    public boolean match(Cookie cookie, CookieOrigin origin) {
        throw new RuntimeException("Stub!");
    }

    public List<Header> formatCookies(List<Cookie> cookies) {
        throw new RuntimeException("Stub!");
    }

    public int getVersion() {
        throw new RuntimeException("Stub!");
    }

    public Header getVersionHeader() {
        throw new RuntimeException("Stub!");
    }
}
