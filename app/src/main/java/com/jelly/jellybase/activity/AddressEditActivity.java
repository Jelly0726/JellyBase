package com.jelly.jellybase.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.base.httpmvp.mvpView.BaseActivityImpl;
import com.jelly.baselibrary.Utils.StringUtil;
import com.jelly.baselibrary.androidPicker.AddressPickTask;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.password.PwdCheckUtil;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.AddaddressEditActivityBinding;
import com.jelly.jellybase.datamodel.RecevierAddress;
import com.jelly.mvp.contact.OperaAddressContact;
import com.jelly.mvp.presenter.OperaAddressPresenter;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.TextParams;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/10/13.
 */

public class AddressEditActivity extends BaseActivityImpl<OperaAddressContact.View
        ,OperaAddressContact.Presenter, AddaddressEditActivityBinding>
        implements OperaAddressContact.View, View.OnClickListener {
    private String mProvince="";
    private String mCity="";
    private String mCounty="";
    private RecevierAddress recevierAddress;
    private int operatype=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recevierAddress= (RecevierAddress) getIntent().getSerializableExtra("recevierAddress");
        iniView();
    }
    @Override
    public OperaAddressContact.Presenter initPresenter() {
        return new OperaAddressPresenter();
    }
    @Override
    public OperaAddressContact.View initIBView() {
        return this;
    }

    @Override
    public <T> ObservableTransformer<T, T> bindLifecycle() {
        return lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY);
    }
    private void iniView(){
        getBinding().leftBack.setOnClickListener(this);
        getBinding().addressTv.setOnClickListener(this);
        getBinding().commitLayout.setOnClickListener(this);
        getBinding().deleteAddress.setOnClickListener(this);
        getBinding().addressTvs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String address=mProvince+mCity+mCounty;
                if (s.toString().trim().length()<address.length()){
                    getBinding().addressTvs.setText(address);
                    getBinding().addressTvs.setSelection(address.length());
                }

            }
        });
        if (recevierAddress!=null){
            mProvince=recevierAddress.getProvince();
            mCity=recevierAddress.getCity();
            mCounty=recevierAddress.getArea();
            getBinding().addressTv.setText(recevierAddress.getProvince()+recevierAddress.getCity()+recevierAddress.getArea());
            String address=mProvince+mCity+mCounty+"";
            getBinding().addressTvs.setText(address+recevierAddress.getAddress());
            if (!TextUtils.isEmpty(recevierAddress.getAddress()))
                getBinding().addressTvs.setSelection(address.length()+recevierAddress.getAddress().length());
            getBinding().nameEdit.setText(recevierAddress.getName());
            if (!TextUtils.isEmpty(recevierAddress.getName()))
                getBinding().nameEdit.setSelection(recevierAddress.getName().length());
            getBinding().phoneEdit.setText(StringUtil.getReplace(recevierAddress.getPhone(),4,8));
            if (!TextUtils.isEmpty(recevierAddress.getPhone()))
                getBinding().phoneEdit.setSelection(recevierAddress.getPhone().length());
            getBinding().defaultCheckBox.setChecked(recevierAddress.isDefault());
        }
        getBinding().phoneEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    getBinding().phoneEdit.getText().clear();
                }
                return false;
            }
        });
    }
    public void onClick(View v) {
        if (AntiShake.check(v))return;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.address_tv:
                onAddressPicker();
                break;
            case R.id.commit_layout:
                operatype=1;
                String phone= getBinding().phoneEdit.getText().toString().trim();
                String name= getBinding().nameEdit.getText().toString();
                String address= getBinding().addressTvs.getText().toString();
                if (TextUtils.isEmpty(name)){
                    ToastUtils.showToast(this,"姓名不能为空!");
                    return;
                }
                if (TextUtils.isEmpty(phone)){
                    ToastUtils.showToast(this,"电话不能为空!");
                    return;
                }
                if (TextUtils.isEmpty(address)){
                    ToastUtils.showToast(this,"收货地址不能为空!");
                    return;
                }
                presenter.operaAddress();
                break;
            case R.id.delete_address:
                CircleDialog.Builder circleDialog = new CircleDialog.Builder()
                        .configDialog(new ConfigDialog() {
                            @Override
                            public void onConfig(DialogParams params) {
                                params.width = 0.6f;
                            }
                        })
                        .setCanceledOnTouchOutside(false)
                        .setCancelable(false)
                        .setTitle("删除！")
                        .configText(new ConfigText() {
                            @Override
                            public void onConfig(TextParams params) {
                                params.gravity = Gravity.CENTER;
                                params.textColor = Color.parseColor("#FF1F50F1");
                                params.padding = new int[]{20, 0, 20, 0};
                            }
                        })
                        .setText("确定要删除该地址？")
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                operatype=2;
                                presenter.operaAddress();
                            }
                        })
                        .setNegative("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                circleDialog.show(getSupportFragmentManager());
                break;
        }
    }
    public void onAddressPicker() {
        AddressPickTask task = new AddressPickTask(this);
        task.setHideProvince(false);
        task.setHideCounty(false);
        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                ToastUtils.showToast(AddressEditActivity.this, "数据初始化失败");
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                if (county == null) {
                    //showToast(province.getAreaName() + city.getAreaName());
                    String addre=province.getAreaName() + city.getAreaName();
                    getBinding().addressTv.setText(addre);
                    String str= getBinding().addressTvs.getText().toString().trim();
                    if(str.length()>0){
                        str=str.replace(mProvince,province.getAreaName());
                        str=str.replace(mCity,city.getAreaName());
                    }else {
                        str=addre;
                    }
                    getBinding().addressTvs.setText(str);
                    getBinding().addressTvs.setSelection(str.length());
                    mProvince=province.getAreaName();
                    mCity=city.getAreaName();
                } else {
                    //showToast(province.getAreaName() + city.getAreaName() + county.getAreaName());
                    String addre=province.getAreaName() + city.getAreaName()+county.getAreaName();
                    getBinding().addressTv.setText(addre);
                    String str= getBinding().addressTvs.getText().toString().trim();
                    if(str.length()>0){
                        str=str.replace(mProvince,province.getAreaName());
                        str=str.replace(mCity,city.getAreaName());
                        str=str.replace(mCounty,county.getAreaName());
                    }else {
                        str=addre;
                    }
                    getBinding().addressTvs.setText(str);
                    getBinding().addressTvs.setSelection(str.length());
                    mProvince=province.getAreaName();
                    mCity=city.getAreaName();
                    mCounty=county.getAreaName();
                }
            }
        });
        if (recevierAddress==null) {
            task.execute("福建", "厦门", "湖里");
        }else {
            task.execute(recevierAddress.getProvince(),recevierAddress.getCity(),recevierAddress.getArea());
        }
    }
    @Override
    public Object operaAddressParam() {
        String phone = getBinding().phoneEdit.getText().toString().trim();
        if (!PwdCheckUtil.isDigit2(phone)){
            phone=recevierAddress.getPhone();
        }
        String name = getBinding().nameEdit.getText().toString();
        String address = getBinding().addressTvs.getText().toString();
        Map map = new TreeMap();
        map.put("addressid", recevierAddress.getAddressid());
        if (operatype == 1){
            map.put("name", name);
            map.put("phone", phone);
            map.put("province", mProvince);
            map.put("city", mCity);
            map.put("area", mCounty);
            map.put("address", address.replace(mProvince + mCity + mCounty, ""));
            map.put("isdefault", getBinding().defaultCheckBox.isChecked());
        }
        map.put("operatype",operatype);
        return map;
    }

    @Override
    public void operaAddressSuccess(Object mCallBackVo) {
        ToastUtils.showToast(this, (String) mCallBackVo);
        finish(2000);
    }

    @Override
    public void operaAddressFailed(String message) {
        ToastUtils.showToast(this,message);
    }
}
