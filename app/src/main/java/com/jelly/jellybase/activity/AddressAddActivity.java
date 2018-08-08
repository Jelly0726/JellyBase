package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.androidPicker.AddressPickTask;
import com.base.toast.ToastUtils;
import com.base.httpmvp.contact.OperaAddressContact;
import com.base.httpmvp.presenter.OperaAddressPresenter;
import com.base.httpmvp.view.BaseActivityImpl;
import com.jelly.jellybase.R;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;

/**
 * Created by Administrator on 2017/10/13.
 */

public class AddressAddActivity extends BaseActivityImpl<OperaAddressContact.Presenter>
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
    @BindView(R.id.commit_tv)
    TextView commit_tv;
    private String mProvince="";
    private String mCity="";
    private String mCounty="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addaddress_activity);
        ButterKnife.bind(this);
        iniView();
    }

    @Override
    public OperaAddressContact.Presenter initPresenter() {
        return new OperaAddressPresenter(this);
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
    }
    @Override
    public void onBackPressed() {
        closeProgress();
        super.onBackPressed();
    }

    @OnClick({R.id.left_back,R.id.address_tv,R.id.commit_tv})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.address_tv:
                onAddressPicker();
                break;
            case R.id.commit_tv:
                String phone=phone_edit.getText().toString().trim();
                String name=name_edit.getText().toString();
                String address=address_tvs.getText().toString();
                if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(name)||TextUtils.isEmpty(address)){
                    ToastUtils.showToast(this,"姓名、电话、收货地址不能为空!");
                    return;
                }
                presenter.operaAddress(lifecycleProvider.<Long>bindUntilEvent(ActivityEvent.DESTROY));
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
                ToastUtils.showToast(AddressAddActivity.this, "数据初始化失败");
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
        task.execute("福建", "厦门", "湖里");
    }

    @Override
    public Object operaAddressParam() {
        String phone=phone_edit.getText().toString().trim();
        String name=name_edit.getText().toString();
        String address=address_tvs.getText().toString();
        Map map=new TreeMap();
        map.put("name",name);
        map.put("phone",phone);
        map.put("province",mProvince);
        map.put("city",mCity);
        map.put("area",mCounty);
        map.put("address",address.replace(mProvince+mCity+mCounty,""));
        map.put("isdefault",default_checkBox.isChecked());
        map.put("operatype",0);
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
