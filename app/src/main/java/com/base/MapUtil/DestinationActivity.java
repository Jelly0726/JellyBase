/**
 * ��������
 * Date:2015��4��3������10:52:03
 *
 */
package com.base.MapUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.base.sqldao.PositionDaoUtils;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.config.BaseBroadcast;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.AmapDestinationBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import systemdb.PositionEntity;

/**
 */
public class DestinationActivity extends BaseActivity<AmapDestinationBinding> implements OnClickListener,TextWatcher
		,OnItemClickListener {
	private RecomandAdapter mRecomandAdapter;
	private RouteTask mRouteTask;
	private InputMethodManager imm;//输入法服务
	private int resultCode=-1;
	private List<OfflineMapCity> cityLisi;
	//private Spinner city_sp;
	private List<PositionEntity> posList;
	private String city;

	private int type=0;
	private int from=0;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		type=getIntent().getIntExtra("type",0);
		from=getIntent().getIntExtra("from",0);

		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		getBinding().destinationBack.setOnClickListener(this);

		getBinding().destinationSearch.setOnClickListener(this);
		getBinding().locationTv.setOnClickListener(this);



		getBinding().destinationEdittext.addTextChangedListener(this);
		getBinding().destinationEdittext.setOnClickListener(this);
		//监听键盘搜索按钮
		getBinding().destinationEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH){
					//隐藏输入法
					imm.hideSoftInputFromWindow(getBinding().destinationEdittext.getWindowToken(), 0);
					if (!TextUtils.isEmpty(getBinding().destinationEdittext.getText().toString()) &&
							RouteTask.getInstance(getApplicationContext()).getStartPoint() !=null) {
						PoiSearchTask poiSearchTask=new PoiSearchTask(getApplicationContext(), mRecomandAdapter);
						poiSearchTask.search(getBinding().destinationEdittext.getText().toString(), RouteTask.getInstance(getApplicationContext()).getStartPoint().city);
					}
					return true;
				}
				return false;
			}
		});
		posList= PositionDaoUtils.getInstance(this).getAllList();
		mRecomandAdapter=new RecomandAdapter(getApplicationContext());
		getBinding().recommendList.setAdapter(mRecomandAdapter);
		getBinding().recommendList.setOnItemClickListener(this);

		mRouteTask= RouteTask.getInstance(getApplicationContext());
		city= RouteTask.getInstance(getApplicationContext()).getStartPoint().city;
		if(posList!=null){
			if(posList.size()>0){
				mRecomandAdapter.setPositionEntities(posList);
				mRecomandAdapter.notifyDataSetChanged();
			}
		}

		resultCode=getIntent().getIntExtra("resultCode",-1);
	}
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
								  int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String newText = s.toString().trim();
		if (!TextUtils.isEmpty(newText) &&
				RouteTask.getInstance(getApplicationContext()).getStartPoint() !=null) {
			InputTipTask.getInstance(getApplicationContext(), mRecomandAdapter).searchTips(s.toString(),
					RouteTask.getInstance(getApplicationContext()).getStartPoint().city);
		}

	}

	@Override
	public void onClick(View v) {

		switch(v.getId()){
			case R.id.destination_back:
				finish();
				break;
			case R.id.destination_search:
				//隐藏输入法
				imm.hideSoftInputFromWindow(getBinding().destinationEdittext.getWindowToken(), 0);
				if (!TextUtils.isEmpty(getBinding().destinationEdittext.getText().toString()) &&
						RouteTask.getInstance(getApplicationContext()).getStartPoint() !=null) {
					PoiSearchTask poiSearchTask=new PoiSearchTask(getApplicationContext(), mRecomandAdapter);
					poiSearchTask.search(getBinding().destinationEdittext.getText().toString(), RouteTask.getInstance(getApplicationContext()).getStartPoint().city);
				}
				break;
			case R.id.destination_edittext://
				//隐藏输入法
				//imm.hideSoftInputFromWindow(zhanghu_layout.getWindowToken(), 0);
				//显示输入法
				imm.showSoftInputFromInputMethod(getBinding().destinationEdittext.getWindowToken(),0);
				break;
			case R.id.location_tv:
				Intent ii=new Intent(BaseBroadcast.SEARCH_RECEIVER);
				setResult(resultCode,ii);
				finish();
				break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
							long id) {

		PositionEntity entity = (PositionEntity) mRecomandAdapter.getItem(position);
		if (entity.latitue == 0 && entity.longitude == 0) {
			PoiSearchTask poiSearchTask=new PoiSearchTask(getApplicationContext(), mRecomandAdapter);
			poiSearchTask.search(entity.address, RouteTask.getInstance(getApplicationContext()).getStartPoint().city);

		} else {
			mRouteTask.setEndPoint(entity);
			mRouteTask.search();
			Intent ii=new Intent(BaseBroadcast.SEARCH_RECEIVER);
			ii.putExtra("search",(Serializable)entity);
			//sendBroadcast(ii);
			setResult(resultCode,ii);
			upPosition(entity);
			finish();
		}
	}
	private void upPosition(PositionEntity positionEntity){
		if(posList!=null){
			PositionDaoUtils.getInstance(this).clear();
			for (int i=0;i<posList.size();i++){
				PositionEntity positionEntity1=posList.get(i);
				if(positionEntity.city.equals(positionEntity1.city)
						&&positionEntity.address.equals(positionEntity1.address)){
					posList.remove(i);
				}
			}
			if(posList.size()==10){
				posList.remove(9);
			}
			List<PositionEntity> list=new ArrayList<>();
			list.addAll(posList);
			posList.clear();
			posList.add(0,positionEntity);
			posList.addAll(list);
			PositionDaoUtils.getInstance(this).addTable(posList);
			list.clear();
			list=null;
		}
	}
}
  
