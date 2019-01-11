package org.apache.http;

import java.io.IOException;
import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
public interface HttpRequestInterceptor {
    void process(HttpRequest var1, HttpContext var2) throws HttpException, IOException;
}
