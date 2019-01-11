//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.protocol;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/** @deprecated */
@Deprecated
public interface HttpExpectationVerifier {
    void verify(HttpRequest var1, HttpResponse var2, HttpContext var3) throws HttpException;
}
