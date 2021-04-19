package com.jelly.baselibrary.imageView;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * Created by BYPC006 on 2017/5/4.
 */

public class ZoomImageView extends ImageView {
	public ZoomImageView(Context context){
		super(context);
		init();
		ImageView imageView = new ImageView(context);

	}

	public ZoomImageView(Context context, @Nullable AttributeSet attrs){
		this(context, attrs, 0);
	}

	public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init(){
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if (visibility == View.VISIBLE){
			this.clearAnimation();
 			ScaleAnimation scaleAnimation0 = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation0.setRepeatMode(Animation.REVERSE);
			scaleAnimation0.setRepeatCount(Animation.INFINITE);
			scaleAnimation0.setDuration(1000);

			this.startAnimation(scaleAnimation0);
		}else if (visibility == View.INVISIBLE || visibility == View.GONE){
			this.clearAnimation();
		}
	}
	/**
	 * 检测是否被遮住显示不全
	 * @return
	 */
	protected boolean isCover() {
		boolean cover = false;
		Rect rect = new Rect();
		cover = getGlobalVisibleRect(rect);
//		if (cover) {
//			if (rect.width() >= getMeasuredWidth() && rect.height() >= getMeasuredHeight()) {
//				return !cover;
//			}
//		}
		return cover;
	}
}
