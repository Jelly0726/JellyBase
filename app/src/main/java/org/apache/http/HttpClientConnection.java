package org.apache.http;

import java.io.IOException;

import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;

/** @deprecated */
@Deprecated
public interface HttpClientConnection extends HttpConnection {
    boolean isResponseAvailable(int var1) throws IOException;

    void sendRequestHeader(HttpRequest var1) throws HttpException, IOException;

    void sendRequestEntity(HttpEntityEnclosingRequest var1) throws HttpException, IOException;

    org.apache.http.HttpResponse receiveResponseHeader() throws HttpException, IOException;

    void receiveResponseEntity(HttpResponse var1) throws HttpException, IOException;

    void flush() throws IOException;
}
