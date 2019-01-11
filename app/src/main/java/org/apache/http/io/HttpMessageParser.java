//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.io;

import org.apache.http.HttpException;
import org.apache.http.HttpMessage;

import java.io.IOException;

/** @deprecated */
@Deprecated
public interface HttpMessageParser {
    HttpMessage parse() throws IOException, HttpException;
}
