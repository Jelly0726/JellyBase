package com.base.nativeUtil;

/**
 * 通过控制台命令，生成对应的.h文件
 * 先 cd app/src/main 这个时候先到指定的main路径下 执行javah -d jni -classpath 自己编译后的class文件的绝对路径
 * 例：javah -d jni -classpath f:/Jelly/JellyBase/app/build/intermediates/classes/debug com.base.nativeUtil.NativeUtils （注意debug后的空格）
 * 就会在main目录下生成jni文件夹，同时生成.h文件
 * 写一个test的C文件testjni.c同.h文件一样放到jni文件夹下
 * 打开app下对应的build.gradle 添加以下代码
 //ndk编译生成.so文件
 ndk {
 moduleName "huazict"         //生成的so名字
 abiFilters "armeabi", "armeabi-v7a", "x86"  //输出指定三种abi体系结构下的so库。
 }
 */
public class NativeUtils {
    static {
        System.loadLibrary("testjni");
    }
    //java调C中的方法都需要用native声明且方法名必须和c的方法名一样
    public static native String getNativeString();
}
