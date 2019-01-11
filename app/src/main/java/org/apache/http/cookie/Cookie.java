//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.cookie;

import java.util.Date;

/** @deprecated */
@Deprecated
public interface Cookie {
    String getName();

    String getValue();

    String getComment();

    String getCommentURL();

    Date getExpiryDate();

    boolean isPersistent();

    String getDomain();

    String getPath();

    int[] getPorts();

    boolean isSecure();

    int getVersion();

    boolean isExpired(Date var1);
}
