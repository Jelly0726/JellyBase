package com.base.Display;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.jelly.baselibrary.imageView.ImageViewPlus;
import com.jelly.jellybase.R;
import com.zxingx.library.utils.CreateQRImage;


/**
 * 支付扫码
 */
public class QRCodeDislay extends Presentation {
    private String code;
    ImageViewPlus qrCode;
    public QRCodeDislay(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public QRCodeDislay(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_dislay);
        qrCode=findViewById(R.id.qrCode);
        CreateQRImage.createQRImage(code, qrCode);
    }
    @Override
    public void dismiss() {
        super.dismiss();

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        if (qrCode!=null)
            CreateQRImage.createQRImage(code, qrCode);
    }
}
