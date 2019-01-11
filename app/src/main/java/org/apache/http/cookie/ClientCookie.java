//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.cookie;


/** @deprecated */
@Deprecated
public interface ClientCookie extends Cookie {
    String COMMENTURL_ATTR = "commenturl";
    String COMMENT_ATTR = "comment";
    String DISCARD_ATTR = "discard";
    String DOMAIN_ATTR = "domain";
    String EXPIRES_ATTR = "expires";
    String MAX_AGE_ATTR = "max-age";
    String PATH_ATTR = "path";
    String PORT_ATTR = "port";
    String SECURE_ATTR = "secure";
    String VERSION_ATTR = "version";

    String getAttribute(String var1);

    boolean containsAttribute(String var1);
}
