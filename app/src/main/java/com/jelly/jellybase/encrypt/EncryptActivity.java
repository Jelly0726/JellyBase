package com.jelly.jellybase.encrypt;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.base.appManager.BaseApplication;
import com.base.encrypt.JniUtils;
import com.base.encrypt.SafetyUtil;
import com.base.log.DebugLog;
import com.base.toast.ToastUtils;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;

public class EncryptActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "body";

    private static final String TEST_KEY = "JA2F8AKJF3D7HF12";

    private static final String TEST_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMJNYzkiQup2Pn3HdVYGuxgiOx8e2avaa5M/xHfz/3hUbLpUwenh1go7kzvhhquh9tkUTmfoLudXpbr56aY4rfAkvzAxHiiStPWYnNeb9irMq9n0G+ykXq5pXu8Y3fyFW2lYGoswZnEFEAw8XTw2zPA4vuSQXz2oBDZsQWzC0VGXAgMBAAECgYAWWHIN0wvhDQI40uSCpTmFGAK2nISqB++ROqcsqGn7+7GZaD/41tkXyiwvmcs0F+dcpcIynvgt8N2FeFJPpHsUTFV8cX9jjzmHKvHMQ+2oile7uUhV8Fj7Of/R4k9ADQuednJ19uzH8yR40skIGGiFhPpf955EqI7tFZdmMf8gYQJBAP6lqmT10Dj4lqCz5d3519cxoRcBkJ9qLtiZ4AFYi7o9hzbVhSwSFZIgtF+A/UvjMGAQ0VFqiFPf+WK+A0hW5hECQQDDVaZNpAvoHgsOkPdA/oQSx9Tm4hIWLa1aHgTuhR9mwlQMhOYtieQ7UZLI3AMWxqGZkz58DWRcNJ8/o2o/BvUnAkB6JmO3LEbmnTA8BC+WrDtCKbdZPtHt9lRkaGOQobXc75jFz+SiwEYCo5eCXHCkj2VsH4UY5d5hRYXuPLF8aNrRAkEAjFaD1fJPb6PuE7gJPFPftdKGXp77mZ2Vl1JL/sX76oshcaEl8n/ITunriI2xVnK89aZ5VQ0WFrzj0QfqNIAeLwJADs9kHrPUKvmG/BL3AF+qe4E2Mg3m22/3RjUQ7Uum3vBO/5RXMGrGnMkJFvtLV/n7YCTSn+Uoq7rQV3J3SHGb5w==";

    private static final String TEST_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCTWM5IkLqdj59x3VWBrsYIjsfHtmr2muTP8R38/94VGy6VMHp4dYKO5M74YarofbZFE5n6C7nV6W6+emmOK3wJL8wMR4okrT1mJzXm/YqzKvZ9BvspF6uaV7vGN38hVtpWBqLMGZxBRAMPF08NszwOL7kkF89qAQ2bEFswtFRlwIDAQAB";

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
    private static String mKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        input = (EditText)findViewById(R.id.input_ori);
        resultText = (TextView)findViewById(R.id.text_result);

        mClipboardManager =(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        jni = new JniUtils();

        mKey=SafetyUtil.getInstance().getAESRandomKeyString(16);
        DebugLog.i("秘钥="+mKey);

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
        findViewById(R.id.btn_AES_key).setOnClickListener(this);
        findViewById(R.id.btn_AESs_key).setOnClickListener(this);

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
    public int getLayoutId(){
        return R.layout.encrypt_activity;
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
                result ="hmacSHA1签名编码->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori, SafetyUtil.HMAC_SHA1);
                break;
            case R.id.btn_native_encode:
                result = "SHA1签名->" +SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.SHA1);
                break;
            case R.id.btn_java_decode:
                result = "SHA224签名->" +SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.SHA224);
                break;
            case R.id.btn_native_decode:
                result= "SHA256签名->" +SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.SHA256);
                break;
            case R.id.btn_java_sign:
                result="SHA384签名->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.SHA384);
                break;
            case R.id.btn_native_sign:
                result = "SHA512签名->" +SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.SHA512);
                break;
            case R.id.btn_md5_sign:
                result="MD5信息摘要->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.MD5).toUpperCase();
                break;
            case R.id.btn_XOR_sign:
                result= "XOR异或加密编码->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.XOR);
                break;
            case R.id.btn_XORs_sign:
                result = "XOR异或解密->" + SafetyUtil.getInstance().decode(BaseApplication.getInstance(),ori,SafetyUtil.XOR);
                break;
            case R.id.btn_AES_sign:
                result ="AES加密编码->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.AES);
                break;
            case R.id.btn_AESs_sign:
                result="AES解密->" +SafetyUtil.getInstance().decode(BaseApplication.getInstance(),ori,SafetyUtil.AES);
                break;
            case R.id.btn_AES_key:
                result ="AES加密编码->" + SafetyUtil.getInstance().encryptByAESEncrypt(BaseApplication.getInstance(),ori,mKey);
                break;
            case R.id.btn_AESs_key:
                result="AES解密->" +SafetyUtil.getInstance().decryptByAESEncrypt(BaseApplication.getInstance(),ori,mKey);
                break;
            case R.id.btn_RSA_sign:
                result ="RSA公钥加密编码->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.RSA_PUBKEY);
                break;
            case R.id.btn_RSAs_sign:
                result ="RSA私钥解密->" + SafetyUtil.getInstance().decode(BaseApplication.getInstance(),ori,SafetyUtil.RSA_PRIVATEKEY);
                break;
            case R.id.btn_RSAa_sign:
//                result ="RSA公钥加密编码->" +  SafetyUtil.getInstance().encryptRSA(BaseApplication.getInstance(),TEST_PUBLIC_KEY,ori,SafetyUtil.RSA_PRIVATEKEY);
                break;
            case R.id.btn_RSAd_sign:
//                result = "RSA私钥解密->" + SafetyUtil.getInstance().decryptRSA(BaseApplication.getInstance(),ori,SafetyUtil.RSA_PUBKEY);
                break;
            case R.id.btn_RSAsa_sign:
                result="RSA私钥签名编码->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.RSA_SIGN);
                break;
            case R.id.btn_RSAsad_sign:
               String signByRSAPrivateKeys = SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.RSA_PRIVATEKEY);

                boolean verifySign = SafetyUtil.getInstance().verify(BaseApplication.getInstance(),ori,signByRSAPrivateKeys,SafetyUtil.RSA_VERIFY);
                result="RSA公钥验证签名-> " + verifySign + "，true：验证成功";
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
