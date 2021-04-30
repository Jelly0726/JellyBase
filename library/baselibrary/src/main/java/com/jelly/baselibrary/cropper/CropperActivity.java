package com.jelly.baselibrary.cropper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.R;
import com.jelly.baselibrary.Utils.BitmapUtil;
import com.jelly.baselibrary.databinding.CropperActivityBinding;

import java.io.IOException;


public class CropperActivity extends BaseActivity<CropperActivityBinding> implements View.OnClickListener {
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUri=getIntent().getData();
        if (imageUri==null)return;
        iniView();
    }
    private void iniView(){
        getBinding().leftBack.setOnClickListener(this);
        getBinding().cropOk.setOnClickListener(this);
        try {
            getBinding().cropImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onClick(View view){
        int id = view.getId();
        if (id == R.id.left_back) {
            finish();
        } else if (id == R.id.cropOk) {//获取裁剪的图片
            Bitmap cropBitMap = getBinding().cropImageView.getCroppedImage();
            if (cropBitMap == null) return;
            getBinding().cropImageView.setImageBitmap(cropBitMap);
            String path = BitmapUtil.getInstance().saveBitmap(this, cropBitMap);
            Intent intent = getIntent();
            intent.putExtra(BitmapUtil.PATH, path);
            setResult(BitmapUtil.IMG_CROP, intent);
            finish();
        }

    }
}
