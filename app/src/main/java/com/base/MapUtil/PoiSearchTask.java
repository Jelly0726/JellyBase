
package com.base.MapUtil;

import android.content.Context;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItemV2;
import com.amap.api.services.poisearch.PoiResultV2;
import com.amap.api.services.poisearch.PoiSearchV2;

import java.util.ArrayList;
import java.util.List;

import systemdb.PositionEntity;

/**
 * ClassName:PoiSearchTask <br/>
 * Function: 简单封装了poi搜索的功能，搜索结果配合RecommendAdapter进行使用显示 <br/>
 * Date: 2015年4月7日 上午11:25:07 <br/>
 *
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class PoiSearchTask implements PoiSearchV2.OnPoiSearchListener {

	private Context mContext;

	private RecomandAdapter mRecommandAdapter;

	public PoiSearchTask(Context context, RecomandAdapter recomandAdapter) {
		mContext = context;

		mRecommandAdapter = recomandAdapter;

	}

	public void search(String keyWord,String city) {
		PoiSearchV2.Query query = new PoiSearchV2.Query(keyWord, "", city);
		query.setPageSize(10); 
		query.setPageNum(0);

		PoiSearchV2 poiSearch = null;
		try {
			poiSearch = new PoiSearchV2(mContext, query);
		} catch (AMapException e) {
			throw new RuntimeException(e);
		}
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}
	@Override
	public void onPoiSearched(PoiResultV2 poiResult, int resultCode) {
		//Log.i("WF","poiResult="+poiResult.getPois()+",resultCode="+resultCode);
		if (resultCode == 1000 && poiResult != null) {
			ArrayList<PoiItemV2> pois=poiResult.getPois();
			if(pois==null){
				return;
			}
			List<PositionEntity>entities=new ArrayList<PositionEntity>();
			for(PoiItemV2 poiItem:pois){
				PositionEntity entity=new PositionEntity(poiItem.getLatLonPoint().getLatitude(),
						poiItem.getLatLonPoint().getLongitude(),poiItem.getTitle()
						,poiItem.getCityName());
				entities.add(entity);
			}
			mRecommandAdapter.setPositionEntities(entities);
			mRecommandAdapter.notifyDataSetChanged();
		}
		//TODO 可以根据app自身需求对查询错误情况进行相应的提示或者逻辑处理
	}

	@Override
	public void onPoiItemSearched(PoiItemV2 poiItemV2, int i) {

	}

}
