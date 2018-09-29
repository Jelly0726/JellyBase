//
// Created by Administrator on 2018-09-29.
//

#include "com_base_nativeUtil_NativeUtils.h"
/**
 * 上边的引用一定要和.h的文件名加后缀，
 * 下面的方法名一定要和.h文件中的方法名称一样
 */
JNIEXPORT jstring JNICALL Java_com_base_nativeUtil_NativeUtils_getNativeString
            (JNIEnv *env, jobject obj) {
        return (*env)->NewStringUTF(env, "小尼玛其乐无穷");
}
