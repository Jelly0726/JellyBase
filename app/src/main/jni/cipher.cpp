//
// Created by Phoenix on 2017/6/25.
//
#include <jni.h>
#include <string>
#include <openssl/hmac.h>
#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <openssl/md5.h>
#include <openssl/evp.h>
#include <openssl/bio.h>
#include <openssl/buffer.h>
#include "endorse.h"
#include <assert.h>
#include <time.h>
#include <iostream>

using std::string;
// ---- rsa非对称加解密 ---- //
#define KEY_LENGTH  1024               // 密钥长度
#define PUB_KEY_FILE "pubkey.pem"    // 公钥路径
#define PRI_KEY_FILE "prikey.pem"    // 私钥路径
//换成你自己的key值
const char *key = "758DA6688786C0D2C7";//"brLxpmIzzN6o7JDW";//"758DA6688786C0D2C7";//"QKBgQC0HjcCrWfRY8o4i2";
//换成你自己的RSAPubKey值
const char *RSAPubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoWPzLsJq9/aozb4EJKytHNT07nFjDTUrP/8dlYyzI0KqFqeiYi9f3e96USm0bOjR4TJkzSq8QN92Maej/2VmgrZqu+6e47eXb6/nWIzaDh+Ae06YguUJij+yfIAagLiGAv2kHXz+w10wiht5Jt1H9gIEtS3+jwf5P/a0tQhBgiQIDAQAB";

//换成你自己的RSAPrivateKey值
const char *RSAPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJSLLZmJ6F3gznNJiC/ZNXA6Bmn0m1YgnsvaUnZRsGOOso01ydFDwLePI35jnS0/RKAtP7GQJK0DAr/o6KPzCpZAL6zFaviYb5GBthrqox8RikDr+VvzG5KN+Z9UD2pzEtmHU0iFc3PT66oAAu4Ss9qVDoWOoOUAUiiI4k+MMEU1AgMBAAECgYAhKzrRcBPs8ofnAmJgnNXr62kHO9F71+jdiDClrvP+Jx0DnyEjk0dzNYktbbzpH5mJUtFIKvGlmGiCxdU81sZkERmiJ0pGf/MszFjYiFwvzZYwHRW2swOG+cYsJhlZjAnn1xcTX9thB4siZ53/k7tvR9YAOwhyLIC/qD89+exAOQJBAPYGnxMv9dOVmlJK5wiMQnGI7ZT3+xa8ddH62JyHukFvb1LVncOa8SQubUD5eoJoJ32eBxbyt4Y3DFJxI10wS/sCQQCakNaKX2+cD+/8e+BHMG85fk3t+jW1iC/rYe5IUKjtY2QHqBArEtF+/9p9UwIbAGPJ67eVTXPRu3PxU7WphTyPAkEAkrHuBf3R4UBRzQG2ckVXlOTlbK7UO4FR60tb/zF64Gt2gHi44hov8LfyEwzufHVoHqGsboV44oFOSpYFVRpoIwJAXa/lGsJ2ODZA1N2ROBVXlZXFTrYW0A3YXehiMlsRybIw86MfCbzCVyRmHwitgghedAn4oPrtdPcWc/S1bCdiaQJAbjcE5f8fDosRrV8A3KWkaflaJHahxPdqPgVeHNNpziy2ZjJ9N9X23GlPy57XtheVX8EBrxO377xIxz9tYsJQyw==";
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
std::string base64Encode(const std::string &decoded_bytes) {
    BIO *bio, *b64;
    BUF_MEM *bufferPtr;
    b64 = BIO_new(BIO_f_base64());
    //不换行
    BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL);
    bio = BIO_new(BIO_s_mem());
    bio = BIO_push(b64, bio);
    //encode
    BIO_write(bio, decoded_bytes.c_str(), (int) decoded_bytes.length());
    BIO_flush(bio);
    BIO_get_mem_ptr(bio, &bufferPtr);
    //这里的第二个参数很重要，必须赋值 第二个参数表示长度，不能少，
    // 否则base64后的字符串长度会出现异常，导致decode的时候末尾会出现一大堆的乱码，而网上大多数的代码，是缺失这一个参数的。
    std::string result(bufferPtr->data, bufferPtr->length);
    BIO_free_all(bio);
    return result;

}

/**
 * base64 解码
 * @param encoded_bytes
 * @return
 */
std::string base64Decode(const std::string &encoded_bytes) {
    BIO *bioMem, *b64;
    bioMem = BIO_new_mem_buf((void *) encoded_bytes.c_str(), -1);
    b64 = BIO_new(BIO_f_base64());
    BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL);
    bioMem = BIO_push(b64, bioMem);
    //获得解码长度
    size_t buffer_length = BIO_get_mem_data(bioMem, NULL);
    char *decode = (char *) malloc(buffer_length + 1);
    //填充0
    memset(decode, 0, buffer_length + 1);
    BIO_read(bioMem, (void *) decode, (int) buffer_length);
    static std::string decoded_bytes(decode);
    BIO_free_all(bioMem);
    return decoded_bytes;
}

/**
 * 根据公钥base64字符串（没换行）生成公钥文本内容
 * @param base64EncodedKey
 * @return
 */
std::string generatePublicKey(std::string base64EncodedKey) {
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
}

/**
 * 根据私钥base64字符串（没换行）生成私钥文本内容
 * @param base64EncodedKey
 * @return
 */
std::string generatePrivateKey(std::string base64EncodedKey) {
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
}

/**
 * 函数方法生成密钥对
 * @param env
 * @param obj
 * @param context
 * @return false 生成失败  true 生成成功
 */
jboolean generateRSAKey(JNIEnv *env, jobject obj, jobject context/*std::string strKey[2]*/) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1加密->apk-sha1值验证不通过");
        return static_cast<jboolean>(false);
    }
    // 公私密钥对
    size_t pri_len;
    size_t pub_len;
    char *pri_key = NULL;
    char *pub_key = NULL;
    // 生成密钥对
    RSA *keypair = RSA_new();
    int ret = 0;
    BIGNUM* bne = BN_new();
    ret=BN_set_word(bne,RSA_F4);
    ret = RSA_generate_key_ex(keypair,KEY_LENGTH,bne,NULL);
//    RSA *keypair = RSA_generate_key(KEY_LENGTH, RSA_F4, NULL, NULL);
    if(ret!=1){
        return static_cast<jboolean>(false);
    }
    BIO *pri = BIO_new(BIO_s_mem());
    BIO *pub = BIO_new(BIO_s_mem());

    PEM_write_bio_RSAPrivateKey(pri, keypair, NULL, NULL, 0, NULL, NULL);
    PEM_write_bio_RSAPublicKey(pub, keypair);

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
//    strKey[0] = pub_key;
//    strKey[1] = pri_key;

    // 存储到磁盘（这种方式存储的是begin rsa public key/ begin rsa private key开头的）
    FILE *pubFile = fopen(PUB_KEY_FILE, "w");
    if (pubFile == NULL) {
        assert(false);
        return static_cast<jboolean>(false);
    }
    fputs(pub_key, pubFile);
    fclose(pubFile);

    FILE *priFile = fopen(PRI_KEY_FILE, "w");
    if (priFile == NULL) {
        assert(false);
        return static_cast<jboolean>(false);
    }
    fputs(pri_key, priFile);
    fclose(priFile);

    // 内存释放
    RSA_free(keypair);
    BIO_free_all(pub);
    BIO_free_all(pri);

    free(pri_key);
    free(pub_key);
    return static_cast<jboolean>(true);
}

/**
 * 获取公钥
 * @param env
 * @param obj
 * @param context
 * @return
 */
jstring getPubRSAKey(JNIEnv *env, jobject obj, jobject context) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA1加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
//    const char* path=env->GetStringUTFChars(str,JNI_FALSE);
//    FILE* file = fopen(path,"r");
//    if(file==NULL){
//        LOGE("file open failed");
//    }
//    char buffer[1024]={0};
//    while(fread(buffer, sizeof(char),1024,file)!=0){
//        LOGE("content:%s",buffer);
//    }
//    if(file!=NULL){
//        fclose(file);
//    }
//    env->ReleaseStringUTFChars(str,path);
}

/**
 * 获取私钥
 * @param env
 * @param obj
 * @param context
 * @return
 */
jstring getPriRSAKey(JNIEnv *env, jobject obj, jobject context) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA1加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
//    const char* path=env->GetStringUTFChars(str,JNI_FALSE);
//    FILE* file = fopen(path,"r");
//    if(file==NULL){
//        LOGE("file open failed");
//    }
//    char buffer[1024]={0};
//    while(fread(buffer, sizeof(char),1024,file)!=0){
//        LOGE("content:%s",buffer);
//    }
//    if(file!=NULL){
//        fclose(file);
//    }
//    env->ReleaseStringUTFChars(str,path);
}

/**
 * 使用公钥对明文加密
 * @param publicKey
 * @param from
 * @return
 */
std::string encryptRSA(const std::string &publicKey, const std::string &from) {
    BIO *bio = NULL;
    RSA *rsa_public_key = NULL;
    //从字符串读取RSA公钥串
    if ((bio = BIO_new_mem_buf((void *) publicKey.c_str(), -1)) == NULL) {
//        std::cout << "BIO_new_mem_buf failed!" << std::endl;
        LOGD("BIO_new_mem_buf failed!");
        return NULL;
    }
    //读取公钥
    rsa_public_key = PEM_read_bio_RSA_PUBKEY(bio, NULL, NULL, NULL);

    //异常处理
    if (rsa_public_key == NULL) {
        //资源释放
        BIO_free_all(bio);
        RSA_free(rsa_public_key);
        //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
        CRYPTO_cleanup_all_ex_data();
        return NULL;
    }

    //rsa模的位数
    int rsa_size = RSA_size(rsa_public_key);

    //RSA_PKCS1_PADDING 最大加密长度 为 128 -11
    //RSA_NO_PADDING 最大加密长度为  128
    rsa_size = rsa_size - RSA_PKCS1_PADDING_SIZE;

    //动态分配内存，用于存储加密后的密文
    unsigned char *to = (unsigned char *) malloc(rsa_size + 1);
    //填充0
    memset(to, 0, rsa_size + 1);

    //明文长度
    int flen = from.length();

    //加密，返回值为加密后的密文长度，-1表示失败
    int status = RSA_public_encrypt(flen, (const unsigned char *) from.c_str(), to, rsa_public_key,
                                    RSA_PKCS1_PADDING);
    //异常处理
    if (status < 0) {
        //资源释放
        free(to);
        BIO_free_all(bio);
        RSA_free(rsa_public_key);
        //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
        CRYPTO_cleanup_all_ex_data();
        return NULL;
    }

    //赋值密文
    static std::string result((char *) to, status);

    //资源释放
    free(to);
    BIO_free_all(bio);
    RSA_free(rsa_public_key);
    //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
    CRYPTO_cleanup_all_ex_data();
    return result;
}

/**
 * 使用私钥对密文解密
 * @param privetaKey
 * @param from
 * @return
 */
std::string decryptRSA(const std::string &privetaKey, const std::string &from) {
    BIO *bio = NULL;
    RSA *rsa_private_key = NULL;
    //从字符串读取RSA公钥串
    if ((bio = BIO_new_mem_buf((void *) privetaKey.c_str(), -1)) == NULL) {
//        std::cout << "BIO_new_mem_buf failed!" << std::endl;
        LOGD("BIO_new_mem_buf failed!");
        return NULL;
    }
    //读取私钥
    rsa_private_key = PEM_read_bio_RSAPrivateKey(bio, NULL, NULL, NULL);
    //异常处理
    if (rsa_private_key == NULL) {
        //资源释放
        BIO_free_all(bio);
        RSA_free(rsa_private_key);
        //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
        CRYPTO_cleanup_all_ex_data();
        return NULL;
    }
    //rsa模的位数
    int rsa_size = RSA_size(rsa_private_key);
    //动态分配内存，用于存储解密后的明文
    unsigned char *to = (unsigned char *) malloc(rsa_size + 1);
    //填充0
    memset(to, 0, rsa_size + 1);

    //密文长度
    int flen = from.length();

    // RSA_NO_PADDING
    // RSA_PKCS1_PADDING
    //解密，返回值为解密后的名文长度，-1表示失败
    int status = RSA_private_decrypt(flen, (const unsigned char *) from.c_str(), to,
                                     rsa_private_key,
                                     RSA_PKCS1_PADDING);
    //异常处理率
    if (status < 0) {
        //释放资源
        free(to);
        BIO_free_all(bio);
        RSA_free(rsa_private_key);
        //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
        CRYPTO_cleanup_all_ex_data();
        return NULL;
    }
    //赋值明文，是否需要指定to的长度？
    static std::string result((char *) to);
    //释放资源
    free(to);
    BIO_free_all(bio);
    RSA_free(rsa_private_key);
    //清除管理CRYPTO_EX_DATA的全局hash表中的数据，避免内存泄漏
    CRYPTO_cleanup_all_ex_data();
    return result;
}

/**
 * 使用公钥对明文加密
 * @param env
 * @param thiz
 * @param context
 * @param base64PublicKey
 * @param content
 * @return
 */
jstring
encryptByRSA(JNIEnv *env, jobject thiz, jobject context, jstring base64PublicKey, jstring content) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA1加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //jstring 转 char*
    char *base64PublicKeyChars = (char *) env->GetStringUTFChars(base64PublicKey, NULL);
    //char* 转 string
    string base64PublicKeyString = string(base64PublicKeyChars);
    //生成公钥字符串
    string generatedPublicKey = generatePublicKey(base64PublicKeyString);
    //释放
    env->ReleaseStringUTFChars(base64PublicKey, base64PublicKeyChars);
    //jstring 转 char*
    char *contentChars = (char *) env->GetStringUTFChars(content, NULL);
    //char* 转 string
    string contentString = string(contentChars);
    //释放
    env->ReleaseStringUTFChars(content, contentChars);
    //调用RSA加密函数加密
    string rsaResult = encryptRSA(generatedPublicKey, contentString);
    if (rsaResult.empty()) {
        return NULL;
    }
    //将密文进行base64
    string base64RSA = base64Encode(rsaResult);
    if (base64RSA.empty()) {
        return NULL;
    }
    //string -> char* -> jstring 返回
    jstring result = env->NewStringUTF(base64RSA.c_str());
    return result;
}

/**
 * 使用私钥对密文解密
 * @param env
 * @param thiz
 * @param context
 * @param base64PublicKey
 * @param content
 * @return
 */
jstring decryptByRSA(JNIEnv *env, jobject thiz, jobject context, jstring base64PrivateKey,
                     jstring content) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("SHA1加密->apk-sha1值验证不通过");
        return env->NewStringUTF(sha1_sign_err);
    }
    //jstring 转 char*
    char *base64PrivateKeyChars = (char *) env->GetStringUTFChars(base64PrivateKey, NULL);
    //char* 转 string
    string base64PrivateKeyString = string(base64PrivateKeyChars);
    //生成公钥字符串
    string generatedPrivateKey = generatePrivateKey(base64PrivateKeyString);
    //释放
    env->ReleaseStringUTFChars(base64PrivateKey, base64PrivateKeyChars);
    //jstring 转 char*
    char *contentChars = (char *) env->GetStringUTFChars(content, NULL);
    //char* 转 string
    string contentString = string(contentChars);
    //释放
    env->ReleaseStringUTFChars(content, contentChars);
    //decode
    string decodeBase64RSA = base64Decode(contentString);
    if (decodeBase64RSA.empty()) {
        return NULL;
    }
    //解密
    string origin = decryptRSA(generatedPrivateKey, decodeBase64RSA);
    if (origin.empty()) {
        return NULL;
    }
    //string -> char* -> jstring 返回
    jstring result = env->NewStringUTF(origin.c_str());
    return result;
}
/**
 * HmacSHA1加密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
encodeByHmacSHA1(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
//    LOGD("HmacSHA1加密->HMAC: Hash-based Message Authentication Code，即基于Hash的消息鉴别码");
    if (!verifySha1OfApk(env, context)) {
        LOGD("HmacSHA1加密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }
//    const char *key = "jellyApp@22383243*335457968";
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
encodeBySHA1(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
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
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("SHA1加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA1加密->源数据-> %s ", src);
    jsize src_Len = strlen(src);
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

    return env->NewStringUTF(hex);
}
/**
 * SHA224加密
 */
extern "C" JNIEXPORT jstring JNICALL
encodeBySHA224(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
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
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("SHA224加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA224加密->源数据-> %s ", src);
    jsize src_Len = strlen(src);
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
        LOGD("SHA224加密->把哈希值按%%02x格式定向到缓冲区->%d", i);
    }
    LOGD("SHA224加密->%s", hex);
    LOGD("SHA224加密->释放内存");
    free(src);//malloc和free要配套使用
    delete[]chars;//new/delete、new[]/delete[] 要配套使用总是没错的

    return env->NewStringUTF(hex);
}
/**
 * SHA256加密
 */
extern "C" JNIEXPORT jstring JNICALL
encodeBySHA256(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
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
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("SHA256加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA256加密->源数据-> %s ", src);
    jsize src_Len = strlen(src);
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
    return env->NewStringUTF(hex);
}
/**
 * SHA384加密
 */
extern "C" JNIEXPORT jstring JNICALL
encodeBySHA384(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
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
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("SHA384加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA384加密->源数据-> %s ", src);
    jsize src_Len = strlen(src);
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

    return env->NewStringUTF(hex);
}
/**
 * SHA512加密
 */
extern "C" JNIEXPORT jstring JNICALL
encodeBySHA512(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
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
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("SHA512加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
    strcat(src, Skey);
    strcat(src, key);
//    LOGD("SHA512加密->源数据-> %s ", src);
    jsize src_Len = strlen(src);
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

    return env->NewStringUTF(hex);
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
        LOGD("AES加密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }

//    LOGD("AES加密->对称密钥，也就是说加密和解密用的是同一个密钥");
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
//    LOGD("AES加密->对数据进行加密运算");
    EVP_EncryptUpdate(&ctx, out, &outlen, (const unsigned char *) src, src_Len);
    cipherText_len = outlen;

//    LOGD("AES加密->结束加密运算");
    EVP_EncryptFinal_ex(&ctx, out + outlen, &outlen);
    cipherText_len += outlen;

//    LOGD("AES加密->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);

    LOGD("AES加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(_key, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);
//    LOGD("out->%s",out);
    jbyteArray cipher = env->NewByteArray(cipherText_len);
//    LOGD("AES->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_len, (jbyte *) out);
    LOGD("AES加密->释放内存");
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
        LOGD("AES解密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }
//    LOGD("AES解密->对称密钥，也就是说加密和解密用的是同一个密钥");
//    const unsigned char *iv = (const unsigned char *) "gjdljgaj748564df";
    jbyte *keys = env->GetByteArrayElements(_key, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int outlen = 0, plaintext_len = 0;

    unsigned char *out = (unsigned char *) malloc(src_Len);
    memset(out, 0, src_Len);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);
//    LOGD("AES解密->指定解密算法，初始化解密key/iv");
//    EVP_DecryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, (const unsigned char *) keys, iv);
    EVP_DecryptInit_ex(&ctx, EVP_aes_128_ecb(), NULL, (const unsigned char *) keys, NULL);
//    LOGD("AES解密->对数据进行解密运算");
    EVP_DecryptUpdate(&ctx, out, &outlen, (const unsigned char *) src, src_Len);
    plaintext_len = outlen;

//    LOGD("AES解密->结束解密运算");
    EVP_DecryptFinal_ex(&ctx, out + outlen, &outlen);
    plaintext_len += outlen;

//    LOGD("AES解密->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);

    LOGD("AES解密->从jni释放数据指针");
    env->ReleaseByteArrayElements(_key, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);

    if (plaintext_len <= 0) {
        LOGD("AES解密->解密失败！");
        LOGD("AES解密->释放内存");
        free(out);
        char *decode_err = "AES解密->解密失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(decode_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(decode_err), (jbyte *) decode_err);
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
        LOGD("AES加密->apk-sha1值验证不通过");
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

    LOGW("source: %s\n", vItem);
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
    LOGW("encrypted : %s\n", encData);
    // base64编码 使用openssl base64编码后 在java成不再编码，调用openssl base64解码会失败 具体原因不明
//    char *baseEnc = base64Encode(reinterpret_cast<const char *>(encData), encLen);
//    LOGW("encrypted : %s\n", baseEnc);
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
        LOGD("AES解密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }
    jbyte *keys = env->GetByteArrayElements(_key, 0);
    jbyte *src = env->GetByteArrayElements(src_, 0);
    jsize src_Len = env->GetArrayLength(src_);
    LOGW("src %s", src);
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
    LOGW("decrypt %s", decData);
    if (decLen <= 0) {
        LOGD("AES解密->解密失败！");
        char *decode_err = "AES解密->解密失败！";
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
/**
 * RSA公钥加密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
encodeByRSAPubKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA公钥加密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }

    //char* 转 string
    string base64PublicKeyString = string(RSAPubKey);
    //生成公钥字符串
    string generatedPublicKey = generatePublicKey(base64PublicKeyString);

//    LOGD("RSA公钥解密->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
//    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
//    keys_ = env->NewByteArray(strlen(RSAPubKey));
//    env->SetByteArrayRegion(keys_, 0, strlen(RSAPubKey), (jbyte *) RSAPubKey);
//
//    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, cipherText_offset = 0, desText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA公钥解密->从内存读取RSA公钥");
    if ((keybio = BIO_new_mem_buf((void *) generatedPublicKey.c_str(), -1)) == NULL) {
        LOGD("RSA公钥加密>BIO_new_mem_buf publicKey error\n");
        char *err = "RSA公钥加密->从内存读取RSA公钥失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
        return byteArray;
    }
//    LOGD("RSA->从bio结构中得到RSA结构");
    if ((rsa = PEM_read_bio_RSA_PUBKEY(keybio, NULL, NULL, NULL)) == NULL) {
        LOGD("RSA公钥加密->PEM_read_bio_RSA_PUBKEY error\n");
        char *err = "RSA公钥加密->从bio结构中得到RSA结构失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
        return byteArray;
    }
    LOGD("RSA公钥加密->释放BIO");
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
        ret = RSA_public_encrypt(src_flen, srcOrigin + src_offset, cipherText, rsa,
                                 RSA_PKCS1_PADDING);
        if (ret < 0) {
            LOGD("RSA公钥加密->加密失败");
            char *err = "RSA公钥加密->加密失败！";
            jbyteArray byteArray = env->NewByteArray(strlen(err));
            env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
            return byteArray;
        }
        memcpy(desText + cipherText_offset, cipherText, ret);
        cipherText_offset += ret;
        src_offset += src_flen;
    }
    LOGD("RSA公钥加密->释放内存");
    RSA_free(rsa);
//    LOGD("RSA->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA公钥加密->从jni释放数据指针");
    //释放
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(cipherText_offset);
//    LOGD("RSA->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_offset, (jbyte *) desText);
    LOGD("RSA公钥加密->释放内存");
    free(srcOrigin);
    free(cipherText);
    free(desText);

    return cipher;
}
/**
 * RSA私钥解密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
decodeByRSAPrivateKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA私钥解密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }
//    LOGD("RSA私钥解密->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
//    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
//    keys_ = env->NewByteArray(strlen(RSAPrivateKey));
//    env->SetByteArrayRegion(keys_, 0, strlen(RSAPrivateKey), (jbyte *) RSAPrivateKey);

//    jbyte *keys = env->GetByteArrayElements(keys_, NULL);

    //char* 转 string
    string base64PrivateKeyString = string(RSAPrivateKey);
    //生成公钥字符串
    string generatedPrivateKey = generatePrivateKey(base64PrivateKeyString);

    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, plaintext_offset = 0, descText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA私钥解密->从内存读取RSA私钥");
    if ((keybio = BIO_new_mem_buf((void *) generatedPrivateKey.c_str(), -1)) == NULL) {
        LOGD("RSA私钥解密->BIO_new_mem_buf privateKey error\n");
        char *err = "RSA私钥解密->从内存读取RSA私钥失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
        return byteArray;
    }

//    LOGD("RSA私钥解密->从bio结构中得到RSA结构");
//    OpenSSL_add_all_algorithms();//密钥有经过口令加密需要这个函数
//    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, (char *)PASS)) == NULL)
    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, NULL)) == NULL) {
        LOGD("RSA私钥解密->PEM_read_RSAPrivateKey error\n");
        char *err = "RSA私钥解密->从bio结构中得到RSA结构失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
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
        ret = RSA_private_decrypt(src_flen, srcOrigin + src_offset, plaintext, rsa,
                                  RSA_PKCS1_PADDING);
        if (ret < 0) {
            LOGD("RSA私钥解密->解密失败");
            char *err = "RSA私钥解密->解密失败！";
            jbyteArray byteArray = env->NewByteArray(strlen(err));
            env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
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
encodeByRSAPrivateKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA私钥加密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }
//    LOGD("RSA私钥加密->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
//    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
//    keys_ = env->NewByteArray(strlen(RSAPrivateKey));
//    env->SetByteArrayRegion(keys_, 0, strlen(RSAPrivateKey), (jbyte *) RSAPrivateKey);
//
//    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    //char* 转 string
    string base64PrivateKeyString = string(RSAPrivateKey);
    //生成公钥字符串
    string generatedPrivateKey = generatePrivateKey(base64PrivateKeyString);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, cipherText_offset = 0, desText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA私钥加密->从内存读取RSA私钥");
    if ((keybio = BIO_new_mem_buf((void *) generatedPrivateKey.c_str(), -1)) == NULL) {
        LOGD("RSA私钥加密->BIO_new_mem_buf privateKey error\n");
        char *err = "RSA私钥加密->从内存读取RSA私钥失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
        return byteArray;
    }
//    LOGD("RSA私钥加密->从bio结构中得到RSA结构");
    //    OpenSSL_add_all_algorithms();//密钥有经过口令加密需要这个函数
//    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, (char *)PASS)) == NULL)
    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, NULL)) == NULL) {
        LOGD("RSA私钥加密->PEM_read_RSAPrivateKey error\n");
        char *err = "RSA私钥加密->从bio结构中得到RSA结构失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
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
        ret = RSA_private_encrypt(src_flen, srcOrigin + src_offset, cipherText, rsa,
                                  RSA_PKCS1_PADDING);
        if (ret < 0) {
            LOGD("RSA私钥加密->加密失败");
            char *err = "RSA私钥加密->加密失败！";
            jbyteArray byteArray = env->NewByteArray(strlen(err));
            env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
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
decodeByRSAPubKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA公钥解密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }
//    LOGD("RSA公钥解密->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
//    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
//    keys_ = env->NewByteArray(strlen(RSAPubKey));
//    env->SetByteArrayRegion(keys_, 0, strlen(RSAPubKey), (jbyte *) RSAPubKey);
//
//    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    //char* 转 string
    string base64PublicKeyString = string(RSAPubKey);
    //生成公钥字符串
    string generatedPublicKey = generatePublicKey(base64PublicKeyString);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    int ret = 0, src_flen = 0, plaintext_offset = 0, desText_len = 0, src_offset = 0;

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA公钥解密->从内存读取RSA公钥");
    if ((keybio = BIO_new_mem_buf((void *) generatedPublicKey.c_str(), -1)) == NULL) {
        LOGD("RSA公钥解密->BIO_new_mem_buf publicKey error\n");
        char *err = "RSA公钥解密->从内存读取RSA公钥失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
        return byteArray;
    }
//    LOGD("RSA公钥解密->从bio结构中得到RSA结构");
    if ((rsa = PEM_read_bio_RSA_PUBKEY(keybio, NULL, NULL, NULL)) == NULL) {
        LOGD("RSA公钥解密->PEM_read_bio_RSA_PUBKEY error\n");
        char *err = "RSA公钥解密->从bio结构中得到RSA结构失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
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
        ret = RSA_public_decrypt(src_flen, srcOrigin + src_offset, plaintext, rsa,
                                 RSA_PKCS1_PADDING);
        if (ret < 0) {
            LOGD("RSA公钥解密->解密失败！");
            char *err = "RSA公钥解密->解密失败！";
            jbyteArray byteArray = env->NewByteArray(strlen(err));
            env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
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
signByRSAPrivateKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA签名->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
        return byteArray;
    }
//    LOGD("RSA签名->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
//    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
//    keys_ = env->NewByteArray(strlen(RSAPrivateKey));
//    env->SetByteArrayRegion(keys_, 0, strlen(RSAPrivateKey), (jbyte *) RSAPrivateKey);
//
//    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    //char* 转 string
    string base64PrivateKeyString = string(RSAPrivateKey);
    //生成公钥字符串
    string generatedPrivateKey = generatePrivateKey(base64PrivateKeyString);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    unsigned int siglen = 0;
    unsigned char digest[SHA_DIGEST_LENGTH];

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA签名->从内存读取RSA公钥");
    if ((keybio = BIO_new_mem_buf((void *) generatedPrivateKey.c_str(), -1)) == NULL) {
        LOGD("RSA签名->BIO_new_mem_buf publicKey error\n");
        char *err = "RSA签名->从内存读取RSA公钥失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
        return byteArray;
    }
//    LOGD("RSA->从bio结构中得到RSA结构");
//    OpenSSL_add_all_algorithms();//密钥有经过口令加密需要这个函数
//    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, (char *)PASS)) == NULL)
    if ((rsa = PEM_read_bio_RSAPrivateKey(keybio, NULL, NULL, NULL)) == NULL) {
        LOGD("RSA签名->PEM_read_RSAPrivateKey error\n");
        char *err = "RSA签名->从bio结构中得到RSA结构失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
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
//    env->ReleaseByteArrayElements(keys_, keys, 0);
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
verifyByRSAPubKey(JNIEnv *env, jobject obj, jobject context, jbyteArray src_, jbyteArray sign_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("RSA签名校验->apk-sha1值验证不通过");
        return -1;
    }
//    LOGD("RSA签名校验->非对称密码算法，也就是说该算法需要一对密钥，使用其中一个加密，则需要用另一个才能解密");
//    jbyteArray keys_ = NULL;//下面一系列操作把char *转成jbyteArray
//    keys_ = env->NewByteArray(strlen(RSAPubKey));
//    env->SetByteArrayRegion(keys_, 0, strlen(RSAPubKey), (jbyte *) RSAPubKey);
//
//    jbyte *keys = env->GetByteArrayElements(keys_, NULL);
    //char* 转 string
    string base64PublicKeyString = string(RSAPubKey);
    //生成公钥字符串
    string generatedPublicKey = generatePublicKey(base64PublicKeyString);
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jbyte *sign = env->GetByteArrayElements(sign_, NULL);

    jsize src_Len = env->GetArrayLength(src_);
    jsize siglen = env->GetArrayLength(sign_);

    int ret;
    unsigned char digest[SHA_DIGEST_LENGTH];

    RSA *rsa = NULL;
    BIO *keybio = NULL;

//    LOGD("RSA->从字符串读取RSA公钥");
    if ((keybio = BIO_new_mem_buf((void *) generatedPublicKey.c_str(), -1)) == NULL) {
        LOGD("RSA签名校验->BIO_new_mem_buf publicKey error\n");
        return -1;
    }
//    LOGD("RSA->从bio结构中得到RSA结构");
    if ((rsa = PEM_read_bio_RSA_PUBKEY(keybio, NULL, NULL, NULL)) == NULL) {
        LOGD("RSA签名校验->PEM_read_bio_RSA_PUBKEY error\n");
        return -1;
    }
    LOGD("RSA签名校验->释放BIO");
    BIO_free_all(keybio);

//    LOGD("RSA签名校验->对数据进行摘要运算");
    SHA1((const unsigned char *) src, src_Len, digest);
//    LOGD("RSA签名校验->对摘要进行RSA公钥验证");
    ret = RSA_verify(NID_sha1, digest, SHA_DIGEST_LENGTH, (const unsigned char *) sign, siglen,
                     rsa);

    RSA_free(rsa);
//    LOGD("RSA签名校验->CRYPTO_cleanup_all_ex_data");
    CRYPTO_cleanup_all_ex_data();

    LOGD("RSA签名校验->从jni释放数据指针");
//    env->ReleaseByteArrayElements(keys_, keys, 0);
    env->ReleaseByteArrayElements(src_, src, 0);
    env->ReleaseByteArrayElements(sign_, sign, 0);

    return ret;
}
/**
 * xOr加解密
 */
extern "C" JNIEXPORT jbyteArray JNICALL
xOr(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
    if (!verifySha1OfApk(env, context)) {
        LOGD("XOr加解密->apk-sha1值验证不通过");
        jbyteArray byteArray = env->NewByteArray(strlen(sha1_sign_err));
        env->SetByteArrayRegion(byteArray, 0, strlen(sha1_sign_err), (jbyte *) sha1_sign_err);
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
    if (src_Len <= 0) {
        LOGD("XOR加解密->加解密失败！");
        LOGD("XOR加解密->释放内存");
        free(chs);
        char *err = "XOR加解密->加解密失败！";
        jbyteArray byteArray = env->NewByteArray(strlen(err));
        env->SetByteArrayRegion(byteArray, 0, strlen(err), (jbyte *) err);
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
md5(JNIEnv *env, jobject obj, jobject context, jbyteArray src_) {
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
    memset(chars, 0, src_Lens + 1);
    memcpy(chars, srcs, src_Lens);
    chars[src_Lens] = 0;
    LOGD("MD5加密->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, srcs, 0);

    char *Skey = "&key=";
    char *src = (char *) malloc(src_Lens + 1 + strlen(Skey) + strlen(key) + 1);
    strcpy(src, chars);
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
    delete[]chars;//new/delete、new[]/delete[] 要配套使用总是没错的

    return env->NewStringUTF(hex);
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
        {"encodeByHmacSHA1",      "(Landroid/content/Context;[B)[B",                                                   (void *) encodeByHmacSHA1},
        {"encodeBySHA1",          "(Landroid/content/Context;[B)Ljava/lang/String;",                                   (void *) encodeBySHA1},
        {"encodeBySHA224",        "(Landroid/content/Context;[B)Ljava/lang/String;",                                   (void *) encodeBySHA224},
        {"encodeBySHA256",        "(Landroid/content/Context;[B)Ljava/lang/String;",                                   (void *) encodeBySHA256},
        {"encodeBySHA384",        "(Landroid/content/Context;[B)Ljava/lang/String;",                                   (void *) encodeBySHA384},
        {"encodeBySHA512",        "(Landroid/content/Context;[B)Ljava/lang/String;",                                   (void *) encodeBySHA512},
        {"encodeByAESEncrypt",    "(Landroid/content/Context;[B)[B",                                                   (void *) encodeByAESEncrypt},
        {"decodeByAESEncrypt",    "(Landroid/content/Context;[B)[B",                                                   (void *) decodeByAESEncrypt},
        {"encryptByAESEncrypt",   "(Landroid/content/Context;[B[B)[B",                                                 (void *) encryptByAESEncrypt},
        {"decryptByAESEncrypt",   "(Landroid/content/Context;[B[B)[B",                                                 (void *) decryptByAESEncrypt},
        {"encodeByAESCipher",     "(Landroid/content/Context;[B)[B",                                                   (void *) encodeByAESCipher},
        {"decodeByAESCipher",     "(Landroid/content/Context;[B)[B",                                                   (void *) decodeByAESCipher},
        {"encryptAESCipher",      "(Landroid/content/Context;[B[B)[B",                                                 (void *) encryptAESCipher},
        {"decryptAESCipher",      "(Landroid/content/Context;[B[B)[B",                                                 (void *) decryptAESCipher},
        {"encodeByRSAPubKey",     "(Landroid/content/Context;[B)[B",                                                   (void *) encodeByRSAPubKey},
        {"decodeByRSAPrivateKey", "(Landroid/content/Context;[B)[B",                                                   (void *) decodeByRSAPrivateKey},
        {"encryptRSA",            "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) encryptByRSA},
        {"decryptRSA",            "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) decryptByRSA},
        {"signByRSAPrivateKey",   "(Landroid/content/Context;[B)[B",                                                   (void *) signByRSAPrivateKey},
        {"verifyByRSAPubKey",     "(Landroid/content/Context;[B[B)I",                                                  (void *) verifyByRSAPubKey},
        {"xOr",                   "(Landroid/content/Context;[B)[B",                                                   (void *) xOr},
        {"md5",                   "(Landroid/content/Context;[B)Ljava/lang/String;",                                   (void *) md5},
        {"sha1OfApk",             "(Landroid/content/Context;)Ljava/lang/String;",                                     (void *) getSha1OfApk},
        {"verifySha1OfApk",       "(Landroid/content/Context;)Z",                                                      (void *) validateSha1OfApk},
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