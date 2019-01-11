package org.apache.http;


/** @deprecated */
@Deprecated
public interface RequestLine {
    String getMethod();

    ProtocolVersion getProtocolVersion();

    String getUri();
}
