package android.net.http;

import org.apache.http.HttpHost;

interface RequestFeeder {
    Request getRequest();

    Request getRequest(HttpHost var1);

    boolean haveRequest(HttpHost var1);

    void requeueRequest(Request var1);
}
