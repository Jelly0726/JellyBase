//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.http.HttpInetConnection;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public class SocketHttpServerConnection extends AbstractHttpServerConnection implements HttpInetConnection {
    public SocketHttpServerConnection() {
        throw new RuntimeException("Stub!");
    }

    protected void assertNotOpen() {
        throw new RuntimeException("Stub!");
    }

    protected void assertOpen() {
        throw new RuntimeException("Stub!");
    }

    protected SessionInputBuffer createHttpDataReceiver(Socket socket, int buffersize, HttpParams params) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected SessionOutputBuffer createHttpDataTransmitter(Socket socket, int buffersize, HttpParams params) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void bind(Socket socket, HttpParams params) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected Socket getSocket() {
        throw new RuntimeException("Stub!");
    }

    public boolean isOpen() {
        throw new RuntimeException("Stub!");
    }

    public InetAddress getLocalAddress() {
        throw new RuntimeException("Stub!");
    }

    public int getLocalPort() {
        throw new RuntimeException("Stub!");
    }

    public InetAddress getRemoteAddress() {
        throw new RuntimeException("Stub!");
    }

    public int getRemotePort() {
        throw new RuntimeException("Stub!");
    }

    public void setSocketTimeout(int timeout) {
        throw new RuntimeException("Stub!");
    }

    public int getSocketTimeout() {
        throw new RuntimeException("Stub!");
    }

    public void shutdown() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }
}
