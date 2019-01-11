//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.entity;

import org.apache.http.HttpException;
import org.apache.http.HttpMessage;

/** @deprecated */
@Deprecated
public interface ContentLengthStrategy {
    int CHUNKED = -2;
    int IDENTITY = -1;

    long determineLength(HttpMessage var1) throws HttpException;
}
