package com.base.Display;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.base.imageView.ImageViewPlus;
import com.base.zxing.CreateQRImage;
import com.jelly.jellybase.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 支付扫码
 */
public class QRCodeDislay extends Presentation {
    private Unbinder unbinder;
    private String code;
    @BindView(R.id.qrCode)
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
        unbinder= ButterKnife.bind(this);
        CreateQRImage.createQRImage(code, qrCode);
    }
    @Override
    public void dismiss() {
        if (unbinder!=null)
            unbinder.unbind();
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
