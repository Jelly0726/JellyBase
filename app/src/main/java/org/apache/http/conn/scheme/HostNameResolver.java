//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetAddress;

public interface HostNameResolver {
    InetAddress resolve(String var1) throws IOException;
}
