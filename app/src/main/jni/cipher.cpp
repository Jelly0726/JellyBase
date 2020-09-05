//
// Created by Phoenix on 2017/6/25.
//
#include <jni.h>
#include <string>
#include <openssl/hmac.h>
#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <openssl/md5.h>
#include"zsd.h"
#include <assert.h>

//换成你自己的key值
const char *key = "758DA6688786C0D2C7";//"brLxpmIzzN6o7JDW";//"758DA6688786C0D2C7";//"QKBgQC0HjcCrWfRY8o4i2";
//换成你自己的RSAPubKey值
const char *RSAPubKey = "-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC0HjcCrWfRY8o4i2NwIvMJdP5l\nzuj7HOOZpjezE39a32fMjmztVtqLjso5MXjwwB5JiAfhOkFhVnNGPAmu0f5HOQly\nx88QgczokVrB73uj6HLUmX/SuFCbUh+YLJ4ZWp0g2Go6qwH2O5b3IQeENqTrIo4l\nrJTSLCzjnQ/QZYUuewIDAQAB\n-----END PUBLIC KEY-----\n";

//换成你自己的RSAPrivateKey值
const char *RSAPrivateKey = "-----BEGIN PRIVATE KEY-----\nMIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALQeNwKtZ9FjyjiL\nY3Ai8wl0/mXO6Psc45mmN7MTf1rfZ8yObO1W2ouOyjkxePDAHkmIB+E6QWFWc0Y8\nCa7R/kc5CXLHzxCBzOiRWsHve6PoctSZf9K4UJtSH5gsnhlanSDYajqrAfY7lvch\nB4Q2pOsijiWslNIsLOOdD9BlhS57AgMBAAECgYAC82fFTx/0nzo7OOq1IJgeCeD3\nvARhxh8NxQUD6wgwGJmJEWBEIc3MxtcV9B3eG9ejLsD/oJuyQ4n57EE1sFJbwziB\nvTLwmL+yLEpghtc/J2Xo1Y22E2Czu+VI0d/WUy3MN2I4a601xxA6rg1tRvPSftDW\n49fDCAhUb5yYLZgo0QJBAPzYRxsoPtoR8LbarPBuz/dwT9U3kiGT5yXVzjv0wkmp\nHblFBi2JY37f2TJ8moLi9hWAUHwEbSrjaxZM/g5GhhkCQQC2XZvPJHGPf8aAKUFz\nx0kZDUW1m436xiKVwHBAJGeYCYk87NJwNOiCN7HafRWkjbZtx2fS3M2lzPmxWqzw\n4COzAkAZpvOn3LBrvXA3jP4IsqVkzD89OZMY1wGXhBaVXKKtiHvchRU4X3z5rUpC\n5gNjDhW7XrZLrsNIm6QMsikAV8VZAkBiZCjvXstCT/8qIJgmvkvLD2Uf8bhtp777\nKuOlR774wZRg4ak8Tt9velsj9b7alHbrzd1PYEA4B1pkfPa300aPAkBbr6fDTKhC\nsckhQPDdrLBxtjGAvJ0Rzhk54FhHa5FLX2jdDPttH/q1QZH4w1TGV68hWogCcfsE\nBha/h6Ag4H5V\n-----END PRIVATE KEY-----\n";
//非法调用，Apk签名校验不通过！
const char *sha1_sign_err = "非法调用，Apk签名校验不通过！";

/**
 * HmacSHA1加密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
encodeByHmacSHA1(JNIEnv *env,jobject obj,jobject context, jbyteArray src_) {
//    LOGD("HmacSHA1加密->HMAC: Hash-based Message Authentication Code，即基于Hash的消息鉴别码");
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1加密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray,0,strlen(sha1_sign_err),(jbyte *)sha1_sign_err);
        return byteArray;
    }
    const char *key = "jellyApp@22383243*335457968";
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    unsigned int result_len;
    unsigned char result[EVP_MAX_MD_SIZE];
    char buff[EVP_MAX_MD_SIZE];
    char hex[EVP_MAX_MD_SIZE];

//    LOGD("HmacSHA1加密->调用函数进行哈希运算");
    HMAC(EVP_sha1(), key, strlen(key), (unsigned char *) src, src_Len, result, &result_len);

    strcpy(hex, "");
//    LOGD("HmacSHA1加密->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != result_len; i++) {
        sprintf(buff, "%02x", result[i]);
        strcat(hex, buff);
    }
    LOGD("HmacSHA1加密->%s", hex);

    LOGD("HmacSHA1加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray signature = env->NewByteArray(strlen(hex));
//    LOGD("HmacSHA1加密->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(signature, 0, strlen(hex), (jbyte *) hex);

    return signature;
}
/**
 * SHA1加密
 */
extern "C" JNIEXPORT jstring JNICALL
encodeBySHA1(JNIEnv *env, jobject obj,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA1加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcs = env->GetByteArrayElements(src_, NULL);//jbyteArray转jbyte
    jsize src_Lens = env->GetArrayLength(src_);//获取jbyteArray的长度
//    LOGD("SHA1加密->源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars,0,src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("SHA1加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens+ 1 +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA1加密->源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA_DIGEST_LENGTH];
    char hex[SHA_DIGEST_LENGTH * 2+1];
    unsigned char digest[SHA_DIGEST_LENGTH];

//    SHA1((unsigned char *)src, src_Len, digest);

    SHA_CTX ctx;
    SHA1_Init(&ctx);
//    LOGD("SHA1加密->正在进行SHA1哈希运算");
    SHA1_Update(&ctx, src, src_Len);
    SHA1_Final(digest, &ctx);

    OPENSSL_cleanse(&ctx, sizeof(ctx));

    strcpy(hex, "");
//    LOGD("SHA1加密->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
    }
    LOGD("SHA1加密->%s", hex);
    LOGD("SHA1加密->释放内存");
    free(src);//malloc和free要配套使用
    delete []chars;//new/delete、new[]/delete[] 要配套使用总是没错的

    return env->NewStringUTF(hex);
}
/**
 * SHA224加密
 */
extern "C" JNIEXPORT jstring JNICALL
encodeBySHA224(JNIEnv *env,jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA224加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcs = env->GetByteArrayElements(src_, NULL);//jbyteArray转jbyte
    jsize src_Lens = env->GetArrayLength(src_);//获取jbyteArray的长度
//    LOGD("SHA224加密->源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars,0,src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("SHA224加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens+ 1 +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA224加密->源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA224_DIGEST_LENGTH];
    char hex[SHA224_DIGEST_LENGTH * 2+1];
    unsigned char digest[SHA224_DIGEST_LENGTH];

//    SHA224((unsigned char *)src, src_Len, digest);

    SHA256_CTX ctx;
    SHA224_Init(&ctx);
//    LOGD("SHA224加密->正在进行SHA224哈希运算");
    SHA224_Update(&ctx, src, src_Len);
    SHA224_Final(digest, &ctx);

    OPENSSL_cleanse(&ctx, sizeof(ctx));

    strcpy(hex, "");
    LOGD("SHA224加密->把哈希值按%%02x格式定向到缓冲区->%d",sizeof(digest));
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
        LOGD("SHA224加密->把哈希值按%%02x格式定向到缓冲区->%d", i);
    }
    LOGD("SHA224加密->%s", hex);
    LOGD("SHA224加密->释放内存");
    free(src);//malloc和free要配套使用
    delete []chars;//new/delete、new[]/delete[] 要配套使用总是没错的

    return env->NewStringUTF(hex);
}
/**
 * SHA256加密
 */
extern "C" JNIEXPORT jstring JNICALL
encodeBySHA256(JNIEnv *env,jobject obj,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA256加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcs = env->GetByteArrayElements(src_, NULL);//jbyteArray转jbyte
    jsize src_Lens = env->GetArrayLength(src_);//获取jbyteArray的长度
//    LOGD("SHA256加密->源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars,0,src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("SHA256加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens+ 1 +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA256加密->源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA256_DIGEST_LENGTH];
    char hex[SHA256_DIGEST_LENGTH * 2+1];
    unsigned char digest[SHA256_DIGEST_LENGTH];

//    SHA256((unsigned char *)src, src_Len, digest);

    SHA256_CTX ctx;
    SHA256_Init(&ctx);
//    LOGD("SHA256加密->正在进行SHA256哈希运算");
    SHA256_Update(&ctx, src, src_Len);
    SHA256_Final(digest, &ctx);

    OPENSSL_cleanse(&ctx, sizeof(ctx));

    strcpy(hex, "");
//    LOGD("SHA256加密->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
    }
    LOGD("SHA256加密->%s", hex);
    LOGD("SHA256加密->释放内存");
    free(src);//malloc和free要配套使用
    delete []chars;//new/delete、new[]/delete[] 要配套使用总是没错的
    return env->NewStringUTF(hex);
}
/**
 * SHA384加密
 */
extern "C" JNIEXPORT jstring JNICALL
encodeBySHA384(JNIEnv *env,jobject obj,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA384加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcs = env->GetByteArrayElements(src_, NULL);//jbyteArray转jbyte
    jsize src_Lens = env->GetArrayLength(src_);//获取jbyteArray的长度
//    LOGD("SHA384加密->源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars,0,src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("SHA384加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens+ 1 +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA384加密->源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA384_DIGEST_LENGTH];
    char hex[SHA384_DIGEST_LENGTH * 2+1];
    unsigned char digest[SHA384_DIGEST_LENGTH];

//    SHA384((unsigned char *)src, src_Len, digest);

    SHA512_CTX ctx;
    SHA384_Init(&ctx);
//    LOGD("SHA384加密->正在进行SHA384哈希运算");
    SHA384_Update(&ctx, src, src_Len);
    SHA384_Final(digest, &ctx);

    OPENSSL_cleanse(&ctx, sizeof(ctx));

    strcpy(hex, "");
//    LOGD("SHA384加密->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
    }
    LOGD("SHA384加密->%s", hex);
    LOGD("SHA384加密->释放内存");
    free(src);//malloc和free要配套使用
    delete []chars;//new/delete、new[]/delete[] 要配套使用总是没错的

    return env->NewStringUTF(hex);
}
/**
 * SHA512加密
 */
extern "C" JNIEXPORT jstring JNICALL
encodeBySHA512(JNIEnv *env,jobject obj,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA512加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcs = env->GetByteArrayElements(src_, NULL);//jbyteArray转jbyte
    jsize src_Lens = env->GetArrayLength(src_);//获取jbyteArray的长度
//    LOGD("SHA512加密->源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars,0,src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("SHA512加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens+ 1 +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA512加密->源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA512_DIGEST_LENGTH];
    char hex[SHA512_DIGEST_LENGTH * 2+1];
    unsigned char digest[SHA512_DIGEST_LENGTH];

//    SHA512((unsigned char *)src, src_Len, digest);

    SHA512_CTX ctx;
    SHA512_Init(&ctx);
//    LOGD("SHA512加密->正在进行SHA512哈希运算");
    SHA512_Update(&ctx, src, src_Len);
    SHA512_Final(digest, &ctx);

    OPENSSL_cleanse(&ctx, sizeof(ctx));

    strcpy(hex, "");
//    LOGD("SHA512加密->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
    }
    LOGD("SHA512加密->%s", hex);
    LOGD("SHA512加密->释放内存");
    free(src);//malloc和free要配套使用
    delete []chars;//new/delete、new[]/delete[] 要配套使用总是没错的

    return env->NewStringUTF(hex);
}
/**
 * AES加密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
encodeByAES(JNIEnv *env, jobject obj,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("AES加密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray,0,strlen(sha1_sign_err),(jbyte *)sha1_sign_err);
        return byteArray;
    }
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(key));
    env->SetByteArrayRegion(keys_, 0, strlen(key), (jbyte*)key );

//    LOGD("AES加密->对称密钥，也就是说加密和解密用的是同一个密钥");
    const unsigned char *iv = (const unsigned char *) "gjdljgaj748564df";
    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int outlen = 0, cipherText_len = 0;

    unsigned char *out = (unsigned char *) malloc((src_Len / 16 + 1) * 16);
    //清空内存空间
    memset(out, 0, (src_Len / 16 + 1) * 16);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);
//    LOGD("AES加密->指定加密算法，初始化加密key/iv");
    EVP_EncryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, (const unsigned char *) keys, iv);
//    LOGD("AES加密->对数据进行加密运算");
    EVP_EncryptUpdate(&ctx, out, &outlen, (const unsigned char *) src, src_Len);
    cipherText_len = outlen;

//    LOGD("AES加密->结束加密运算");
    EVP_EncryptFinal_ex(&ctx, out + outlen, &outlen);
    cipherText_len += outlen;

//    LOGD("AES加密->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);

    LOGD("AES加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(cipherText_len);
//    LOGD("AES->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_len, (jbyte *) out);
    LOGD("AES加密->释放内存");
    free(out);

    return cipher;
}
/**
 * AES解密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
decodeByAES(JNIEnv *env,jobject obj,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("AES解密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray,0,strlen(sha1_sign_err),(jbyte *)sha1_sign_err);
        return byteArray;
    }
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(key));
    env->SetByteArrayRegion(keys_, 0, strlen(key), (jbyte*)key );

//    LOGD("AES解密->对称密钥，也就是说加密和解密用的是同一个密钥");
    const unsigned char *iv = (const unsigned char *) "gjdljgaj748564df";
    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int outlen = 0, plaintext_len = 0;

    unsigned char *out  = (unsigned char *) malloc(src_Len);
    memset(out, 0, src_Len);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);
//    LOGD("AES解密->指定解密算法，初始化解密key/iv");
    EVP_DecryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, (const unsigned char *) keys, iv);
//    LOGD("AES解密->对数据进行解密运算");
    EVP_DecryptUpdate(&ctx, out, &outlen, (const unsigned char *) src, src_Len);
    plaintext_len = outlen;

//    LOGD("AES解密->结束解密运算");
    EVP_DecryptFinal_ex(&ctx, out + outlen, &outlen);
    plaintext_len += outlen;

//    LOGD("AES解密->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);

    LOGD("AES解密->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    if (plaintext_len<=0){
        LOGD("AES解密->解密失败！");
        LOGD("AES解密->释放内存");
        free(out);
        char *decode_err= "AES解密->解密失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(decode_err));
        env->SetByteArrayRegion(byteArray,0,strlen(decode_err),(jbyte *)decode_err);
        return byteArray;
    }
    jbyteArray cipher = env->NewByteArray(plaintext_len);
//    LOGD("AES->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, plaintext_len, (jbyte *) out);
    LOGD("AES解密->释放内存");
    free(out);

    return cipher;
}
/**
 * RSA公钥加密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
encodeByRSAPubKey(JNIEnv *env,jobject obj,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA公钥解密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray,0,strlen(sha1_sign_err),(jbyte *)sha1_sign_err);
        return byteArray;
    }
//    LOGD("RSA公钥解密->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(RSAPubKey));
    env->SetByteArrayRegion(keys_, 0, strlen(RSAPubKey), (jbyte*)RSAPubKey );

    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, cipherText_offset = 0, desText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA公钥解密->从内存读取RSA公钥");
    if ((keybio = BIO_new_mem_buf(keys, -1)) == NULL)
    {
        LOGD("RSA公钥解密>BIO_new_mem_buf publicKey error\n");
        char *err= "RSA公钥解密->从内存读取RSA公钥失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
        return byteArray;
    }
//    LOGD("RSA->从bio结构中得到RSA结构");
    if ((rsa = PEM_read_bio_RSA_PUBKEY(keybio, NULL, NULL, NULL)) == NULL)
    {
        LOGD("RSA公钥解密->PEM_read_bio_RSA_PUBKEY error\n");
        char *err= "RSA公钥解密->从bio结构中得到RSA结构失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
        return byteArray;
    }
    LOGD("RSA公钥解密->释放BIO");
    BIO_free_all(keybio);

    int flen = RSA_size(rsa);
    desText_len = flen * (src_Len / (flen - 11) + 1);

    unsigned char *srcOrigin = (unsigned char *) malloc(src_Len);
    unsigned char *cipherText = (unsigned char *) malloc(flen);
    unsigned char *desText = (unsigned char *) malloc(desText_len);
    memset(desText, 0, desText_len);

    memset(srcOrigin, 0, src_Len);
    memcpy(srcOrigin, src, src_Len);

//    LOGD("RSA公钥解密->对数据进行公钥加密运算");
    //RSA_PKCS1_PADDING最大加密长度：128-11；RSA_NO_PADDING最大加密长度：128
    for (int i = 0; i <= src_Len / (flen - 11); i++) {
        src_flen = (i == src_Len / (flen - 11)) ? src_Len % (flen - 11) : flen - 11;
        if (src_flen == 0) {
            break;
        }

        memset(cipherText, 0, flen);
//        src_flen: 填充方式加密长度 srcOrigin + src_offset: 要加密信息 cipherText: 加密后的信息 rsa 秘钥 RSA_PKCS1_PADDING: 填充方式
        ret = RSA_public_encrypt(src_flen, srcOrigin + src_offset, cipherText, rsa, RSA_PKCS1_PADDING);
        if(ret<0){
            LOGD("RSA公钥解密->加密失败");
            char *err= "RSA公钥解密->加密失败！";
            jbyteArray byteArray = env->NewByteArray(strlen(err));
            env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
            return byteArray;
        }
        memcpy(desText + cipherText_offset, cipherText, ret);
        cipherText_offset += ret;
        src_offset += src_flen;
    }

    RSA_free(rsa);
//    LOGD("RSA->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA公钥解密->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(cipherText_offset);
//    LOGD("RSA->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_offset, (jbyte *) desText);
    LOGD("RSA公钥解密->释放内存");
    free(srcOrigin);
    free(cipherText);
    free(desText);

    return cipher;
}
/**
 * RSA私钥解密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
decodeByRSAPrivateKey(JNIEnv *env, jobject obj,jobject context,jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA私钥解密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray,0,strlen(sha1_sign_err),(jbyte *)sha1_sign_err);
        return byteArray;
    }
//    LOGD("RSA私钥解密->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(RSAPrivateKey));
    env->SetByteArrayRegion(keys_, 0, strlen(RSAPrivateKey), (jbyte*)RSAPrivateKey );

    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, plaintext_offset = 0, descText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA私钥解密->从内存读取RSA私钥");
    if ((keybio = BIO_new_mem_buf(keys, -1)) == NULL)
    {
        LOGD("RSA私钥解密->BIO_new_mem_buf privateKey error\n");
        char *err= "RSA私钥解密->从内存读取RSA私钥失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
        return byteArray;
    }

//    LOGD("RSA私钥解密->从bio结构中得到RSA结构");
//    OpenSSL_add_all_algorithms();//密钥有经过口令加密需要这个函数
//    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, (char *)PASS)) == NULL)
    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, NULL)) == NULL)
    {
        LOGD("RSA私钥解密->PEM_read_RSAPrivateKey error\n");
        char *err= "RSA私钥解密->从bio结构中得到RSA结构失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
        return byteArray;
    }
    LOGD("RSA私钥解密->释放BIO");
    BIO_free_all(keybio);

    int flen = RSA_size(rsa);
    descText_len = (flen - 11) * (src_Len / flen + 1);

    unsigned char *srcOrigin = (unsigned char *) malloc(src_Len);
    unsigned char *plaintext = (unsigned char *) malloc(flen - 11);
    unsigned char *desText = (unsigned char *) malloc(descText_len);
    memset(desText, 0, descText_len);

    memset(srcOrigin, 0, src_Len);
    memcpy(srcOrigin, src, src_Len);

//    LOGD("RSA私钥解密->对数据进行私钥解密运算");
    //一次性解密数据最大字节数RSA_size
    for (int i = 0; i <= src_Len / flen; i++) {
        src_flen = (i == src_Len / flen) ? src_Len % flen : flen;
        if (src_flen == 0) {
            break;
        }
        memset(plaintext, 0, flen - 11);
//      src_flen   解密密钥长度 srcOrigin + src_offset 要解密信息  plaintext 解密后的信息 rsa 密钥 RSA_PKCS1_PADDING 填充方式
        ret = RSA_private_decrypt(src_flen, srcOrigin + src_offset, plaintext, rsa, RSA_PKCS1_PADDING);
        if(ret<0){
            LOGD("RSA私钥解密->解密失败");
            char *err= "RSA私钥解密->解密失败！";
            jbyteArray byteArray = env->NewByteArray(strlen(err));
            env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
            return byteArray;
        }
        memcpy(desText + plaintext_offset, plaintext, ret);
        plaintext_offset += ret;
        src_offset += src_flen;
    }
    LOGD("RSA私钥解密->CRYPTO_cleanup_all_ex_data");
    RSA_free(rsa);
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA私钥解密->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(plaintext_offset);
//    LOGD("RSA->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, plaintext_offset, (jbyte *) desText);
    LOGD("RSA私钥解密->释放内存");
    free(srcOrigin);
    free(plaintext);
    free(desText);

    return cipher;
}
/**
 * RSA私钥加密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
encodeByRSAPrivateKey(JNIEnv *env,jobject obj,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA私钥加密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray,0,strlen(sha1_sign_err),(jbyte *)sha1_sign_err);
        return byteArray;
    }
//    LOGD("RSA私钥加密->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(RSAPrivateKey));
    env->SetByteArrayRegion(keys_, 0, strlen(RSAPrivateKey), (jbyte*)RSAPrivateKey );

    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, cipherText_offset = 0, desText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA私钥加密->从内存读取RSA私钥");
    if ((keybio = BIO_new_mem_buf(keys, -1)) == NULL)
    {
        LOGD("RSA私钥加密->BIO_new_mem_buf privateKey error\n");
        char *err= "RSA私钥加密->从内存读取RSA私钥失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
        return byteArray;
    }
//    LOGD("RSA私钥加密->从bio结构中得到RSA结构");
    //    OpenSSL_add_all_algorithms();//密钥有经过口令加密需要这个函数
//    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, (char *)PASS)) == NULL)
    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, NULL)) == NULL)
    {
        LOGD("RSA私钥加密->PEM_read_RSAPrivateKey error\n");
        char *err= "RSA私钥加密->从bio结构中得到RSA结构失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
        return byteArray;
    }
    LOGD("RSA私钥加密->释放BIO");
    BIO_free_all(keybio);

    int flen = RSA_size(rsa);
    desText_len = flen * (src_Len / (flen - 11) + 1);

    unsigned char *srcOrigin = (unsigned char *) malloc(src_Len);
    unsigned char *cipherText = (unsigned char *) malloc(flen);
    unsigned char *desText = (unsigned char *) malloc(desText_len);
    memset(desText, 0, desText_len);

    memset(srcOrigin, 0, src_Len);
    memcpy(srcOrigin, src, src_Len);

//    LOGD("RSA私钥加密->对数据进行私钥加密运算");
    //RSA_PKCS1_PADDING最大加密长度：128-11；RSA_NO_PADDING最大加密长度：128
    for (int i = 0; i <= src_Len / (flen - 11); i++) {
        src_flen = (i == src_Len / (flen - 11)) ? src_Len % (flen - 11) : flen - 11;
        if (src_flen == 0) {
            break;
        }

        memset(cipherText, 0, flen);
        ret = RSA_private_encrypt(src_flen, srcOrigin + src_offset, cipherText, rsa, RSA_PKCS1_PADDING);
        if (ret<0){
            LOGD("RSA私钥加密->加密失败");
            char *err= "RSA私钥加密->加密失败！";
            jbyteArray byteArray = env->NewByteArray(strlen(err));
            env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
            return byteArray;
        }
        memcpy(desText + cipherText_offset, cipherText, ret);
        cipherText_offset += ret;
        src_offset += src_flen;
    }

    RSA_free(rsa);
//    LOGD("RSA私钥加密->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA私钥加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(cipherText_offset);
//    LOGD("RSA->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_offset, (jbyte *) desText);
    LOGD("RSA私钥加密->释放内存");
    free(srcOrigin);
    free(cipherText);
    free(desText);

    return cipher;
}
/**
 * RSA公钥解密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
decodeByRSAPubKey(JNIEnv *env, jobject obj,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA公钥解密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray,0,strlen(sha1_sign_err),(jbyte *)sha1_sign_err);
        return byteArray;
    }
//    LOGD("RSA公钥解密->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(RSAPubKey));
    env->SetByteArrayRegion(keys_, 0, strlen(RSAPubKey), (jbyte*)RSAPubKey );

    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, plaintext_offset = 0, desText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA公钥解密->从内存读取RSA公钥");
    if ((keybio = BIO_new_mem_buf(keys, -1)) == NULL)
    {
        LOGD("RSA公钥解密->BIO_new_mem_buf publicKey error\n");
        char *err= "RSA公钥解密->从内存读取RSA公钥失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
        return byteArray;
    }
//    LOGD("RSA公钥解密->从bio结构中得到RSA结构");
    if ((rsa = PEM_read_bio_RSA_PUBKEY(keybio, NULL, NULL, NULL)) == NULL)
    {
        LOGD("RSA公钥解密->PEM_read_bio_RSA_PUBKEY error\n");
        char *err= "RSA公钥解密->从bio结构中得到RSA结构失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
        return byteArray;
    }
    LOGD("RSA公钥解密->释放BIO");
    BIO_free_all(keybio);

    int flen = RSA_size(rsa);
    desText_len = (flen - 11) * (src_Len / flen + 1);

    unsigned char *srcOrigin = (unsigned char *) malloc(src_Len);
    unsigned char *plaintext = (unsigned char *) malloc(flen - 11);
    unsigned char *desText = (unsigned char *) malloc(desText_len);
    memset(desText, 0, desText_len);

    memset(srcOrigin, 0, src_Len);
    memcpy(srcOrigin, src, src_Len);

//    LOGD("RSA公钥解密->对数据进行公钥解密运算");
    //一次性解密数据最大字节数RSA_size
    for (int i = 0; i <= src_Len / flen; i++) {
        src_flen = (i == src_Len / flen) ? src_Len % flen : flen;
        if (src_flen == 0) {
            break;
        }
        memset(plaintext, 0, flen - 11);
        ret = RSA_public_decrypt(src_flen, srcOrigin + src_offset, plaintext, rsa, RSA_PKCS1_PADDING);
        if(ret<0){
            LOGD("RSA公钥解密->解密失败！");
            char *err= "RSA公钥解密->解密失败！";
            jbyteArray byteArray = env->NewByteArray(strlen(err));
            env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
            return byteArray;
        }
        memcpy(desText + plaintext_offset, plaintext, ret);
        plaintext_offset += ret;
        src_offset += src_flen;
    }

    RSA_free(rsa);
//    LOGD("RSA->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(plaintext_offset);
//    LOGD("RSA->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, plaintext_offset, (jbyte *) desText);
    LOGD("RSA->释放内存");
    free(srcOrigin);
    free(plaintext);
    free(desText);

    return cipher;
}
/**
 * RSA私钥签名
 */
extern "C" JNIEXPORT jbyteArray JNICALL
signByRSAPrivateKey(JNIEnv *env,jobject obj, jobject context,  jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA签名->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray,0,strlen(sha1_sign_err),(jbyte *)sha1_sign_err);
        return byteArray;
    }
//    LOGD("RSA签名->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(RSAPrivateKey));
    env->SetByteArrayRegion(keys_, 0, strlen(RSAPrivateKey), (jbyte*)RSAPrivateKey );

    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    unsigned int siglen = 0;
    unsigned char digest[SHA_DIGEST_LENGTH];

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA签名->从内存读取RSA公钥");
    if ((keybio = BIO_new_mem_buf(keys, -1)) == NULL)
    {
        LOGD("RSA签名->BIO_new_mem_buf publicKey error\n");
        char *err= "RSA签名->从内存读取RSA公钥失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
        return byteArray;
    }
//    LOGD("RSA->从bio结构中得到RSA结构");
//    OpenSSL_add_all_algorithms();//密钥有经过口令加密需要这个函数
//    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, (char *)PASS)) == NULL)
    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, NULL)) == NULL)
    {
        LOGD("RSA签名->PEM_read_RSAPrivateKey error\n");
        char *err= "RSA签名->从bio结构中得到RSA结构失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
        return byteArray;
    }
    LOGD("RSA签名->释放BIO");
    BIO_free_all(keybio);

    unsigned char *sign = (unsigned char *) malloc(129);
    memset(sign, 0, 129);

//    LOGD("RSA签名->对数据进行摘要运算");
    SHA1((const unsigned char *) src, src_Len, digest);
//    LOGD("RSA签名->对摘要进行RSA私钥加密");
    RSA_sign(NID_sha1, digest, SHA_DIGEST_LENGTH, sign, &siglen, rsa);

    RSA_free(rsa);
//    LOGD("RSA签名->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA签名->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(siglen);
//    LOGD("RSA->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, siglen, (jbyte *) sign);
    LOGD("RSA签名->释放内存");
    free(sign);

    return cipher;
}
/**
 * RSA公钥校验
 */
extern "C" JNIEXPORT jint JNICALL
verifyByRSAPubKey(JNIEnv *env,jobject obj,jobject context, jbyteArray src_, jbyteArray sign_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA签名校验->apk-sha1值验证不通过");
        return -1;
    }
//    LOGD("RSA签名校验->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(RSAPubKey));
    env->SetByteArrayRegion(keys_, 0, strlen(RSAPubKey), (jbyte*)RSAPubKey );

    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jbyte *sign = env->GetByteArrayElements(sign_, NULL);

    jsize src_Len = env->GetArrayLength(src_);
    jsize siglen = env->GetArrayLength(sign_);

    int ret;
    unsigned char digest[SHA_DIGEST_LENGTH];

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA->从字符串读取RSA公钥");
    if ((keybio = BIO_new_mem_buf(keys, -1)) == NULL)
    {
        LOGD("RSA签名校验->BIO_new_mem_buf publicKey error\n");
        return -1;
    }
//    LOGD("RSA->从bio结构中得到RSA结构");
    if ((rsa = PEM_read_bio_RSA_PUBKEY(keybio, NULL, NULL, NULL)) == NULL)
    {
        LOGD("RSA签名校验->PEM_read_bio_RSA_PUBKEY error\n");
        return -1;
    }
    LOGD("RSA签名校验->释放BIO");
    BIO_free_all(keybio);

//    LOGD("RSA签名校验->对数据进行摘要运算");
    SHA1((const unsigned char *) src, src_Len, digest);
//    LOGD("RSA签名校验->对摘要进行RSA公钥验证");
    ret = RSA_verify(NID_sha1, digest, SHA_DIGEST_LENGTH, (const unsigned char *) sign, siglen, rsa);

    RSA_free(rsa);
//    LOGD("RSA签名校验->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA签名校验->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);
    env->ReleaseByteArrayElements(sign_, sign, 0);

    return ret;
}
/**
 * xOr加解密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
xOr(JNIEnv *env,jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("XOr加解密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray,0,strlen(sha1_sign_err),(jbyte *)sha1_sign_err);
        return byteArray;
    }
//    LOGD("XOR加解密->异或加解密: 相同为假，不同为真");
    const char keys[] = "jelly20180829";
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    char *chs = (char *) malloc(src_Len);
    memset(chs, 0, src_Len);
    memcpy(chs, src, src_Len);

//    LOGD("XOR加解密->对数据进行异或运算");
    for (int i = 0; i < src_Len; i++) {
        *chs = *chs ^ keys[i % strlen(keys)];
        chs++;
    }
    chs = chs - src_Len;

    LOGD("XOR加解密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, src, 0);
    if (src_Len<=0){
        LOGD("XOR加解密->加解密失败！");
        LOGD("XOR加解密->释放内存");
        free(chs);
        char *err= "XOR加解密->加解密失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray,0,strlen(err),(jbyte *)err);
        return byteArray;
    }
    jbyteArray cipher = env->NewByteArray(src_Len);
//    LOGD("XOR->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, src_Len, (const jbyte *) chs);
    LOGD("XOR加解密->释放内存");
    free(chs);

    return cipher;
}
/**
 * MD5加密
 */
extern "C" JNIEXPORT jstring JNICALL
md5(JNIEnv *env,jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("MD5加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcs = env->GetByteArrayElements(src_, NULL);//jbyteArray转jbyte
    jsize src_Lens = env->GetArrayLength(src_);//获取jbyteArray的长度
//    LOGD("源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars,0,src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("MD5加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens+1 +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================
//    LOGD("MD5加密->信息摘要算法第五版");

    char buff[3] = {'\0'};
    char hex[33] = {'\0'};
    unsigned char digest[MD5_DIGEST_LENGTH];

//    MD5((const unsigned char *) src, src_Len, digest);

    MD5_CTX ctx;
    MD5_Init(&ctx);
//    LOGD("MD5加密->进行MD5信息摘要运算");
    MD5_Update(&ctx, src, src_Len);
    MD5_Final(digest, &ctx);

    strcpy(hex, "");
//    LOGD("MD5加密->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
    }
    LOGD("MD5加密->%s", hex);
    LOGD("MD5加密->释放内存");
    free(src);//malloc和free要配套使用
    delete []chars;//new/delete、new[]/delete[] 要配套使用总是没错的

    return env->NewStringUTF(hex);
}
/**
 * 获取Apk的SHA1值
 */
extern "C" JNIEXPORT jstring JNICALL
getSha1OfApk(JNIEnv *env,jobject obj,jobject context) {
    return env->NewStringUTF(sha1OfApk(env, context));
}
/**
 * 校验Apk的SHA1值
 */
extern "C" JNIEXPORT jboolean JNICALL
validateSha1OfApk(JNIEnv *env,jobject obj,jobject context) {
    return verifySha1OfApk(env, context);
}
/*需要注册的函数列表，放在JNINativeMethod 类型的数组中，
以后如果需要增加函数，只需在这里添加就行了
参数：
1.java中用native关键字声明的函数名
2.签名（传进来参数类型和返回值类型的说明） 
3.C/C++中对应函数的函数名（地址）

签名对应关系入下表
符号    C/C++           java
V	    void	        void
Z	    jboolean	    boolean
I	    jint	        int
J	    jlong	        long
D	    jdouble	        double
F	    jfloat	        float
B	    jbyte	        byte
C	    jchar	        char
S	    jshort	        short
[Z	    jbooleanArray	boolean[]
[I	    jintArray	    int[]
[J	    jlongArray	    long[]
[D	    jdoubleArray	double[]
[F	    jfloatArray	    float[]
[B	    jbyteArray	    byte[]
[C	    jcharArray	    char[]
[S	    jshortArray	    short[]
特殊的String
Ljava/lang/String;	jstring	    String
L完整包名加类名;	jobject	    class

*/
static JNINativeMethod getMethods[] = {
        {"encodeByHmacSHA1","(Landroid/content/Context;[B)[B",(void*)encodeByHmacSHA1},
        {"encodeBySHA1","(Landroid/content/Context;[B)Ljava/lang/String;",(void*)encodeBySHA1},
        {"encodeBySHA224","(Landroid/content/Context;[B)Ljava/lang/String;",(void*)encodeBySHA224},
        {"encodeBySHA256","(Landroid/content/Context;[B)Ljava/lang/String;",(void*)encodeBySHA256},
        {"encodeBySHA384","(Landroid/content/Context;[B)Ljava/lang/String;",(void*)encodeBySHA384},
        {"encodeBySHA512","(Landroid/content/Context;[B)Ljava/lang/String;",(void*)encodeBySHA512},
        {"encodeByAES","(Landroid/content/Context;[B)[B",(void*)encodeByAES},
        {"decodeByAES","(Landroid/content/Context;[B)[B",(void*)decodeByAES},
        {"encodeByRSAPubKey","(Landroid/content/Context;[B)[B",(void*)encodeByRSAPubKey},
        {"decodeByRSAPrivateKey","(Landroid/content/Context;[B)[B",(void*)decodeByRSAPrivateKey},
        {"encodeByRSAPrivateKey","(Landroid/content/Context;[B)[B",(void*)encodeByRSAPrivateKey},
        {"decodeByRSAPubKey","(Landroid/content/Context;[B)[B",(void*)decodeByRSAPubKey},
        {"signByRSAPrivateKey","(Landroid/content/Context;[B)[B",(void*)signByRSAPrivateKey},
        {"verifyByRSAPubKey","(Landroid/content/Context;[B[B)I",(void*)verifyByRSAPubKey},
        {"xOr","(Landroid/content/Context;[B)[B",(void*)xOr},
        {"md5","(Landroid/content/Context;[B)Ljava/lang/String;",(void*)md5},
        {"sha1OfApk","(Landroid/content/Context;)Ljava/lang/String;",(void*)getSha1OfApk},
        {"verifySha1OfApk","(Landroid/content/Context;)Z",(void*)validateSha1OfApk},
};
//此函数通过调用RegisterNatives方法来注册我们的函数
static int registerNativeMethods(JNIEnv* env, const char* className,JNINativeMethod* getMethods,int methodsNum){
    jclass clazz;
    //找到声明native方法的类
    clazz = env->FindClass(className);
    if(clazz == NULL){
        return JNI_FALSE;
    }
    //注册函数 参数：java类 所要注册的函数数组 注册函数的个数
    if(env->RegisterNatives(clazz,getMethods,methodsNum) < 0){
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static int registerNatives(JNIEnv* env){
    //指定类的路径，通过FindClass 方法来找到对应的类
    const char* className  = "com/base/encrypt/JniUtils";
    return registerNativeMethods(env,className,getMethods, sizeof(getMethods)/ sizeof(getMethods[0]));
}
//回调函数
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved){
    JNIEnv* env = NULL;
    //获取JNIEnv
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    assert(env != NULL);
    //注册函数 registerNatives ->registerNativeMethods ->env->RegisterNatives
    if(!registerNatives(env)){
        return JNI_ERR;
    }
    //返回jni 的版本 
    return JNI_VERSION_1_6;
}