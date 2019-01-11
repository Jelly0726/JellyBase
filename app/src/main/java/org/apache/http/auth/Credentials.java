package org.apache.http.auth;

import java.security.Principal;

/** @deprecated */
@Deprecated
public interface Credentials {
    Principal getUserPrincipal();

    String getPassword();
}
