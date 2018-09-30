package com.jelly.jellybase.encrypt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jelly.jellybase.R;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        try{
            System.loadLibrary("native-lib");
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }


    private EditText input;
    private TextView resultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encrypt_activity);
        input = (EditText)findViewById(R.id.input_ori);
        resultText = (TextView)findViewById(R.id.text_result);

        findViewById(R.id.btn_native_encode).setOnClickListener(this);
        findViewById(R.id.btn_java_encode).setOnClickListener(this);

        findViewById(R.id.btn_native_decode).setOnClickListener(this);
        findViewById(R.id.btn_java_decode).setOnClickListener(this);

        findViewById(R.id.btn_java_sign).setOnClickListener(this);
        findViewById(R.id.btn_native_sign).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        String ori  = input.getText().toString().trim();
        String result = "";
        switch (v.getId()) {
            case R.id.btn_java_encode:
                result = javaEncode(ori);
                break;
            case R.id.btn_native_encode:
                result = nativeEncode(ori);
                break;
            case R.id.btn_java_decode:
                result = javaDecode(ori);
                break;
            case R.id.btn_native_decode:
                result = nativeDecode(ori);
                break;
            case R.id.btn_java_sign:
                result = javaSign(ori);
                break;
            case R.id.btn_native_sign:
                result = nativeSign(ori);
                break;
        }
        resultText.setText(result);
    }


    private String javaEncode(String str) {
        try {
            byte[] key = MD5Util.toByte(getKey());
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = { 49, 50, 51, 52, 49, 50, 51, 52, 49, 50, 51, 52, 49, 50, 51, 52};
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(str.getBytes());

            String base64 = Base64.encodeToString(encrypted, Base64.DEFAULT);
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    private String javaDecode(String str) {
        try {
            byte[] deData = Base64.decode(str, Base64.DEFAULT);
            byte[] key = MD5Util.toByte(getKey());
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = { 49, 50, 51, 52, 49, 50, 51, 52, 49, 50, 51, 52, 49, 50, 51, 52};
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
            byte[] oriData = cipher.doFinal(deData);
            return new String(oriData);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public String javaSign(String param) {
        String salt = getSalt();
        String key = salt + param + salt;
        String sign = null;
        try {
            sign = MD5Util.to32Str(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

    private String nativeEncode(String str) {
        byte[] enData = SecureUtil.encryptData(str.getBytes());
        String result = new String(Base64.encode(enData, Base64.DEFAULT));
        return result;
    }

    private String nativeDecode(String str) {
        byte[] deData = Base64.decode(str, Base64.DEFAULT);
        byte[] oriData = SecureUtil.decryptData(deData);
        String oriStr = new String(oriData);
        return oriStr;
    }

    private String nativeSign(String param) {
        return SecureUtil.getSign(param);
    }

    private String getKey() {
        return "appKey" + SecureUtil.getDeviceId() + "appKey";
    }

    private String getSalt() {
        return SecureUtil.getDeviceId() + "appKey" + SecureUtil.getDeviceId();
    }

}
