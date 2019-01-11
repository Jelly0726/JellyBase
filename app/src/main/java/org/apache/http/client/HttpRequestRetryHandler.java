package org.apache.http.client;

import java.io.IOException;
import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
public interface HttpRequestRetryHandler {
    boolean retryRequest(IOException var1, int var2, HttpContext var3);
}
