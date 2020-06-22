package com.jelly.jellybase.videorecord;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.base.view.BaseActivity;
import com.jelly.jellybase.R;

import java.io.File;

public class MainActivity extends BaseActivity {

    Button button;
    String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vedio_record_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.CAMERA}, 5);
        }
        button = (Button) findViewById(R.id.btn_record_video);
        File fpath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "video");
//        File fpath = new File(MediaUtils.getOutputMediaFileUri(this, MediaUtils.MEDIA_TYPE_VIDEO).getPath());
        if (!fpath.exists()) {
            fpath.mkdirs();
        }
        videoPath = fpath.getAbsolutePath();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = VedioRecordActivity.startRecordActivity(videoPath,MainActivity.this);
                startActivity(intent);
            }
        });
    }
}
