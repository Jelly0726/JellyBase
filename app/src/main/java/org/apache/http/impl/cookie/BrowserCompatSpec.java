//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.cookie;

import java.util.List;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;

/** @deprecated */
@Deprecated
public class BrowserCompatSpec extends CookieSpecBase {
    protected static final String[] DATE_PATTERNS = null;

    public BrowserCompatSpec(String[] datepatterns) {
        throw new RuntimeException("Stub!");
    }

    public BrowserCompatSpec() {
        throw new RuntimeException("Stub!");
    }

    public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
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
