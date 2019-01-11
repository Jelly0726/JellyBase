//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.cookie;

import java.util.List;
import org.apache.http.Header;

/** @deprecated */
@Deprecated
public interface CookieSpec {
    int getVersion();

    List<Cookie> parse(Header var1, CookieOrigin var2) throws MalformedCookieException;

    void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException;

    boolean match(Cookie var1, CookieOrigin var2);

    List<Header> formatCookies(List<Cookie> var1);

    Header getVersionHeader();
}
