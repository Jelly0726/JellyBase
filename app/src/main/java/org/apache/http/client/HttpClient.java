package org.apache.http.client;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/** @deprecated */
//@Deprecated
public interface HttpClient {
    HttpParams getParams();

    ClientConnectionManager getConnectionManager();

    HttpResponse execute(HttpUriRequest var1) throws IOException, ClientProtocolException;

    HttpResponse execute(HttpUriRequest var1, HttpContext var2) throws IOException, ClientProtocolException;

    HttpResponse execute(HttpHost var1, HttpRequest var2) throws IOException, ClientProtocolException;

    HttpResponse execute(HttpHost var1, HttpRequest var2, HttpContext var3) throws IOException, ClientProtocolException;

    <T> T execute(HttpUriRequest var1, ResponseHandler<? extends T> var2) throws IOException, ClientProtocolException;

    <T> T execute(HttpUriRequest var1, ResponseHandler<? extends T> var2, HttpContext var3) throws IOException, ClientProtocolException;

    <T> T execute(HttpHost var1, HttpRequest var2, ResponseHandler<? extends T> var3) throws IOException, ClientProtocolException;

    <T> T execute(HttpHost var1, HttpRequest var2, ResponseHandler<? extends T> var3, HttpContext var4) throws IOException, ClientProtocolException;
}
