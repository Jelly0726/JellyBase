package com.king.imageswitcher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.king.imageswitcher.R;
import com.king.imageswitcher.util.Bimp;
import com.king.imageswitcher.util.PublicWay;
import com.king.imageswitcher.zoom.PhotoView;
import com.king.imageswitcher.zoom.ViewPagerFixed;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个是用于进行图片浏览时的界面
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:47:53
 */
public class GalleryActivity extends AppCompatActivity {
	private Intent intent;
    // 返回按钮
    private Button back_bt;
	// 发送按钮
	private Button send_bt;
	//删除按钮
	private Button del_bt;
	//顶部显示预览图片位置的textview
	private TextView positionTextView;
	//获取前一个activity传过来的position
	private int position;
	//当前的位置
	private int location = 0;
	
	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	
	private Context mContext;

	RelativeLayout photo_relativeLayout;

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.imageswitcher_camera_gallery);// 切屏到主界面
		PublicWay.activityList.add(this);
		mContext = this;
		back_bt = (Button) findViewById(R.id.gallery_back);
		send_bt = (Button) findViewById(R.id.send_button);
		del_bt = (Button)findViewById(R.id.gallery_del);
		back_bt.setOnClickListener(new BackListener());
		send_bt.setOnClickListener(new GallerySendListener());
		del_bt.setOnClickListener(new DelListener());
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		position = Integer.parseInt(intent.getStringExtra("position"));
		isShowOkBt();
		// 为发送按钮设置文字
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			initListViews( Bimp.tempSelectBitmap.get(i).getBitmap(getApplicationContext()) );
		}
		
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setPageMargin((int)getResources().getDimensionPixelOffset(R.dimen.ui_10_dip));
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}
	
	private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			location = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageScrollStateChanged(int arg0) {

		}
	};
	
	private void initListViews(Bitmap bm) {
		Log.i("SSSS", "bm="+bm);
		if (listViews == null)
			listViews = new ArrayList<View>();
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(ContextCompat.getColor(this,R.color.imageswitcher_img_bg ));
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}
	
	// 返回按钮添加的监听器
	private class BackListener implements OnClickListener {

		public void onClick(View v) {
			PublicWay.remove(GalleryActivity.this);
			finish();
			intent.setClass(GalleryActivity.this, ImageFile.class);
			startActivity(intent);
		}
	}
	
	// 删除按钮添加的监听器
	private class DelListener implements OnClickListener {

		public void onClick(View v) {
			if (listViews.size() == 1) {
				Bimp.tempSelectBitmap.clear();
				Bimp.max = 0;
				send_bt.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
				Intent intent = new Intent("data.broadcast.action");
                sendBroadcast(intent);
				PublicWay.remove(GalleryActivity.this);
				finish();
			} else {
				Bimp.tempSelectBitmap.remove(location);
				Bimp.max--;
				pager.removeAllViews();
				listViews.remove(location);
				adapter.setListViews(listViews);
				send_bt.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
				adapter.notifyDataSetChanged();
			}
		}
	}

	// 完成按钮的监听
	private class GallerySendListener implements OnClickListener {
		public void onClick(View v) {
			PublicWay.finshAll();
			//finish();
//			intent.setClass(mContext, ShareOrderActivity.class);
//			startActivity(intent);
		}

	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			send_bt.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
			send_bt.setPressed(true);
			send_bt.setClickable(true);
			send_bt.setTextColor(ContextCompat.getColor(this,R.color.imageswitcher_text ));
		} else {
			send_bt.setPressed(false);
			send_bt.setClickable(false);
			send_bt.setTextColor(ContextCompat.getColor(this,R.color.imageswitcher_text_hint));
		}
	}

	/**
	 * 监听返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(position==1){
				PublicWay.remove(GalleryActivity.this);
				this.finish();
				//intent.setClass(GalleryActivity.this, AlbumActivity.class);
//				startActivity(intent);
			}else if(position==2){
				PublicWay.remove(GalleryActivity.this);
				this.finish();
				//intent.setClass(GalleryActivity.this, ShowAllPhoto.class);
				//startActivity(intent);
			}
		}
		return true;
	}
	
	
	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;
		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
