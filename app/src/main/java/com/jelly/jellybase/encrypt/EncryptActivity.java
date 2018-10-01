package com.jelly.jellybase.encrypt;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.base.toast.ToastUtils;
import com.jelly.jellybase.R;

public class EncryptActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "body";

    private static final String TEST_KEY = "JA2F8AKJF3D7HF12";

    private static final String TEST_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" + "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKLy3gcwWwy+mhcr\n" + "gcXccrRu+UQUHDwvogZbGjbBDGsyt5hY69FtwIy/45tdj42xb4Tr0o1qKjuXHmIt\n" + "zWAlgm+e9Fi4vwj6sIIbgdvYhi2dm/N2abNzEMJ2WsG2kei64qsaZtlawWv9k2GG\n" + "ChP63MR79Z9+ucBzu+SZp+XrEt9DAgMBAAECgYB4Tr51KlOfJj7YqounDWs3ItQx\n" + "WnO6UCTdcnf5QzErGIgLGGQL/W9zu92NgVeS8xV2WzLarC7AToPlUxHWUftpxqCa\n" + "alQ+HtJ2zROnbblMwmEcnwsPXD8SncjJGDg1mSxkhi/jw1riPg36Exw1VGgmww4b\n" + "+iMboCv3ApBDdxn7yQJBAM7rgodIHGf11d9+TO+PUkglc9AfCDMXQraDirU6JjCh\n" + "6AVJXH76k2oLz4DCvd3CCBcM5qGmdAzTK/X1MSToGgUCQQDJmVtKiJkPOe/N2Vi3\n" + "MkIIalnqZ9GFYtDjUV3dgI1QVgLQ8qpN2y98j8PU9nM/BpU0fU4qSX36vPCfYn0e\n" + "mS6nAkABiAKmR6VWK56Skde16iScvhI2VxRzdFedDCopny2LLJeP+nQByI7wuPen\n" + "J0nKa1Yt/X1zcsznD2UC4/aiJEmVAkACL+a8pUS71I4UdqIuwp3Sx4yYLW4pe0v2\n" + "22AgUg+2amh3adqNI66dNFYUjmPrsB+YRS++57M1MC2QHRpsZY8LAkAKFNUtX47a\n" + "4LYofojZrEdcz9O8xisB4bsv04G+WiM4bqTrlQo/6Y3YofvaP5jGSwBW8K/w6KPX\n" + "D0VGzyfqFiL7\n" + "-----END PRIVATE KEY-----\n";

    private static final String TEST_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" + "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCi8t4HMFsMvpoXK4HF3HK0bvlE\n" + "FBw8L6IGWxo2wQxrMreYWOvRbcCMv+ObXY+NsW+E69KNaio7lx5iLc1gJYJvnvRY\n" + "uL8I+rCCG4Hb2IYtnZvzdmmzcxDCdlrBtpHouuKrGmbZWsFr/ZNhhgoT+tzEe/Wf\n" + "frnAc7vkmafl6xLfQwIDAQAB\n" + "-----END PUBLIC KEY-----\n";

    static {
        try{
//            System.loadLibrary("native-lib");
            System.loadLibrary("crypto");
            System.loadLibrary("cipher");
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }


    private EditText input;
    private TextView resultText;

    private ClipboardManager mClipboardManager;
    private JniUtils jni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encrypt_activity);
        input = (EditText)findViewById(R.id.input_ori);
        resultText = (TextView)findViewById(R.id.text_result);

        mClipboardManager =(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        jni = new JniUtils();

        findViewById(R.id.btn_sha1OfApk).setOnClickListener(this);
        findViewById(R.id.btn_apk).setOnClickListener(this);

        findViewById(R.id.btn_native_encode).setOnClickListener(this);
        findViewById(R.id.btn_java_encode).setOnClickListener(this);

        findViewById(R.id.btn_native_decode).setOnClickListener(this);
        findViewById(R.id.btn_java_decode).setOnClickListener(this);

        findViewById(R.id.btn_java_sign).setOnClickListener(this);
        findViewById(R.id.btn_native_sign).setOnClickListener(this);

        findViewById(R.id.btn_md5_sign).setOnClickListener(this);

        findViewById(R.id.btn_XOR_sign).setOnClickListener(this);
        findViewById(R.id.btn_XORs_sign).setOnClickListener(this);

        findViewById(R.id.btn_AES_sign).setOnClickListener(this);
        findViewById(R.id.btn_AESs_sign).setOnClickListener(this);

        findViewById(R.id.btn_RSA_sign).setOnClickListener(this);
        findViewById(R.id.btn_RSAs_sign).setOnClickListener(this);

        findViewById(R.id.btn_RSAa_sign).setOnClickListener(this);
        findViewById(R.id.btn_RSAd_sign).setOnClickListener(this);

        findViewById(R.id.btn_RSAsa_sign).setOnClickListener(this);
        findViewById(R.id.btn_RSAsad_sign).setOnClickListener(this);

        findViewById(R.id.btn_copy_sign).setOnClickListener(this);
        findViewById(R.id.btn_past_sign).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        String ori  = input.getText().toString().trim();
        String result = "";
        switch (v.getId()) {
            case R.id.btn_sha1OfApk:
                result ="sha1OfApk-> " + jni.sha1OfApk(this);
                break;
            case R.id.btn_apk:
                result ="验证apk签名-> " + jni.verifySha1OfApk(this);
                break;
            case R.id.btn_java_encode:
                byte[] encodeByHmacSHA1 = jni.encodeByHmacSHA1(this, ori.getBytes());
                result ="hmacSHA1签名编码->" + Base64.encodeToString(encodeByHmacSHA1, Base64.NO_WRAP);
                break;
            case R.id.btn_native_encode:
                result = "SHA1签名->" +jni.encodeBySHA1(this,ori.getBytes());
                break;
            case R.id.btn_java_decode:
                result = "SHA224签名->" +jni.encodeBySHA224(this,ori.getBytes());
                break;
            case R.id.btn_native_decode:
                result= "SHA256签名->" +jni.encodeBySHA256(this,ori.getBytes());
                break;
            case R.id.btn_java_sign:
                result="SHA384签名->" + jni.encodeBySHA384(this,ori.getBytes());
                break;
            case R.id.btn_native_sign:
                result = "SHA512签名->" +jni.encodeBySHA512(this,ori.getBytes());
                break;
            case R.id.btn_md5_sign:
                result="MD5信息摘要->" + jni.md5(this,ori.getBytes()).toUpperCase();
                break;
            case R.id.btn_XOR_sign:
                result= "XOR异或加密编码->" + Base64.encodeToString(jni.xOr(this,ori.getBytes()), Base64.NO_WRAP);
                break;
            case R.id.btn_XORs_sign:
                result = "XOR异或解密->" + new String(jni.xOr(this,Base64.decode(ori, Base64.NO_WRAP)));
                break;
            case R.id.btn_AES_sign:
                byte[] encodeAES = jni.encodeByAES(this,TEST_KEY.getBytes(), ori.getBytes());
                result ="AES加密编码->" + Base64.encodeToString(encodeAES, Base64.NO_WRAP);
                break;
            case R.id.btn_AESs_sign:
                byte[] decodeAES = jni.decodeByAES(this,TEST_KEY.getBytes(), ori.getBytes());
                result="AES解密->" + new String(decodeAES);
                break;
            case R.id.btn_RSA_sign:
                byte[] encodeByRSAPubKey = jni.encodeByRSAPubKey(this,TEST_PUBLIC_KEY.getBytes(),
                        ori.getBytes());
                result ="RSA公钥加密编码->" + Base64.encodeToString(encodeByRSAPubKey, Base64.NO_WRAP);
                break;
            case R.id.btn_RSAs_sign:
                byte[] decodeByRSAPrivateKey = jni.decodeByRSAPrivateKey(this,TEST_PRIVATE_KEY.getBytes(),
                        ori.getBytes());
                result ="RSA私钥解密->" + new String(decodeByRSAPrivateKey);
                break;
            case R.id.btn_RSAa_sign:
                byte[] encodeByRSAPrivateKey = jni.encodeByRSAPrivateKey(this,TEST_PRIVATE_KEY.getBytes(),
                        ori.getBytes());
                result ="RSA私钥加密编码->" +  Base64.encodeToString(encodeByRSAPrivateKey, Base64.NO_WRAP);
                break;
            case R.id.btn_RSAd_sign:
                byte[] decodeByRSAPubKey = jni.decodeByRSAPubKey(this,TEST_PUBLIC_KEY.getBytes(),
                        ori.getBytes());
                result = "RSA公钥解密->" + new String(decodeByRSAPubKey);
                break;
            case R.id.btn_RSAsa_sign:
                byte[] signByRSAPrivateKey = jni.signByRSAPrivateKey(this,TEST_PRIVATE_KEY.getBytes(),
                        ori.getBytes());
                result="RSA私钥签名编码->" + Base64.encodeToString(signByRSAPrivateKey, Base64.NO_WRAP);
                break;
            case R.id.btn_RSAsad_sign:
                byte[] signByRSAPrivateKeys = jni.signByRSAPrivateKey(this,TEST_PRIVATE_KEY.getBytes(),
                        ori.getBytes());

                int verifySign = jni.verifyByRSAPubKey(this,TEST_PUBLIC_KEY.getBytes(),
                        ori.getBytes(), signByRSAPrivateKeys);
                result="RSA公钥验证签名-> " + verifySign + "，1：验证成功";
                break;
            case R.id.btn_copy_sign:
                String ssss=resultText.getText().toString();
                ClipData mClipData =ClipData.newPlainText("Label", ssss);         //‘Label’这是任意文字标签
                mClipboardManager.setPrimaryClip(mClipData);
                ToastUtils.showShort(this, "复制成功，可以发给朋友们了。");
                break;
            case R.id.btn_past_sign:
                // 粘贴板有数据，并且是文本
                if (mClipboardManager.hasPrimaryClip()
                        && mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
                    CharSequence text = item.getText();
                    if (text == null) {
                        return;
                    }
                    input.setText(text);
                    input.setSelection(text.length());
                }
                break;
        }
        resultText.setText(result);
    }

}
