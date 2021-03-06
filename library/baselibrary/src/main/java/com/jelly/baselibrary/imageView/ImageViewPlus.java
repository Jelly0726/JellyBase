package com.jelly.baselibrary.imageView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.jelly.baselibrary.R;


/**
 * <com.base.imageview.ImageViewPlus
 * android:id="@+id/imgplus"
 * android:layout_width="200dp"
 * android:layout_height="300dp"
 * android:layout_marginBottom="50dp"
 * android:layout_centerHorizontal="true"
 * android:layout_alignParentBottom="true"
 * android:src="@drawable/img_rectangle"
 * app:type="round"
 * app:borderColor="#FF0080FF"
 * app:borderWidth="10dp"
 * app:radius="30dp" />
 * 自定义ImageView类
 * 实现圆形、圆角，椭圆等自定义图片View可加边框。
 */
public class ImageViewPlus extends AppCompatImageView {

	private static final String STATE_INSTANCE = "state_instance";
	private static final String STATE_TYPE = "state_type";
	private static final String STATE_BORDER_WIDTH = "state_border_width";
	private static final String STATE_BORDER_COLOR = "state_border_color";
	private static final String STATE_ARC_HEIGHT = "state_arc_height";
	private static final String STATE_ARC_MODE = "state_arc_mode";
	private static final String STATE_ARC_LOCATION = "state_arc_location";
	private static final String STATE_RADIUS = "state_radius";
	/**
	 * android.widget.ImageView
	 */
	public static final int TYPE_NONE = 0;
	/**
	 * 圆形
	 */
	public static final int TYPE_CIRCLE = 1;
	/**
	 * 圆角矩形
	 */
	public static final int TYPE_ROUNDED = 2;
	/**
	 * 椭圆形
	 */
	public static final int TYPE_OVAL = 3;
	/**
	 * 弧形
	 */
	public static final int TYPE_ARC = 4;
	private static final int DEFAULT_TYPE = TYPE_NONE;
	private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;
	private static final int DEFAULT_BORDER_WIDTH = 0;
	private static final int DEFAULT_RECT_ROUND_RADIUS = 0;

	private int mType;
	private int mBorderColor;
	private int mBorderWidth;
	//弧形的高占控件宽高的百分比
	private float mArcHeight = 0;
	//弧形的模式 外凸、内凹
	private int mArcMode = 0;
	//弧形的位置
	private int mArcLocation = 0;
	private int mLeft = 0x001;
	private int mTop = 0x002;
	private int mRight = 0x008;
	private int mBottom = 0x004;

	private Paint mPaintBitmap = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);

	private RectF mRectBorder = new RectF();
	private RectF mRectBitmap = new RectF();

	private Bitmap mRawBitmap;
	private BitmapShader mShader;
	private Matrix mMatrix = new Matrix();

	private boolean mIsUseImageColor = false;
	private int mImageColor = Color.TRANSPARENT;
	/*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/
	//此处可根据自己需要修改大小
	private float[] mRadiusArray = null;
	public ImageViewPlus(Context context) {
		this(context, null);
		// TODOAuto-generated constructor stub
	}

	public ImageViewPlus(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODOAuto-generated constructor stub
	}

	public ImageViewPlus(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//取xml文件中设定的参数
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageViewPlus);
		mType = ta.getInt(R.styleable.ImageViewPlus_ivpType, DEFAULT_TYPE);
		mBorderColor = ta.getColor(R.styleable.ImageViewPlus_ivpBorderColor, DEFAULT_BORDER_COLOR);
		mBorderWidth = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpBorderWidth, dip2px(DEFAULT_BORDER_WIDTH));
		mArcHeight = ta.getFloat(R.styleable.ImageViewPlus_ivpArcHeight, 1);
		mArcMode = ta.getInt(R.styleable.ImageViewPlus_ivpArcMode, 0);
		mArcLocation = ta.getInt(R.styleable.ImageViewPlus_ivpArcLocation, 0);
		float mRadius = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpRadius, dip2px(DEFAULT_RECT_ROUND_RADIUS));
		if (mRadius > 0) {
			mRadiusArray = new float[]{
					mRadius,
					mRadius,
					mRadius,
					mRadius,
					mRadius,
					mRadius,
					mRadius,
					mRadius};
		} else {
			float mTopLeftRadius = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpTopLeftRadius, dip2px(DEFAULT_RECT_ROUND_RADIUS));
			float mTopRightRadius = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpTopRightRadius, dip2px(DEFAULT_RECT_ROUND_RADIUS));
			float mBottomLeftRadius = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpBottomLeftRadius, dip2px(DEFAULT_RECT_ROUND_RADIUS));
			float mBottomRightRadius = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpBottomRightRadius, dip2px(DEFAULT_RECT_ROUND_RADIUS));
			mRadiusArray = new float[]{
					mTopLeftRadius,
					mTopLeftRadius,
					mTopRightRadius,
					mTopRightRadius,
					mBottomRightRadius,
					mBottomRightRadius,
					mBottomLeftRadius,
					mBottomLeftRadius};
		}
		ta.recycle();
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
		bundle.putInt(STATE_TYPE, mType);
		bundle.putFloatArray(STATE_RADIUS, mRadiusArray);
		bundle.putInt(STATE_BORDER_COLOR, mBorderColor);
		bundle.putInt(STATE_BORDER_WIDTH, mBorderWidth);
		bundle.putFloat(STATE_ARC_HEIGHT, mArcHeight);
		bundle.putInt(STATE_ARC_LOCATION, mArcLocation);
		bundle.putInt(STATE_ARC_MODE, mArcMode);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			super.onRestoreInstanceState(((Bundle) state)
					.getParcelable(STATE_INSTANCE));
			mType = bundle.getInt(STATE_TYPE);
			mBorderColor = bundle.getInt(STATE_BORDER_COLOR);
			mBorderWidth = bundle.getInt(STATE_BORDER_WIDTH);
			mArcHeight = bundle.getFloat(STATE_ARC_HEIGHT);
			mArcMode = bundle.getInt(STATE_ARC_MODE);
			mArcLocation = bundle.getInt(STATE_ARC_LOCATION);
		} else {
			super.onRestoreInstanceState(state);
		}

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODOAuto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (null == drawable) {
			return;
		}
		Bitmap rawBitmap = getBitmap(drawable);
		if (rawBitmap != null && mType != TYPE_NONE) {
			int viewWidth = getWidth();
			int viewHeight = getHeight();
			//控件的最小边大小
			int viewMinSize = Math.min(viewWidth, viewHeight);
			//绘制区域的宽和高 当为圆形时去控件的最小边
			float dstWidth = mType == TYPE_CIRCLE ? viewMinSize : viewWidth;
			float dstHeight = mType == TYPE_CIRCLE ? viewMinSize : viewHeight;
			float halfBorderWidth = mBorderWidth / 2.0f;
			float doubleBorderWidth = mBorderWidth * 2;

			if (mType == TYPE_ARC && !rawBitmap.equals(mRawBitmap)) {//图片是否缩放过的
				if (viewHeight < viewWidth) {//
					rawBitmap = zoomImg(rawBitmap, 0, viewHeight);
				} else if (viewHeight >= viewWidth) {
					rawBitmap = zoomImg(rawBitmap, viewWidth, 0);
				}
				if (rawBitmap.getWidth() < viewWidth) {
					int bWidth = viewWidth - rawBitmap.getWidth();
					rawBitmap = addFrame(rawBitmap, bWidth, 0, Color.WHITE);
				}
				if (viewHeight > rawBitmap.getHeight()) {
					int bWidth = viewHeight - rawBitmap.getHeight();
					rawBitmap = addFrame(rawBitmap, 0, bWidth, Color.WHITE);
				}
			}
			if (mShader == null || !rawBitmap.equals(mRawBitmap)) {
				mRawBitmap = rawBitmap;
				mShader = new BitmapShader(mRawBitmap, TileMode.CLAMP, TileMode.CLAMP);
			}
			float scaleX = 1.0f;
			float scaleY = 1.0f;
//            float dx = 0; //x方向平移，正数显示图片右边区域内容,负数显示图片左边区域内容
//            float dy = 0; //y方向平移，正数显示图片底边区域内容,负数显示图片上边区域内容
			if (mType == TYPE_CIRCLE) {
				// 拿到bitmap宽或高的小值
				int bSize = Math.min(rawBitmap.getWidth(), rawBitmap.getHeight());
				scaleX = viewMinSize * 1.0f / bSize;
				scaleY = viewMinSize * 1.0f / bSize;
			} else {
				// // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
				scaleY = scaleX = Math.max((dstWidth - doubleBorderWidth) * 1.0f / rawBitmap.getWidth(),
						(dstHeight - doubleBorderWidth) * 1.0f / rawBitmap.getHeight());
			}
			mMatrix.setScale(scaleX, scaleY);
//            mMatrix.postTranslate(Math.round(dx), Math.round(dy));
			if (mShader != null) {
				mShader.setLocalMatrix(mMatrix);
			}
			mPaintBitmap.setShader(mShader);
			mPaintBorder.setStyle(Paint.Style.STROKE);
			mPaintBorder.setStrokeWidth(mBorderWidth);
			mPaintBorder.setColor(mBorderWidth > 0 ? mBorderColor : Color.TRANSPARENT);

			if (mType == TYPE_CIRCLE) {
				float radius = viewMinSize / 2.0f;
				canvas.drawCircle(radius, radius, radius - halfBorderWidth, mPaintBorder);
				canvas.translate(mBorderWidth, mBorderWidth);
				canvas.drawCircle(radius - mBorderWidth, radius - mBorderWidth, radius - mBorderWidth, mPaintBitmap);
			} else if (mType == TYPE_ROUNDED) {
//				if (mRadius==0){
				float[] borderRadius = {
						mRadiusArray[0] - halfBorderWidth > 0.0f ? mRadiusArray[0] - halfBorderWidth : 0.0f,
						mRadiusArray[1] - halfBorderWidth > 0.0f ? mRadiusArray[1] - halfBorderWidth : 0.0f,
						mRadiusArray[2] - halfBorderWidth > 0.0f ? mRadiusArray[2] - halfBorderWidth : 0.0f,
						mRadiusArray[3] - halfBorderWidth > 0.0f ? mRadiusArray[3] - halfBorderWidth : 0.0f,
						mRadiusArray[4] - halfBorderWidth > 0.0f ? mRadiusArray[4] - halfBorderWidth : 0.0f,
						mRadiusArray[5] - halfBorderWidth > 0.0f ? mRadiusArray[5] - halfBorderWidth : 0.0f,
						mRadiusArray[6] - halfBorderWidth > 0.0f ? mRadiusArray[6] - halfBorderWidth : 0.0f,
						mRadiusArray[7] - halfBorderWidth > 0.0f ? mRadiusArray[7] - halfBorderWidth : 0.0f,
				};
				mRectBorder.set(halfBorderWidth, halfBorderWidth, dstWidth - halfBorderWidth, dstHeight - halfBorderWidth);
				Path mBorderPath = new Path();
				mBorderPath.addRoundRect(mRectBorder, borderRadius, Path.Direction.CW);
				canvas.drawPath(mBorderPath, mPaintBorder);
				canvas.translate(mBorderWidth, mBorderWidth);

				float[] bitmapRadius = {
						mRadiusArray[0] - mBorderWidth > 0.0f ? mRadiusArray[0] - mBorderWidth : 0.0f,
						mRadiusArray[1] - mBorderWidth > 0.0f ? mRadiusArray[1] - mBorderWidth : 0.0f,
						mRadiusArray[2] - mBorderWidth > 0.0f ? mRadiusArray[2] - mBorderWidth : 0.0f,
						mRadiusArray[3] - mBorderWidth > 0.0f ? mRadiusArray[3] - mBorderWidth : 0.0f,
						mRadiusArray[4] - mBorderWidth > 0.0f ? mRadiusArray[4] - mBorderWidth : 0.0f,
						mRadiusArray[5] - mBorderWidth > 0.0f ? mRadiusArray[5] - mBorderWidth : 0.0f,
						mRadiusArray[6] - mBorderWidth > 0.0f ? mRadiusArray[6] - mBorderWidth : 0.0f,
						mRadiusArray[7] - mBorderWidth > 0.0f ? mRadiusArray[7] - mBorderWidth : 0.0f,
				};
				mRectBitmap.set(0.0f, 0.0f, dstWidth - doubleBorderWidth, dstHeight - doubleBorderWidth);
				Path mBitmapPath = new Path();
				mBitmapPath.addRoundRect(mRectBitmap, bitmapRadius, Path.Direction.CW);
				canvas.drawPath(mBitmapPath, mPaintBitmap);
//				}else {
//					mRectBorder.set(halfBorderWidth, halfBorderWidth, dstWidth - halfBorderWidth, dstHeight - halfBorderWidth);
//					mRectBitmap.set(0.0f, 0.0f, dstWidth - doubleBorderWidth, dstHeight - doubleBorderWidth);
//					float borderRadius = mRadius - halfBorderWidth > 0.0f ? mRadius - halfBorderWidth : 0.0f;
//					float bitmapRadius = mRadius - mBorderWidth > 0.0f ? mRadius - mBorderWidth : 0.0f;
//					canvas.drawRoundRect(mRectBorder, borderRadius, borderRadius, mPaintBorder);
//					canvas.translate(mBorderWidth, mBorderWidth);
//					canvas.drawRoundRect(mRectBitmap, bitmapRadius, bitmapRadius, mPaintBitmap);
//				}
			} else if (mType == TYPE_OVAL) {
				mRectBorder.set(halfBorderWidth, halfBorderWidth, dstWidth - halfBorderWidth,
						dstHeight - halfBorderWidth);
				mRectBitmap.set(0.0f, 0.0f, dstWidth - doubleBorderWidth,
						dstHeight - doubleBorderWidth);
				canvas.drawOval(mRectBorder, mPaintBorder);
				canvas.translate(mBorderWidth, mBorderWidth);
				canvas.drawOval(mRectBitmap, mPaintBitmap);
			} else if (mType == TYPE_ARC) {//画弧
				if ((mArcLocation & mLeft) == mLeft) {//左边弧形
					//画外边框
					Path mBorderPath = new Path();
					if (mArcMode == 1) {//内凹
						mBorderPath.moveTo(halfBorderWidth, halfBorderWidth);
						//画左边
						mBorderPath.cubicTo(halfBorderWidth, halfBorderWidth
								, (getWidth() * mArcHeight) - halfBorderWidth, getHeight() / 2
								, halfBorderWidth, getHeight() - halfBorderWidth);
						//画下面直线
						mBorderPath.lineTo(getWidth() - halfBorderWidth, getHeight() - halfBorderWidth);
						//画右边
						mBorderPath.lineTo(getWidth() - halfBorderWidth, halfBorderWidth);
					} else {
						mBorderPath.moveTo((getWidth() * mArcHeight) - halfBorderWidth, halfBorderWidth);
						//画左边
						mBorderPath.cubicTo((getWidth() * mArcHeight) - halfBorderWidth, halfBorderWidth
								, -(getWidth() * mArcHeight) - doubleBorderWidth * 2, getHeight() / 2
								, (getWidth() * mArcHeight) - halfBorderWidth, getHeight() - halfBorderWidth);
						//画下面直线
						mBorderPath.lineTo(getWidth() - halfBorderWidth, getHeight() - halfBorderWidth);
						//画右边
						mBorderPath.lineTo(getWidth() - halfBorderWidth, halfBorderWidth);
					}
					mBorderPath.close();
					canvas.drawPath(mBorderPath, mPaintBorder);
					canvas.translate(mBorderWidth, mBorderWidth);
					//画图片
					Path mBitmapPath = new Path();
					if (mArcMode == 1) {//内凹
						mBitmapPath.moveTo(0, 0);
						//画左边
						mBitmapPath.cubicTo(halfBorderWidth, -halfBorderWidth
								, (getWidth() * mArcHeight) - doubleBorderWidth, getHeight() / 2
								, halfBorderWidth, getHeight() - doubleBorderWidth);
						//画下面
						mBitmapPath.lineTo(getWidth() - doubleBorderWidth, getHeight() - doubleBorderWidth);
						//画右边
						mBitmapPath.lineTo(getWidth() - doubleBorderWidth, 0);
					} else {
						mBitmapPath.moveTo((getWidth() * mArcHeight) - halfBorderWidth * 3, 0);
						//画左边
						mBitmapPath.cubicTo((getWidth() * mArcHeight) - halfBorderWidth * 3, halfBorderWidth
								, -(getWidth() * mArcHeight) - doubleBorderWidth * 2 + halfBorderWidth, getHeight() / 2 - halfBorderWidth * 3
								, (getWidth() * mArcHeight) - doubleBorderWidth, getHeight() - doubleBorderWidth);
						//画下面直线
						mBitmapPath.lineTo(getWidth() - doubleBorderWidth, getHeight() - doubleBorderWidth);
						//画右边
						mBitmapPath.lineTo(getWidth() - doubleBorderWidth, 0);
					}
					mBitmapPath.close();
					canvas.drawPath(mBitmapPath, mPaintBitmap);
				}
				if ((mArcLocation & mTop) == mTop) {//上边弧形
					//画外边框
					Path mBorderPath = new Path();
					if (mArcMode == 1) {//内凹
						mBorderPath.moveTo(halfBorderWidth, halfBorderWidth);
						mBorderPath.lineTo(halfBorderWidth, getHeight() - halfBorderWidth);
						mBorderPath.lineTo(getWidth() - halfBorderWidth, getHeight() - halfBorderWidth);
						mBorderPath.lineTo(getWidth() - halfBorderWidth, -halfBorderWidth);
						//画上边
						mBorderPath.cubicTo(getWidth() + halfBorderWidth * 3, doubleBorderWidth * 2
								, getWidth() / 2, 2 * (getHeight() * mArcHeight)
								, halfBorderWidth, halfBorderWidth);
					} else {
						mBorderPath.moveTo(halfBorderWidth, (getHeight() * mArcHeight));
						mBorderPath.lineTo(halfBorderWidth, getHeight() - halfBorderWidth);
						mBorderPath.lineTo(getWidth() - halfBorderWidth, getHeight() - halfBorderWidth);
						mBorderPath.lineTo(getWidth() - halfBorderWidth, (getHeight() * mArcHeight));
						//画上边
						mBorderPath.cubicTo(getWidth() + halfBorderWidth * 3, (getHeight() * mArcHeight)
								, getWidth() / 2 + halfBorderWidth, -(getHeight() * mArcHeight) - mBorderWidth
								, halfBorderWidth, (getHeight() * mArcHeight));
					}
					mBorderPath.close();
//				canvas.clipPath(mBorderPath);
					canvas.drawPath(mBorderPath, mPaintBorder);
					canvas.translate(mBorderWidth, mBorderWidth);
					//画图片
					Path mBitmapPath = new Path();
					if (mArcMode == 1) {//内凹
						mBitmapPath.moveTo(0, -halfBorderWidth);
						mBitmapPath.lineTo(0, getHeight() - doubleBorderWidth);
						mBitmapPath.lineTo(getWidth() - doubleBorderWidth, getHeight() - doubleBorderWidth);
						mBitmapPath.lineTo(getWidth() - doubleBorderWidth, -doubleBorderWidth);
						//画上边
						mBitmapPath.cubicTo(getWidth() + doubleBorderWidth * 2, doubleBorderWidth * 2 - halfBorderWidth
								, getWidth() / 2, 2 * (getHeight() * mArcHeight) - halfBorderWidth
								, 0, 0);
					} else {
						mBitmapPath.moveTo(0, (getHeight() * mArcHeight));
						mBitmapPath.lineTo(0, getHeight() - doubleBorderWidth);
						mBitmapPath.lineTo(getWidth() - doubleBorderWidth, getHeight() - doubleBorderWidth);
						mBitmapPath.lineTo(getWidth() - doubleBorderWidth, (getHeight() * mArcHeight));
						//画上边
						mBitmapPath.cubicTo(getWidth() + doubleBorderWidth, (getHeight() * mArcHeight) - doubleBorderWidth - halfBorderWidth
								, getWidth() / 2 - doubleBorderWidth * 4, -(getHeight() * mArcHeight) - halfBorderWidth * 2
								, 0, (getHeight() * mArcHeight) - halfBorderWidth);
					}
					mBitmapPath.close();
					canvas.drawPath(mBitmapPath, mPaintBitmap);
				}
				if ((mArcLocation & mRight) == mRight) {//右边弧形
					//画外边框
					Path mBorderPath = new Path();
					mBorderPath.moveTo(halfBorderWidth, halfBorderWidth);
					if (mArcMode == 1) {//内凹
						mBorderPath.lineTo(getWidth() - halfBorderWidth, halfBorderWidth);
						//画右边
						mBorderPath.quadTo(getWidth() - (getWidth() * mArcHeight) - halfBorderWidth, getHeight() / 2
								, getWidth() - halfBorderWidth, getHeight() - halfBorderWidth);
						mBorderPath.lineTo(halfBorderWidth, getHeight() - halfBorderWidth);
					} else {
						mBorderPath.lineTo(getWidth() - getWidth() * mArcHeight - halfBorderWidth, halfBorderWidth);
						//画右边
						mBorderPath.quadTo(getWidth() + getWidth() * mArcHeight - halfBorderWidth, getHeight() / 2,
								getWidth() - getWidth() * mArcHeight - halfBorderWidth, getHeight() - halfBorderWidth);
						mBorderPath.lineTo(halfBorderWidth, getHeight() - halfBorderWidth);
					}
					mBorderPath.close();
//				canvas.clipPath(mBorderPath);
					canvas.drawPath(mBorderPath, mPaintBorder);
					canvas.translate(mBorderWidth, mBorderWidth);
					//画图片
					Path mBitmapPath = new Path();
					mBitmapPath.moveTo(0, 0);
					if (mArcMode == 1) {//内凹
						mBitmapPath.lineTo(getWidth() - doubleBorderWidth, 0);
						//画右边
						mBitmapPath.quadTo(getWidth() - (getWidth() * mArcHeight) - halfBorderWidth * 3, getHeight() / 2,
								getWidth() - doubleBorderWidth, getHeight() - doubleBorderWidth);
						mBitmapPath.lineTo(0, getHeight() - doubleBorderWidth);
					} else {
						mBitmapPath.lineTo(getWidth() - getWidth() * mArcHeight - halfBorderWidth * 3, 0);
						//画右边
						mBitmapPath.quadTo(getWidth() + getWidth() * mArcHeight - doubleBorderWidth, getHeight() / 2,
								getWidth() - getWidth() * mArcHeight - halfBorderWidth * 3, getHeight() - doubleBorderWidth);
						mBitmapPath.lineTo(0, getHeight() - doubleBorderWidth);
					}
					mBitmapPath.close();
					canvas.drawPath(mBitmapPath, mPaintBitmap);
				}
				if ((mArcLocation & mBottom) == mBottom) {//下边弧形
					//画外边框
					Path mBorderPath = new Path();
					mBorderPath.moveTo(halfBorderWidth, halfBorderWidth);
					if (mArcMode == 1) {//内凹
						mBorderPath.lineTo(halfBorderWidth, getHeight() - halfBorderWidth);
						//画下边
						mBorderPath.quadTo(getWidth() / 2, getHeight() - (getHeight() * mArcHeight) - halfBorderWidth
								, getWidth() - halfBorderWidth, getHeight() - halfBorderWidth);
						mBorderPath.lineTo(getWidth() - halfBorderWidth, halfBorderWidth);
					} else {
						mBorderPath.lineTo(halfBorderWidth, getHeight() - getHeight() * mArcHeight - halfBorderWidth);
						//画下边
						mBorderPath.quadTo(getWidth() / 2, getHeight() + getHeight() * mArcHeight - halfBorderWidth,
								getWidth() - halfBorderWidth, getHeight() - getHeight() * mArcHeight - halfBorderWidth);
						mBorderPath.lineTo(getWidth() - halfBorderWidth, halfBorderWidth);
					}
					mBorderPath.close();
//				canvas.clipPath(mBorderPath);
					canvas.drawPath(mBorderPath, mPaintBorder);
					canvas.translate(mBorderWidth, mBorderWidth);
					//画图片
					Path mBitmapPath = new Path();
					mBitmapPath.moveTo(0, 0);
					if (mArcMode == 1) {//内凹
						mBitmapPath.lineTo(0, getHeight() - doubleBorderWidth);
						//画下边
						mBitmapPath.quadTo(getWidth() / 2, getHeight() - (getHeight() * mArcHeight) - halfBorderWidth * 3
								, getWidth() - doubleBorderWidth, getHeight() - doubleBorderWidth);
						mBitmapPath.lineTo(getWidth() - doubleBorderWidth, 0);
					} else {
						mBitmapPath.lineTo(0, getHeight() - getHeight() * mArcHeight - halfBorderWidth * 3);
						//画下边
						mBitmapPath.quadTo(getWidth() / 2, getHeight() + getHeight() * mArcHeight - doubleBorderWidth,
								getWidth() - doubleBorderWidth, getHeight() - getHeight() * mArcHeight - halfBorderWidth * 3);
						mBitmapPath.lineTo(getWidth() - doubleBorderWidth, 0);
					}
					mBitmapPath.close();
					canvas.drawPath(mBitmapPath, mPaintBitmap);
				}
			}
		} else {
			super.onDraw(canvas);
		}
	}

	private int dip2px(int dipVal) {
		float scale = getResources().getDisplayMetrics().density;
		return (int) (dipVal * scale + 0.5f);
	}

	private Bitmap getBitmap(Drawable drawable) {
		if (mIsUseImageColor || drawable instanceof ColorDrawable) {
			Rect rect = drawable.getBounds();
			int width = rect.right - rect.left;
			int height = rect.bottom - rect.top;
			int color = mIsUseImageColor ? mImageColor : ((ColorDrawable) drawable).getColor();
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
			return bitmap;
		} else if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else {
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();
			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, w, h);
			drawable.draw(canvas);
			return bitmap;
		}
	}

	/**
	 * 添加颜色边框
	 *
	 * @param src          源图片
	 * @param borderWidth  左右边框宽度
	 * @param borderHeight 上下边框宽度
	 * @param color        边框的颜色值
	 * @return 带颜色边框图
	 */
	private Bitmap addFrame(Bitmap src, int borderWidth, int borderHeight, int color) {
		int newWidth = src.getWidth() + borderWidth;
		int newHeight = src.getHeight() + borderHeight;
		Bitmap out = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(out);
		Rect rec = new Rect();
		rec.bottom = newHeight--;
		rec.right = newWidth--;
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(borderWidth);
		canvas.drawRect(rec, paint);
		canvas.drawBitmap(src, borderWidth / 2, borderHeight / 2, null);
		canvas.save();
		canvas.restore();
		if (!src.isRecycled()) src.recycle();
		return out;
	}

	/**
	 * 设置bitmap的宽高
	 *
	 * @param bm
	 * @param newWidth  为0则以高为准
	 * @param newHeight 为0则以宽为准
	 * @return
	 */
	private Bitmap zoomImg(Bitmap bm, float newWidth, float newHeight) {
		if (newWidth <= 0 && newHeight <= 0) return bm;
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = 0f;
		float scaleHeight = 0f;
		/**
		 * height/width=newHeight/newWidth
		 * newHeight=newWidth*(height/width);
		 * newWidth=newHeight/(height/width);
		 */
		if (newWidth > 0 && newHeight > 0) {//判断以哪边为准
			scaleWidth = newWidth / (float) width;
			scaleHeight = newHeight / (float) height;
		} else if (newWidth > 0) {//以宽为准 高通过原宽高比计算
			newHeight = (newWidth * ((float) height / (float) width));
			scaleWidth = newWidth / (float) width;
			scaleHeight = newHeight / (float) height;
		} else if (newHeight > 0) {//以高为准 宽通过原宽高比计算
			newWidth = (newHeight / ((float) height / (float) width));
			scaleWidth = newWidth / (float) width;
			scaleHeight = newHeight / (float) height;
		}
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		return newbm;
	}

	/**
	 * 获取类型
	 *
	 * @return TYPE_CIRCLE(圆形) TYPE_ROUNDED(圆角矩形)  TYPE_OVAL(椭圆)
	 */
	public int getType() {
		return this.mType;
	}

	/**
	 * 设置类型(自动重绘)
	 *
	 * @param type TYPE_CIRCLE(圆形) TYPE_ROUNDED(圆角矩形) TYPE_OVAL(椭圆)
	 * @see #setType(int type, boolean fUpdateView)
	 */
	public void setType(int type) {
		setType(type, true);
	}

	/**
	 * 设置类型
	 *
	 * @param type        TYPE_CIRCLE(圆形) TYPE_ROUNDED(圆角矩形) TYPE_OVAL(椭圆)
	 * @param fUpdateView 是否自动重绘
	 */
	public void setType(int type, boolean fUpdateView) {
		if (type != this.mType
				&& (type == TYPE_CIRCLE || type == TYPE_ROUNDED || type == TYPE_OVAL)) {
			this.mType = type;
			if (fUpdateView) {
				invalidate();
			}
		}
	}


	/**
	 * 获取边缘宽度
	 *
	 * @return 边缘宽度(像素)
	 */
	public int getBorderWidth() {
		return this.mBorderWidth;
	}

	/**
	 * 设置边缘宽度(自动重绘)
	 *
	 * @param width 边缘宽度(像素)
	 * @see #setBorderWidth(int width, boolean fUpdateView)
	 */
	public void setBorderWidth(int width) {
		setBorderWidth(width, true);
	}

	/**
	 * 设置边缘宽度
	 *
	 * @param width       边缘宽度(像素)
	 * @param fUpdateView 是否自动重绘
	 */
	public void setBorderWidth(int width, boolean fUpdateView) {
		if (width != this.mBorderWidth
				&& width >= 0
				&& width <= Math.min(getWidth(), getHeight()) / 2) {
			this.mBorderWidth = width;
			if (fUpdateView) {
				invalidate();
			}
		}
	}

	/**
	 * 获取边缘颜色
	 *
	 * @return 边缘颜色(color - int)
	 */
	public int getBorderColor() {
		return this.mBorderColor;
	}

	/**
	 * 设置边缘颜色(自动重绘)
	 *
	 * @param colorid 边缘颜色(color-int)
	 * @see #setBorderColor(int colorid, boolean fUpdateView)
	 */
	public void setBorderColor(int colorid) {
		setBorderColor(colorid, true);
	}

	/**
	 * 设置边缘颜色
	 *
	 * @param colorid     边缘颜色(color-int)
	 * @param fUpdateView 是否自动重绘
	 */
	public void setBorderColor(int colorid, boolean fUpdateView) {
		if (colorid != this.mBorderColor) {
			this.mBorderColor = colorid;
			if (fUpdateView) {
				invalidate();
			}
		}
	}

	/**
	 * 获取圆角矩形弧度半径
	 *
	 * @return 弧度半径(像素)
	 */
	public float[] getRectRoundRadius() {
		return this.mRadiusArray;
	}

	/**
	 * 设置圆角矩形弧度半径(自动重绘)
	 *
	 * @param radius 弧度半径(像素)
	 * @see #setRectRoundRadius(int radius, boolean fUpdateView)
	 */
	public void setRectRoundRadius(int radius) {
		setRectRoundRadius(radius, true);
	}

	/**
	 * 设置圆角矩形弧度半径
	 *
	 * @param radius      弧度半径(像素)
	 * @param fUpdateView 是否自动重绘
	 */
	public void setRectRoundRadius(int radius, boolean fUpdateView) {
		if (this.mType == TYPE_ROUNDED
				&& radius != this.mRadiusArray[0]
				&& radius >= 0
				&& radius <= Math.min(getWidth(), getHeight()) / 2) {
			mRadiusArray = new float[]{
					radius,
					radius,
					radius,
					radius,
					radius,
					radius,
					radius,
					radius};
			if (fUpdateView) {
				invalidate();
			}
		}
	}

	/**
	 * 设置圆角矩形弧度半径(自动重绘)
	 *
	 * @param radius 弧度半径(像素)
	 */
	public void setRectRoundRadius(float[] radius) {
		setRectRoundRadius(radius, true);
	}

	/**
	 * 设置圆角矩形弧度半径
	 *
	 * @param radius      弧度半径(像素)
	 * @param fUpdateView 是否自动重绘
	 */
	public void setRectRoundRadius(@NonNull float[] radius, boolean fUpdateView) {
		if (this.mType == TYPE_ROUNDED
				&& radius != this.mRadiusArray
				&& radius.length >= 0
				&& radius[0] <= Math.min(getWidth(), getHeight()) / 2
				&& radius[2] <= Math.min(getWidth(), getHeight()) / 2
				&& radius[4] <= Math.min(getWidth(), getHeight()) / 2
				&& radius[6] <= Math.min(getWidth(), getHeight()) / 2) {
			this.mRadiusArray = radius;
			if (fUpdateView) {
				invalidate();
			}
		}
	}

	/**
	 * 设置图像为纯色(自动重绘)
	 *
	 * @param colorid color-int
	 */
	public void setImageColor(int colorid) {
		setImageColor(colorid, true);
	}

	/**
	 * 设置图像为纯色
	 *
	 * @param colorid     color-int
	 * @param fUpdateView 是否自动重绘
	 */
	public void setImageColor(int colorid, boolean fUpdateView) {
		if (!mIsUseImageColor || colorid != mImageColor) {
			this.mImageColor = colorid;
			mIsUseImageColor = true;
			if (fUpdateView) {
				invalidate();
			}
		}
	}

	/**
	 * 重置图像为非纯色模式
	 */
	public void resetImageColor() {
		mIsUseImageColor = false;
		mImageColor = Color.TRANSPARENT;
	}
}
