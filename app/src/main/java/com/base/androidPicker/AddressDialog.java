package com.base.androidPicker;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.base.addressmodel.Address;
import com.base.addressmodel.Area;
import com.base.addressmodel.City;
import com.base.addressmodel.Province;
import com.base.circledialog.BaseCircleDialog;
import com.base.xrefreshview.listener.OnItemClickListener;
import com.base.xrefreshview.view.SimpleItemDecoration;
import com.jelly.jellybase.R;
import com.jelly.jellybase.adpater.AddressAdapter;

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
public class AddressDialog extends BaseCircleDialog {
    protected View rootView;
    private Unbinder mUnbinder;
    @BindView(R.id.cancel_img)
    ImageView cancel_img;
    @BindView(R.id.address_gp)
    RadioGroup address_gp;
    @BindView(R.id.province_rb)
    RadioButton province_rb;
    @BindView(R.id.city_rb)
    RadioButton city_rb;
    @BindView(R.id.district_rb)
    RadioButton district_rb;
    @BindView(R.id.recycler_view)
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
        dialogFragment.setCanceledBack(false);
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
            rootView =inflater.inflate(R.layout.address_dialog, container, false);
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
    @OnClick({R.id.cancel_img,R.id.province_rb,R.id.city_rb})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_img:
                dismiss();
                break;
            case R.id.province_rb:
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
            case R.id.city_rb:
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