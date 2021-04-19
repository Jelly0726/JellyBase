//
// Created by Phoenix on 2017/6/25.
//
#include <android/log.h>
#include <string>

#ifndef OPENSSL_ENDORSE_H
#define OPENSSL_ENDORSE_H

#endif //OPENSSL_ENDORSE_H
#define BLOCK_SIZE 16
#define MAX_SIZE 65536
#if 1
#define TAG "cipher"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL, TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG ,__VA_ARGS__)
#else
#define LOGI(...)
#define LOGD(...)
#define LOGE(...)
#define LOGF(...)
#define LOGW(...)
#endif
using namespace std;
char *sha1OfApk(JNIEnv *env, jobject context);

jboolean verifySha1OfApk(JNIEnv *env, jobject context);

const char base[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
int base64_encode(const char* data,char *&out);
int base64_decode(const char* data,char *&out);
int is_base64(const char* data);
static char find_pos(char ch);
char *Jstring2CStr(JNIEnv *env, jstring jstr);
jstring CStr2Jstring(JNIEnv *env, const char *pat);
unsigned char *jstring_2unsigchar(JNIEnv *e, jstring pJstring);
jstring unsigchar2jstring(JNIEnv *e, unsigned char *pChar);
char *jByteArrayToChar(JNIEnv *env, jbyteArray buf);
jbyteArray charToJByteArray(JNIEnv *env, unsigned char *buf);
unsigned char *Bytes2HexStr( unsigned char *src);
unsigned char * hexStr2Bytes(string src);