//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.auth;

import org.apache.http.Header;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.util.CharArrayBuffer;

/** @deprecated */
@Deprecated
public abstract class AuthSchemeBase implements AuthScheme {
    public AuthSchemeBase() {
        throw new RuntimeException("Stub!");
    }

    public void processChallenge(Header header) throws MalformedChallengeException {
        throw new RuntimeException("Stub!");
    }

    protected abstract void parseChallenge(CharArrayBuffer var1, int var2, int var3) throws MalformedChallengeException;

    public boolean isProxy() {
        throw new RuntimeException("Stub!");
    }
}
