package com.jelly.baselibrary.androidPicker;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jelly.baselibrary.R;
import com.jelly.baselibrary.R2;
import com.jelly.baselibrary.addressmodel.Address;
import com.jelly.baselibrary.addressmodel.Area;
import com.jelly.baselibrary.addressmodel.City;
import com.jelly.baselibrary.addressmodel.Province;
import com.jelly.baselibrary.xrefreshview.view.SimpleItemDecoration;
import com.mylhyl.circledialog.AbsBaseCircleDialog;
import com.yanzhenjie.album.impl.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 地址选择器
 * Created by hupei on 2017/4/5.
 */
public class AddressDialog extends AbsBaseCircleDialog {
    protected View rootView;
    private Unbinder mUnbinder;
    @BindView(R2.id.address_cancel_img)
    ImageView cancel_img;
    @BindView(R2.id.address_gp)
    RadioGroup address_gp;
    @BindView(R2.id.province_rb)
    RadioButton province_rb;
    @BindView(R2.id.city_rb)
    RadioButton city_rb;
    @BindView(R2.id.district_rb)
    RadioButton district_rb;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private AddressAdapter adapter;
    private List<Province> mList=new ArrayList<Province>();
    private Province province;
    private City city;
    private Area district;
    private Address address;
    private OnAddressPickListener onConfirmListener;
    public static AddressDialog getInstance() {
        AddressDialog dialogFragment = new AddressDialog();
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(1f);
        return dialogFragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUnbinder.unbind();
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        if (rootView == null)
            rootView =inflater.inflate(R2.layout.address_dialog, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniData();
    }
    private void iniData(){
        adapter=new AddressAdapter(getActivity(),mList);
        adapter.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(View view,int position) {
                if (province==null){
                    province=mList.get(position);
                    adapter.setData(province.getCities());
                    province_rb.setText(province.getAreaName());
                    city_rb.setChecked(true);
                    city_rb.setVisibility(View.VISIBLE);
                    city_rb.setText("请选择");
                }else  if (city==null){
                    city=province.getCities().get(position);
                    adapter.setData(city.getCounties());
                    city_rb.setText(city.getAreaName());
                    district_rb.setChecked(true);
                    district_rb.setVisibility(View.VISIBLE);
                    district_rb.setText("请选择");
                }else  {
                    district =city.getCounties().get(position);
                    if(onConfirmListener!=null){
                        onConfirmListener.onAddressPicked(province, city, district);
                    }
                    dismiss();
                }
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleItemDecoration(0,3, SimpleItemDecoration.NONE));
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    public void setData(ArrayList<Province> result){
        mList.clear();
        mList.addAll(result);
        if (getActivity()!=null)
            adapter.setData(mList);

    }
    @OnClick({R2.id.cancel_img,R2.id.province_rb,R2.id.city_rb})
    public void onClick(View v) {
        switch (v.getId()){
            case R2.id.cancel_img:
                dismiss();
                break;
            case R2.id.province_rb:
                province_rb.setChecked(true);
                province_rb.setText("请选择");
                province=null;
                city=null;
                district =null;
                city_rb.setText("");
                city_rb.setVisibility(View.GONE);
                district_rb.setText("");
                district_rb.setVisibility(View.GONE);
                adapter.setData(mList);
                break;
            case R2.id.city_rb:
                city_rb.setChecked(true);
                city_rb.setText("请选择");
                city=null;
                district =null;
                district_rb.setText("");
                district_rb.setVisibility(View.GONE);
                adapter.setData(province.getCities());
                break;
        }
    }
    public OnAddressPickListener getOnAddressPickListener() {
        return onConfirmListener;
    }

    public void setOnAddressPickListener(OnAddressPickListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }
    /**
     * 地址选择回调
     */
    public interface OnAddressPickListener {

        void onAddressPicked(Province province, City city, Area county);

    }
}