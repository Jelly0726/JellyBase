//
// Created by Phoenix on 2017/6/25.
//
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include "endorse.h"

//换成你自己的apk-sha1值
//NPayHelper.jsk "73C03C185B565BB1D78589B2424DF0C190265ACD";
//Jelly.jsk "758DA6688786C0D2C10CA074C29351FB02686237";
//Tpay.jks "45DC14DD20AAFD30ADDE2B9C7840B1AC91F9A433";
//Washed.jks "01B8F029B06CAD2D8435B1BF3E119A1408329DD5";
//GSPay.jks "2640FD5C374C35C530DBAB8B0692758B848999E2";
//PayAssist.jks "890241C127FC5989CC6FFC0FE7AD4A3F59E12737";
//RedP.jks "F4CC465B89E4B38E5A2627885270138E4E303BD2";
//LuoLiFen.jks "90DCB0B56B86301F483BDB5B415331BBD804BA2B";
//JiangPing.jks "E9E786734009012999D9E3D883E50D7923BEA06D"
//BuYun.jks "CB60B06EEB5668C481CA05CBFA84BF8F2D2E4B0A"
const char *signatureOfApk = "758DA6688786C0D2C10CA074C29351FB02686237";

const char digest[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
                       'F'};

char *sha1OfApk(JNIEnv *env, jobject context) {
    //上下文对象
    jclass clazz = env->GetObjectClass(context);
    //反射获取PackageManager
    jmethodID methodID = env->GetMethodID(clazz, "getPackageManager",
                                          "()Landroid/content/pm/PackageManager;");
    jobject package_manager = env->CallObjectMethod(context, methodID);
    if (package_manager == NULL) {
        LOGD("sha1OfApk->package_manager is NULL!!!");
        return NULL;
    }

    //反射获取包名
    methodID = env->GetMethodID(clazz, "getPackageName", "()Ljava/lang/String;");
    jstring package_name = (jstring) env->CallObjectMethod(context, methodID);
    if (package_name == NULL) {
        LOGD("sha1OfApk->package_name is NULL!!!");
        return NULL;
    }
    env->DeleteLocalRef(clazz);

    //获取PackageInfo对象
    jclass pack_manager_class = env->GetObjectClass(package_manager);
    methodID = env->GetMethodID(pack_manager_class, "getPackageInfo",
                                "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    env->DeleteLocalRef(pack_manager_class);
    jobject package_info = env->CallObjectMethod(package_manager, methodID, package_name, 0x40);
    if (package_info == NULL) {
        LOGD("sha1OfApk->getPackageInfo() is NULL!!!");
        return NULL;
    }
    env->DeleteLocalRef(package_manager);

    //获取签名信息
    jclass package_info_class = env->GetObjectClass(package_info);
    jfieldID fieldId = env->GetFieldID(package_info_class, "signatures",
                                       "[Landroid/content/pm/Signature;");
    env->DeleteLocalRef(package_info_class);
    jobjectArray signature_object_array = (jobjectArray) env->GetObjectField(package_info, fieldId);
    if (signature_object_array == NULL) {
        LOGD("sha1OfApk->signature is NULL!!!");
        return NULL;
    }
    jobject signature_object = env->GetObjectArrayElement(signature_object_array, 0);
    env->DeleteLocalRef(package_info);

    //签名信息转换成sha1值
    jclass signature_class = env->GetObjectClass(signature_object);
    methodID = env->GetMethodID(signature_class, "toByteArray", "()[B");
    env->DeleteLocalRef(signature_class);

    jbyteArray signature_byte = (jbyteArray) env->CallObjectMethod(signature_object, methodID);
    jclass byte_array_input_class = env->FindClass("java/io/ByteArrayInputStream");
    methodID = env->GetMethodID(byte_array_input_class, "<init>", "([B)V");
    jobject byte_array_input = env->NewObject(byte_array_input_class, methodID, signature_byte);
    jclass certificate_factory_class = env->FindClass("java/security/cert/CertificateFactory");
    methodID = env->GetStaticMethodID(certificate_factory_class, "getInstance",
                                      "(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;");
    jstring x_509_jstring = env->NewStringUTF("X.509");
    jobject cert_factory = env->CallStaticObjectMethod(certificate_factory_class, methodID,
                                                       x_509_jstring);
    methodID = env->GetMethodID(certificate_factory_class, "generateCertificate",
                                ("(Ljava/io/InputStream;)Ljava/security/cert/Certificate;"));
    jobject x509_cert = env->CallObjectMethod(cert_factory, methodID, byte_array_input);
    env->DeleteLocalRef(certificate_factory_class);

    jclass x509_cert_class = env->GetObjectClass(x509_cert);
    methodID = env->GetMethodID(x509_cert_class, "getEncoded", "()[B");
    jbyteArray cert_byte = (jbyteArray) env->CallObjectMethod(x509_cert, methodID);
    env->DeleteLocalRef(x509_cert_class);

    jclass message_digest_class = env->FindClass("java/security/MessageDigest");
    methodID = env->GetStaticMethodID(message_digest_class, "getInstance",
                                      "(Ljava/lang/String;)Ljava/security/MessageDigest;");
    jstring sha1_jstring = env->NewStringUTF("SHA1");
    jobject sha1_digest = env->CallStaticObjectMethod(message_digest_class, methodID, sha1_jstring);
    methodID = env->GetMethodID(message_digest_class, "digest", "([B)[B");
    jbyteArray sha1_byte = (jbyteArray) env->CallObjectMethod(sha1_digest, methodID, cert_byte);
    env->DeleteLocalRef(message_digest_class);

    //转换成char
    jsize arraySize = env->GetArrayLength(sha1_byte);
    jbyte *sha1 = env->GetByteArrayElements(sha1_byte, NULL);
    char *hex = new char[arraySize * 2 + 1];
    for (int i = 0; i < arraySize; ++i) {
        hex[2 * i] = digest[((unsigned char) sha1[i]) / 16];
        hex[2 * i + 1] = digest[((unsigned char) sha1[i]) % 16];
    }
    hex[arraySize * 2] = '\0';

//    LOGD("sha1OfApk->sha1 %s ", hex);
    return hex;
}

jboolean verifySha1OfApk(JNIEnv *env, jobject context) {
    char *signature = sha1OfApk(env, context);
    //比较签名
    if (strcmp(signature, signatureOfApk) == 0) {
        LOGD("sha1OfApk->签名验证成功");
        return static_cast<jboolean>(true);
    }
    LOGD("sha1OfApk->签名验证失败");
    return static_cast<jboolean>(false);
}

/**base64 编码 */
char *base64_encode(const char *data) {
    LOGD("cipher data= %s", data);
    int data_len = strlen(data);
    int prepare = 0;
    int ret_len;
    int temp = 0;
    char *ret = NULL;
    char *f = NULL;
    int tmp = 0;
    char changed[4];
    int i = 0;
    ret_len = data_len / 3;
    temp = data_len % 3;
    if (temp > 0) {
        ret_len += 1;
    }
    ret_len = ret_len * 4 + 1;
    ret = (char *) malloc(ret_len);

    if (ret == NULL) {
        LOGD("No enough memory.\n");
        exit(0);
    }
    memset(ret, 0, ret_len);
    f = ret;
    while (tmp < data_len) {
        temp = 0;
        prepare = 0;
        memset(changed, '\0', 4);
        while (temp < 3) {
            //printf("tmp = %d\n", tmp);
            if (tmp >= data_len) {
                break;
            }
            prepare = ((prepare << 8) | (data[tmp] & 0xFF));
            tmp++;
            temp++;
        }
        prepare = (prepare << ((3 - temp) * 8));
//        LOGD("cipher before for : temp = %d, prepare = %d\n", temp, prepare);
        for (i = 0; i < 4; i++) {
            if (temp < i) {
                changed[i] = 0x40;
            } else {
                changed[i] = (prepare >> ((3 - i) * 6)) & 0x3F;
            }
            *f = base[changed[i]];
//            LOGD("cipher %.2X", changed[i]);
            f++;
        }
    }
    *f = '\0';
    LOGD("cipher ret= %s", ret);
    return ret;

}

/* */
static char find_pos(char ch) {
    char *ptr = (char *) strrchr(base, ch);//the last position (the only) in base[]
    return (ptr - base);
}

/** base64 解码 */
char *base64_decode(const char *data) {
    int data_len = strlen(data);
    int ret_len = (data_len / 4) * 3;
    int equal_count = 0;
    char *ret = NULL;
    char *f = NULL;
    int tmp = 0;
    int temp = 0;
    char need[4];
    int prepare = 0;
    int i = 0;
    if (*(data + data_len - 1) == '=') {
        equal_count += 1;
    }
    if (*(data + data_len - 2) == '=') {
        equal_count += 1;
    }
    if (*(data + data_len - 3) == '=') {//seems impossible
        equal_count += 1;
    }
    switch (equal_count) {
        case 0:
            ret_len += 4;//3 + 1 [1 for NULL]
            break;
        case 1:
            ret_len += 4;//Ceil((6*3)/8)+1
            break;
        case 2:
            ret_len += 3;//Ceil((6*2)/8)+1
            break;
        case 3:
            ret_len += 2;//Ceil((6*1)/8)+1
            break;
    }
    ret = (char *) malloc(ret_len);
    if (ret == NULL) {
        LOGD("No enough memory.\n");
        exit(0);
    }
    memset(ret, 0, ret_len);
    f = ret;
    while (tmp < (data_len - equal_count)) {
        temp = 0;
        prepare = 0;
        memset(need, 0, 4);
        while (temp < 4) {
            if (tmp >= (data_len - equal_count)) {
                break;
            }
            prepare = (prepare << 6) | (find_pos(data[tmp]));
            temp++;
            tmp++;
        }
        prepare = prepare << ((4 - temp) * 6);
        for (i = 0; i < 3; i++) {
            if (i == temp) {
                break;
            }
            *f = (char) ((prepare >> ((2 - i) * 8)) & 0xFF);
            f++;
        }
    }
    *f = '\0';
    return ret;
}

int is_base64(const char* str) {
    int len = strlen(str);
    for (int i = 0; i < len; i++) {
        int c = str[i];
        int bytes=0;
        if (c > 128) {
            if ((c > 247)) {
                return 0;
            }
            else if(c > 239)
            {
                bytes = 4;
            }
            else if(c > 223)
            {
                bytes = 3;
            }
            else if(c > 191)
            {
                bytes = 2;
            } else {
                return 0;
            }
            if ((i + bytes) > len) {
                return 0;
            }
            while (bytes > 1) {
                i++;
               int b = str[i];
                if (b < 128 || b > 191) {
                    return 0;
                }
                bytes--;
            }
        }
    }
    return 1;
}
