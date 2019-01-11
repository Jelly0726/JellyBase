package org.apache.http.client.params;

/** @deprecated */
@Deprecated
public interface ClientPNames {
    String ALLOW_CIRCULAR_REDIRECTS = "http.protocol.allow-circular-redirects";
    String CONNECTION_MANAGER_FACTORY = "http.connection-manager.factory-object";
    String CONNECTION_MANAGER_FACTORY_CLASS_NAME = "http.connection-manager.factory-class-name";
    String COOKIE_POLICY = "http.protocol.cookie-policy";
    String DEFAULT_HEADERS = "http.default-headers";
    String DEFAULT_HOST = "http.default-host";
    String HANDLE_AUTHENTICATION = "http.protocol.handle-authentication";
    String HANDLE_REDIRECTS = "http.protocol.handle-redirects";
    String MAX_REDIRECTS = "http.protocol.max-redirects";
    String REJECT_RELATIVE_REDIRECT = "http.protocol.reject-relative-redirect";
    String VIRTUAL_HOST = "http.virtual-host";
}

