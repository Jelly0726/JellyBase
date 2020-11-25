package com.base.cropper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.base.Utils.BitmapUtil;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

public class CropperActivity extends BaseActivity {
    @BindView(R.id.cropImageView)
    CropImageView cropImageView;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUri=getIntent().getData();
        if (imageUri==null)return;
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.cropper_activity;
    }
    private void iniView(){
        try {
            cropImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @OnClick({R.id.left_back,R.id.cropOk})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.cropOk:
                //获取裁剪的图片
                Bitmap cropBitMap = cropImageView.getCroppedImage();
                if (cropBitMap==null)return;
                cropImageView.setImageBitmap(cropBitMap);
                String path= BitmapUtil.getInstance().saveBitmap(this, cropBitMap);
                Intent intent=getIntent();
                intent.putExtra(BitmapUtil.PATH, path);
                setResult(BitmapUtil.IMG_CROP, intent);
                finish();
                break;
        }

    }
}
