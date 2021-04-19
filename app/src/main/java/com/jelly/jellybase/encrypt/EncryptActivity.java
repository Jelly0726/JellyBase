package com.jelly.jellybase.encrypt;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.base.BaseApplication;
import com.jelly.baselibrary.encrypt.JniUtils;
import com.jelly.baselibrary.encrypt.SafetyUtil;
import com.jelly.baselibrary.log.LogUtils;
import com.jelly.baselibrary.toast.ToastUtils;
import com.base.BaseActivity;
import com.jelly.jellybase.R;

public class EncryptActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "body";

    static {
        try {
//            System.loadLibrary("native-lib");
//            System.loadLibrary("crypto");
//            System.loadLibrary("cipher");
        } catch (Exception e) {
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

        input = (EditText) findViewById(R.id.input_ori);
//        input.setText("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");
        input.setText("{\"account\":\"18750941314\",\"encrypted\":\"\",\"expiresIn\":180000,\"name\":\"金亚杰\",\"nickName\":\"金亚杰\",\"orgRole\":{\"address\":\"\",\"check\":false,\"cityName\":\"\",\"countryName\":\"\",\"default\":false,\"orgAddr\":\"安岭路1001号10楼\",\"orgBalance\":\"0.00\",\"orgCode\":\"G2005250001\",\"orgId\":\"402879a1724b207201724b208b650000\",\"orgName\":\"厦门市江平生物基质技术股份有限公司\",\"orgPhone\":\"0592-6019177\",\"orgRoleCode\":\"group\",\"provinceName\":\"\",\"roleCode\":\"SALES_MAN\",\"roleId\":\"\",\"roleName\":\"业务员\",\"sysRoleCode\":\"salesMan\",\"userRoleId\":\"402879a1725fd84501725fd85a430000\"},\"orgRoleVOs\":[{\"address\":\"\",\"check\":true,\"cityName\":\"\",\"countryName\":\"\",\"default\":false,\"orgAddr\":\"安岭路1001号10楼\",\"orgBalance\":\"0.00\",\"orgCode\":\"G2005250001\",\"orgId\":\"402879a1724b207201724b208b650000\",\"orgName\":\"厦门市江平生物基质技术股份有限公司\",\"orgPhone\":\"0592-6019177\",\"orgRoleCode\":\"group\",\"provinceName\":\"\",\"roleCode\":\"SALES_MAN\",\"roleId\":\"\",\"roleName\":\"业务员\",\"sysRoleCode\":\"salesMan\",\"userRoleId\":\"402879a1725fd84501725fd85a430000\"}],\"password\":\"123456\",\"phone\":\"18750941314\",\"requestData\":\"\",\"token\":\"eyJhbGciOiJIUzUxMiIsInppcCI6IkRFRiJ9.eNpEj8EKgzAMht8lZ4XWtjb1vtvGYC8gUSPrJnXYCoOxd1-dB8nlz0f-_MkH4tpBAxKtEU5LJTUUsEZeWj9kjpRLoLFqsE52WshN9cSuUkL8h8McWn6__MLZkJaVC8gtNLIW6Go0ShXgKe3A4g44UDcdhm3HNPfPg9Ca7vPik-eYz7hdz6c20sTxQiFnPpLP1KA23A9jqYTVpa4Iyw6xL0fN-Zea3KgkfH8AAAD__w.MNHiYP89jHfe0PTAsq3zciT4jYcWYEpkC8tMwwicWS7kmiTXn0Kb8CV1bakgyq1bhK5yPFqieSauV2MRpYwhJg\",\"userId\":\"8a8a808573d791b40173d7cae8cd0013\"}");
//        input.setText(":\"\",\"roleName\":\"业务员\",\"sysRoleCode\":\"salesMan\",\"userRoleId\":\"402879a1725fd84501725fd85a430000\"},\"orgRoleVOs\":[{\"");
//        input.setText("术股份有限公司\",\"orgPhone\":\"0592-6019177\",\"orgRoleCode\":\"group\",\"provinceName\":\"\",\"roleCode\":\"SALES_MAN\",\"role");
//        input.setText(":\"\",\"roleName\":\"业务员\",\"sysRoleCode\":\"salesMan\",\"userRoleId\":\"402879a1725fd84501725fd85a430000\"},\"orgRoleVOs\":[{\"");
        resultText = (TextView) findViewById(R.id.text_result);

        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        jni = new JniUtils();

        mKey = SafetyUtil.getInstance().getAESRandomKeyString(16);
        LogUtils.i("秘钥=" + mKey);
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
        findViewById(R.id.btn_createRSA).setOnClickListener(this);
        findViewById(R.id.btn_read).setOnClickListener(this);
        //Edittext通知父控件自己处理自己的滑动事件
        input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.input_ori && canVerticalScroll(input)) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.encrypt_activity;
    }

    /**
     * EditText竖直方向是否可以滚动
     *
     * @param //editText需要判断的EditText
     * @return true：可以滚动   false：不可以滚动
     */
    private boolean canVerticalScroll(EditText contentEt) {
        //滚动的距离
        int scrollY = contentEt.getScrollY();
        //控件内容的总高度
        int scrollRange = contentEt.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = contentEt.getHeight() - contentEt.getCompoundPaddingTop() - contentEt.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    @Override
    public void onClick(View v) {
        String ori = input.getText().toString().trim();
        String result = "";
        switch (v.getId()) {
            case R.id.btn_sha1OfApk:
                result = "sha1OfApk-> " + jni.sha1OfApk(this);
                break;
            case R.id.btn_apk:
                result = "验证apk签名-> " + jni.verifySha1OfApk(this);
                break;
            case R.id.btn_java_encode:
                result = "hmacSHA1签名编码->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(), ori, SafetyUtil.HMAC_SHA1);
                break;
            case R.id.btn_native_encode:
                result = "SHA1签名->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(), ori, SafetyUtil.SHA1);
                break;
            case R.id.btn_java_decode:
                result = "SHA224签名->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(), ori, SafetyUtil.SHA224);
                break;
            case R.id.btn_native_decode:
                result = "SHA256签名->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(), ori, SafetyUtil.SHA256);
                break;
            case R.id.btn_java_sign:
                result = "SHA384签名->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(), ori, SafetyUtil.SHA384);
                break;
            case R.id.btn_native_sign:
                result = "SHA512签名->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(), ori, SafetyUtil.SHA512);
                break;
            case R.id.btn_md5_sign:
                result = "MD5信息摘要->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(), ori, SafetyUtil.MD5).toUpperCase();
                break;
            case R.id.btn_AES_sign:
                result = "AES加密编码->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(), ori, SafetyUtil.AES);
                break;
            case R.id.btn_AESs_sign:
                result = "AES解密->" + SafetyUtil.getInstance().decode(BaseApplication.getInstance(), ori, SafetyUtil.AES);
                break;
            case R.id.btn_AES_key:
                result = "AES加密编码->" + SafetyUtil.getInstance().encryptByAESEncrypt(BaseApplication.getInstance(), ori, mKey);
                break;
            case R.id.btn_AESs_key:
                result = "AES解密->" + SafetyUtil.getInstance().decryptByAESEncrypt(BaseApplication.getInstance(), ori, mKey);
                break;
            case R.id.btn_RSA_sign:
                result = "RSA公钥加密编码->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(), ori, SafetyUtil.RSA_PUBKEY);
//                result ="RSA私钥加密编码->" + SafetyUtil.getInstance().encode(BaseApplication.getInstance(),ori,SafetyUtil.RSA_PRIVATEKEY);
                break;
            case R.id.btn_RSAs_sign:
                result = "RSA私钥解密->" + SafetyUtil.getInstance().decode(BaseApplication.getInstance(), ori, SafetyUtil.RSA_PRIVATEKEY);
//                result ="RSA公钥解密->" + SafetyUtil.getInstance().decode(BaseApplication.getInstance(),ori,SafetyUtil.RSA_PUBKEY);
                break;
            case R.id.btn_RSAa_sign:
                result = "RSA公钥加密编码->" + SafetyUtil.getInstance().encryptRSA(BaseApplication.getInstance(), ori, SafetyUtil.PUBLIC_KEY);
//                result ="RSA私钥加密编码->" +  SafetyUtil.getInstance().encodeByRSAPriKey(BaseApplication.getInstance(),ori,SafetyUtil.PRIVATE_KEY);
                break;
            case R.id.btn_RSAd_sign:
                result = "RSA私钥解密->" + SafetyUtil.getInstance().decryptRSA(BaseApplication.getInstance(), ori, SafetyUtil.PRIVATE_KEY);
//                result = "RSA公钥解密->" + SafetyUtil.getInstance().decodeByRSAPubKey(BaseApplication.getInstance(),ori,SafetyUtil.PUBLIC_KEY);
                break;
            case R.id.btn_copy_sign:
                String ssss = resultText.getText().toString();
                ClipData mClipData = ClipData.newPlainText("Label", ssss);         //‘Label’这是任意文字标签
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
            case R.id.btn_createRSA:
                String[] RSAKey = SafetyUtil.getInstance().generateRSAKey(this);
                LogUtils.i("公钥=\n" + RSAKey[0]);
                LogUtils.i("私钥=\n" + RSAKey[1]);
                break;
            case R.id.btn_read:
                result = SafetyUtil.getInstance().readAssets(this,ori);
                break;
        }
        resultText.setText(result);
    }

}
