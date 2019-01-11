//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.cookie;

/** @deprecated */
@Deprecated
public interface CookieAttributeHandler {
    void parse(SetCookie var1, String var2) throws MalformedCookieException;

    void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException;

    boolean match(Cookie var1, CookieOrigin var2);
}
