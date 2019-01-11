//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.protocol;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import java.io.IOException;

/** @deprecated */
@Deprecated
public interface HttpRequestHandler {
    void handle(HttpRequest var1, HttpResponse var2, HttpContext var3) throws HttpException, IOException;
}
