package com.king.imageswitcher.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.king.imageswitcher.R;
import com.king.imageswitcher.adapter.AlbumGridViewAdapter;
import com.king.imageswitcher.util.Bimp;
import com.king.imageswitcher.util.ImageItem;
import com.king.imageswitcher.util.PublicWay;

import java.util.ArrayList;

/**
 * 这个是显示一个文件夹里面的所有图片时的界面
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:49:10
 */
public class ShowAllPhoto extends AppCompatActivity {
	private GridView gridView;
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	// 完成按钮
	private Button okButton;
	// 预览按钮
	private Button preview;
	// 返回按钮
	private Button back;
	// 取消按钮
	private Button cancel;
	// 标题
	private TextView headTitle;
	private Intent intent;
	private Context mContext;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

	public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.imageswitcher_camera_show_all_photo);
		PublicWay.activityList.add(this);
		mContext = this;
		back = (Button) findViewById(R.id.showallphoto_back);
		cancel = (Button) findViewById(R.id.showallphoto_cancel);
		preview = (Button) findViewById(R.id.showallphoto_preview);
		okButton = (Button) findViewById(R.id.showallphoto_ok_button);
		headTitle = (TextView) findViewById(R.id.showallphoto_headtitle);
		this.intent = getIntent();
		String folderName = intent.getStringExtra("folderName");
		if (folderName.length() > 8) {
			folderName = folderName.substring(0, 9) + "...";
		}
		headTitle.setText(folderName);
		cancel.setOnClickListener(new CancelListener());
		back.setOnClickListener(new BackListener(intent));
		preview.setOnClickListener(new PreviewListener());
		init();
		initListener();
		isShowOkBt();
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		  
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub  
        	gridImageAdapter.notifyDataSetChanged();
        }  
    };  

	private class PreviewListener implements OnClickListener {
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() > 0) {
				intent.putExtra("position", "2");
				intent.setClass(ShowAllPhoto.this, GalleryActivity.class);
				startActivity(intent);
			}
		}

	}

	private class BackListener implements OnClickListener {// 返回按钮监听
		Intent intent;

		public BackListener(Intent intent) {
			this.intent = intent;
		}

		public void onClick(View v) {
			PublicWay.remove(ShowAllPhoto.this);
			finish();
//			intent.setClass(ShowAllPhoto.this, ImageFile.class);
//			startActivity(intent);
		}

	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			//清空选择的图片
			Bimp.tempSelectBitmap.clear();
			PublicWay.finshAll();
			finish();
//			intent.setClass(mContext, ShareOrderActivity.class);
//			startActivity(intent);
		}
	}

	private void init() {
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);  
		progressBar = (ProgressBar) findViewById(R.id.showallphoto_progressbar);
		progressBar.setVisibility(View.GONE);
		gridView = (GridView) findViewById(R.id.showallphoto_myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this,dataList,
				Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		okButton = (Button) findViewById(R.id.showallphoto_ok_button);
	}

	private void initListener() {

		gridImageAdapter
				.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
					public void onItemClick(final ToggleButton toggleButton,
							int position, boolean isChecked,
							Button button) {
						if (Bimp.tempSelectBitmap.size() >= PublicWay.num&&isChecked) {
							button.setVisibility(View.GONE);
							toggleButton.setChecked(false);
							Toast.makeText(ShowAllPhoto.this, R.string.only_choose_num,
									Toast.LENGTH_LONG)
									.show();
							return;
						}

						if (isChecked) {
							button.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size()
									+ "/"+ PublicWay.num+")");
						} else {
							button.setVisibility(View.GONE);
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
						}
						isShowOkBt();
					}
				});

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				okButton.setClickable(false);
//				if (PublicWay.photoService != null) {
//					PublicWay.selectedDataList.addAll(Bimp.tempSelectBitmap);
//					Bimp.tempSelectBitmap.clear();
//					PublicWay.photoService.onActivityResult(0, -2,
//							intent);
//				}
//				intent.setClass(mContext, ShareOrderActivity.class);
//				startActivity(intent);
				// Intent intent = new Intent();
				// Bundle bundle = new Bundle();
				// bundle.putStringArrayList("selectedDataList",
				// selectedDataList);
				// intent.putExtras(bundle);
				// intent.setClass(ShowAllPhoto.this, UploadPhoto.class);
				// startActivity(intent);
				PublicWay.finshAll();
				finish();

			}
		});

	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
			preview.setPressed(true);
			okButton.setPressed(true);
			preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(ContextCompat.getColor(this,R.color.imageswitcher_text ));
			preview.setTextColor(ContextCompat.getColor(this,R.color.imageswitcher_text));
		} else {
			okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+ PublicWay.num+")");
			preview.setPressed(false);
			preview.setClickable(false);
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
			preview.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			/*intent.setClass(ShowAllPhoto.this, ImageFile.class);
			startActivity(intent);*/
		}

		return false;

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		isShowOkBt();
		super.onRestart();
	}

}
