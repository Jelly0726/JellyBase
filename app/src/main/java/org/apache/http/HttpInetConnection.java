package org.apache.http;


import java.net.InetAddress;

/** @deprecated */
@Deprecated
public interface HttpInetConnection extends HttpConnection {
    InetAddress getLocalAddress();

    int getLocalPort();

    InetAddress getRemoteAddress();

    int getRemotePort();
}
