package com.king.imageswitcher.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.king.imageswitcher.R;
import com.king.imageswitcher.adapter.FolderAdapter;
import com.king.imageswitcher.util.PublicWay;


/**
 * 这个类主要是用来进行显示包含图片的文件夹
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:48:06
 */
public class ImageFile extends AppCompatActivity {

	private FolderAdapter folderAdapter;
	private Button bt_cancel;
	private Context mContext;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.imageswitcher_camera_image_file);
		PublicWay.activityList.add(this);
		mContext = this;
		bt_cancel = (Button) findViewById(R.id.cancel);
		bt_cancel.setOnClickListener(new CancelListener());
		GridView gridView = (GridView) findViewById(R.id.fileGridView);
		TextView textView = (TextView) findViewById(R.id.headerTitle);
		textView.setText(R.string.photo);
		folderAdapter = new FolderAdapter(this);
		gridView.setAdapter(folderAdapter);
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			//清空选择的图片
			//Bimp.tempSelectBitmap.clear();
			//Intent intent = new Intent();
			PublicWay.finshAll();
			//intent.setClass(mContext, ShareOrderActivity.class);
			//startActivity(intent);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			PublicWay.remove(this);
			finish();
			/*Intent intent = new Intent();
			intent.setClass(mContext, ShareOrderActivity.class);
			startActivity(intent);*/
		}

		return true;
	}

}
