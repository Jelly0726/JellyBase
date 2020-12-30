//
// Created by Phoenix on 2017/6/25.
//
#include <jni.h>
#include <string.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <openssl/hmac.h>
#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <openssl/md5.h>
#include <openssl/evp.h>
#include <openssl/bio.h>
#include <openssl/buffer.h>
#include <openssl/err.h>
#include "endorse.h"

using namespace std;
// ---- rsa非对称加解密 ---- //
#define KEY_LENGTH  1024               // 密钥长度
#define PUB_KEY_FILE "pubkey.pem"    // 公钥路径
#define PRI_KEY_FILE "prikey.pem"    // 私钥路径
//换成你自己的key值
const char *key = "758DA6688786C0D2";//"brLxpmIzzN6o7JDW";//"758DA6688786C0D2C7";//"QKBgQC0HjcCrWfRY8o4i2";
//换成你自己的RSAPubKey值 RSAPrivateKey值
const char *RSAPublicKey = "-----BEGIN RSA PUBLIC KEY-----\nMIGJAoGBALzVeoQp2EAgbl1J6mzq1Y25dw+uOkG/MayWzuWNY08TPAmuZZ8Q7qJF\nRjHBlfJD7bx7kE0d2V8mlLwvrAHg1X8hLKxcfmTSoAlgMQ/iN5FKGLfxIVOowb3k\nJM0kUpgwd6Nk0wyjJi9UgP+nTclFeOhz0RVxA/ST5J4j3MVyqOwlAgMBAAE=\n-----END RSA PUBLIC KEY-----\n";
const char *RSAPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\nMIICXAIBAAKBgQC81XqEKdhAIG5dSeps6tWNuXcPrjpBvzGsls7ljWNPEzwJrmWf\nEO6iRUYxwZXyQ+28e5BNHdlfJpS8L6wB4NV/ISysXH5k0qAJYDEP4jeRShi38SFT\nqMG95CTNJFKYMHejZNMMoyYvVID/p03JRXjoc9EVcQP0k+SeI9zFcqjsJQIDAQAB\nAoGATiISwI7D4LzKjaUg75I3bJ3Z6s4PYtbmieAYmZjoB3cQ93yGpcuOweviAIJ2\nNbjvrHaAHbiFEb7X+gnLpTdPfsR4jWFvAvhUFLu9zQwXRuOS2PSYGNtJzOXyh8p7\nZ74IR8i7I1hb1IhDsENL3aUadvOQC762D34t9IRohGmac0ECQQD4uG5AeZAcVvQg\nvO2/FpQy99lkoThTRhvIH2yvVDcKV7HRRv5/f+rvppqeM4z0nObtjzBGyNVN65VL\nQLLWNDbRAkEAwlxXKjRQLDKfkDjeD/FLiJGc85m0kdSPyaXg4IKv3qZl5L8GL4O+\neBdFnhLQ3RHavghGxflRxzNXpxCxm67dFQJADR6lai8/Y89OZ1+v5tGJFbsvM3ix\noOrk0kSeFg2KLbh8f76P9CfKO8P9CfVMLScNo2BXOpSjc83GfUa3aEcu0QJAJCCx\n+yBaPrzyOAa6EFCT78DRYd6SWAEg8SSqVlE0i7h2fDyd07szbnM095sbw9wLwwMa\n1LXxY4vBoUZTHVM1uQJBALgJq2Cr4KSQMS5QS4uaq6oQIfJiBqDU7abL0HItnonm\nT6EfzsK/Hp2t9ieiMQ4SZQ0xZYlZEc9qxTqny0ryUSw=\n-----END RSA PRIVATE KEY-----\n";
//江平加密
//const char *RSAPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoWPzLsJq9/aozb4EJKytHNT07nFjDTUrP/8dlYyzI0KqFqeiYi9f3e96USm0bOjR4TJkzSq8QN92Maej/2VmgrZqu+6e47eXb6/nWIzaDh+Ae06YguUJij+yfIAagLiGAv2kHXz+w10wiht5Jt1H9gIEtS3+jwf5P/a0tQhBgiQIDAQAB";
//const char *RSAPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAPDO8feOL6npbREoDZvSiwutsv1BIbM4SGcg0ENr4jpGXA9ezTYFZtY+oKqnULuCu9XlyfaBjZBh2UjJobt9/qIYMckF+lH5VdTnEJ66NzGIpMRuZeB2K/3tgEZ4UHz9cE1+VKANLQm3INOUz/efW8IL7tcvbOVxfi46+KhZWX37AgMBAAECgYBXP5O5zwGD/Xgv7CQqHfmVDKU6TxvntG+/NctfcjowRQfb5cxSU1i0LVLHIgIFPlPSJwSq1Lu24Uz5q1x28yqLWmUIZlI+1zxkkiGpZSfkgw9Ap3yvHzW9/4Dwql4qYy1eESnC2cA0qSgzZCBZuL+MvgE2ZsCph5QFMymMt6m7AQJBAP2dXRbpiTZn+rjUh227SGd0W24yM5wF69VYqT9cch3AhlJFMuDj1ZrrXhTLQSW6fg5JnXk/AQ7UizTgpU1Tt3sCQQDzEr9Sn4aaSsyZ9Uj7B2FML+gSLmhLp6MZXOdk2G81+21b3bQN0U/eL5wSXY05oTTWU6lBJgINfvIR2RgRa0uBAkBt3A3oD+/NLouQJxgnM3pwvhmhCYQLsRDwkQB3nu8b7jsBRlJ80pTED/LoJQeqdTeHruHVFLK7ntxSYoGYlowjAkB9u7jTmEkcDoz/Jm31y798lVtJ7E9usuwe9nuLIpmEJuZOmsPEUf0qRR5KMbIXmVendecbF/n/2SaGLPHSMNoBAkEA8bQVDljkndCPVPYE+3HcsPfF1Y36lUIA4765iemgbB2Clwvq7aUpYlO1XixKwXk/YqUrwKIHpMKt+yvIuy2gNA==";

//非法调用，Apk签名校验不通过！
const char *sha1_sign_err = "非法调用，Apk签名校验不通过！";
/**
 * 获取Apk的SHA1值
 */
extern "C" JNIEXPORT jstring JNICALL
getSha1OfApk(JNIEnv *env, jobject obj, jobject context) {
    return env->NewStringUTF(sha1OfApk(env, context));
}
/**
 * 校验Apk的SHA1值
 */
extern "C" JNIEXPORT jboolean JNICALL
validateSha1OfApk(JNIEnv *env, jobject obj, jobject context) {
    return verifySha1OfApk(env, context);
}

/**
 * base64 编码
 * @param decoded_bytes
 * @return
 */
int base64Encode(char *&decoded_bytes, char *&out) {
//    out=decoded_bytes;
//    return strlen(out);
    /*使用openssl的base64api*/
    BIO *bio, *b64;
    BUF_MEM *bufferPtr;
    b64 = BIO_new(BIO_f_base64());
    //不换行
    BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL);
    bio = BIO_new(BIO_s_mem());
    bio = BIO_push(b64, bio);
    //encode
    BIO_write(bio, decoded_bytes, (int) strlen(decoded_bytes));
    BIO_flush(bio);
    BIO_get_mem_ptr(bio, &bufferPtr);
    //这里的第二个参数很重要，必须赋值 第二个参数表示长度，不能少，
    // 否则base64后的字符串长度会出现异常，导致decode的时候末尾会出现一大堆的乱码，而网上大多数的代码，是缺失这一个参数的。
//    std::string result(bufferPtr->data, bufferPtr->length);
    int outlen = bufferPtr->length;
    out = new char[outlen + 1];
    memset(out, 0, outlen + 1);
    memcpy(out, bufferPtr->data, outlen);
    BIO_free_all(bio);
    return outlen;
    /*自己组装改造的算法*/
//    int len = base64_encode(decoded_bytes.c_str(),out);
////    std::string result(out);
////    free(decode);
//    return len;
}

/**
 * base64 解码
 * @param encoded_bytes
 * @return
 */
int base64Decode(char *&encoded_bytes, char *&decode) {
//    decode=encoded_bytes;
//    return strlen(decode);
    if (is_base64(encoded_bytes) == 0) {//不是base64
        LOGD("base64 解码->原数据不是合法的base64字符串 = %s", encoded_bytes);
        int textLength = strlen(encoded_bytes);
        decode = new char[textLength];
        memset(decode, 0, textLength);
        memcpy(decode, encoded_bytes, textLength);
        return textLength;
    }
//    /*使用openssl的base64api*/
    BIO *bioMem, *b64;
    bioMem = BIO_new_mem_buf((void *) encoded_bytes, -1);
    b64 = BIO_new(BIO_f_base64());
    BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL);
    bioMem = BIO_push(b64, bioMem);
    //获得解码长度
    size_t buffer_length = BIO_get_mem_data(bioMem, NULL);
    LOGD("base64 解码->buffer_length= %d", buffer_length);
//    char *decode = (char *) malloc(buffer_length + 1);
    decode = (char *) malloc(buffer_length + 1);
    //填充0
    memset(decode, 0, buffer_length + 1);
    BIO_read(bioMem, (void *) decode, (int) buffer_length);
    LOGD("base64 解码->decode= %s", decode);
    LOGD("base64 解码->decode_length= %d", strlen(decode));
//    std::string decoded_bytes(decode);
    BIO_free_all(bioMem);
    return strlen(decode);
    /*自己组装改造的算法*/
//    int len = base64_decode(encoded_bytes,decode);
//    LOGD("base64 解码->decode= %s", decode);
////    std::string decoded_bytes(decode);
////    free(decode);
//    return len;
}

/**
 * HmacSHA1加密
 */
jstring
encodeByHmacSHA1(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
//    LOGD("HmacSHA1加密->HMAC: Hash-based Message Authentication Code，即基于Hash的消息鉴别码");
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1加密->apk-sha1值验证不通过");
        return CStr2Jstring(env, sha1_sign_err);
    }
//    const char *key = "jellyApp@22383243*335457968";
    jbyte *srcChars = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    unsigned int result_len;
    unsigned char result[EVP_MAX_MD_SIZE];
    char buff[EVP_MAX_MD_SIZE];
    char hex[EVP_MAX_MD_SIZE];

//    LOGD("HmacSHA1加密->调用函数进行哈希运算");
    HMAC(EVP_sha1(), key, strlen(key), (unsigned char *) srcChars, src_Len, result, &result_len);

    strcpy(hex, "");
//    LOGD("HmacSHA1加密->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != result_len; i++) {
        sprintf(buff, "%02x", result[i]);
        strcat(hex, buff);
    }
    LOGD("HmacSHA1加密->%s", hex);

    LOGD("HmacSHA1加密->从jni释放数据指针");
    LOGD("释放资源->src_");
    env->DeleteLocalRef(src_);
    free(srcChars);
    //string -> char* -> jstring 返回
    jstring resultHex = CStr2Jstring(env, hex);
//    jstring result =env->NewStringUTF(base64RSA.c_str());
    return resultHex;
}

/**
 * SHA1加密
 */
jstring
encodeBySHA1(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA1加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcChars = env->GetByteArrayElements(src_, NULL);
    jsize src_Lens = env->GetArrayLength(src_);
//    LOGD("SHA1加密->源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcChars, src_Lens);
    chars[src_Lens] = 0;

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA1加密->源数据-> %s ", src);
    int src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA_DIGEST_LENGTH];
    char hex[SHA_DIGEST_LENGTH * 2 + 1];
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
    delete[]chars;//new/delete、new[]/delete[] 要配套使用总是没错的
    LOGD("释放资源->src_");
    env->DeleteLocalRef(src_);
    free(srcChars);
    //string -> char* -> jstring 返回
    jstring resultHex = CStr2Jstring(env, hex);
//    jstring result =env->NewStringUTF(base64RSA.c_str());
    return resultHex;
}

/**
 * SHA224加密
 */
jstring
encodeBySHA224(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA224加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcChars = env->GetByteArrayElements(src_, NULL);
    jsize src_Lens = env->GetArrayLength(src_);
//    LOGD("SHA1加密->源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcChars, src_Lens);
    chars[src_Lens] = 0;

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA1加密->源数据-> %s ", src);
    int src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA224_DIGEST_LENGTH];
    char hex[SHA224_DIGEST_LENGTH * 2 + 1];
    unsigned char digest[SHA224_DIGEST_LENGTH];

//    SHA224((unsigned char *)src, src_Len, digest);

    SHA256_CTX ctx;
    SHA224_Init(&ctx);
//    LOGD("SHA224加密->正在进行SHA224哈希运算");
    SHA224_Update(&ctx, src, src_Len);
    SHA224_Final(digest, &ctx);

    OPENSSL_cleanse(&ctx, sizeof(ctx));

    strcpy(hex, "");
    LOGD("SHA224加密->把哈希值按%%02x格式定向到缓冲区->%d", sizeof(digest));
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
//        LOGD("SHA224加密->把哈希值按%%02x格式定向到缓冲区->%d", i);
    }
    LOGD("SHA224加密->%s", hex);
    LOGD("SHA224加密->释放内存");
    free(src);//malloc和free要配套使用
    delete[]chars;//new/delete、new[]/delete[] 要配套使用总是没错的
    LOGD("释放资源->src_");
    env->DeleteLocalRef(src_);
    free(srcChars);
    //string -> char* -> jstring 返回
    jstring resultHex = CStr2Jstring(env, hex);
//    jstring result =env->NewStringUTF(base64RSA.c_str());
    return resultHex;
}

/**
 * SHA256加密
 */
jstring
encodeBySHA256(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA256加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcChars = env->GetByteArrayElements(src_, NULL);
    jsize src_Lens = env->GetArrayLength(src_);
//    LOGD("SHA1加密->源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcChars, src_Lens);
    chars[src_Lens] = 0;

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA1加密->源数据-> %s ", src);
    int src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA256_DIGEST_LENGTH];
    char hex[SHA256_DIGEST_LENGTH * 2 + 1];
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
    delete[]chars;//new/delete、new[]/delete[] 要配套使用总是没错的
    LOGD("释放资源->src_");
    env->DeleteLocalRef(src_);
    free(srcChars);
    //string -> char* -> jstring 返回
    jstring resultHex = CStr2Jstring(env, hex);
//    jstring result =env->NewStringUTF(base64RSA.c_str());
    return resultHex;
}

/**
 * SHA384加密
 */
jstring
encodeBySHA384(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA384加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcChars = env->GetByteArrayElements(src_, NULL);
    jsize src_Lens = env->GetArrayLength(src_);
//    LOGD("SHA1加密->源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcChars, src_Lens);
    chars[src_Lens] = 0;

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA1加密->源数据-> %s ", src);
    int src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA384_DIGEST_LENGTH];
    char hex[SHA384_DIGEST_LENGTH * 2 + 1];
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
    delete[]chars;//new/delete、new[]/delete[] 要配套使用总是没错的
    LOGD("释放资源->src_");
    env->DeleteLocalRef(src_);
    free(srcChars);
    //string -> char* -> jstring 返回
    jstring resultHex = CStr2Jstring(env, hex);
//    jstring result =env->NewStringUTF(base64RSA.c_str());
    return resultHex;
}

/**
 * SHA512加密
 */
jstring
encodeBySHA512(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA512加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcChars = env->GetByteArrayElements(src_, NULL);
    jsize src_Lens = env->GetArrayLength(src_);
//    LOGD("SHA1加密->源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcChars, src_Lens);
    chars[src_Lens] = 0;

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA1加密->源数据-> %s ", src);
    int src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA512_DIGEST_LENGTH];
    char hex[SHA512_DIGEST_LENGTH * 2 + 1];
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
    delete[]chars;//new/delete、new[]/delete[] 要配套使用总是没错的
    LOGD("释放资源->src_");
    env->DeleteLocalRef(src_);
    free(srcChars);
    //string -> char* -> jstring 返回
    jstring resultHex = CStr2Jstring(env, hex);
//    jstring result =env->NewStringUTF(base64RSA.c_str());
    return resultHex;
}

/**
 * 使用AES 的 Encrypt 加密
 * @param env
 * @param obj
 * @param context
 * @param src_
 * @param _key
 * @return
 */
jbyteArray
encryptByAESEncrypt(JNIEnv *env, jobject obj, jobject context, jbyteArray src_, jbyteArray _key) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("AES加密Encrypt->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }

//    LOGD("AES加密Encrypt->对称密钥，也就是说加密和解密用的是同一个密钥");
//    const unsigned char *iv = (const unsigned char *) "gjdljgaj748564df";
    jbyte *keys = env->GetByteArrayElements(_key, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int outlen = 0, cipherText_len = 0;

    unsigned char *out = (unsigned char *) malloc((src_Len / 16 + 1) * 16);
    //清空内存空间
    memset(out, 0, (src_Len / 16 + 1) * 16);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);
//    LOGD("AES加密->指定加密算法，初始化加密key/iv");
//  初始化加密上下文 参数ctx必须在调用本函数之前已经进行了初始化
//  参数type通常通过函数类型来提供参数，如EVP_des_cbc函数的形式 如果参数impl为NULL，那么就会使用缺省的实现算法
//    参数key是用来加密的对称密钥，iv参数是初始化向量（如果需要的话）
//    EVP_EncryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, (const unsigned char *) keys, iv);
    EVP_EncryptInit_ex(&ctx, EVP_aes_128_ecb(), NULL, (const unsigned char *) keys, NULL);
//    LOGD("AES加密Encrypt->对数据进行加密运算");
    EVP_EncryptUpdate(&ctx, out, &outlen, (const unsigned char *) src, src_Len);
    cipherText_len = outlen;

//    LOGD("AES加密->结束加密运算");
    EVP_EncryptFinal_ex(&ctx, out + outlen, &outlen);
    cipherText_len += outlen;

//    LOGD("AES加密->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);

    LOGD("AES加密Encrypt->从jni释放数据指针");
    env->ReleaseByteArrayElements(_key, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);
//    LOGD("out->%s",out);
    jbyteArray cipher = env->NewByteArray(cipherText_len);
//    LOGD("AES加密Encrypt->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_len, (jbyte *) out);
    LOGD("AES加密Encrypt->释放内存");
    free(out);

    return cipher;
}

/**
 * 使用AES 的 Encrypt 解密
 * @param env
 * @param obj
 * @param context
 * @param src_
 * @param _key
 * @return
 */
jbyteArray
decryptByAESEncrypt(JNIEnv *env, jobject obj, jobject context, jbyteArray src_, jbyteArray _key) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("AES解密Encrypt->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }
//    LOGD("AES解密Encrypt->对称密钥，也就是说加密和解密用的是同一个密钥");
//    const unsigned char *iv = (const unsigned char *) "gjdljgaj748564df";
    jbyte *keys = env->GetByteArrayElements(_key, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int outlen = 0, plaintext_len = 0;

    unsigned char *out = (unsigned char *) malloc(src_Len);
    memset(out, 0, src_Len);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);
//    LOGD("AES解密Encrypt->指定解密算法，初始化解密key/iv");
//    EVP_DecryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, (const unsigned char *) keys, iv);
    EVP_DecryptInit_ex(&ctx, EVP_aes_128_ecb(), NULL, (const unsigned char *) keys, NULL);
//    LOGD("AES解密->对数据进行解密运算");
    EVP_DecryptUpdate(&ctx, out, &outlen, (const unsigned char *) src, src_Len);
    plaintext_len = outlen;

//    LOGD("AES解密Encrypt->结束解密运算");
    EVP_DecryptFinal_ex(&ctx, out + outlen, &outlen);
    plaintext_len += outlen;

//    LOGD("AES解密Encrypt->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);

    LOGD("AES解密Encrypt->从jni释放数据指针");
    env->ReleaseByteArrayElements(_key, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    if (plaintext_len <= 0) {
        LOGD("AES解密Encrypt->解密失败！");
        LOGD("AES解密Encrypt->释放内存");
        free(out);
        char *decode_err = "AES解密Encrypt解密失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(decode_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(decode_err), (jbyte *) decode_err);
        return byteArray;
    }
    jbyteArray cipher = env->NewByteArray(plaintext_len);
//    LOGD("AES->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, plaintext_len, (jbyte *) out);
    LOGD("AES解密Encrypt->释放内存");
    free(out);
    return cipher;
}

/**
 *  使用AES的 Cipher 加密
 * @param env
 * @param obj
 * @param context
 * @param src_
 * @param _key
 * @return
 */
jbyteArray
encryptAESCipher(JNIEnv *env, jobject obj, jobject context, jbyteArray src_, jbyteArray _key) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("AES加密Cipher->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }
    jbyte *keys = env->GetByteArrayElements(_key, 0);
    jbyte *vItem = env->GetByteArrayElements(src_, 0);
    jsize inLen = env->GetArrayLength(src_);
    // 待加密的数据
//    const unsigned char *vItem = reinterpret_cast<const unsigned char *>(src);
//    // 源数据长度
//    int inLen = (int) strlen((const char *) vItem);
    // 加密长度
    int encLen = 0;
    // 输出长度
    int outlen = 0;
    // 加密数据长度
    unsigned char *encData = (unsigned char *) malloc((inLen / 16 + 1) * 16);
    //清空内存空间
    memset(encData, 0, (inLen / 16 + 1) * 16);

    LOGW("AES加密Cipher->source: %s\n", vItem);
    // 创建加密上下文
    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    // 初始化加密上下文
    EVP_CipherInit_ex(ctx, EVP_aes_128_ecb(), NULL, reinterpret_cast<const unsigned char *>(keys),
                      NULL, 1);
    // 加密数据
    EVP_CipherUpdate(ctx, encData, &outlen, reinterpret_cast<const unsigned char *>(vItem), inLen);
    // 拼接长度
    encLen = outlen;
    // 结束加密
    EVP_CipherFinal(ctx, encData + outlen, &outlen);
    // 拼接
    encLen += outlen;
    // 释放
    EVP_CIPHER_CTX_free(ctx);
    LOGW("AES加密Cipher->encrypted : %s\n", encData);
    // base64编码 使用openssl base64编码后 在java成不再编码，调用openssl base64解码会失败 具体原因不明
//    char *baseEnc = base64Encode(reinterpret_cast<const char *>(encData), encLen);
//    LOGW("AES加密Cipher->encrypted : %s\n", baseEnc);
    jbyteArray result = NULL;//下面一系列操作把char *转成jbyteArray
    result = env->NewByteArray(encLen);
    env->SetByteArrayRegion(result, 0, encLen, (jbyte *) encData);
    return result;
}

/**
 * 使用AES的 Cipher 解密
 * @param env
 * @param obj
 * @param context
 * @param src_
 * @param _key
 * @return
 */
jbyteArray
decryptAESCipher(JNIEnv *env, jobject obj, jobject context, jbyteArray src_, jbyteArray _key) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("AES解密Cipher->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }
    jbyte *keys = env->GetByteArrayElements(_key, 0);
    jbyte *src = env->GetByteArrayElements(src_, 0);
    jsize src_Len = env->GetArrayLength(src_);
    LOGW("AES解密Cipher->src %s", src);
    // base64解码
//    char *encData1 = base64Decode(reinterpret_cast<char *>(src), src_Len);
//    LOGW("encData1 %s", encData1);
    // 解密长度
    int decLen = 0;
    // 解码数据长度
    int outlen = 0;
    // 解码后的数据
    unsigned char *decData = (unsigned char *) malloc(src_Len);
    //清空内存空间
    memset(decData, 0, src_Len);
    // 创建解密上下文
    EVP_CIPHER_CTX *ctx2 = EVP_CIPHER_CTX_new();
    // 初始化解密
    EVP_CipherInit_ex(ctx2, EVP_aes_128_ecb(), NULL, reinterpret_cast<const unsigned char *>(keys),
                      NULL, 0);
    // 执行解密
    EVP_CipherUpdate(ctx2, decData, &outlen, (const unsigned char *) src, src_Len);
    // 设置长度
    decLen = outlen;
    // 结束解密
    EVP_CipherFinal(ctx2, decData + outlen, &outlen);
    // 拼接长度
    decLen += outlen;
    // 释放
    EVP_CIPHER_CTX_free(ctx2);
    // 设置字符串结尾标识
    decData[decLen] = '\0';
    LOGW("AES解密Cipher->decrypt %s", decData);
    if (decLen <= 0) {
        LOGD("AES解密Cipher->解密失败！");
        char *decode_err = "AES解密Cipher解密失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(decode_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(decode_err), (jbyte *) decode_err);
        return byteArray;
    }
    jbyteArray result = NULL;//下面一系列操作把char *转成jbyteArray
    result = env->NewByteArray(decLen);
    env->SetByteArrayRegion(result, 0, decLen, (jbyte *) decData);
    return result;
}

/**
 * 使用AES的Cipher加密
 */
jbyteArray encodeByAESCipher(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ = env->NewByteArray(strlen(key));
    env->SetByteArrayRegion(keys_, 0, strlen(key), (jbyte *) key);
    return encryptAESCipher(env, obj, context, src_, keys_);
}

/**
 * 使用AES的Cipher解密
 */
jbyteArray decodeByAESCipher(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ = env->NewByteArray(strlen(key));
    env->SetByteArrayRegion(keys_, 0, strlen(key), (jbyte *) key);
    return decryptAESCipher(env, obj, context, src_, keys_);
}

/**
 * 使用AES的Encrypt加密
 */
jbyteArray encodeByAESEncrypt(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ = env->NewByteArray(strlen(key));
    env->SetByteArrayRegion(keys_, 0, strlen(key), (jbyte *) key);
    return encryptByAESEncrypt(env, obj, context, src_, keys_);
}

/**
 * 使用AES的Encrypt解密
 */
jbyteArray decodeByAESEncrypt(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ = env->NewByteArray(strlen(key));
    env->SetByteArrayRegion(keys_, 0, strlen(key), (jbyte *) key);
    return decryptByAESEncrypt(env, obj, context, src_, keys_);
}

/**************************************
* Function Name  : readFromAssets
* Description    : void  readFromAssets(AssetManager ass,String filename);
* Input          : context上下文 filename资源名
* Output         : None
* Return         : None
***************************************/
std::string readFromAssets(JNIEnv *env, jobject context, char *mfile) {
    //上下文对象
    jclass clazz = env->GetObjectClass(context);
    jmethodID getAssets = env->GetMethodID(clazz, "getAssets",
                                           "()Landroid/content/res/AssetManager;");
    jobject assetManager = env->CallObjectMethod(context, getAssets);
    LOGI("读取Assets下文件->ReadAssets");
    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
    LOGI("读取Assets下文件->释放内存");
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(assetManager);
    if (mgr == NULL) {
        LOGI("读取Assets下文件-> %s", "AAssetManager==NULL");
        return NULL;
    }
    /*获取文件名并打开*/
//    char *mfile = (char *) env->GetStringUTFChars(filename, 0);
    AAsset *asset = AAssetManager_open(mgr, mfile, AASSET_MODE_UNKNOWN);

    if (asset == NULL) {
        LOGI("读取Assets下文件->%s", "asset==NULL");
        return NULL;
    }
    /*获取文件大小*/
    off_t bufferSize = AAsset_getLength(asset);
    LOGI("读取Assets下文件->file size         : %d\n", bufferSize);
    char *buffer = (char *) malloc(bufferSize + 1);
    buffer[bufferSize] = 0;
//    int numBytesRead = AAsset_read(asset, buffer, bufferSize);
    int numBytesRead = AAsset_read(asset, buffer, bufferSize);
    if (numBytesRead < 0) {
        string err = string("读取文件失败！");
        LOGI("读取Assets下文件->%s", "读取文件失败！");
        return NULL;
    }
    LOGI("读取Assets下文件->: %s", buffer);
    string result = string(buffer);
    LOGI("读取Assets下文件->释放内存");
    free(buffer);
    LOGI("读取Assets下文件->关闭文件");
    AAsset_close(asset);
    return result;
}

/**
 * 根据文件名读取Assets目录下的文件 注耗时的io操作
 * @param env
 * @param tis
 * @param context
 * @param filename  文件名（含后缀）
 * @return
 */
jbyteArray readAssets(JNIEnv *env, jclass tis, jobject context, jstring filename) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("读取Assets下文件->apk-sha1值验证不通过");
        return NULL;
    }
    char *mfile = (char *) env->GetStringUTFChars(filename, 0);
    string buffer = readFromAssets(env, context, mfile);
    LOGD("读取Assets下文件->释放资源");
   env->ReleaseStringUTFChars(filename,mfile);
    //把char *转成jbyteArray
    jbyteArray result = charToJByteArray(env, (unsigned char *) buffer.c_str());
    return result;
}

/**
 * 获取当前程序的私有路径
 * @param env
 * @param obj
 * @param context
 * @return
 */
std::string getAbsolutePath(_JNIEnv *env, jobject obj, jobject context) {
    //上下文对象
    jclass clazz = env->GetObjectClass(context);
    jmethodID getFilesDir = env->GetMethodID(clazz, "getFilesDir",
                                             "()Ljava/io/File;");
    jobject jobject1 = env->CallObjectMethod(context, getFilesDir);

    jmethodID getAbsolutePath = env->GetMethodID(env->FindClass("java/io/File"),
                                                 "getAbsolutePath",
                                                 "()Ljava/lang/String;");
    jstring jstring1 = static_cast<jstring>(env->CallObjectMethod(jobject1, getAbsolutePath));
    const char *path_utf = env->GetStringUTFChars(jstring1, NULL);
//    LOGD("获取当前程序的私有路径->%s", path_utf);
    LOGD("获取当前程序的私有路径->释放内存");
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(jobject1);
    env->DeleteLocalRef(jstring1);
    return path_utf;
}

/**
 * 根据路径读取文件
 * @param path  文件路径
 * @return
 */
char *readFile(const char *path) {
    FILE *pFile;
    pFile = fopen(path, "rw");
    if (pFile == NULL) {
        LOGD("根据路径读取文件->读取文件失败！");
        return NULL;
    }
    char *pBuf;  //定义文件指针
    fseek(pFile, 0, SEEK_END); //把指针移动到文件的结尾 ，获取文件长度
    int size = (int) ftell(pFile); //获取文件长度
    pBuf = new char[size + 1]; //定义数组长度
    rewind(pFile); //把指针移动到文件开头 因为我们一开始把指针移动到结尾，如果不移动回来 会出错
    fread(pBuf, 1, (size_t) size, pFile); //读文件
    pBuf[size] = 0; //把读到的文件最后一位 写为0 要不然系统会一直寻找到0后才结束
    LOGD("根据路径读取文件->关闭文件");
    fclose(pFile); // 关闭文件
    return pBuf;
}

/**
 * 函数方法生成密钥对
 * @param env
 * @param obj
 * @param context
 * @return
 */
jobjectArray generateRSAKey(JNIEnv *env, jobject obj, jobject context) {
    jobjectArray result = (jobjectArray) env->NewObjectArray(2,
                                                             env->FindClass("java/lang/String"),
                                                             env->NewStringUTF(""));
    if (!verifySha1OfApk(env, context)) {
        LOGD("生成RSA密钥对->apk-sha1值验证不通过");
        return result;
    }
    // 公私密钥对
    size_t pri_len;
    size_t pub_len;
    char *pri_key = NULL;
    char *pub_key = NULL;
    string pub_path = getAbsolutePath(env, obj, context).append("/").append(PUB_KEY_FILE);
    string pri_path = getAbsolutePath(env, obj, context).append("/").append(PRI_KEY_FILE);
    //从文件读取RSA秘钥对
    pri_key = readFile(pri_path.c_str());
    pub_key = readFile(pub_path.c_str());
//    LOGD("公钥路径->%s",pub_path.c_str());
//    LOGD("私钥路径->%s",pri_path.c_str());
    if (pri_key != NULL && pub_key != NULL) {
        // 存储密钥对
        env->SetObjectArrayElement(result, 0, env->NewStringUTF(pub_key));
        env->SetObjectArrayElement(result, 1, env->NewStringUTF(pri_key));
//        LOGD("从文件读取公钥->\n%s",pub_key);
//        LOGD("从文件读取私钥->\n%s",pri_key);
        LOGD("资源释放->pri_key");
        free(pri_key);
        LOGD("资源释放->pub_key");
        free(pub_key);
        return result;
    }
    // 生成密钥对
    RSA *keypair = RSA_new();
    int ret = 0;
    BIGNUM *bne = BN_new();
    ret = BN_set_word(bne, RSA_F4);
    ret = RSA_generate_key_ex(keypair, KEY_LENGTH, bne, NULL);
//    RSA *keypair = RSA_generate_key(KEY_LENGTH, RSA_F4, NULL, NULL);
    if (ret != 1) {
        return result;
    }
    BIO *pri = BIO_new(BIO_s_mem());
    BIO *pub = BIO_new(BIO_s_mem());
    PEM_write_bio_RSAPrivateKey(pri, keypair, NULL, NULL, 0, NULL, NULL);
    // 注意------生成第1种格式的公钥 pkcs1
//    PEM_write_bio_RSAPublicKey(pub, keypair);
    // 注意------生成第2种格式的公钥 pkcs8（此处代码中使用这种）
    PEM_write_bio_RSA_PUBKEY(pub, keypair);


    // 获取长度
    pri_len = BIO_pending(pri);
    pub_len = BIO_pending(pub);

    // 密钥对读取到字符串
    pri_key = (char *) malloc(pri_len + 1);
    pub_key = (char *) malloc(pub_len + 1);

    BIO_read(pri, pri_key, pri_len);
    BIO_read(pub, pub_key, pub_len);

    pri_key[pri_len] = '\0';
    pub_key[pub_len] = '\0';

    // 存储密钥对
    env->SetObjectArrayElement(result, 0, env->NewStringUTF(pub_key));
    env->SetObjectArrayElement(result, 1, env->NewStringUTF(pri_key));

    // 存储到磁盘（这种方式存储的是begin rsa public key/ begin rsa private key开头的）
    FILE *pubFile = fopen(pub_path.c_str(), "w");
    if (pubFile == NULL) {
        LOGD("资源释放->keypair");
        RSA_free(keypair);
        LOGD("资源释放->pub");
        BIO_free_all(pub);
        LOGD("资源释放->pri");
        BIO_free_all(pri);
        LOGD("资源释放->pri_key");
        free(pri_key);
        LOGD("资源释放->pub_key");
        free(pub_key);
        return result;
    }
    LOGD("写入数据->pub_key");
    fputs(pub_key, pubFile);
    LOGD("关闭文件->pubFile");
    fclose(pubFile);

    FILE *priFile = fopen(pri_path.c_str(), "w");
    if (priFile == NULL) {
        LOGD("资源释放->keypair");
        RSA_free(keypair);
        LOGD("资源释放->pub");
        BIO_free_all(pub);
        LOGD("资源释放->pri");
        BIO_free_all(pri);
        LOGD("资源释放->pri_key");
        free(pri_key);
        LOGD("资源释放->pub_key");
        free(pub_key);
        return result;
    }
    LOGD("写入数据->pri_key");
    fputs(pri_key, priFile);
    LOGD("关闭文件->priFile");
    fclose(priFile);

    LOGD("资源释放->keypair");
    RSA_free(keypair);
    LOGD("资源释放->pub");
    BIO_free_all(pub);
    LOGD("资源释放->pri");
    BIO_free_all(pri);
    LOGD("资源释放->pri_key");
    free(pri_key);
    LOGD("资源释放->pub_key");
    free(pub_key);
    return result;
}

/**
 * 根据公钥base64字符串（没换行）生成公钥文本内容
 * @param base64EncodedKey
 * @return
 */
std::string generatePublicKey(std::string base64EncodedKey) {
    string base = base64EncodedKey;
    char *pkcs1_header = "-----BEGIN RSA PUBLIC KEY-----";
    char *pkcs8_header = "-----BEGIN PUBLIC KEY-----";//
    string::size_type pkcs1_idx, pkcs8_idx;
    pkcs1_idx = base.find(pkcs1_header);//在a中查找b.
    pkcs8_idx = base.find(pkcs8_header);//在a中查找b.
    //base64EncodedKey是否存在"-----BEGIN PUBLIC KEY-----"
    if (pkcs1_idx == string::npos && pkcs8_idx == string::npos)//不存在。
    {
        std::string publicKey = base64EncodedKey;
        size_t base64Length = 64;//每64个字符一行
        size_t publicKeyLength = base64EncodedKey.size();
        for (size_t i = base64Length; i < publicKeyLength; i += base64Length) {
            //每base64Length个字符，增加一个换行
            if (base64EncodedKey[i] != '\n') {
                publicKey.insert(i, "\n");
            }
            i++;
        }
        //最前面追加公钥begin字符串
        publicKey.insert(0, "-----BEGIN PUBLIC KEY-----\n");
        //最前面追加公钥end字符串
        publicKey.append("\n-----END PUBLIC KEY-----");
        return publicKey;
    } else//存在。
    {
        return base64EncodedKey;
    }
}

/**
 * 根据私钥base64字符串（没换行）生成私钥文本内容
 * @param base64EncodedKey
 * @return
 */
std::string generatePrivateKey(std::string base64EncodedKey) {
    string base = base64EncodedKey;
    char *pkcs1_header = "-----BEGIN RSA PRIVATE KEY-----";
    char *pkcs8_header = "-----BEGIN PRIVATE KEY-----";//
    string::size_type pkcs1_idx, pkcs8_idx;
    pkcs1_idx = base.find(pkcs1_header);//在a中查找b.
    pkcs8_idx = base.find(pkcs8_header);//在a中查找b.
    //base64EncodedKey是否存在"-----BEGIN PRIVATE KEY-----"
    if (pkcs1_idx == string::npos && pkcs8_idx == string::npos)//不存在。
    {
        std::string privateKey = base64EncodedKey;
        size_t base64Length = 64;//每64个字符一行
        size_t privateKeyLength = base64EncodedKey.size();
        for (size_t i = base64Length; i < privateKeyLength; i += base64Length) {
            //每base64Length个字符，增加一个换行
            if (base64EncodedKey[i] != '\n') {
                privateKey.insert(i, "\n");
            }
            i++;
        }
        //最前面追加私钥begin字符串
        privateKey.insert(0, "-----BEGIN PRIVATE KEY-----\n");
        //最后面追加私钥end字符串
        privateKey.append("\n-----END PRIVATE KEY-----");
        return privateKey;
    } else//存在。
    {
        return base64EncodedKey;
    }
}

/**
@brief : 公钥加密
@para  : clear_text  -[i] 需要进行加密的明文
         pub_key     -[i] 公钥
         output     -[i] 加密后的数据
@return: 加密后的数据长度
**/
int RsaPubEncrypt(const std::string &pub_key, const std::string &clear_text, char *&output) {
    int length = clear_text.length();
    char *encrypt_text = new char[MAX_SIZE];
    memset(encrypt_text, 0, MAX_SIZE);
    BIO *keybio = BIO_new_mem_buf((unsigned char *) pub_key.c_str(), -1);
    RSA *rsa = RSA_new();
    string pkcs1_header = "-----BEGIN RSA PUBLIC KEY-----";
    string pkcs8_header = "-----BEGIN PUBLIC KEY-----";//
    //读取公钥
    if (0 == strncmp(pub_key.c_str(), pkcs8_header.c_str(), pkcs8_header.size())) {
        rsa = PEM_read_bio_RSA_PUBKEY(keybio, &rsa, NULL, NULL);
    } else if (0 == strncmp(pub_key.c_str(), pkcs1_header.c_str(), pkcs1_header.size())) {
        rsa = PEM_read_bio_RSAPublicKey(keybio, &rsa, NULL, NULL);
    }
    if (!rsa) {
        unsigned long err = ERR_get_error(); //获取错误号
        char err_msg[1024] = {0};
        ERR_load_crypto_strings();
        ERR_error_string(err, err_msg);// 格式：error:errId:库:函数:原因
        LOGD("RSA 公钥加密->解析公钥失败！err:%ld, msg:%s", err, err_msg);
//        printf("err msg: err:%ld, msg:%s\n", err, err_msg);
        LOGD("RSA 公钥加密->释放内存");
        BIO_free_all(keybio);
        delete[]encrypt_text;
        //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
        CRYPTO_cleanup_all_ex_data();
        output = new char[1];
        output[0] = 0;
        return 0;
    }

    // 获取RSA单次可以处理的数据块的最大长度
    int key_len = RSA_size(rsa);
    // 因为填充方式为RSA_PKCS1_PADDING, 所以要在key_len基础上减去11
    int block_len = key_len - 11;

    // 申请内存：存贮加密后的密文数据
    int len = key_len + 1;
    char *sub_text = new char[len];
    memset(sub_text, 0, len);
    char *textCs = new char[block_len + 1];
    int ret = 0;
    int pos = 0;
    int left = length;
    int flagLength = 0;
    bool error = false;
    int lenText = 0;
    int sussce = 1;//标记是否加密成功
    int count = 0;//记录一段明文的加密次数
    while (pos < length) {
        if (sussce == 1) {//加密成功才继续下一段加密
            if (left >= block_len) {
                flagLength = block_len;
            } else {
                flagLength = left;
            }
            left -= flagLength;
            count = 0;//加密成功重置加密次数
        } else if (count > 10) {//当同一段明文加密次数超过10次，表示这段明文有问题，尝试缩短一个字符
            flagLength--;
            if (flagLength <= 0) {//一直失败到加密的字符长度为0表示这段明文异常 整个加密失败
                error = true;
                unsigned long err = ERR_get_error(); //获取错误号
                char err_msg[1024] = {0};
                ERR_load_crypto_strings();
                ERR_error_string(err, err_msg);// 格式：error:errId:库:函数:原因
//        printf("err msg: err:%ld, msg:%s\n", err, err_msg);
                LOGD("RSA 公钥加密->公钥加密失败！err:%ld, msg:%s", err, err_msg);
                break;
            }
        } else {
            count++;
        }
        memset(textCs, 0, block_len + 1);
        memcpy(textCs, clear_text.c_str() + pos, flagLength);
        memset(sub_text, 0, len);
        ret = RSA_public_encrypt(flagLength, (const unsigned char *) textCs,
                                 (unsigned char *) sub_text, rsa, RSA_PKCS1_PADDING);
        if (ret >= 0) {
            sussce = 0;//先置为加密失败状态
            //RSA加密成功后密文一般都是128，如果密文不是128就当做当前加密失败，重新加密
            if (strlen(sub_text) == 128) {
                if (pos == 0)
                    strncpy(encrypt_text, sub_text, strlen(sub_text));
                else
                    strncat(encrypt_text, sub_text, strlen(sub_text));
                lenText += ret;
                pos += block_len;
                sussce = 1;//加密成功
            }
        } else {
            error = true;
            unsigned long err = ERR_get_error(); //获取错误号
            char err_msg[1024] = {0};
            ERR_load_crypto_strings();
            ERR_error_string(err, err_msg);// 格式：error:errId:库:函数:原因
//        printf("err msg: err:%ld, msg:%s\n", err, err_msg);
            LOGD("RSA 公钥加密->公钥加密失败！err:%ld, msg:%s", err, err_msg);
            break;
        }
    }
    LOGD("RSA 公钥加密->密文长度=%d", strlen(encrypt_text));
    LOGD("RSA 公钥加密->释放内存");
    BIO_free_all(keybio);
    RSA_free(rsa);
    //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
    CRYPTO_cleanup_all_ex_data();
    delete[] sub_text;
    delete[] textCs;
    if (error) {
        output = new char[1];
        output[0] = 0;
        delete[]encrypt_text;
        return 0;
    }
//    const char *chars = encrypt_text.c_str();
    int outlen = lenText + 1;
    output = new char[outlen];
    memset(output, 0, outlen);
    memcpy(output, encrypt_text, outlen - 1);
    delete[] encrypt_text;
    return outlen - 1;
}

/**
@brief : 私钥解密
@para  : cipher_text -[i] 加密的密文
         pub_key     -[i] 公钥
         output     -[i] 解密后的数据
@return: 解密后的数据长度
**/
int RsaPriDecrypt(const std::string &pri_key, const std::string &cipher_text, char *&output) {
    int length = cipher_text.length();
    char *decrypt_text = new char[MAX_SIZE];
    memset(decrypt_text, 0, MAX_SIZE);
    RSA *rsa = RSA_new();
    BIO *keybio;
    keybio = BIO_new_mem_buf((unsigned char *) pri_key.c_str(), -1);
    rsa = PEM_read_bio_RSAPrivateKey(keybio, &rsa, NULL, NULL);
    if (rsa == nullptr) {
        unsigned long err = ERR_get_error(); //获取错误号
        char err_msg[1024] = {0};
        ERR_load_crypto_strings();
        ERR_error_string(err, err_msg);// 格式：error:errId:库:函数:原因
        LOGD("RSA 私钥解密->解析私钥失败！err:%ld, msg:%s", err, err_msg);
//        printf("err msg: err:%ld, msg:%s\n", err, err_msg);
        LOGD("RSA 私钥解密->释放内存");
        BIO_free_all(keybio);
        delete[]decrypt_text;
        //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
        CRYPTO_cleanup_all_ex_data();
        output = new char[1];
        output[0] = 0;
        return 0;
    }
    int key_len = RSA_size(rsa);
    int len = key_len + 1;
    char *sub_text = new char[len];
    memset(sub_text, 0, len);
    char *eTextCs = new char[key_len];

    int ret = 0;
    int pos = 0;
    bool error = false;
    int left = length;
    int flagLength = 0;
    int textLength = 0;
    // 对密文进行分段解密
    while (pos < length) {
        if (left >= key_len) {
            flagLength = key_len;
        } else {
            flagLength = left;
        }
        left -= flagLength;
        memset(eTextCs, 0, key_len);
        memcpy(eTextCs, (cipher_text.c_str() + pos), flagLength);
        memset(sub_text, 0, len);
        ret = RSA_private_decrypt(flagLength, (const unsigned char *) eTextCs,
                                  (unsigned char *) sub_text, rsa, RSA_PKCS1_PADDING);
        if (ret >= 0) {
            if (pos == 0)
                strncpy(decrypt_text, sub_text, strlen(sub_text));
            else
                strncat(decrypt_text, sub_text, strlen(sub_text));
            textLength += ret;
            pos += key_len;
        } else {
            error = true;
            unsigned long err = ERR_get_error(); //获取错误号
            char err_msg[1024] = {0};
            ERR_load_crypto_strings();
            ERR_error_string(err, err_msg);// 格式：error:errId:库:函数:原因
//        printf("err msg: err:%ld, msg:%s\n", err, err_msg);
            LOGD("RSA 私钥解密->私钥解密失败！err:%ld, msg:%s", err, err_msg);
            break;
        }
    }
    LOGD("RSA 私钥解密->明文文长度=%d", strlen(decrypt_text));
    LOGD("RSA 私钥解密->释放内存");
    delete[] sub_text;
    delete[] eTextCs;
    BIO_free_all(keybio);
    RSA_free(rsa);
    //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
    CRYPTO_cleanup_all_ex_data();
    if (error) {
        output = new char[1];
        output[0] = 0;
        delete[]decrypt_text;
        return 0;
    }
    textLength++;
    output = new char[textLength];
    memset(output, 0, textLength);
    memcpy(output, decrypt_text, textLength - 1);
    delete[]decrypt_text;
    return textLength - 1;
}

/**
@brief : 私钥加密
@para  : clear_text  -[i] 需要进行加密的明文
         pri_key     -[i] 私钥
         output     -[i] 加密后的数据
@return: 加密后的数据长度
**/
int RsaPriEncrypt(const std::string &pri_key, std::string &clear_text, char *&output) {
    int length = clear_text.length();
    char *encrypt_text = new char[MAX_SIZE];
    memset(encrypt_text, 0, MAX_SIZE);
    RSA *rsa = RSA_new();
    BIO *keybio;
    keybio = BIO_new_mem_buf((unsigned char *) pri_key.c_str(), -1);
    rsa = PEM_read_bio_RSAPrivateKey(keybio, &rsa, NULL, NULL);
    if (rsa == nullptr) {
        unsigned long err = ERR_get_error(); //获取错误号
        char err_msg[1024] = {0};
        ERR_load_crypto_strings();
        ERR_error_string(err, err_msg);// 格式：error:errId:库:函数:原因
        LOGD("RSA 私钥加密->解析私钥失败！err:%ld, msg:%s", err, err_msg);
//        printf("err msg: err:%ld, msg:%s\n", err, err_msg);
        LOGD("RSA 私钥加密->释放内存");
        BIO_free_all(keybio);
        //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
        CRYPTO_cleanup_all_ex_data();
        output = new char[1];
        output[0] = 0;
        return 0;
    }
    // 获取RSA单次可以处理的数据块的最大长度
    int key_len = RSA_size(rsa);
    // 因为填充方式为RSA_PKCS1_PADDING, 所以要在key_len基础上减去11
    int block_len = key_len - 11;

    // 申请内存：存贮加密后的密文数据
    int len = key_len + 1;
    char *sub_text = new char[len];
    memset(sub_text, 0, len);
    char *textCs = new char[block_len + 1];
    int ret = 0;
    int pos = 0;
    int left = length;
    int flagLength = 0;
    bool error = false;
    int lenText = 0;
    int sussce = 1;//标记是否加密成功
    int count = 0;//记录加密次数
    while (pos < length) {
        if (sussce == 1) {//加密成功才继续下一段加密
            if (left >= block_len) {
                flagLength = block_len;
            } else {
                flagLength = left;
            }
            left -= flagLength;
            count = 0;//加密成功重置加密次数
        } else if (count > 10) {//当同一段明文加密次数超过10次，表示这段明文有问题，尝试缩短一个字符
            flagLength--;
            if (flagLength <= 0) {//一直失败到加密的字符长度为0表示这段明文异常 整个加密失败
                error = true;
                unsigned long err = ERR_get_error(); //获取错误号
                char err_msg[1024] = {0};
                ERR_load_crypto_strings();
                ERR_error_string(err, err_msg);// 格式：error:errId:库:函数:原因
//        printf("err msg: err:%ld, msg:%s\n", err, err_msg);
                LOGD("RSA 私钥加密->私钥加密失败！err:%ld, msg:%s", err, err_msg);
                break;
            }
        } else {
            count++;
        }
        memset(textCs, 0, block_len + 1);
        memcpy(textCs, clear_text.c_str() + pos, flagLength);
        memset(sub_text, 0, len);
        LOGD("RSA 私钥加密->分段加密 pos=%d", pos);
        LOGD("RSA 私钥加密->分段加密 textCs=%d", strlen(textCs));
        LOGD("RSA 私钥加密->分段加密 textCs=%s", textCs);
        ret = RSA_private_encrypt(flagLength, (const unsigned char *) textCs,
                                  (unsigned char *) sub_text, rsa, RSA_PKCS1_PADDING);
        if (ret >= 0) {
            LOGD("RSA 私钥加密->分段加密 sub_text=%d", strlen(sub_text));
            LOGD("RSA 私钥加密->分段加密 sub_text=%s", sub_text);
            sussce = 0;//先置为加密失败状态
            //RSA加密成功后密文一般都是128，如果密文不是128就当做当前加密失败，重新加密
            if (strlen(sub_text) == 128) {
                if (pos == 0)
                    strncpy(encrypt_text, sub_text, strlen(sub_text));
                else
                    strncat(encrypt_text, sub_text, strlen(sub_text));
                lenText += ret;
                pos += block_len;
                sussce = 1;//加密成功
            }
        } else {
            error = true;
            unsigned long err = ERR_get_error(); //获取错误号
            char err_msg[1024] = {0};
            ERR_load_crypto_strings();
            ERR_error_string(err, err_msg);// 格式：error:errId:库:函数:原因
//        printf("err msg: err:%ld, msg:%s\n", err, err_msg);
            LOGD("RSA 私钥加密->私钥加密失败！err:%ld, msg:%s", err, err_msg);
            break;
        }
    }
    LOGD("RSA 私钥加密->密文长度=%d", strlen(encrypt_text));
    LOGD("RSA 私钥加密->释放内存");
    BIO_free_all(keybio);
    RSA_free(rsa);
    //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
    CRYPTO_cleanup_all_ex_data();
    delete[] sub_text;
    delete[] textCs;
    if (error) {
        output = new char[1];
        output[0] = 0;
        return 0;
    }
    int outlen = lenText + 1;
    output = new char[outlen];
    memset(output, 0, outlen);
    memcpy(output, encrypt_text, outlen - 1);
    delete[] encrypt_text;
    return outlen - 1;
}

/**
@brief : 公钥解密
@para  : cipher_text -[i] 加密的密文
         pub_key     -[i] 公钥
@return: 解密后的数据
**/
int RsaPubDecrypt(const std::string &pub_key, const std::string &cipher_text, char *&output) {
    int length = cipher_text.length();
    char *decrypt_text = new char[MAX_SIZE];
    memset(decrypt_text, 0, MAX_SIZE);
    BIO *keybio = BIO_new_mem_buf((unsigned char *) pub_key.c_str(), -1);
    RSA *rsa = RSA_new();
    string pkcs1_header = "-----BEGIN RSA PUBLIC KEY-----";
    string pkcs8_header = "-----BEGIN PUBLIC KEY-----";//
    //读取公钥
    if (0 == strncmp(pub_key.c_str(), pkcs8_header.c_str(), pkcs8_header.size())) {
        rsa = PEM_read_bio_RSA_PUBKEY(keybio, &rsa, NULL, NULL);
    } else if (0 == strncmp(pub_key.c_str(), pkcs1_header.c_str(), pkcs1_header.size())) {
        rsa = PEM_read_bio_RSAPublicKey(keybio, &rsa, NULL, NULL);
    }
    if (!rsa) {
        unsigned long err = ERR_get_error(); //获取错误号
        char err_msg[1024] = {0};
        ERR_load_crypto_strings();
        ERR_error_string(err, err_msg);// 格式：error:errId:库:函数:原因
        LOGD("RSA 公钥解密->解析公钥失败！err:%ld, msg:%s", err, err_msg);
//        printf("err msg: err:%ld, msg:%s\n", err, err_msg);
        LOGD("RSA 公钥解密->释放内存");
        BIO_free_all(keybio);
        //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
        CRYPTO_cleanup_all_ex_data();
        output = new char[1];
        output[0] = 0;
        return 0;
    }
    int key_len = RSA_size(rsa);
    int len = key_len + 1;
    char *sub_text = new char[len];
    memset(sub_text, 0, len);
    char *eTextCs = new char[key_len];

    int ret = 0;
    int pos = 0;
    bool error = false;
    int left = length;
    int flagLength = 0;
    int textLength = 0;
    // 对密文进行分段解密
    while (pos < length) {
        if (left >= key_len) {
            flagLength = key_len;
        } else {
            flagLength = left;
        }
        left -= flagLength;
        memset(eTextCs, 0, key_len);
        memcpy(eTextCs, (cipher_text.c_str() + pos), flagLength);
        memset(sub_text, 0, len);
        LOGD("RSA 公钥解密->分段解密 pos=%d", pos);
        LOGD("RSA 公钥解密->分段解密 eTextCs=%d", strlen(eTextCs));
        LOGD("RSA 公钥解密->分段解密 eTextCs=%s", eTextCs);
        ret = RSA_public_decrypt(flagLength, (const unsigned char *) eTextCs,
                                 (unsigned char *) sub_text, rsa, RSA_PKCS1_PADDING);
        if (ret >= 0) {
            if (pos == 0)
                strncpy(decrypt_text, sub_text, strlen(sub_text));
            else
                strncat(decrypt_text, sub_text, strlen(sub_text));
            LOGD("RSA 公钥解密->分段解密 sub_text=%d", strlen(sub_text));
            LOGD("RSA 公钥解密->分段解密 sub_text=%s", sub_text);
            textLength += ret;
            pos += key_len;
        } else {
            error = true;
            unsigned long err = ERR_get_error(); //获取错误号
            char err_msg[1024] = {0};
            ERR_load_crypto_strings();
            ERR_error_string(err, err_msg);// 格式：error:errId:库:函数:原因
//        printf("err msg: err:%ld, msg:%s\n", err, err_msg);
            LOGD("RSA 公钥解密->公钥解密失败！err:%ld, msg:%s", err, err_msg);
            break;
        }
    }
    LOGD("RSA 公钥解密->明文文长度=%d", strlen(decrypt_text));
    LOGD("RSA 公钥解密->释放内存");
    delete[] sub_text;
    delete[] eTextCs;
    BIO_free_all(keybio);
    RSA_free(rsa);
    //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
    CRYPTO_cleanup_all_ex_data();
    if (error) {
        output = new char[1];
        output[0] = 0;
        delete[]decrypt_text;
        return 0;
    }
    textLength++;
    output = new char[textLength];
    memset(output, 0, textLength);
    memcpy(output, decrypt_text, textLength - 1);
    delete[]decrypt_text;
    return textLength - 1;
}

/**
 * 使用公钥对明文加密
 * @param env
 * @param thiz
 * @param context
 * @param content
 * @param base64PublicKey
 * @return
 */
jbyteArray
encryptByRSA(JNIEnv *env, jobject thiz, jobject context, jbyteArray src,
             jbyteArray base64PublicKey) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA 公钥加密->apk-sha1值验证不通过");
        return charToJByteArray(env, (unsigned char *) sha1_sign_err);
    }
    //jstring 转 char*
    char *base64PublicKeyChars = jByteArrayToChar(env, base64PublicKey);
//    char *base64PublicKeyChars = (char *)env->GetStringUTFChars(base64PublicKey,0);
//    LOGD("base64PublicKeyChars=%s",base64PublicKeyChars);
    //char* 转 string
    string base64PublicKeyString = string(base64PublicKeyChars);
//    LOGD("base64PublicKeyString=%s",base64PublicKeyString.c_str());
    //生成公钥字符串
    string generatedPublicKey = generatePublicKey(base64PublicKeyString);
//    LOGD("generatedPublicKey=\n%s",generatedPublicKey.c_str());
    //jstring 转 char*
    char *srcChars = jByteArrayToChar(env, src);
//    LOGD("srcChars=%s",srcChars);
    //char* 转 string
    string srcString = string(srcChars);
//    LOGD("srcString=%s",srcString.c_str());
    LOGD("RSA 公钥加密->释放资源");
    env->DeleteLocalRef(base64PublicKey);
    env->DeleteLocalRef(src);
    free(srcChars);
    free(base64PublicKeyChars);
    //调用RSA加密函数加密
    char *rsaResult;
    int len = RsaPubEncrypt(generatedPublicKey, srcString, rsaResult);
    if (len == 0) {
        delete[] rsaResult;
        return charToJByteArray(env, (unsigned char *) "RSA 公钥加密失败！");
    }
    LOGD("RSA 公钥加密->rsaResult=%s", rsaResult);
    LOGD("RSA 公钥加密->length=%d", strlen(rsaResult));
    //将密文进行base64
    char *base64RSA;
    int base64len = base64Encode(rsaResult, base64RSA);
    delete[] rsaResult;
    if (base64len == 0) {
        delete[] base64RSA;
        return charToJByteArray(env, (unsigned char *) "RSA 公钥加密base64编码失败！");
    }
//    LOGD("base64RSA=%s",base64RSA.c_str());
    //string -> char* -> jstring 返回
    jbyteArray result = charToJByteArray(env, reinterpret_cast<unsigned char *>(base64RSA));
//    jstring result =env->NewStringUTF(base64RSA.c_str());
    return result;
}

/**
 * 使用私钥对密文解密
 * @param env
 * @param thiz
 * @param context
 * @param src
 * @param base64PublicKey
 * @return
 */
jbyteArray
decryptByRSA(JNIEnv *env, jobject thiz, jobject context, jbyteArray src,
             jbyteArray base64PrivateKey) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA 私钥解密->apk-sha1值验证不通过");
        return charToJByteArray(env, (unsigned char *) sha1_sign_err);
    }
    //jstring 转 char*
    char *base64PrivateKeyChars = jByteArrayToChar(env, base64PrivateKey);
//    char *base64PrivateKeyChars = (char *)env->GetStringUTFChars(base64PrivateKey,0);
//    LOGD("base64PrivateKeyChars=%s",base64PrivateKeyChars);
    //char* 转 string
    string base64PrivateKeyString = string(base64PrivateKeyChars);
//    LOGD("base64PrivateKeyString=%s",base64PrivateKeyString.c_str());
    //生成公钥字符串
    string generatedPrivateKey = generatePrivateKey(base64PrivateKeyString);
//    LOGD("generatedPrivateKey=%s",generatedPrivateKey.c_str());
    //jstring 转 char*
    char *srcChars = jByteArrayToChar(env, src);
//    char *srcChars = (char *)env->GetStringUTFChars(src,0);
//    LOGD("srcChars=%s", srcChars);
    //char* 转 string
    //decode
    char *decodeBase64RSA;
    int decode_len = base64Decode(srcChars, decodeBase64RSA);
    LOGD("RSA 私钥解密->释放资源");
    env->DeleteLocalRef(src);
    env->DeleteLocalRef(base64PrivateKey);
    free(base64PrivateKeyChars);
    free(srcChars);
    if (decode_len == 0) {
        LOGD("RSA 私钥解密->RSA base64解密失败！");
        delete[]decodeBase64RSA;
        string err = string("RSA base64解密失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
//    LOGD("decodeBase64RSA=%s", decodeBase64RSA.c_str());
    //解密
    char *origin;
    int len = RsaPriDecrypt(generatedPrivateKey, decodeBase64RSA, origin);
    if (len == 0) {
        LOGD("RSA 私钥解密->解密失败！");
        delete[] origin;
        string err = string("RSA 私钥解密失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
    LOGD("RSA 私钥解密->origin=%s", origin);
    LOGD("RSA 私钥解密->length=%d", strlen(origin));
    //string -> char* -> jstring 返回
    jbyteArray result = charToJByteArray(env, reinterpret_cast<unsigned char *>(origin));
    delete[] origin;
//    jstring result =env->NewStringUTF(origin.c_str());
    return result;
}

/**
 * RSA公钥加密
 * @param env
 * @param obj
 * @param context
 * @param src_
 * @return
 */
jbyteArray
encodeByRSAPubKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA 公钥加密->apk-sha1值验证不通过");
        return charToJByteArray(env, (unsigned char *) sha1_sign_err);
    }
    //char* 转 string
    string base64PublicKeyString = string(RSAPublicKey);
//    LOGD("base64PublicKeyString=%s",base64PublicKeyString.c_str());
    //生成公钥字符串
    string generatedPublicKey = generatePublicKey(base64PublicKeyString);
//    LOGD("generatedPublicKey=%s",generatedPublicKey.c_str());
    //jstring 转 char*
    char *srcChars = jByteArrayToChar(env, src_);
//    LOGD("srcChars=%s",srcChars);
    //char* 转 string
    string srcString = string(srcChars);
//    LOGD("srcString=%s",srcString.c_str());
    //释放
    LOGD("RSA 公钥加密->释放资源->src_");
    free(srcChars);
    env->DeleteLocalRef(src_);
    //调用RSA加密函数加密
    char *rsaResult;
    int len = RsaPubEncrypt(generatedPublicKey, srcString, rsaResult);
    if (len == 0) {
        LOGD("RSA 公钥加密->RSA公钥加密失败");
        delete[] rsaResult;
        string err = string("RSA公钥加密失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
    LOGD("RSA公钥加密->rsaResult=%s", rsaResult);
    LOGD("RSA公钥加密->length=%d", strlen(rsaResult));
    //将密文进行base64
    char *base64RSA;
    int base64_len = base64Encode(rsaResult, base64RSA);
    delete[] rsaResult;
    if (base64_len == 0) {
        LOGD("RSA公钥加密->base64编码失败");
        delete[] base64RSA;
        string err = string("RSA base64编码失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
    LOGD("RSA公钥加密->base64RSA=%s", base64RSA);
    LOGD("RSA公钥加密->length=%d", base64_len);
    //string -> char* -> jstring 返回
    jbyteArray result = charToJByteArray(env, reinterpret_cast<unsigned char *>(base64RSA));
    delete[] base64RSA;
//    jstring result =env->NewStringUTF(base64RSA.c_str());
    return result;
}

/**
 * RSA私钥解密
 * @param env
 * @param obj
 * @param context
 * @param src_
 * @return
 */
jbyteArray
decodeByRSAPriKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA 私钥解密->apk-sha1值验证不通过");
        return charToJByteArray(env, (unsigned char *) sha1_sign_err);
    }
    //char* 转 string
    string base64PrivateKeyString = string(RSAPrivateKey);
//    LOGD("base64PrivateKeyString=%s",base64PrivateKeyString.c_str());
    //生成公钥字符串
    string generatedPrivateKey = generatePrivateKey(base64PrivateKeyString);
//    LOGD("generatedPrivateKey=%s",generatedPrivateKey.c_str());
    //jstring 转 char*
    char *srcChars = jByteArrayToChar(env, src_);
    LOGD("srcChars=%s", srcChars);
    //decode
    char *decodeBase64RSA;
    int decode_len = base64Decode(srcChars, decodeBase64RSA);
    //释放
    LOGD("RSA 私钥解密->释放资源->src_");
    free(srcChars);
    env->DeleteLocalRef(src_);
    if (decode_len == 0) {
        LOGD("RSA 私钥解密->base64解密失败");
        delete[]decodeBase64RSA;
        string err = string("RSA base64解密失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
//    LOGD("decodeBase64RSA=%s",decodeBase64RSA.c_str());
    //解密
    char *origin;
    int len = RsaPriDecrypt(generatedPrivateKey, decodeBase64RSA, origin);
    delete[]decodeBase64RSA;
    if (len == 0) {
        LOGD("RSA 私钥解密->解密失败!");
        delete[] origin;
        string err = string("RSA 私钥解密失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
    LOGD("RSA 私钥解密->origin=%s", origin);
    LOGD("RSA 私钥解密->length=%d", strlen(origin));
    //string -> char* -> jstring 返回
    jbyteArray result = charToJByteArray(env, reinterpret_cast<unsigned char *>(origin));
    delete[] origin;
//    jstring result =env->NewStringUTF(origin.c_str());
    return result;
}

/**
 * RSA 私钥加密
 * @param env
 * @param obj
 * @param context
 * @param src_
 * @return
 */
jbyteArray
encodeByRSAPriKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA 私钥加密->apk-sha1值验证不通过");
        return charToJByteArray(env, (unsigned char *) sha1_sign_err);
    }
    //char* 转 string
    string base64PrivateKeyString = string(RSAPrivateKey);
//    LOGD("base64PrivateKeyString=%s",base64PrivateKeyString.c_str());
    //生成公钥字符串
    string generatedPrivateKey = generatePrivateKey(base64PrivateKeyString);
//    LOGD("generatedPrivateKey=%s",generatedPrivateKey.c_str());
    //jstring 转 char*
    char *srcChars = jByteArrayToChar(env, src_);
//    char *srcChars = (char *)env->GetStringUTFChars(src_,0);
//    LOGD("srcChars=%s",srcChars);
    //char* 转 string
    string srcString = string(srcChars);
//    LOGD("srcString=%s",srcString.c_str());
    LOGD("RSA 私钥加密->释放资源->src_");
    free(srcChars);
    env->DeleteLocalRef(src_);
    //调用RSA加密函数加密
    char *rsaResult;
    int len = RsaPriEncrypt(generatedPrivateKey, srcString, rsaResult);
    LOGD("RSA 私钥加密->rsaResult=%s", rsaResult);
    LOGD("RSA 私钥加密->length=%d", strlen(rsaResult));
    if (len == 0) {
        LOGD("RSA 私钥加密->RSA 私钥加密失败");
        delete[] rsaResult;
        string err = string("RSA 私钥加密失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
//    LOGD("rsaResult=%s",rsaResult.c_str());
    //将密文进行base64
    char *base64RSA;
    int base64_len = base64Encode(rsaResult, base64RSA);
    delete[] rsaResult;
    if (base64_len == 0) {
        LOGD("RSA 私钥加密->base64编码失败");
        delete[]base64RSA;
        string err = string("RSA base64编码失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
    LOGD("RSA 私钥加密->base64RSA=%s", base64RSA);
    LOGD("RSA 私钥加密->length=%d", base64_len);
    //string -> char* -> jstring 返回
    jbyteArray result = charToJByteArray(env, reinterpret_cast<unsigned char *>(base64RSA));
    delete[]base64RSA;
//    jstring result =env->NewStringUTF(base64RSA.c_str());
    return result;
}

/**
 * RSA 公钥解密
 * @param env
 * @param obj
 * @param context
 * @param src_
 * @return
 */
jbyteArray
decodeByRSAPubKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA 公钥解密->apk-sha1值验证不通过");
        return charToJByteArray(env, (unsigned char *) sha1_sign_err);
    }
    //char* 转 string
    string base64PublicKeyString = string(RSAPublicKey);
//    LOGD("base64PublicKeyString=%s",base64PublicKeyString.c_str());
    //生成公钥字符串
    string generatedPublicKey = generatePublicKey(base64PublicKeyString);
//    LOGD("generatedPublicKey=%s",generatedPublicKey.c_str());
    //jstring 转 char*
    char *srcChars = jByteArrayToChar(env, src_);
    LOGD("srcChars=%s", srcChars);
    //decode
    char *decodeBase64RSA;
    int decode_len = base64Decode(srcChars, decodeBase64RSA);
    //释放
    LOGD("RSA 公钥解密->释放资源->src_");
    free(srcChars);
    env->DeleteLocalRef(src_);
    if (decode_len == 0) {
        LOGD("RSA 公钥解密->base64解密失败");
        delete[]decodeBase64RSA;
        string err = string("RSA base64解密失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
//    LOGD("decodeBase64RSA=%s",decodeBase64RSA.c_str());
    //解密
    char *origin;
    int len = RsaPubDecrypt(generatedPublicKey, decodeBase64RSA, origin);
    delete[]decodeBase64RSA;
    if (len == 0) {
        LOGD("RSA 公钥解密->解密失败!");
        delete[] origin;
        string err = string("RSA 公钥解密失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
    LOGD("RSA 公钥解密->origin=%s", origin);
    LOGD("RSA 公钥解密->length=%d", strlen(origin));
    //string -> char* -> jstring 返回
    jbyteArray result = charToJByteArray(env, reinterpret_cast<unsigned char *>(origin));
    delete[] origin;
//    jstring result =env->NewStringUTF(origin.c_str());
    return result;
}

/**
 * RSA 私钥加密
 * @param env
 * @param obj
 * @param context
 * @param src_
 * @param base64PrivateKey
 * @return
 */
jbyteArray
encryptByRSAPriKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_,
                   jbyteArray base64PrivateKey) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA 私钥加密->apk-sha1值验证不通过");
        return charToJByteArray(env, (unsigned char *) sha1_sign_err);
    }
    //jstring 转 char*
    char *base64PrivateKeyChars = jByteArrayToChar(env, base64PrivateKey);
//    LOGD("base64PrivateKeyChars=%s",base64PrivateKeyChars);
    //char* 转 string
    string base64PrivateKeyString = string(base64PrivateKeyChars);
//    LOGD("base64PrivateKeyString=%s",base64PrivateKeyString.c_str());
    //生成公钥字符串
    string generatedPrivateKey = generatePrivateKey(base64PrivateKeyString);
//    LOGD("generatedPrivateKey=%s",generatedPrivateKey.c_str());
    //jstring 转 char*
    char *srcChars = jByteArrayToChar(env, src_);
//    LOGD("srcChars=%s",srcChars);
    //char* 转 string
    string srcString = string(srcChars);
//    LOGD("srcString=%s",srcString.c_str());
    //释放
    LOGD("释放资源->src_");
    free(srcChars);
    env->DeleteLocalRef(src_);
    //调用RSA加密函数加密
    char *rsaResult;
    int len = RsaPriEncrypt(generatedPrivateKey, srcString, rsaResult);
    if (len == 0) {
        LOGD("RSA 私钥加密->RSA 私钥加密失败");
        delete[] rsaResult;
        string err = string("RSA 私钥加密失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
    LOGD("RSA 私钥加密->rsaResult=%s", rsaResult);
    LOGD("RSA 私钥加密->length=%d", strlen(rsaResult));
    //将密文进行base64
    char *base64RSA;
    int base64_len = base64Encode(rsaResult, base64RSA);
    delete[] rsaResult;
    if (base64_len == 0) {
        LOGD("RSA 私钥加密->base64编码失败");
        delete[]base64RSA;
        string err = string("RSA base64编码失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
//    LOGD("base64RSA=%s",base64RSA.c_str());
    //string -> char* -> jstring 返回
    jbyteArray result = charToJByteArray(env, reinterpret_cast<unsigned char *>(base64RSA));
    delete[]base64RSA;
//    jstring result =env->NewStringUTF(base64RSA.c_str());
    return result;
}

/**
 * RSA 公钥解密
 * @param env
 * @param obj
 * @param context
 * @param src_
 * @param base64PublicKey
 * @return
 */
jbyteArray
decryptByRSAPubKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_,
                   jbyteArray base64PublicKey) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA 公钥解密->apk-sha1值验证不通过");
        return charToJByteArray(env, (unsigned char *) sha1_sign_err);
    }
    //jstring 转 char*
    char *base64PublicKeyChars = jByteArrayToChar(env, base64PublicKey);
//    LOGD("base64PublicKeyChars=%s",base64PublicKeyChars);
    //char* 转 string
    string base64PublicKeyString = string(base64PublicKeyChars);
//    LOGD("base64PublicKeyString=%s",base64PublicKeyString.c_str());
    //生成公钥字符串
    string generatedPublicKey = generatePublicKey(base64PublicKeyString);
//    LOGD("generatedPublicKey=%s",generatedPublicKey.c_str());
    //jstring 转 char*
    char *srcChars = jByteArrayToChar(env, src_);
//    LOGD("srcChars=%s",srcChars);
    //decode
    char *decodeBase64RSA;
    int decode_len = base64Decode(srcChars, decodeBase64RSA);
    LOGD("释放资源->src_");
    free(srcChars);
    env->DeleteLocalRef(src_);
    if (decode_len == 0) {
        LOGD("RSA 公钥解密->base64解密失败");
        delete[]decodeBase64RSA;
        string err = string("RSA base64解密失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
//    LOGD("decodeBase64RSA=%s",decodeBase64RSA.c_str());
    //解密
    char *origin;
    int len = RsaPubDecrypt(generatedPublicKey, decodeBase64RSA, origin);
    delete[]decodeBase64RSA;
    if (len == 0) {
        LOGD("RSA 公钥解密->解密失败!");
        delete[] origin;
        string err = string("RSA 公钥解密失败！");
        return charToJByteArray(env, (unsigned char *) err.c_str());
    }
    LOGD("RSA 公钥解密->origin=%s", origin);
    LOGD("RSA 公钥解密->length=%d", strlen(origin));
    //string -> char* -> jstring 返回
    jbyteArray result = charToJByteArray(env, (unsigned char *) origin);
    delete[] origin;
//    jstring result =env->NewStringUTF(origin.c_str());
    return result;
}

/**
 * MD5加密
 */
jstring
md5(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("MD5加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //====================将秘钥拼接到数据源末端=========================
    jbyte *srcChars = env->GetByteArrayElements(src_, NULL);
    jsize src_Lens = env->GetArrayLength(src_);
//    LOGD("SHA1加密->源数据->sss %s ", srcs);
    char *chars = NULL;
    chars = new char[src_Lens + 1];
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcChars, src_Lens);
    chars[src_Lens] = 0;

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA1加密->源数据-> %s ", src);
    int src_Len = strlen(src);
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
    delete[]chars;//new/delete、new[]/delete[] 要配套使用总是没错的
    LOGD("释放资源->src_");
    env->DeleteLocalRef(src_);
    free(srcChars);
    //string -> char* -> jstring 返回
    jstring resultHex = CStr2Jstring(env, hex);
//    jstring result =env->NewStringUTF(base64RSA.c_str());
    return resultHex;
}
//========================================动态注册==================================================
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

举个例子:
传入的java参数有两个 分别是 int 和 long[] 函数返回值为 String
即函数的定义为：String getString(int a ,long[] b)
签名就应该是 :"(I[J)Ljava/lang/String;"(不要漏掉英文分号)
如果有内部类 则用 $ 来分隔 如:Landroid/os/FileUtils$FileStatus;
*/
static JNINativeMethod getMethods[] = {
        {"encodeByHmacSHA1",    "(Landroid/content/Context;[B)Ljava/lang/String;", (void *) encodeByHmacSHA1},
        {"encodeBySHA1",        "(Landroid/content/Context;[B)Ljava/lang/String;", (void *) encodeBySHA1},
        {"encodeBySHA224",      "(Landroid/content/Context;[B)Ljava/lang/String;", (void *) encodeBySHA224},
        {"encodeBySHA256",      "(Landroid/content/Context;[B)Ljava/lang/String;", (void *) encodeBySHA256},
        {"encodeBySHA384",      "(Landroid/content/Context;[B)Ljava/lang/String;", (void *) encodeBySHA384},
        {"encodeBySHA512",      "(Landroid/content/Context;[B)Ljava/lang/String;", (void *) encodeBySHA512},
        {"encodeByAESEncrypt",  "(Landroid/content/Context;[B)[B",                 (void *) encodeByAESEncrypt},
        {"decodeByAESEncrypt",  "(Landroid/content/Context;[B)[B",                 (void *) decodeByAESEncrypt},
        {"encryptByAESEncrypt", "(Landroid/content/Context;[B[B)[B",               (void *) encryptByAESEncrypt},
        {"decryptByAESEncrypt", "(Landroid/content/Context;[B[B)[B",               (void *) decryptByAESEncrypt},
        {"encodeByAESCipher",   "(Landroid/content/Context;[B)[B",                 (void *) encodeByAESCipher},
        {"decodeByAESCipher",   "(Landroid/content/Context;[B)[B",                 (void *) decodeByAESCipher},
        {"encryptAESCipher",    "(Landroid/content/Context;[B[B)[B",               (void *) encryptAESCipher},
        {"decryptAESCipher",    "(Landroid/content/Context;[B[B)[B",               (void *) decryptAESCipher},
        {"readAssets",          "(Landroid/content/Context;Ljava/lang/String;)[B", (void *) readAssets},
        {"generateRSAKey",      "(Landroid/content/Context;)[Ljava/lang/String;",  (void *) generateRSAKey},
        {"encodeByRSAPubKey",   "(Landroid/content/Context;[B)[B",                 (void *) encodeByRSAPubKey},
        {"decodeByRSAPriKey",   "(Landroid/content/Context;[B)[B",                 (void *) decodeByRSAPriKey},
        {"encodeByRSAPriKey",   "(Landroid/content/Context;[B)[B",                 (void *) encodeByRSAPriKey},
        {"decodeByRSAPubKey",   "(Landroid/content/Context;[B)[B",                 (void *) decodeByRSAPubKey},
        {"encryptRSA",          "(Landroid/content/Context;[B[B)[B",               (void *) encryptByRSA},
        {"decryptRSA",          "(Landroid/content/Context;[B[B)[B",               (void *) decryptByRSA},
        {"encodeByRSAPriKey",   "(Landroid/content/Context;[B[B)[B",               (void *) encryptByRSAPriKey},
        {"decodeByRSAPubKey",   "(Landroid/content/Context;[B[B)[B",               (void *) decryptByRSAPubKey},
        {"md5",                 "(Landroid/content/Context;[B)Ljava/lang/String;", (void *) md5},
        {"sha1OfApk",           "(Landroid/content/Context;)Ljava/lang/String;",   (void *) getSha1OfApk},
        {"verifySha1OfApk",     "(Landroid/content/Context;)Z",                    (void *) validateSha1OfApk},
};

//此函数通过调用RegisterNatives方法来注册我们的函数
static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *getMethods,
                                 int methodsNum) {
    jclass clazz;
    //找到声明native方法的类
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    //注册函数 参数：java类 所要注册的函数数组 注册函数的个数
    if (env->RegisterNatives(clazz, getMethods, methodsNum) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static int registerNatives(JNIEnv *env) {
    //指定类的路径，通过FindClass 方法来找到对应的类
    const char *className = "com/base/encrypt/JniUtils";
    return registerNativeMethods(env, className, getMethods,
                                 sizeof(getMethods) / sizeof(getMethods[0]));
}
//回调函数
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    //获取JNIEnv
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    assert(env != NULL);
    //注册函数 registerNatives ->registerNativeMethods ->env->RegisterNatives
    if (!registerNatives(env)) {
        return JNI_ERR;
    }
    //返回jni 的版本 
    return JNI_VERSION_1_6;
}
//========================================动态注册==============================================