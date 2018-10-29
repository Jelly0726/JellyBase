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
//换成你自己的key值
const char *key = "758DA6688786C0D2C7";
//换成你自己的RSAPubKey值
const char *RSAPubKey = "-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC0HjcCrWfRY8o4i2NwIvMJdP5l\nzuj7HOOZpjezE39a32fMjmztVtqLjso5MXjwwB5JiAfhOkFhVnNGPAmu0f5HOQly\nx88QgczokVrB73uj6HLUmX/SuFCbUh+YLJ4ZWp0g2Go6qwH2O5b3IQeENqTrIo4l\nrJTSLCzjnQ/QZYUuewIDAQAB\n-----END PUBLIC KEY-----\n";

//换成你自己的RSAPrivateKey值
const char *RSAPrivateKey = "-----BEGIN PRIVATE KEY-----\nMIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALQeNwKtZ9FjyjiL\nY3Ai8wl0/mXO6Psc45mmN7MTf1rfZ8yObO1W2ouOyjkxePDAHkmIB+E6QWFWc0Y8\nCa7R/kc5CXLHzxCBzOiRWsHve6PoctSZf9K4UJtSH5gsnhlanSDYajqrAfY7lvch\nB4Q2pOsijiWslNIsLOOdD9BlhS57AgMBAAECgYAC82fFTx/0nzo7OOq1IJgeCeD3\nvARhxh8NxQUD6wgwGJmJEWBEIc3MxtcV9B3eG9ejLsD/oJuyQ4n57EE1sFJbwziB\nvTLwmL+yLEpghtc/J2Xo1Y22E2Czu+VI0d/WUy3MN2I4a601xxA6rg1tRvPSftDW\n49fDCAhUb5yYLZgo0QJBAPzYRxsoPtoR8LbarPBuz/dwT9U3kiGT5yXVzjv0wkmp\nHblFBi2JY37f2TJ8moLi9hWAUHwEbSrjaxZM/g5GhhkCQQC2XZvPJHGPf8aAKUFz\nx0kZDUW1m436xiKVwHBAJGeYCYk87NJwNOiCN7HafRWkjbZtx2fS3M2lzPmxWqzw\n4COzAkAZpvOn3LBrvXA3jP4IsqVkzD89OZMY1wGXhBaVXKKtiHvchRU4X3z5rUpC\n5gNjDhW7XrZLrsNIm6QMsikAV8VZAkBiZCjvXstCT/8qIJgmvkvLD2Uf8bhtp777\nKuOlR774wZRg4ak8Tt9velsj9b7alHbrzd1PYEA4B1pkfPa300aPAkBbr6fDTKhC\nsckhQPDdrLBxtjGAvJ0Rzhk54FhHa5FLX2jdDPttH/q1QZH4w1TGV68hWogCcfsE\nBha/h6Ag4H5V\n-----END PRIVATE KEY-----\n";

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_base_encrypt_JniUtils_encodeByHmacSHA1(JNIEnv *env, jobject instance, jobject context, jbyteArray src_) {
    LOGD("HmacSHA1->HMAC: Hash-based Message Authentication Code，即基于Hash的消息鉴别码");
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewByteArray(0);
    }
    const char *key = "jellyApp@22383243*335457968";
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    unsigned int result_len;
    unsigned char result[EVP_MAX_MD_SIZE];
    char buff[EVP_MAX_MD_SIZE];
    char hex[EVP_MAX_MD_SIZE];

    LOGD("HmacSHA1->调用函数进行哈希运算");
    HMAC(EVP_sha1(), key, strlen(key), (unsigned char *) src, src_Len, result, &result_len);

    strcpy(hex, "");
    LOGD("HmacSHA1->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != result_len; i++) {
        sprintf(buff, "%02x", result[i]);
        strcat(hex, buff);
    }
    LOGD("HmacSHA1->%s", hex);

    LOGD("HmacSHA1->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray signature = env->NewByteArray(strlen(hex));
    LOGD("HmacSHA1->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(signature, 0, strlen(hex), (jbyte *) hex);

    return signature;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_base_encrypt_JniUtils_encodeBySHA1(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewStringUTF("非法调用");
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
    LOGD("SHA1->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA_DIGEST_LENGTH];
    char hex[SHA_DIGEST_LENGTH * 2];
    unsigned char digest[SHA_DIGEST_LENGTH];

//    SHA1((unsigned char *)src, src_Len, digest);

    SHA_CTX ctx;
    SHA1_Init(&ctx);
    LOGD("SHA1->正在进行SHA1哈希运算");
    SHA1_Update(&ctx, src, src_Len);
    SHA1_Final(digest, &ctx);

    OPENSSL_cleanse(&ctx, sizeof(ctx));

    strcpy(hex, "");
    LOGD("SHA1->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
    }
    LOGD("SHA1->%s", hex);
    return env->NewStringUTF(hex);
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_base_encrypt_JniUtils_encodeBySHA224(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewStringUTF("非法调用");
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
    LOGD("SHA224->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA224_DIGEST_LENGTH];
    char hex[SHA224_DIGEST_LENGTH * 2];
    unsigned char digest[SHA224_DIGEST_LENGTH];

//    SHA224((unsigned char *)src, src_Len, digest);

    SHA256_CTX ctx;
    SHA224_Init(&ctx);
    LOGD("SHA224->正在进行SHA224哈希运算");
    SHA224_Update(&ctx, src, src_Len);
    SHA224_Final(digest, &ctx);

    OPENSSL_cleanse(&ctx, sizeof(ctx));

    strcpy(hex, "");
    LOGD("SHA224->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
    }
    LOGD("SHA224->%s", hex);

    return env->NewStringUTF(hex);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_base_encrypt_JniUtils_encodeBySHA256(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewStringUTF("非法调用");
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
    LOGD("SHA256->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA256_DIGEST_LENGTH];
    char hex[SHA256_DIGEST_LENGTH * 2];
    unsigned char digest[SHA256_DIGEST_LENGTH];

//    SHA256((unsigned char *)src, src_Len, digest);

    SHA256_CTX ctx;
    SHA256_Init(&ctx);
    LOGD("SHA256->正在进行SHA256哈希运算");
    SHA256_Update(&ctx, src, src_Len);
    SHA256_Final(digest, &ctx);

    OPENSSL_cleanse(&ctx, sizeof(ctx));

    strcpy(hex, "");
    LOGD("SHA256->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
    }
    LOGD("SHA256->%s", hex);

    return env->NewStringUTF(hex);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_base_encrypt_JniUtils_encodeBySHA384(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewStringUTF("非法调用");
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
    LOGD("SHA384->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA384_DIGEST_LENGTH];
    char hex[SHA384_DIGEST_LENGTH * 2];
    unsigned char digest[SHA384_DIGEST_LENGTH];

//    SHA384((unsigned char *)src, src_Len, digest);

    SHA512_CTX ctx;
    SHA384_Init(&ctx);
    LOGD("SHA384->正在进行SHA384哈希运算");
    SHA384_Update(&ctx, src, src_Len);
    SHA384_Final(digest, &ctx);

    OPENSSL_cleanse(&ctx, sizeof(ctx));

    strcpy(hex, "");
    LOGD("SHA384->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
    }
    LOGD("SHA384->%s", hex);

    return env->NewStringUTF(hex);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_base_encrypt_JniUtils_encodeBySHA512(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewStringUTF("非法调用");
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
    LOGD("SHA512->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================

    char buff[SHA512_DIGEST_LENGTH];
    char hex[SHA512_DIGEST_LENGTH * 2];
    unsigned char digest[SHA512_DIGEST_LENGTH];

//    SHA512((unsigned char *)src, src_Len, digest);

    SHA512_CTX ctx;
    SHA512_Init(&ctx);
    LOGD("SHA512->正在进行SHA256哈希运算");
    SHA512_Update(&ctx, src, src_Len);
    SHA512_Final(digest, &ctx);

    OPENSSL_cleanse(&ctx, sizeof(ctx));

    strcpy(hex, "");
    LOGD("SHA512->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
    }
    LOGD("SHA512->%s", hex);

    return env->NewStringUTF(hex);
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_base_encrypt_JniUtils_encodeByAES(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewByteArray(0);
    }
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(key));
    env->SetByteArrayRegion(keys_, 0, strlen(key), (jbyte*)key );

    LOGD("AES->对称密钥，也就是说加密和解密用的是同一个密钥");
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
    LOGD("AES->指定加密算法，初始化加密key/iv");
    EVP_EncryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, (const unsigned char *) keys, iv);
    LOGD("AES->对数据进行加密运算");
    EVP_EncryptUpdate(&ctx, out, &outlen, (const unsigned char *) src, src_Len);
    cipherText_len = outlen;

    LOGD("AES->结束加密运算");
    EVP_EncryptFinal_ex(&ctx, out + outlen, &outlen);
    cipherText_len += outlen;

    LOGD("AES->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);

    LOGD("AES->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(cipherText_len);
    LOGD("AES->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_len, (jbyte *) out);
    LOGD("AES->释放内存");
    free(out);

    return cipher;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_base_encrypt_JniUtils_decodeByAES(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewByteArray(0);
    }
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(key));
    env->SetByteArrayRegion(keys_, 0, strlen(key), (jbyte*)key );

    LOGD("AES->对称密钥，也就是说加密和解密用的是同一个密钥");
    const unsigned char *iv = (const unsigned char *) "gjdljgaj748564df";
    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int outlen = 0, plaintext_len = 0;

    unsigned char *out  = (unsigned char *) malloc(src_Len);
    memset(out, 0, src_Len);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);
    LOGD("AES->指定解密算法，初始化解密key/iv");
    EVP_DecryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, (const unsigned char *) keys, iv);
    LOGD("AES->对数据进行解密运算");
    EVP_DecryptUpdate(&ctx, out, &outlen, (const unsigned char *) src, src_Len);
    plaintext_len = outlen;

    LOGD("AES->结束解密运算");
    EVP_DecryptFinal_ex(&ctx, out + outlen, &outlen);
    plaintext_len += outlen;

    LOGD("AES->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);

    LOGD("AES->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(plaintext_len);
    LOGD("AES->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, plaintext_len, (jbyte *) out);
    LOGD("AES->释放内存");
    free(out);

    return cipher;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_base_encrypt_JniUtils_encodeByRSAPubKey(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewByteArray(0);
    }
    LOGD("RSA->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(RSAPubKey));
    env->SetByteArrayRegion(keys_, 0, strlen(RSAPubKey), (jbyte*)RSAPubKey );

    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, cipherText_offset = 0, desText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

    LOGD("RSA->从字符串读取RSA公钥");
    keybio = BIO_new_mem_buf(keys, -1);
    LOGD("RSA->从bio结构中得到RSA结构");
    rsa = PEM_read_bio_RSA_PUBKEY(keybio, NULL, NULL, NULL);
    LOGD("RSA->释放BIO");
    BIO_free_all(keybio);

    int flen = RSA_size(rsa);
    desText_len = flen * (src_Len / (flen - 11) + 1);

    unsigned char *srcOrigin = (unsigned char *) malloc(src_Len);
    unsigned char *cipherText = (unsigned char *) malloc(flen);
    unsigned char *desText = (unsigned char *) malloc(desText_len);
    memset(desText, 0, desText_len);

    memset(srcOrigin, 0, src_Len);
    memcpy(srcOrigin, src, src_Len);

    LOGD("RSA->对数据进行公钥加密运算");
    //RSA_PKCS1_PADDING最大加密长度：128-11；RSA_NO_PADDING最大加密长度：128
    for (int i = 0; i <= src_Len / (flen - 11); i++) {
        src_flen = (i == src_Len / (flen - 11)) ? src_Len % (flen - 11) : flen - 11;
        if (src_flen == 0) {
            break;
        }

        memset(cipherText, 0, flen);
        ret = RSA_public_encrypt(src_flen, srcOrigin + src_offset, cipherText, rsa, RSA_PKCS1_PADDING);

        memcpy(desText + cipherText_offset, cipherText, ret);
        cipherText_offset += ret;
        src_offset += src_flen;
    }

    RSA_free(rsa);
    LOGD("RSA->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(cipherText_offset);
    LOGD("RSA->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_offset, (jbyte *) desText);
    LOGD("RSA->释放内存");
    free(srcOrigin);
    free(cipherText);
    free(desText);

    return cipher;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_base_encrypt_JniUtils_decodeByRSAPrivateKey(JNIEnv *env, jobject instance,jobject context,jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewByteArray(0);
    }
    LOGD("RSA->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(RSAPrivateKey));
    env->SetByteArrayRegion(keys_, 0, strlen(RSAPrivateKey), (jbyte*)RSAPrivateKey );

    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, plaintext_offset = 0, descText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

    LOGD("RSA->从字符串读取RSA私钥");
    keybio = BIO_new_mem_buf(keys, -1);
    LOGD("RSA->从bio结构中得到RSA结构");
    rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, NULL);
    LOGD("RSA->释放BIO");
    BIO_free_all(keybio);

    int flen = RSA_size(rsa);
    descText_len = (flen - 11) * (src_Len / flen + 1);

    unsigned char *srcOrigin = (unsigned char *) malloc(src_Len);
    unsigned char *plaintext = (unsigned char *) malloc(flen - 11);
    unsigned char *desText = (unsigned char *) malloc(descText_len);
    memset(desText, 0, descText_len);

    memset(srcOrigin, 0, src_Len);
    memcpy(srcOrigin, src, src_Len);

    LOGD("RSA->对数据进行私钥解密运算");
    //一次性解密数据最大字节数RSA_size
    for (int i = 0; i <= src_Len / flen; i++) {
        src_flen = (i == src_Len / flen) ? src_Len % flen : flen;
        if (src_flen == 0) {
            break;
        }

        memset(plaintext, 0, flen - 11);
        ret = RSA_private_decrypt(src_flen, srcOrigin + src_offset, plaintext, rsa, RSA_PKCS1_PADDING);

        memcpy(desText + plaintext_offset, plaintext, ret);
        plaintext_offset += ret;
        src_offset += src_flen;
    }

    RSA_free(rsa);
    LOGD("RSA->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(plaintext_offset);
    LOGD("RSA->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, plaintext_offset, (jbyte *) desText);
    LOGD("RSA->释放内存");
    free(srcOrigin);
    free(plaintext);
    free(desText);

    return cipher;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_base_encrypt_JniUtils_encodeByRSAPrivateKey(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewByteArray(0);
    }
    LOGD("RSA->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(RSAPrivateKey));
    env->SetByteArrayRegion(keys_, 0, strlen(RSAPrivateKey), (jbyte*)RSAPrivateKey );

    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, cipherText_offset = 0, desText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

    LOGD("RSA->从字符串读取RSA私钥");
    keybio = BIO_new_mem_buf(keys, -1);
    LOGD("RSA->从bio结构中得到RSA结构");
    rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, NULL);
    LOGD("RSA->释放BIO");
    BIO_free_all(keybio);

    int flen = RSA_size(rsa);
    desText_len = flen * (src_Len / (flen - 11) + 1);

    unsigned char *srcOrigin = (unsigned char *) malloc(src_Len);
    unsigned char *cipherText = (unsigned char *) malloc(flen);
    unsigned char *desText = (unsigned char *) malloc(desText_len);
    memset(desText, 0, desText_len);

    memset(srcOrigin, 0, src_Len);
    memcpy(srcOrigin, src, src_Len);

    LOGD("RSA->对数据进行私钥加密运算");
    //RSA_PKCS1_PADDING最大加密长度：128-11；RSA_NO_PADDING最大加密长度：128
    for (int i = 0; i <= src_Len / (flen - 11); i++) {
        src_flen = (i == src_Len / (flen - 11)) ? src_Len % (flen - 11) : flen - 11;
        if (src_flen == 0) {
            break;
        }

        memset(cipherText, 0, flen);
        ret = RSA_private_encrypt(src_flen, srcOrigin + src_offset, cipherText, rsa, RSA_PKCS1_PADDING);

        memcpy(desText + cipherText_offset, cipherText, ret);
        cipherText_offset += ret;
        src_offset += src_flen;
    }

    RSA_free(rsa);
    LOGD("RSA->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(cipherText_offset);
    LOGD("RSA->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_offset, (jbyte *) desText);
    LOGD("RSA->释放内存");
    free(srcOrigin);
    free(cipherText);
    free(desText);

    return cipher;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_base_encrypt_JniUtils_decodeByRSAPubKey(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewByteArray(0);
    }
    LOGD("RSA->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
    keys_ =env->NewByteArray(strlen(RSAPubKey));
    env->SetByteArrayRegion(keys_, 0, strlen(RSAPubKey), (jbyte*)RSAPubKey );

    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, plaintext_offset = 0, desText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

    LOGD("RSA->从字符串读取RSA公钥");
    keybio = BIO_new_mem_buf(keys, -1);
    LOGD("RSA->从bio结构中得到RSA结构");
    rsa = PEM_read_bio_RSA_PUBKEY(keybio, NULL, NULL, NULL);
    LOGD("RSA->释放BIO");
    BIO_free_all(keybio);

    int flen = RSA_size(rsa);
    desText_len = (flen - 11) * (src_Len / flen + 1);

    unsigned char *srcOrigin = (unsigned char *) malloc(src_Len);
    unsigned char *plaintext = (unsigned char *) malloc(flen - 11);
    unsigned char *desText = (unsigned char *) malloc(desText_len);
    memset(desText, 0, desText_len);

    memset(srcOrigin, 0, src_Len);
    memcpy(srcOrigin, src, src_Len);

    LOGD("RSA->对数据进行公钥解密运算");
    //一次性解密数据最大字节数RSA_size
    for (int i = 0; i <= src_Len / flen; i++) {
        src_flen = (i == src_Len / flen) ? src_Len % flen : flen;
        if (src_flen == 0) {
            break;
        }

        memset(plaintext, 0, flen - 11);
        ret = RSA_public_decrypt(src_flen, srcOrigin + src_offset, plaintext, rsa, RSA_PKCS1_PADDING);

        memcpy(desText + plaintext_offset, plaintext, ret);
        plaintext_offset += ret;
        src_offset += src_flen;
    }

    RSA_free(rsa);
    LOGD("RSA->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(plaintext_offset);
    LOGD("RSA->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, plaintext_offset, (jbyte *) desText);
    LOGD("RSA->释放内存");
    free(srcOrigin);
    free(plaintext);
    free(desText);

    return cipher;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_base_encrypt_JniUtils_signByRSAPrivateKey(JNIEnv *env, jobject instance,jobject context,  jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewByteArray(0);
    }
    LOGD("RSA->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
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

    LOGD("RSA->从字符串读取RSA公钥");
    keybio = BIO_new_mem_buf(keys, -1);
    LOGD("RSA->从bio结构中得到RSA结构");
    rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, NULL);
    LOGD("RSA->释放BIO");
    BIO_free_all(keybio);

    unsigned char *sign = (unsigned char *) malloc(129);
    memset(sign, 0, 129);

    LOGD("RSA->对数据进行摘要运算");
    SHA1((const unsigned char *) src, src_Len, digest);
    LOGD("RSA->对摘要进行RSA私钥加密");
    RSA_sign(NID_sha1, digest, SHA_DIGEST_LENGTH, sign, &siglen, rsa);

    RSA_free(rsa);
    LOGD("RSA->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(siglen);
    LOGD("RSA->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, siglen, (jbyte *) sign);
    LOGD("RSA->释放内存");
    free(sign);

    return cipher;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_base_encrypt_JniUtils_verifyByRSAPubKey(JNIEnv *env, jobject instance,jobject context, jbyteArray src_, jbyteArray sign_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return -1;
    }
    LOGD("RSA->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
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

    LOGD("RSA->从字符串读取RSA公钥");
    keybio = BIO_new_mem_buf(keys, -1);
    LOGD("RSA->从bio结构中得到RSA结构");
    rsa = PEM_read_bio_RSA_PUBKEY(keybio, NULL, NULL, NULL);
    LOGD("RSA->释放BIO");
    BIO_free_all(keybio);

    LOGD("RSA->对数据进行摘要运算");
    SHA1((const unsigned char *) src, src_Len, digest);
    LOGD("RSA->对摘要进行RSA公钥验证");
    ret = RSA_verify(NID_sha1, digest, SHA_DIGEST_LENGTH, (const unsigned char *) sign, siglen, rsa);

    RSA_free(rsa);
    LOGD("RSA->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA->从jni释放数据指针");
    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);
    env->ReleaseByteArrayElements(sign_, sign, 0);

    return ret;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_base_encrypt_JniUtils_xOr(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewByteArray(0);
    }
    LOGD("XOR->异或加解密: 相同为假，不同为真");
    const char keys[] = "jelly20180829";
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    char *chs = (char *) malloc(src_Len);
    memset(chs, 0, src_Len);
    memcpy(chs, src, src_Len);

    LOGD("XOR->对数据进行异或运算");
    for (int i = 0; i < src_Len; i++) {
        *chs = *chs ^ keys[i % strlen(keys)];
        chs++;
    }
    chs = chs - src_Len;

    LOGD("XOR->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(src_Len);
    LOGD("XOR->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, src_Len, (const jbyte *) chs);
    LOGD("XOR->释放内存");
    free(chs);

    return cipher;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_base_encrypt_JniUtils_md5(JNIEnv *env, jobject instance,jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1->apk-sha1值验证不通过");
        return env->NewStringUTF("非法调用");
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
    LOGD("MD5->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey="&key=";
    char* src = (char*)malloc(src_Lens +strlen(Skey) + strlen(key) + 1);
    strcpy(src,chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("源数据-> %s ", src);
    jsize src_Len = strlen(src);
    //====================将秘钥拼接到数据源末端=========================
    LOGD("MD5->信息摘要算法第五版");

    char buff[3] = {'\0'};
    char hex[33] = {'\0'};
    unsigned char digest[MD5_DIGEST_LENGTH];

//    MD5((const unsigned char *) src, src_Len, digest);

    MD5_CTX ctx;
    MD5_Init(&ctx);
    LOGD("MD5->进行MD5信息摘要运算");
    MD5_Update(&ctx, src, src_Len);
    MD5_Final(digest, &ctx);

    strcpy(hex, "");
    LOGD("MD5->把哈希值按%%02x格式定向到缓冲区");
    for (int i = 0; i != sizeof(digest); i++) {
        sprintf(buff, "%02x", digest[i]);
        strcat(hex, buff);
    }
    LOGD("MD5->%s", hex);
    return env->NewStringUTF(hex);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_base_encrypt_JniUtils_sha1OfApk(JNIEnv *env, jobject instance, jobject context) {
    return env->NewStringUTF(sha1OfApk(env, context));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_base_encrypt_JniUtils_verifySha1OfApk(JNIEnv *env, jobject instance, jobject context) {
    return verifySha1OfApk(env, context);
}