package com.base.nativeUtil;

public class NativeUtils {
    static {
        System.loadLibrary("testjni");
    }
    public static native String getNativeString();
}
