package com.jelly.jellybase.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.Utils.StringUtil;
import com.base.androidPicker.AddressPickTask;
import com.base.httpmvp.contact.OperaAddressContact;
import com.base.httpmvp.presenter.OperaAddressPresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.base.password.PwdCheckUtil;
import com.base.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.datamodel.RecevierAddress;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.TextParams;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/10/13.
 */

public class AddressEditActivity extends BaseActivityImpl<OperaAddressContact.View,OperaAddressContact.Presenter>
        implements OperaAddressContact.View{
    @BindView(R.id.left_back)
    LinearLayout left_back;
    @BindView(R.id.address_tv)
    TextView address_tv;
    @BindView(R.id.name_edit)
    EditText name_edit;
    @BindView(R.id.phone_edit)
    EditText phone_edit;
    @BindView(R.id.address_tvs)
    EditText address_tvs;
    @BindView(R.id.default_checkBox)
    CheckBox default_checkBox;
    @BindView(R.id.commit_layout)
    LinearLayout commit_layout;
    @BindView(R.id.delete_address)
    TextView delete_address;
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
    public int getLayoutId(){
        return R.layout.addaddress_edit_activity;
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
        address_tvs.addTextChangedListener(new TextWatcher() {
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
                    address_tvs.setText(address);
                    address_tvs.setSelection(address.length());
                }

            }
        });
        if (recevierAddress!=null){
            mProvince=recevierAddress.getProvince();
            mCity=recevierAddress.getCity();
            mCounty=recevierAddress.getArea();
            address_tv.setText(recevierAddress.getProvince()+recevierAddress.getCity()+recevierAddress.getArea());
            String address=mProvince+mCity+mCounty+"";
            address_tvs.setText(address+recevierAddress.getAddress());
            if (!TextUtils.isEmpty(recevierAddress.getAddress()))
                address_tvs.setSelection(address.length()+recevierAddress.getAddress().length());
            name_edit.setText(recevierAddress.getName());
            if (!TextUtils.isEmpty(recevierAddress.getName()))
                name_edit.setSelection(recevierAddress.getName().length());
            phone_edit.setText(StringUtil.getReplace(recevierAddress.getPhone(),4,8));
            if (!TextUtils.isEmpty(recevierAddress.getPhone()))
                phone_edit.setSelection(recevierAddress.getPhone().length());
            default_checkBox.setChecked(recevierAddress.isDefault());
        }
        phone_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    phone_edit.getText().clear();
                }
                return false;
            }
        });
    }
    @OnClick({R.id.left_back,R.id.address_tv,R.id.commit_layout,R.id.delete_address})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.address_tv:
                onAddressPicker();
                break;
            case R.id.commit_layout:
                operatype=1;
                String phone=phone_edit.getText().toString().trim();
                String name=name_edit.getText().toString();
                String address=address_tvs.getText().toString();
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
                    address_tv.setText(addre);
                    String str=address_tvs.getText().toString().trim();
                    if(str.length()>0){
                        str=str.replace(mProvince,province.getAreaName());
                        str=str.replace(mCity,city.getAreaName());
                    }else {
                        str=addre;
                    }
                    address_tvs.setText(str);
                    address_tvs.setSelection(str.length());
                    mProvince=province.getAreaName();
                    mCity=city.getAreaName();
                } else {
                    //showToast(province.getAreaName() + city.getAreaName() + county.getAreaName());
                    String addre=province.getAreaName() + city.getAreaName()+county.getAreaName();
                    address_tv.setText(addre);
                    String str=address_tvs.getText().toString().trim();
                    if(str.length()>0){
                        str=str.replace(mProvince,province.getAreaName());
                        str=str.replace(mCity,city.getAreaName());
                        str=str.replace(mCounty,county.getAreaName());
                    }else {
                        str=addre;
                    }
                    address_tvs.setText(str);
                    address_tvs.setSelection(str.length());
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
        String phone = phone_edit.getText().toString().trim();
        if (!PwdCheckUtil.isDigit2(phone)){
            phone=recevierAddress.getPhone();
        }
        String name = name_edit.getText().toString();
        String address = address_tvs.getText().toString();
        Map map = new TreeMap();
        map.put("addressid", recevierAddress.getAddressid());
        if (operatype == 1){
            map.put("name", name);
            map.put("phone", phone);
            map.put("province", mProvince);
            map.put("city", mCity);
            map.put("area", mCounty);
            map.put("address", address.replace(mProvince + mCity + mCounty, ""));
            map.put("isdefault", default_checkBox.isChecked());
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
