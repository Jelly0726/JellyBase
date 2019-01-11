//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.impl.auth;

/** @deprecated */
@Deprecated
public interface NTLMEngine {
    String generateType1Msg(String var1, String var2) throws NTLMEngineException;

    String generateType3Msg(String var1, String var2, String var3, String var4, String var5) throws NTLMEngineException;
}
