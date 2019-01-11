//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.conn.routing;

import org.apache.http.HttpHost;

import java.net.InetAddress;

/** @deprecated */
@Deprecated
public interface RouteInfo {
    HttpHost getTargetHost();

    InetAddress getLocalAddress();

    int getHopCount();

    HttpHost getHopTarget(int var1);

    HttpHost getProxyHost();

    RouteInfo.TunnelType getTunnelType();

    boolean isTunnelled();

    RouteInfo.LayerType getLayerType();

    boolean isLayered();

    boolean isSecure();

    public static enum LayerType {
        LAYERED,
        PLAIN;

        private LayerType() {
        }
    }

    public static enum TunnelType {
        PLAIN,
        TUNNELLED;

        private TunnelType() {
        }
    }
}
