package com.jelly.jellybase.activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.base.album.AlbumAdapter;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.view.listener.OnRvItemClickListener;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.impl.OnItemClickListener;
import com.yanzhenjie.album.util.AlbumUtils;
import com.yanzhenjie.album.util.DisplayUtils;
import com.yanzhenjie.album.widget.divider.Divider;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/2.
 */

public class AnswerActivity extends BaseActivity implements View.OnClickListener{
    private static final String filePath=Environment.getExternalStorageDirectory().getPath();//文件路径
    private Uri imageUri;
    private LinearLayout left_back;

    private AlbumAdapter mAdapter;
    private ArrayList<AlbumFile> mAlbumFiles=new ArrayList<>();
    private RecyclerView recyclerView;
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.initScreen(this);
        iniView();
    }
    @Override
    public int getLayoutId(){
        return R.layout.answer_activity;
    }
    private void iniView(){
        left_back= (LinearLayout) findViewById(R.id.left_back);
        left_back.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        Divider divider = AlbumUtils.getDivider(Color.WHITE);
        recyclerView.addItemDecoration(divider);

        int itemSize = (DisplayUtils.sScreenWidth - (divider.getWidth() * 4)) / 3;
        mAdapter = new AlbumAdapter(this, itemSize, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position>=mAlbumFiles.size()){
                    showDialog();
                }else
                    previewImage(position);
            }
        });
        mAdapter.setMaxItem(3);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mAlbumFiles);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
        }
    }
    private void showDialog(){
        final String[] items = {"拍照", "从相册选择"};
        new CircleDialog.Builder()
                .configDialog(new ConfigDialog() {
                    @Override
                    public void onConfig(DialogParams params) {
                        //增加弹出动画
                        params.animStyle = R.style.Anim_bottom;
                    }
                })
                .setTitle("标题")
                .setTitleColor(Color.BLUE)
                .setItems(items, new OnRvItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position) {
                        switch (position){
                            case 0:
                                cameraImage();
                                break;
                            case 1:
                                selectImage();
                                break;
                        }
                        return true;
                    }

                })
                .setNegative("取消", null)
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        //取消按钮字体颜色
                        params.textColor = Color.RED;
                    }
                })
                .show(getSupportFragmentManager());
    }
    /**
     * camera picture, from album.
     */
    private void cameraImage() {
//        File fileUri = new File(filePath+ "/"+System.currentTimeMillis()+".jpg");
//        imageUri= Uri.fromFile(fileUri);
        Album.camera(this) // 相机功能。
                .image() // 拍照。
                //.filePath(fileUri.getPath()) // 文件保存路径，非必须。
                .requestCode(200)
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, @NonNull String result) {
                        File fileUri = new File(result);
                        AlbumFile albumFile=new AlbumFile();
                        albumFile.setPath(result);
                        albumFile.setChecked(true);
                        albumFile.setMediaType(AlbumFile.TYPE_IMAGE);
                        albumFile.setName(fileUri.getName());

                        if(mAlbumFiles!=null){
                            mAlbumFiles.add(albumFile);
                        }else {
                            mAlbumFiles=new ArrayList<AlbumFile>();
                            mAlbumFiles.add(albumFile);
                        }
                        mAdapter.notifyDataSetChanged(mAlbumFiles);

                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, @NonNull String result) {
                    }
                })
                .start();
    }
    /**
     * Select picture, from album.
     */
    private void selectImage() {
        Album.image(this)
                .multipleChoice()
                .requestCode(200)
                .camera(true)
                .columnCount(2)
                .selectCount(3)
                .checkedList(mAlbumFiles)
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(int requestCode, @NonNull ArrayList<AlbumFile> result) {
                        if(mAlbumFiles!=null){
                            mAlbumFiles.clear();
                            mAlbumFiles.addAll(result);
                        }else {
                            mAlbumFiles = result;
                        }
                        mAdapter.notifyDataSetChanged(mAlbumFiles);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, @NonNull String result) {
                        Toast.makeText(AnswerActivity.this,"取消", Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    /**
     * Preview image, to album.
     */
    private void previewImage(int position) {
        if (mAlbumFiles == null || mAlbumFiles.size() == 0) {
            Toast.makeText(this, "没有选择图片", Toast.LENGTH_LONG).show();
        } else {
            Album.galleryAlbum(this)
                    .checkable(true)
                    .checkedList(mAlbumFiles)
                    .currentPosition(position)
                    .onResult(new Action<ArrayList<AlbumFile>>() {
                        @Override
                        public void onAction(int requestCode, @NonNull ArrayList<AlbumFile> result) {
                            mAlbumFiles = result;
                            mAdapter.notifyDataSetChanged(mAlbumFiles);
                        }
                    })
                    .start();
        }
    }

}
