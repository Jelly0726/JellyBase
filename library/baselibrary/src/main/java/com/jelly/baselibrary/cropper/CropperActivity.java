package com.jelly.baselibrary.cropper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.jelly.baselibrary.R2;
import com.jelly.baselibrary.Utils.BitmapUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

public class CropperActivity extends AppCompatActivity {
    @BindView(R2.id.cropImageView)
    CropImageView cropImageView;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R2.layout.cropper_activity);
        imageUri=getIntent().getData();
        if (imageUri==null)return;
        iniView();
    }
    private void iniView(){
        try {
            cropImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @OnClick({R2.id.left_back,R2.id.cropOk})
    public void onClick(View view){
        switch (view.getId()){
            case R2.id.left_back:
                finish();
                break;
            case R2.id.cropOk:
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
