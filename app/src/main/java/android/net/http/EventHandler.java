package android.net.http;

public interface EventHandler {
    int ERROR = -1;
    int ERROR_AUTH = -4;
    int ERROR_BAD_URL = -12;
    int ERROR_CONNECT = -6;
    int ERROR_FAILED_SSL_HANDSHAKE = -11;
    int ERROR_IO = -7;
    int ERROR_LOOKUP = -2;
    int ERROR_PROXYAUTH = -5;
    int ERROR_REDIRECT_LOOP = -9;
    int ERROR_TIMEOUT = -8;
    int ERROR_UNSUPPORTED_AUTH_SCHEME = -3;
    int ERROR_UNSUPPORTED_SCHEME = -10;
    int FILE_ERROR = -13;
    int FILE_NOT_FOUND_ERROR = -14;
    int OK = 0;
    int TOO_MANY_REQUESTS_ERROR = -15;

    void status(int var1, int var2, int var3, String var4);

    void headers(Headers var1);

    void data(byte[] var1, int var2);

    void endData();

    void certificate(SslCertificate var1);

    void error(int var1, String var2);

    boolean handleSslErrorRequest(SslError var1);
}
