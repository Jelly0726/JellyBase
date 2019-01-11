package org.apache.http;


import java.io.IOException;

/** @deprecated */
@Deprecated
public interface HttpConnection {
    void close() throws IOException;

    boolean isOpen();

    boolean isStale();

    void setSocketTimeout(int var1);

    int getSocketTimeout();

    void shutdown() throws IOException;

    HttpConnectionMetrics getMetrics();
}
