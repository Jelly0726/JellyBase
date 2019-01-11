//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;

import java.util.List;
import java.util.NoSuchElementException;

/** @deprecated */
@Deprecated
public class BasicListHeaderIterator implements HeaderIterator {
    protected final List allHeaders;
    protected int currentIndex;
    protected String headerName;
    protected int lastIndex;

    public BasicListHeaderIterator(List headers, String name) {
        throw new RuntimeException("Stub!");
    }

    protected int findNext(int from) {
        throw new RuntimeException("Stub!");
    }

    protected boolean filterHeader(int index) {
        throw new RuntimeException("Stub!");
    }

    public boolean hasNext() {
        throw new RuntimeException("Stub!");
    }

    public Header nextHeader() throws NoSuchElementException {
        throw new RuntimeException("Stub!");
    }

    public final Object next() throws NoSuchElementException {
        throw new RuntimeException("Stub!");
    }

    public void remove() throws UnsupportedOperationException {
        throw new RuntimeException("Stub!");
    }
}
