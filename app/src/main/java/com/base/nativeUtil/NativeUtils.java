package com.base.nativeUtil;

/**
 * 通过控制台命令，生成对应的.h文件
 * 先 cd app/src/main 这个时候先到指定的main路径下 执行javah -d jni -classpath (之前带你们查看的那个.class文件的路径)
 * 例：javah -d jni -classpath f:/Jelly/JellyBase/app/build/intermediates/classes/debug com.base.nativeUtil.NativeUtils
 */
public class NativeUtils {
    static {
        System.loadLibrary("testjni");
    }
    public static native String getNativeString();
}
