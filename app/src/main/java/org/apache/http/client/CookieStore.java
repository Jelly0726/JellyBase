package org.apache.http.client;


import java.util.Date;
import java.util.List;
import org.apache.http.cookie.Cookie;

/** @deprecated */
@Deprecated
public interface CookieStore {
    void addCookie(Cookie var1);

    List<Cookie> getCookies();

    boolean clearExpired(Date var1);

    void clear();
}
