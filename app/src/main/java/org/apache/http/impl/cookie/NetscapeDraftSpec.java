//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.cookie;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;

import java.util.List;

/** @deprecated */
@Deprecated
public class NetscapeDraftSpec extends CookieSpecBase {
    protected static final String EXPIRES_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";

    public NetscapeDraftSpec(String[] datepatterns) {
        throw new RuntimeException("Stub!");
    }

    public NetscapeDraftSpec() {
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
