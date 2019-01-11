package org.apache.http;
import java.util.Iterator;

/** @deprecated */
@Deprecated
public interface TokenIterator extends Iterator {
    boolean hasNext();

    String nextToken();
}
