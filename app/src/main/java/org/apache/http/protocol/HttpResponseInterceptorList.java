//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.protocol;

import org.apache.http.HttpResponseInterceptor;

import java.util.List;

/** @deprecated */
@Deprecated
public interface HttpResponseInterceptorList {
    void addResponseInterceptor(HttpResponseInterceptor var1);

    void addResponseInterceptor(HttpResponseInterceptor var1, int var2);

    int getResponseInterceptorCount();

    HttpResponseInterceptor getResponseInterceptor(int var1);

    void clearResponseInterceptors();

    void removeResponseInterceptorByClass(Class var1);

    void setInterceptors(List var1);
}
