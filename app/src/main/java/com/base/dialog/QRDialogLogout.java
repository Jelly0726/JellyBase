package com.base.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.base.screenCapture.ScreenUtils;
import com.base.zxing.CreateQRImage;
import com.jelly.jellybase.R;
import com.mylhyl.circledialog.AbsBaseCircleDialog;

/**
 * 二维码框-自定义
 * Created by hupei on 2017/4/5.
 */
public class QRDialogLogout extends AbsBaseCircleDialog implements View.OnClickListener {
    private ImageView imageView;
    private Button rebuild_btn;
    private Button save_btn;
    private int rebuildBtnVisibility = View.VISIBLE;
    private int saveBtnVisibility = View.GONE;

    public static QRDialogLogout getInstance() {
        QRDialogLogout dialogFragment = new QRDialogLogout();
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(1f);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.qrcode_dialog_logout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        rebuild_btn = (Button) view.findViewById(R.id.rebuild_btn);
        rebuild_btn.setVisibility(rebuildBtnVisibility);
        rebuild_btn.setOnClickListener(this);
        save_btn = (Button) view.findViewById(R.id.save_btn);
        save_btn.setVisibility(saveBtnVisibility);
        save_btn.setOnClickListener(this);
        view.findViewById(R.id.but_cancle).setOnClickListener(this);
        imageView = (ImageView) view.findViewById(R.id.qr_img);
        CreateQRImage.createQRImage("123", imageView);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setRebuildBtnVisibility(int visibility) {
        rebuildBtnVisibility = visibility;
    }

    public void setSaveBtnVisibility(int visibility) {
        saveBtnVisibility = visibility;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                ScreenUtils.getInstance().ScreenShot(this.getContext(), getView());
                break;
            case R.id.rebuild_btn:
                CreateQRImage.createQRImage("12346546465", imageView);
                break;
            case R.id.but_cancle:
                dismiss();
                break;
        }
    }
}
