package com.jelly.jellybase.userInfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XScrollView;
import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jelly.baselibrary.model.PersonalInfo;
import com.jelly.baselibrary.model.Sex;
import com.jelly.baselibrary.model.UploadBean;
import com.jelly.baselibrary.model.UploadData;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.PersonalInfoActivityBinding;
import com.jelly.mvp.contact.PersonalInfoContact;
import com.jelly.mvp.presenter.PersonalInfoPresenter;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.view.listener.OnRvItemClickListener;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.SinglePicker;
import cn.qqtheme.framework.util.ConvertUtils;


/**
 * Created by Administrator on 2017/9/26.
 */

public class PersonalInforActivity extends BaseActivityImpl<PersonalInfoContact.View
        ,PersonalInfoContact.Presenter, PersonalInfoActivityBinding>
        implements PersonalInfoContact.View, View.OnClickListener {
    private static final int REQUEST_CODE = 234;
    private String mYear;
    private String mMonth;
    private String mDay;
    private ArrayList<AlbumFile> mAlbumFiles=new ArrayList<>();
    private UploadData uploadData;
    private PersonalInfo userInfo;
    private Sex sex;
    private List<Sex> sexList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo= (PersonalInfo) getIntent().getSerializableExtra("userInfo");
        if (sexList==null){
            sexList = new ArrayList<>();
            sexList.add(new Sex(1, "男"));
            sexList.add(new Sex(2, "女"));
            sex=sexList.get(0);
            getBinding().sexTv.setText(sex.getName());
        }
        iniView();
        initXRefreshView();
        presenter.getInfo();

    }
    private void iniView(){
        getBinding().leftBack.setOnClickListener(this);
        getBinding().commitTv.setOnClickListener(this);
        getBinding().changePwd.setOnClickListener(this);
        getBinding().avatarLayout.setOnClickListener(this);
        getBinding().birthdayTv.setOnClickListener(this);
        getBinding().editTv.setOnClickListener(this);
        getBinding().sexTv.setOnClickListener(this);
        if (userInfo!=null) {
            //iniEditable(false);
            getBinding().niknameTv.setText(userInfo.getNickname());
            if (!TextUtils.isEmpty(userInfo.getNickname()))
                getBinding().niknameTv.setSelection(userInfo.getNickname().length());
            getBinding().nameTv.setText(userInfo.getName());
            if (!TextUtils.isEmpty(userInfo.getName()))
                getBinding().nameTv.setSelection(userInfo.getName().length());
            getBinding().emailTv.setText(userInfo.getEmail());
            if (!TextUtils.isEmpty(userInfo.getEmail()))
                getBinding().emailTv.setSelection(userInfo.getEmail().length());
            if (userInfo.getSex()) {
                sex=sexList.get(0);
            }else {
                sex=sexList.get(1);
            }
            getBinding().sexTv.setText(sex.getName());
            getBinding().birthdayTv.setText(userInfo.getBirthday());
            getBinding().idcartTv.setText(userInfo.getIdcard());
            if (!TextUtils.isEmpty(userInfo.getIdcard()))
                getBinding().idcartTv.setSelection(userInfo.getIdcard().length());
            getBinding().phoneTv.setText("未绑定");
            if (!TextUtils.isEmpty(userInfo.getMobile())){
                getBinding().phoneTv.setText(userInfo.getMobile());
            }
            if (!TextUtils.isEmpty(userInfo.getPhoto())&&!TextUtils.isEmpty(userInfo.getIP())){
                String phto=userInfo.getIP()+userInfo.getPhoto();
                if (userInfo.getPhoto().contains("http://")
                        ||userInfo.getPhoto().contains("https://")){
                    phto=userInfo.getPhoto();
                }
                Glide.with(this)
                        .load(phto)
                        .skipMemoryCache(true)//不使用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
                        .into(getBinding().storeImg);
            }
        }
    }
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }
    // iniEditable(false);是否编辑
    private void iniEditable(boolean editable){
        if (editable){
            getBinding().editTv.setVisibility(View.GONE);
            getBinding().commitTv.setVisibility(View.VISIBLE);
        }else {
            getBinding().editTv.setVisibility(View.VISIBLE);
            getBinding().commitTv.setVisibility(View.GONE);
        }
        getBinding().niknameTv.setEnabled(editable);
        getBinding().avatarLayout.setEnabled(editable);
        getBinding().niknameTv.setEnabled(editable);
        getBinding().nameTv.setEnabled(editable);
        getBinding().birthdayTv.setEnabled(editable);
        getBinding().idcartTv.setEnabled(editable);
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
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        File fileUri = new File(result);
                        AlbumFile albumFile=new AlbumFile();
                        albumFile.setPath(result);
                        albumFile.setChecked(true);
                        albumFile.setMediaType(AlbumFile.TYPE_IMAGE);

                        if(mAlbumFiles!=null){
                            mAlbumFiles.clear();
                            mAlbumFiles.add(albumFile);
                        }else {
                            mAlbumFiles=new ArrayList<AlbumFile>();
                            mAlbumFiles.add(albumFile);
                        }
                        presenter.upload();
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                    }
                })
                .start();
    }
    /**
     * Select picture, from album.
     */
    private void selectImage() {
        Album.image(this)
                .singleChoice()//单选
                .camera(false)
                .columnCount(2)
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction( @NonNull ArrayList<AlbumFile> result) {
                        if(mAlbumFiles!=null){
                            mAlbumFiles.clear();
                            mAlbumFiles.addAll(result);
                        }else {
                            mAlbumFiles = result;
                        }
                        presenter.upload();
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction( @NonNull String result) {
                        Toast.makeText(PersonalInforActivity.this,"取消", Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }
    private void initXRefreshView(){
        getBinding().xscrollview.setOnScrollListener(new XScrollView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(ScrollView view, int scrollState, boolean arriveBottom) {
            }

            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
            }
        });
        getBinding().customView.setAutoRefresh(false);
        getBinding().customView.setPullLoadEnable(false);
        getBinding().customView.setPullRefreshEnable(false);
        getBinding().customView.setPinnedTime(1000);
        getBinding().customView.setAutoLoadMore(false);
//		outView.setSilenceLoadMore();
        getBinding().customView.setXRefreshViewListener(simpleXRefreshListener);
        //getViewBinding().customView.setCustomFooterView(new CustomerFooter(this.getActivity()));
    }
    @Override
    public PersonalInfoContact.Presenter initPresenter() {
        return new PersonalInfoPresenter();
    }
    @Override
    public PersonalInfoContact.View initIBView() {
        return this;
    }

    @Override
    public LifecycleOwner bindLifecycle() {
        return this;
    }
   
    public void onClick(View v) {
        if (AntiShake.check(v.getId())){
            return;
        }
        Intent intent;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.change_pwd:
                intent=new Intent(this,ChangePWDActivity.class);
                startActivity(intent);
                break;
            case R.id.avatar_layout:
                showDialog();
                break;
            case R.id.birthday_tv:
                onYearMonthDayPicker();
                break;
            case R.id.commit_tv:
                String nikname= getBinding().niknameTv.getText().toString();
                String name= getBinding().nameTv.getText().toString();
                String birthday= getBinding().birthdayTv.getText().toString().trim();
                String idcart= getBinding().idcartTv.getText().toString().trim();
                if (TextUtils.isEmpty(nikname)||TextUtils.isEmpty(name)||
                        TextUtils.isEmpty(birthday)||TextUtils.isEmpty(idcart)){
                    ToastUtils.showToast(this,"昵称，姓名，生日，身份证不能为空!");
                    return;
                }
                presenter.upPersonalInfo();
                break;
            case R.id.edit_tv:
                iniEditable(true);
                break;
            case R.id.sex_tv:
                onSinglePicker();
                break;
        }
    }
    /**
     * 滑动刷新
     */
    private XRefreshView.SimpleXRefreshListener simpleXRefreshListener =new XRefreshView.SimpleXRefreshListener() {

        @Override
        public void onRefresh(boolean isPullDown) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getBinding().customView.stopRefresh();
                }
            }, 2000);
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //getViewBinding().customView.setLoadComplete(true);
                    // 刷新完成必须调用此方法停止加载
                    getBinding().customView.stopLoadMore();
                }
            }, 1000);
        }
    };
    public void onSinglePicker() {
        SinglePicker<Sex> picker = new SinglePicker<>(this, sexList);
        picker.setCanceledOnTouchOutside(false);
        if (sex==null || sex.getId()==1){
            picker.setSelectedIndex(0);
        }else
            picker.setSelectedIndex(1);
        picker.setCycleDisable(true);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<Sex>() {
            @Override
            public void onItemPicked(int index, Sex item) {
//                showToast("index=" + index + ", id=" + item.getId() + ", name=" + item.getName());
                sex=item;
                getBinding().sexTv.setText(sex.getName());
            }
        });
        picker.show();
    }

    public void onYearMonthDayPicker() {
        final DatePicker picker = new DatePicker(this);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 10));
        int endY=Calendar.getInstance().get(Calendar.YEAR);
        int Start=endY-100;
        int endM=Calendar.getInstance().get(Calendar.MONTH)+1;
        int endD=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        picker.setRangeEnd(endY, endM, endD);
        picker.setRangeStart(Start, 1, 1);
        picker.setSelectedItem(endY, endM, endD);
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                //showToast(year + "-" + month + "-" + day);
                mYear=year;
                mMonth=month;
                mDay=day;
                getBinding().birthdayTv.setText(year + "-" + month + "-" + day);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }
    @Override
    public void findPersonalInfoSuccess(Object mCallBackVo) {
        userInfo= (PersonalInfo) mCallBackVo;
        if (userInfo!=null)
            iniView();
    }

    @Override
    public void findPersonalInfoFailed(String message) {
        ToastUtils.showToast(this,message);
    }
    @Override
    public Object getPersonalInfoParam() {
        String nikname= getBinding().niknameTv.getText().toString();
        String name= getBinding().nameTv.getText().toString();
        String birthday= getBinding().birthdayTv.getText().toString().trim();
        String idcart= getBinding().idcartTv.getText().toString().trim();
        String phone= getBinding().phoneTv.getText().toString().trim();
        String email= getBinding().emailTv.getText().toString().trim();
        boolean sexs=false;
        if (sex.getId()==1){
            sexs=true;
        }else {
            sexs=false;
        }
        Map map=new TreeMap();
        map.put("alias",nikname);
        map.put("user_real_name",name);
        map.put("mobile",phone);
        map.put("user_email",email);
        if (uploadData!=null&&!TextUtils.isEmpty(uploadData.getFileUrl())){
            map.put("user_headimg",uploadData.getFileUrl());
        }
        map.put("sex",sexs);
        map.put("birthday",birthday);
        map.put("user_idcard",idcart);
        map.put("type","1");
        return map;
    }

    @Override
    public void personalInfoSuccess(Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        //iniEditable(false);
        finish(2000);
    }

    @Override
    public void personalInfoFailed(String message) {
        ToastUtils.showToast(this,message);
    }

    @Override
    public Object getUpParam() {
        String photo_path=mAlbumFiles.get(0).getPath();
        UploadBean uploadBean=new UploadBean();
        uploadBean.setFilePath(photo_path);
        uploadBean.setFileDesc("个人头像");
        return uploadBean;
    }

    @Override
    public void uploadSuccess(Object mCallBackVo) {
        uploadData=(UploadData)mCallBackVo;
        Album.getAlbumConfig().
                getAlbumLoader()
                .load( getBinding().storeImg, mAlbumFiles.get(0));
    }

    @Override
    public void uploadFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
