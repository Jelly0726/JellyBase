package org.apache.http;

import java.util.Iterator;

/** @deprecated */
@Deprecated
public interface HeaderElementIterator extends Iterator {
    boolean hasNext();

    HeaderElement nextElement();
}
