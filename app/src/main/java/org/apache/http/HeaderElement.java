package org.apache.http;

/** @deprecated */
@Deprecated
public interface HeaderElement {
    String getName();

    String getValue();

    NameValuePair[] getParameters();

    NameValuePair getParameterByName(String var1);

    int getParameterCount();

    NameValuePair getParameter(int var1);
}