//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.conn;

import java.io.IOException;

/** @deprecated */
@Deprecated
public interface ConnectionReleaseTrigger {
    void releaseConnection() throws IOException;

    void abortConnection() throws IOException;
}
