package com.base.ToolUtil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by BYPC006 on 2017/4/25.
 */

public class MyToast{
	public static final int LENGTH_LOGN = 1;
	public static final int LENGTH_SHORT = 0;
	private final Context mContext;
	private WindowManager mWindowManager;
	private View mNextView;
	private int mDuration;
	private WindowManager.LayoutParams mParams;
	//维护toast的队列
	private static BlockingQueue<MyToast> mQueue = new LinkedBlockingQueue<>();
	//判断是否在读取队列来显示toast
	protected static AtomicInteger atomicInteger = new AtomicInteger(0);
	private static Handler mHandler = new Handler();

	public MyToast(Context context){
		mContext = context.getApplicationContext();
		mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.windowAnimations = android.R.style.Animation_Toast;
//		mParams.y = dip2px(mContext, 646);
		mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		mParams.gravity = Gravity.CENTER;
	}

	public static MyToast makeText(Context context, CharSequence  text, int duration){
		MyToast myToast = new MyToast(context);
		View view = Toast.makeText(context, text, duration).getView();
		if (view != null){
			TextView textView = (TextView) view.findViewById(android.R.id.message);
			textView.setText(text);
		}
		myToast.mNextView = view;
		myToast.setDuration(duration);
		return myToast;
	}

	public static MyToast makeText(Context context, int resId, int duration) throws Resources.NotFoundException{
		return makeText(context, context.getResources().getText(resId), duration);

	}

	public void show(){
		// 将需要显示的加入到队列
		mQueue.offer(this);
		// 激活队列
		if (atomicInteger.get() == 0){
			atomicInteger.incrementAndGet();
			mHandler.post(mActive);
		}
	}

	/**
	 * dip 与px的转换
	 * @param context
	 * @param dipValue
	 * @return
	 */
	private int dip2px(Context context,float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue*scale + 0.5f);
	}
	public MyToast setDuration(int duration) {
		if (duration<0){
			this.mDuration = 0;
		}
		if (duration == Toast.LENGTH_SHORT){
			this.mDuration = 1000;
		} else if (duration == Toast.LENGTH_LONG){
			this.mDuration = 2000;
		}else {
			this.mDuration = duration;
		}
		return this;
	}
	public MyToast setView(View view) {
		this.mNextView = view;
		return this;
	}

	private void handleShow(){
		if (mNextView != null){
			if (mNextView.getParent() != null){
				mWindowManager.removeView(mNextView);
			}
			mWindowManager.addView(mNextView, mParams);
		}
	}

	private void handleHide(){
		if (mNextView != null){
			if (mNextView.getParent() != null){
				mWindowManager.removeView(mNextView);
				mQueue.poll();
			}
		}
	}
	private static void activeQueue(){
		final MyToast myToast = mQueue.peek();
		if (myToast == null){
			// 非激活
			atomicInteger.decrementAndGet();
		}else {
			mHandler.post(myToast.mShow);
		}
	}

	private final static Runnable mActive = new Runnable() {
		@Override
		public void run() {
			activeQueue();
		}
	};

	private final Runnable mShow = new Runnable() {
		@Override
		public void run() {
			handleShow();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					handleHide();
				}
			}, mDuration);
			new Handler().postDelayed(mActive, mDuration);
		}
	};
}
