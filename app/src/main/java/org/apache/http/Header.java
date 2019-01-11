package org.apache.http;
/** @deprecated */
@Deprecated
public interface Header {
    String getName();

    String getValue();

    HeaderElement[] getElements() throws ParseException;
}
