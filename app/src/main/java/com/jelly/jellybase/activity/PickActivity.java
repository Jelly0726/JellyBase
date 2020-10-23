package com.jelly.jellybase.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.base.Utils.MyDate;
import com.base.androidPicker.AddressPickTask;
import com.base.toast.ToastUtils;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;

/**
 * Created by JELLY on 2017/11/3.
 */

public class PickActivity extends BaseActivity implements View.OnClickListener{
    private TextView select_address;
    private TextView begin_date;
    private DatePicker picker;//时间选择器
    private String year;
    private String month;
    private String day;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_time_activity);
        iniView();
    }
    private void iniView(){
        select_address=(TextView) findViewById(R.id.select_address);
        select_address.setOnClickListener(this);

        begin_date= (TextView) findViewById(R.id.begin_date);
        begin_date.setOnClickListener(this);
    }
    /**
     * 弹出选择框
     */
    private void showPickerDialog() {
        if(picker==null){
            picker = new DatePicker(this);
            picker.setCanceledOnTouchOutside(true);
            picker.setUseWeight(true);
            picker.setTopPadding(ConvertUtils.toPx(this, 10));
            picker.setRangeEnd(2067, 1, 11);
            picker.setRangeStart(MyDate.getTimeToStampY(MyDate.getDateDaEN()),MyDate.getTimeToStampM(MyDate.getDateDaEN())
                    ,MyDate.getTimeToStampD(MyDate.getDateDaEN()));
            picker.setSelectedItem(MyDate.getTimeToStampY(MyDate.getDateDaEN()),MyDate.getTimeToStampM(MyDate.getDateDaEN())
                    ,MyDate.getTimeToStampD(MyDate.getDateDaEN()));
            picker.setResetWhileWheel(false);
            picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                @Override
                public void onDatePicked(String year, String month, String day) {
                    //showToast(year + "-" + month + "-" + day);
                    PickActivity.this.year=year;
                    PickActivity.this.month=month;
                    PickActivity.this.day=day;
                        begin_date.setText(year + "-" + month + "-" + day);
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
        }
        if(picker.isShowing()){
            picker.dismiss();
        }
        picker.show();
    }
    public void onAddressPicker() {
        AddressPickTask task = new AddressPickTask(this);
        task.setHideProvince(false);
        task.setHideCounty(false);
        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                ToastUtils.showToast(PickActivity.this, "数据初始化失败");
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                if (county == null) {
                    //showToast(province.getAreaName() + city.getAreaName());
                    select_address.setText(province.getAreaName() + city.getAreaName());
                } else {
                    //showToast(province.getAreaName() + city.getAreaName() + county.getAreaName());
                    select_address.setText(province.getAreaName() + city.getAreaName() + county.getAreaName());
                }
            }
        });
        task.execute("福建", "厦门", "湖里");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.begin_date:
                showPickerDialog();
                break;
            case R.id.select_address:
                onAddressPicker();
                break;
        }
    }
}
