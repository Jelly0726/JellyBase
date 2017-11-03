/**
 * ��������
 * Date:2015��4��3������10:52:03
 *
 */
package com.base.MapUtil;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.base.config.BaseBroadcast;
import com.jelly.jellybase.R;

import java.io.Serializable;

import cn.jpush.android.api.JPushInterface;

/**
 */
public class DestinationActivity extends Activity implements OnClickListener,TextWatcher
		,OnItemClickListener {

	private ListView mRecommendList;

	private ImageView mBack_Image;

	private TextView mSearchText;

	private EditText mDestinaionText;

	private RecomandAdapter mRecomandAdapter;

	private RouteTask mRouteTask;
	private InputMethodManager imm;//输入法服务
	private int resultCode=-1;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.amap_destination);
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		mRecommendList=(ListView) findViewById(R.id.recommend_list);
		mBack_Image=(ImageView) findViewById(R.id.destination_back);
		mBack_Image.setOnClickListener(this);

		mSearchText=(TextView) findViewById(R.id.destination_search);
		mSearchText.setOnClickListener(this);

		mDestinaionText=(EditText) findViewById(R.id.destination_edittext);
		mDestinaionText.addTextChangedListener(this);
		mDestinaionText.setOnClickListener(this);
		//监听键盘搜索按钮
		mDestinaionText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH){
					//隐藏输入法
					imm.hideSoftInputFromWindow(mDestinaionText.getWindowToken(), 0);
					if (!TextUtils.isEmpty(mDestinaionText.getText().toString()) &&
							RouteTask.getInstance(getApplicationContext()).getStartPoint() !=null) {
						PoiSearchTask poiSearchTask=new PoiSearchTask(getApplicationContext(), mRecomandAdapter);
						poiSearchTask.search(mDestinaionText.getText().toString(),RouteTask.getInstance(getApplicationContext()).getStartPoint().city);
					}
					return true;
				}
				return false;
			}
		});
		mRecomandAdapter=new RecomandAdapter(getApplicationContext());
		mRecommendList.setAdapter(mRecomandAdapter);
		mRecommendList.setOnItemClickListener(this);

		mRouteTask=RouteTask.getInstance(getApplicationContext());

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
				imm.hideSoftInputFromWindow(mDestinaionText.getWindowToken(), 0);
				if (!TextUtils.isEmpty(mDestinaionText.getText().toString()) &&
						RouteTask.getInstance(getApplicationContext()).getStartPoint() !=null) {
					PoiSearchTask poiSearchTask=new PoiSearchTask(getApplicationContext(), mRecomandAdapter);
					poiSearchTask.search(mDestinaionText.getText().toString(),RouteTask.getInstance(getApplicationContext()).getStartPoint().city);
				}
				break;
			case R.id.destination_edittext://
				//隐藏输入法
				//imm.hideSoftInputFromWindow(zhanghu_layout.getWindowToken(), 0);
				//显示输入法
				imm.showSoftInputFromInputMethod(mDestinaionText.getWindowToken(),0);
				break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
							long id) {

		PositionEntity entity = (PositionEntity) mRecomandAdapter.getItem(position);
		if (entity.latitue == 0 && entity.longitude == 0) {
			PoiSearchTask poiSearchTask=new PoiSearchTask(getApplicationContext(), mRecomandAdapter);
			poiSearchTask.search(entity.address,RouteTask.getInstance(getApplicationContext()).getStartPoint().city);

		} else {
			mRouteTask.setEndPoint(entity);
			mRouteTask.search();
			Intent ii=new Intent(BaseBroadcast.SEARCH_RECEIVER);
			ii.putExtra("search",(Serializable)entity);
			//sendBroadcast(ii);
			setResult(resultCode,ii);
			finish();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}
}
  
