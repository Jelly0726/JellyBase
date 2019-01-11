package org.apache.http;

import org.apache.http.util.CharArrayBuffer;

/** @deprecated */
@Deprecated
public interface FormattedHeader extends Header {
    CharArrayBuffer getBuffer();

    int getValuePos();
}
