package org.apache.http;

/** @deprecated */
@Deprecated
public interface StatusLine {
    ProtocolVersion getProtocolVersion();

    int getStatusCode();

    String getReasonPhrase();
}
