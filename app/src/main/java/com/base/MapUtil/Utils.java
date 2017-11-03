
package com.base.MapUtil;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;
/**
 * ClassName:Utils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:   TODO ADD REASON. <br/>
 * Date:     2015年4月7日 下午3:43:05 <br/>
 * @author   yiyi.qi
 * @version
 * @since    JDK 1.6
 * @see
 */
public class Utils {
	public static final String DAY_NIGHT_MODE="daynightmode";
	public static final String DEVIATION="deviationrecalculation";
	public static final String JAM="jamrecalculation";
	public static final String TRAFFIC="trafficbroadcast";
	public static final String CAMERA="camerabroadcast";
	public static final String SCREEN="screenon";
	public static final String THEME="theme";
	public static final String ISEMULATOR="isemulator";


	public static final String ACTIVITYINDEX="activityindex";

	public static final int SIMPLEHUDNAVIE=0;
	public static final int EMULATORNAVI=1;
	public static final int SIMPLEGPSNAVI=2;
	public static final int SIMPLEROUTENAVI=3;

	public static final int LOWCost=1;//费用最低
	public static final int MAXSPEED=0;//速度最快
	public static final int MINDISTANCE=2;//路程最短
	public static final int NOTHIGHSPEED=3;//不走快速路
	public static final int HIGHSPEED=4;//参考交通信息最快
	public static final boolean DAY_MODE=false;
	public static final boolean NIGHT_MODE=true;
	public static final boolean YES_MODE=true;
	public static final boolean NO_MODE=false;
	public static final boolean OPEN_MODE=true;
	public static final boolean CLOSE_MODE=false;
	private static ArrayList<Marker> markers=new ArrayList<Marker>();

	/**
	 * 添加模拟测试的车的点
	 */
	public static void addEmulateData(AMap amap,ArrayList list,Context context,int type){
//		if(markers.size()==0){
//			for(int i=0;i<list.size();i++) {
//				Driver dre = (Driver) list.get(i);
//				//if (markers.size() > 0) {
//				if (false) {
//					for (Marker markerss : markers) {
//						Driver dr= (Driver)markerss.getObject();
//						if(dre.getDriverid()!=dr.getDriverid()) {
//							BitmapDescriptor bitmapDescriptor = drawBitmap(dre, context);
//							MarkerOptions markerOptions = new MarkerOptions();
//							markerOptions.setFlat(false);//设置标志是否贴地显示
//							markerOptions.anchor(0.5f, 1.0f);//放在地图上的基准点
//							markerOptions.icon(bitmapDescriptor);
//							markerOptions.draggable(false);//允许用户可以自由移动标记
//							markerOptions.perspective(true);//标记有近大远小效果
//							markerOptions.position(new LatLng(dre.getLocationx()
//									, dre.getLocationy()));//标记位置的经纬度值
//							if (amap!=null){
//								Marker marker = amap.addMarker(markerOptions);
//								marker.setRotateAngle(0);//方法设置标记的旋转角度
//								marker.setObject(dre);
//								if(type==1) {
//									dropInto(amap, marker);
//									//growInto(marker);
//								}
//								markers.add(marker);
//							}
//						}
//					}
//				}else{
//					BitmapDescriptor bitmapDescriptor = drawBitmap(dre, context);
//					MarkerOptions markerOptions = new MarkerOptions();
//					markerOptions.setFlat(false);//设置标志是否贴地显示
//					markerOptions.anchor(0.5f, 1.0f);//放在地图上的基准点
//					markerOptions.icon(bitmapDescriptor);
//					markerOptions.draggable(false);//允许用户可以自由移动标记
//					markerOptions.perspective(true);//标记有近大远小效果
//					markerOptions.position(new LatLng(dre.getLocationx()
//							, dre.getLocationy()));//标记位置的经纬度值
//					if (amap!=null){
//						Marker marker = amap.addMarker(markerOptions);
//						marker.setRotateAngle(0);//方法设置标记的旋转角度
//						marker.setObject(dre);
//						if(type==1) {
//							dropInto(amap, marker);
//							//growInto(marker);
//						}
//						markers.add(marker);
//					}
//				}
//				// bitmapDescriptor.recycle();
//			}
//		}

	}
	public static void remove(List list){
		//if(list.size()==0) {
		if(true) {
			if (markers.size() > 0) {
				for (Marker marker : markers) {
					marker.remove();
				}
			}
			markers.clear();
		}else {
//			for(int i=0;i<list.size();i++) {
//				Driver dre = (Driver) list.get(i);
//				if (markers.size() > 0) {
//					for (Marker marker : markers) {
//						Driver dr= (Driver)marker.getObject();
//						if(dre.getDriverid()!=dr.getDriverid()) {
//							marker.remove();
//							markers.remove(marker);
//						}
//					}
//				}
//			}
		}
	}
//	/**
//	 * 添加模拟测试的车的点
//	 */
//	public static Marker addEmulate(AMap amap,Driver dre,Context context){
//		Marker marker=null;
//		BitmapDescriptor bitmapDescriptor = drawBitmap(dre, context);
//		MarkerOptions markerOptions = new MarkerOptions();
//		markerOptions.setFlat(false);//设置标志是否贴地显示
//		markerOptions.anchor(0.5f, 1.0f);//放在地图上的基准点
//		markerOptions.icon(bitmapDescriptor);
//		markerOptions.draggable(false);//允许用户可以自由移动标记
//		markerOptions.perspective(true);//标记有近大远小效果
//		markerOptions.position(new LatLng(dre.getLocationx()
//				, dre.getLocationy()));//标记位置的经纬度值
//		if (amap!=null){
//			marker = amap.addMarker(markerOptions);
//			marker.setRotateAngle(0);//方法设置标记的旋转角度
//			marker.setObject(dre);
//		}
//		// bitmapDescriptor.recycle();
//		return marker;
//	}
	/**
	 * 自定义位图
	 * @return
	 */
	@SuppressLint("InflateParams")
//	public static BitmapDescriptor drawBitmap(Driver dre, Context context) {
//		BitmapDescriptor drawable = null;
//		try{
//			Bitmap bmp = Bitmap.createBitmap(140,70, Bitmap.Config.ARGB_8888);
//			Canvas canvas = new Canvas(bmp);
//			LayoutInflater inflater = (LayoutInflater) MyApplication.getMyApp()
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			View layout = inflater.inflate(R.layout.custom_text_views, null);
//			LinearLayout driver_location=(LinearLayout) layout.findViewById(R.id.driver_location);
//			//上班
//			driver_location.setBackgroundResource(R.mipmap.mark_hg);
//			CircleImageView imageView= (CircleImageView) layout.findViewById(R.id.driver_hend);
//			if(!TextUtils.isEmpty(dre.getPhoto())) {
//				Picasso.with(context).load(dre.getPhoto())
//						.placeholder(R.mipmap.ic_launcher)
//						.error(R.mipmap.ic_launcher).into(imageView);
//			}
//			CircleImageView gradeView = (CircleImageView) layout.findViewById(R.id.driver_grade);
//			String dg="D";
//			if(!TextUtils.isEmpty(dre.getDrivergrade())){
//				dg=dre.getDrivergrade();
//			}
//			switch (dg){
//				case "A":
//					gradeView.setImageResource(R.drawable.a_icon);
//					break;
//				case "B":
//					gradeView.setImageResource(R.drawable.b_icon);
//					break;
//				case "C":
//					gradeView.setImageResource(R.drawable.c_icon);
//					break;
//				case "D":
//					gradeView.setImageResource(R.drawable.d_icon);
//					break;
//				default:
//					break;
//			}
//			TextView titleView = (TextView) layout.findViewById(R.id.driver_location_name);
//			titleView.setVisibility(View.VISIBLE);
//			titleView.setText(dre.getName());
//			TextView driver_id=(TextView) layout.findViewById(R.id.driver_id);
//			driver_id.setVisibility(View.GONE);
//			driver_id.setText(dre.getDriverid()+"");
//			RatingBar driver_location_ratingBar=(RatingBar) layout.findViewById(R.id.driver_location_ratingBar);
//			driver_location_ratingBar.setRating(dre.getDriverlevel());
//			layout.setDrawingCacheEnabled(true);
//			layout.measure(View.MeasureSpec.makeMeasureSpec(canvas.getWidth(),
//					View.MeasureSpec.EXACTLY), View.MeasureSpec
//					.makeMeasureSpec(canvas.getHeight(),
//							View.MeasureSpec.EXACTLY));
//			layout.layout(0, 0, layout.getMeasuredWidth(),
//					layout.getMeasuredHeight());
//			Paint paint = new Paint();
//			canvas.drawBitmap(layout.getDrawingCache(), 0, 0, paint);
//			drawable = BitmapDescriptorFactory.fromView(layout);
//
////			bmp=getViewBitmap(layout,x,y);
////			drawable = BitmapDescriptorFactory.fromBitmap(bmp);
//			// 先判断是否已经回收
//			if(bmp != null && !bmp.isRecycled()){
//				// 回收并且置为null
//				bmp.recycle();
//				bmp = null;
//			}
//			return drawable;
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return drawable;
//
//	}
	/**
	 * 把View绘制到Bitmap上
	 * @param comBitmap 需要绘制的View
	 * @param width 该View的宽度
	 * @param height 该View的高度
	 * @return 返回Bitmap对象
	 * add by csj 13-11-6
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static Bitmap getViewBitmap(View comBitmap, int width, int height) {
		Bitmap bitmap = null;
		if (comBitmap != null) {
			comBitmap.clearFocus();
			comBitmap.setPressed(false);

			boolean willNotCache = comBitmap.willNotCacheDrawing();
			comBitmap.setWillNotCacheDrawing(false);

			// Reset the drawing cache background color to fully transparent
			// for the duration of this operation
			int color = comBitmap.getDrawingCacheBackgroundColor();
			comBitmap.setDrawingCacheBackgroundColor(0);
			float alpha = comBitmap.getAlpha();
			comBitmap.setAlpha(1.0f);

			if (color != 0) {
				comBitmap.destroyDrawingCache();
			}

			int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
			int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
			comBitmap.measure(widthSpec, heightSpec);
			comBitmap.layout(0, 0, width, height);

			comBitmap.buildDrawingCache();
			Bitmap cacheBitmap = comBitmap.getDrawingCache();
			if (cacheBitmap == null) {
				Log.e("view.ProcessImageToBlur", "failed getViewBitmap(" + comBitmap + ")",
						new RuntimeException());
				return null;
			}
			bitmap = Bitmap.createBitmap(cacheBitmap);
			// Restore the view
			comBitmap.setAlpha(alpha);
			comBitmap.destroyDrawingCache();
			comBitmap.setWillNotCacheDrawing(willNotCache);
			comBitmap.setDrawingCacheBackgroundColor(color);
		}
		return bitmap;
	}
	private static Bitmap getViewBitmap(View v) {
		v.clearFocus();
		v.setPressed(false);

		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);
		// Reset the drawing cache background color to fully transparent
		// for the duration of this operation
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);

		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			Log.e("Folder", "failed getViewBitmap(" + v + ")", new RuntimeException());
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);

		return bitmap;
	}

	/**
	 * 返回简要地址(去除省市区)
	 * @return
	 */
	public static String getAddress(String str,String province,String city){
		String add;
		add=str.replace(province,"");
		add=add.replace(city,"");
		return add;
	}
	/**
	 * marker点击时跳动一下
	 */
	public static void jumpPoint(AMap amap,final Marker marker) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = amap.getProjection();
		final LatLng markerLatlng = marker.getPosition();
		Point startPoint = proj.toScreenLocation(markerLatlng);
		startPoint.offset(0, -100);
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 1500;

		final Interpolator interpolator = new BounceInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t *markerLatlng.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t *markerLatlng.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});
	}

	/**
	 * 重上掉下
	 * marker 必须有设置图标，否则无效果
	 *
	 * @param marker
	 */
	public static void dropInto(AMap amap,final Marker marker) {

		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		final LatLng markerLatlng = marker.getPosition();
		Projection proj = amap.getProjection();
		Point markerPoint = proj.toScreenLocation(markerLatlng);
		Point startPoint = new Point(markerPoint.x, 0);// 从marker的屏幕上方下落
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 600;// 动画总时长

		final Interpolator interpolator = new AccelerateInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * markerLatlng.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * markerLatlng.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});
	}

	private static int count = 1;
	private static Bitmap lastMarkerBitMap = null;

	/**
	 * 从地上生长效果，实现思路
	 * 在较短的时间内，修改marker的图标大小，从而实现动画<br>
	 * 1.保存原始的图片；
	 * 2.在原始图片上缩放得到新的图片，并设置给marker；
	 * 3.回收上一张缩放后的图片资源；
	 * 4.重复2，3步骤到时间结束；
	 * 5.回收上一张缩放后的图片资源，设置marker的图标为最原始的图片；
	 *
	 * 其中时间变化由AccelerateInterpolator控制
	 * @param marker
	 */
	public static void growInto(final Marker marker) {
		marker.setVisible(false);
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		final long duration = 250;// 动画总时长
		final Bitmap bitMap = marker.getIcons().get(0).getBitmap();// BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
		final int width = bitMap.getWidth();
		final int height = bitMap.getHeight();

		final Interpolator interpolator = new AccelerateInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);

				if (t > 1) {
					t = 1;
				}

				// 计算缩放比例
				int scaleWidth = (int) (t * width);
				int scaleHeight = (int) (t * height);
				if (scaleWidth > 0 && scaleHeight > 0) {

					// 使用最原始的图片进行大小计算
					marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap
							.createScaledBitmap(bitMap, scaleWidth,
									scaleHeight, true)));
					marker.setVisible(true);

					// 因为替换了新的图片，所以把旧的图片销毁掉，注意在设置新的图片之后再销毁
					if (lastMarkerBitMap != null
							&& !lastMarkerBitMap.isRecycled()) {
						lastMarkerBitMap.recycle();
					}

					//第一次得到的缩放图片，在第二次回收，最后一次的缩放图片，在动画结束时回收
					ArrayList<BitmapDescriptor> list = marker.getIcons();
					if (list != null && list.size() > 0) {
						// 保存旧的图片
						lastMarkerBitMap = marker.getIcons().get(0).getBitmap();
					}

				}

				if (t < 1.0 && count < 10) {
					handler.postDelayed(this, 16);
				} else {
					// 动画结束回收缩放图片，并还原最原始的图片
					if (lastMarkerBitMap != null
							&& !lastMarkerBitMap.isRecycled()) {
						lastMarkerBitMap.recycle();
					}
					marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitMap));
					marker.setVisible(true);
				}
			}
		});
	}
}
  
