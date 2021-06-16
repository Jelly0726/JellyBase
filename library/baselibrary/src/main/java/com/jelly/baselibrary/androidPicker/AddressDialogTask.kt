package com.jelly.baselibrary.androidPicker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSON;
import com.jelly.baselibrary.addressmodel.Province;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * 获取地址数据并显示地址选择器
 *
 * @author 李玉江[QQ:1032694760]
 * @since 2015/12/15
 */
public class AddressDialogTask extends AsyncTask<String, Void, ArrayList<Province>> {
    private WeakReference<Activity> activity;
    private ProgressDialog dialog;
    private Callback callback;
    private String selectedProvince = "", selectedCity = "", selectedCounty = "";
    private boolean hideProvince = false;
    private boolean hideCounty = false;

    public AddressDialogTask(Activity activity) {
        this.activity = new WeakReference<>(activity);;
    }

    public void setHideProvince(boolean hideProvince) {
        this.hideProvince = hideProvince;
    }

    public void setHideCounty(boolean hideCounty) {
        this.hideCounty = hideCounty;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        if (activity.get()==null)return;
        dialog = ProgressDialog.show(activity.get(), null, "正在初始化数据...", true, true);
    }

    @Override
    protected ArrayList<Province> doInBackground(String... params) {
        if (params != null) {
            switch (params.length) {
                case 1:
                    selectedProvince = params[0];
                    break;
                case 2:
                    selectedProvince = params[0];
                    selectedCity = params[1];
                    break;
                case 3:
                    selectedProvince = params[0];
                    selectedCity = params[1];
                    selectedCounty = params[2];
                    break;
                default:
                    break;
            }
        }
        ArrayList<Province> data = new ArrayList<>();
        try {
            if (activity.get()==null)return data;
            String json = ConvertUtils.toString(activity.get().getAssets().open("city.json"));
            data.addAll(JSON.parseArray(json, Province.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(ArrayList<Province> result) {
        dialog.dismiss();
        if (result.size() > 0) {
            if (activity.get()==null){callback.onAddressInitFailed();return;}
            AddressDialog addCartDialog= AddressDialog.getInstance();
            addCartDialog.setData(result);
            addCartDialog.setOnAddressPickListener(callback);
            addCartDialog.show(((FragmentActivity)activity.get()).getSupportFragmentManager(),"addCartDialog");
        } else {
            callback.onAddressInitFailed();
        }
    }

    public interface Callback extends AddressDialog.OnAddressPickListener {

        void onAddressInitFailed();

    }

}
