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
//        String[] RSAKey=SafetyUtil.getInstance().generateRSAKey(this);
//        DebugLog.i("公钥=\n"+RSAKey[0]);
//        DebugLog.i("私钥=\n"+RSAKey[1]);
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
//                result ="RSA私钥加密编码->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.RSA_PRIVATEKEY);
                break;
            case R.id.btn_RSAs_sign:
                result ="RSA私钥解密->" + SafetyUtil.getInstance().decode(BaseApplication.getInstance(),ori,SafetyUtil.RSA_PRIVATEKEY);
//                result ="RSA公钥解密->" + SafetyUtil.getInstance().decode(BaseApplication.getInstance(),ori,SafetyUtil.RSA_PUBKEY);
                break;
            case R.id.btn_RSAa_sign:
                result ="RSA公钥加密编码->" +  SafetyUtil.getInstance().encryptRSA(BaseApplication.getInstance(),ori,SafetyUtil.PUBLIC_KEY);
//                result ="RSA私钥加密编码->" +  SafetyUtil.getInstance().encodeByRSAPriKey(BaseApplication.getInstance(),ori,SafetyUtil.PRIVATE_KEY);
                break;
            case R.id.btn_RSAd_sign:
                result = "RSA私钥解密->" + SafetyUtil.getInstance().decryptRSA(BaseApplication.getInstance(),ori,SafetyUtil.PRIVATE_KEY);
//                result = "RSA公钥解密->" + SafetyUtil.getInstance().decodeByRSAPubKey(BaseApplication.getInstance(),ori,SafetyUtil.PUBLIC_KEY);
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
