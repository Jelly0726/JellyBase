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
import com.jelly.baselibrary.addressmodel.Address;
import com.jelly.baselibrary.addressmodel.Area;
import com.jelly.baselibrary.addressmodel.City;
import com.jelly.baselibrary.addressmodel.Province;
import com.jelly.baselibrary.recyclerViewUtil.SimpleItemDecoration;
import com.mylhyl.circledialog.AbsBaseCircleDialog;
import com.yanzhenjie.album.impl.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
/**
 * 地址选择器
 * Created by hupei on 2017/4/5.
 */
public class AddressDialog extends AbsBaseCircleDialog implements View.OnClickListener {
    protected View rootView;
    ImageView cancel_img;
    RadioGroup address_gp;
    RadioButton province_rb;
    RadioButton city_rb;
    RadioButton district_rb;
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
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        if (rootView == null)
            rootView =inflater.inflate(R.layout.address_dialog, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniView();
        iniData();
    }
    private void iniView(){
        cancel_img=rootView.findViewById(R.id.address_cancel_img);
        cancel_img.setOnClickListener(this);
        address_gp=rootView.findViewById(R.id.address_gp);
        province_rb=rootView.findViewById(R.id.province_rb);
        province_rb.setOnClickListener(this);
        city_rb=rootView.findViewById(R.id.city_rb);
        city_rb.setOnClickListener(this);
        district_rb=rootView.findViewById(R.id.district_rb);
        recyclerView=rootView.findViewById(R.id.recycler_view);
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel_img) {
            dismiss();
        } else if (id == R.id.province_rb) {
            province_rb.setChecked(true);
            province_rb.setText("请选择");
            province = null;
            city = null;
            district = null;
            city_rb.setText("");
            city_rb.setVisibility(View.GONE);
            district_rb.setText("");
            district_rb.setVisibility(View.GONE);
            adapter.setData(mList);
        } else if (id == R.id.city_rb) {
            city_rb.setChecked(true);
            city_rb.setText("请选择");
            city = null;
            district = null;
            district_rb.setText("");
            district_rb.setVisibility(View.GONE);
            adapter.setData(province.getCities());
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