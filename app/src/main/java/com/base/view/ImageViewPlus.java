package com.base.view;

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
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.jelly.jellybase.R;


/**
 *  <com.base.view.ImageViewPlus
 android:id="@+id/imgplus"
 android:layout_width="200dp"
 android:layout_height="300dp"
 android:layout_marginBottom="50dp"
 android:layout_centerHorizontal="true"
 android:layout_alignParentBottom="true"
 android:src="@drawable/img_rectangle"
 app:type="round"
 app:borderColor="#FF0080FF"
 app:borderWidth="10dp"
 app:radius="30dp" />
 自定义ImageView类
 * 实现圆形、圆角，椭圆等自定义图片View可加边框。
 */
public class ImageViewPlus extends AppCompatImageView {
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
	private static final int DEFAULT_TYPE = TYPE_NONE;
	private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;
	private static final int DEFAULT_BORDER_WIDTH = 0;
	private static final int DEFAULT_RECT_ROUND_RADIUS = 0;

	private int mType;
	private int mBorderColor;
	private int mBorderWidth;
	private int mRadius;

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
	private float[] rids =null;
	public ImageViewPlus(Context context) {
		this(context, null);
		// TODOAuto-generated constructor stub
	}

	public ImageViewPlus(Context context, AttributeSet attrs) {
		this(context,attrs, 0);
		// TODOAuto-generated constructor stub
	}
	public ImageViewPlus(Context context, AttributeSet attrs, int defStyle){
		super(context,attrs, defStyle);
		//取xml文件中设定的参数
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageViewPlus);
		mType = ta.getInt(R.styleable.ImageViewPlus_ivpType, DEFAULT_TYPE);
		mBorderColor = ta.getColor(R.styleable.ImageViewPlus_ivpBorderColor, DEFAULT_BORDER_COLOR);
		mBorderWidth = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpBorderWidth, dip2px(DEFAULT_BORDER_WIDTH));
		mRadius = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpRadius, dip2px(DEFAULT_RECT_ROUND_RADIUS));
		float mTopLeftRadius = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpTopLeftRadius, dip2px(DEFAULT_RECT_ROUND_RADIUS));
		float mTopRightRadius = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpTopRightRadius, dip2px(DEFAULT_RECT_ROUND_RADIUS));
		float mBottomLeftRadius = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpBottomLeftRadius, dip2px(DEFAULT_RECT_ROUND_RADIUS));
		float mBottomRightRadius = ta.getDimensionPixelSize(R.styleable.ImageViewPlus_ivpBottomRightRadius, dip2px(DEFAULT_RECT_ROUND_RADIUS));
		rids= new float[]{mTopLeftRadius, mTopLeftRadius, mTopRightRadius, mTopRightRadius, mBottomLeftRadius, mBottomLeftRadius, mBottomRightRadius, mBottomRightRadius};
		ta.recycle();
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODOAuto-generated method stub
		super.onSizeChanged(w,h, oldw, oldh);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (null ==drawable) {
			return;
		}
		Bitmap rawBitmap = getBitmap(drawable);

		if (rawBitmap != null && mType != TYPE_NONE){
			int viewWidth = getWidth();
			int viewHeight = getHeight();
			int viewMinSize = Math.min(viewWidth, viewHeight);
			float dstWidth = mType == TYPE_CIRCLE ? viewMinSize : viewWidth;
			float dstHeight = mType == TYPE_CIRCLE ? viewMinSize : viewHeight;
			float halfBorderWidth = mBorderWidth / 2.0f;
			float doubleBorderWidth = mBorderWidth * 2;

			if (mShader == null || !rawBitmap.equals(mRawBitmap)){
				mRawBitmap = rawBitmap;
				mShader = new BitmapShader(mRawBitmap, TileMode.CLAMP, TileMode.CLAMP);
			}
			if (mShader != null) {
				mMatrix.setScale((dstWidth - doubleBorderWidth) / rawBitmap.getWidth(), (dstHeight - doubleBorderWidth) / rawBitmap.getHeight());
				mShader.setLocalMatrix(mMatrix);
			}

			mPaintBitmap.setShader(mShader);
			mPaintBorder.setStyle(Paint.Style.STROKE);
			mPaintBorder.setStrokeWidth(mBorderWidth);
			mPaintBorder.setColor(mBorderWidth > 0 ? mBorderColor : Color.TRANSPARENT);

			if (mType == TYPE_CIRCLE){
				float radius = viewMinSize / 2.0f;
				canvas.drawCircle(radius, radius, radius - halfBorderWidth, mPaintBorder);
				canvas.translate(mBorderWidth, mBorderWidth);
				canvas.drawCircle(radius - mBorderWidth, radius - mBorderWidth, radius - mBorderWidth, mPaintBitmap);
			} else if (mType == TYPE_ROUNDED){

				if (mRadius==0){
					float[] borderRadius={
							rids[0] - halfBorderWidth > 0.0f ? rids[0] - halfBorderWidth : 0.0f,
							rids[1] - halfBorderWidth > 0.0f ? rids[1] - halfBorderWidth : 0.0f,
							rids[2] - halfBorderWidth > 0.0f ? rids[2] - halfBorderWidth : 0.0f,
							rids[3] - halfBorderWidth > 0.0f ? rids[3] - halfBorderWidth : 0.0f,
							rids[4] - halfBorderWidth > 0.0f ? rids[4] - halfBorderWidth : 0.0f,
							rids[5] - halfBorderWidth > 0.0f ? rids[5] - halfBorderWidth : 0.0f,
							rids[6] - halfBorderWidth > 0.0f ? rids[6] - halfBorderWidth : 0.0f,
							rids[7] - halfBorderWidth > 0.0f ? rids[7] - halfBorderWidth : 0.0f,
					};
					mRectBorder.set(halfBorderWidth, halfBorderWidth, dstWidth - halfBorderWidth, dstHeight - halfBorderWidth);
					Path mBorderPath = new Path();
					mBorderPath.addRoundRect(mRectBorder, borderRadius,  Path.Direction.CW);
					canvas.drawPath(mBorderPath,mPaintBorder);
					canvas.translate(mBorderWidth, mBorderWidth);


					float[] bitmapRadius={
							rids[0] - mBorderWidth > 0.0f ? rids[0] - mBorderWidth : 0.0f,
							rids[1] - mBorderWidth > 0.0f ? rids[1] - mBorderWidth : 0.0f,
							rids[2] - mBorderWidth > 0.0f ? rids[2] - mBorderWidth : 0.0f,
							rids[3] - mBorderWidth > 0.0f ? rids[3] - mBorderWidth : 0.0f,
							rids[4] - mBorderWidth > 0.0f ? rids[4] - mBorderWidth : 0.0f,
							rids[5] - mBorderWidth > 0.0f ? rids[5] - mBorderWidth : 0.0f,
							rids[6] - mBorderWidth > 0.0f ? rids[6] - mBorderWidth : 0.0f,
							rids[7] - mBorderWidth > 0.0f ? rids[7] - mBorderWidth : 0.0f,
					};
					mRectBitmap.set(0.0f, 0.0f, dstWidth - doubleBorderWidth, dstHeight - doubleBorderWidth);
					Path mBitmapPath = new Path();
					mBitmapPath.addRoundRect(mRectBitmap, bitmapRadius, Path.Direction.CW);
					canvas.drawPath(mBitmapPath,mPaintBitmap);
				}else {
					mRectBorder.set(halfBorderWidth, halfBorderWidth, dstWidth - halfBorderWidth, dstHeight - halfBorderWidth);
					mRectBitmap.set(0.0f, 0.0f, dstWidth - doubleBorderWidth, dstHeight - doubleBorderWidth);
					float borderRadius = mRadius - halfBorderWidth > 0.0f ? mRadius - halfBorderWidth : 0.0f;
					float bitmapRadius = mRadius - mBorderWidth > 0.0f ? mRadius - mBorderWidth : 0.0f;
					canvas.drawRoundRect(mRectBorder, borderRadius, borderRadius, mPaintBorder);
					canvas.translate(mBorderWidth, mBorderWidth);
					canvas.drawRoundRect(mRectBitmap, bitmapRadius, bitmapRadius, mPaintBitmap);
				}
			} else if(mType == TYPE_OVAL){
				mRectBorder.set(halfBorderWidth, halfBorderWidth, dstWidth - halfBorderWidth, dstHeight - halfBorderWidth);
				mRectBitmap.set(0.0f, 0.0f, dstWidth - doubleBorderWidth, dstHeight - doubleBorderWidth);
				canvas.drawOval(mRectBorder, mPaintBorder);
				canvas.translate(mBorderWidth, mBorderWidth);
				canvas.drawOval(mRectBitmap, mPaintBitmap);
			}
		} else {
			super.onDraw(canvas);
		}
	}

	private int dip2px(int dipVal)
	{
		float scale = getResources().getDisplayMetrics().density;
		return (int)(dipVal * scale + 0.5f);
	}

	private Bitmap getBitmap(Drawable drawable){
		if (mIsUseImageColor || drawable instanceof ColorDrawable){
			Rect rect = drawable.getBounds();
			int width = rect.right - rect.left;
			int height = rect.bottom - rect.top;
			int color = mIsUseImageColor ? mImageColor : ((ColorDrawable)drawable).getColor();
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
			return bitmap;
		} else if (drawable instanceof BitmapDrawable){
			return ((BitmapDrawable)drawable).getBitmap();
		} else {
			int w =drawable.getIntrinsicWidth();
			int h =drawable.getIntrinsicHeight();
			Bitmap bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, w, h);
			drawable.draw(canvas);
			return bitmap;
		}
	}

	/**
	 * 获取类型
	 * @return TYPE_CIRCLE(圆形) TYPE_ROUNDED(圆角矩形)  TYPE_OVAL(椭圆)
	 */
	public int getType(){
		return this.mType;
	}
	/**
	 * 设置类型(自动重绘)
	 * @param type TYPE_CIRCLE(圆形) TYPE_ROUNDED(圆角矩形) TYPE_OVAL(椭圆)
	 * @see #setType(int type, boolean fUpdateView)
	 */
	public void setType(int type){
		setType(type, true);
	}
	/**
	 * 设置类型
	 * @param type TYPE_CIRCLE(圆形) TYPE_ROUNDED(圆角矩形) TYPE_OVAL(椭圆)
	 * @param fUpdateView 是否自动重绘
	 */
	public void setType(int type, boolean fUpdateView){
		if (type != this.mType
				&& (type == TYPE_CIRCLE || type == TYPE_ROUNDED || type == TYPE_OVAL)){
			this.mType = type;
			if (fUpdateView){
				invalidate();
			}
		}
	}


	/**
	 * 获取边缘宽度
	 * @return 边缘宽度(像素)
	 */
	public int getBorderWidth(){
		return this.mBorderWidth;
	}
	/**
	 * 设置边缘宽度(自动重绘)
	 * @param width 边缘宽度(像素)
	 * @see #setBorderWidth(int width, boolean fUpdateView)
	 */
	public void setBorderWidth(int width){
		setBorderWidth(width, true);
	}
	/**
	 * 设置边缘宽度
	 * @param width 边缘宽度(像素)
	 * @param fUpdateView 是否自动重绘
	 */
	public void setBorderWidth(int width, boolean fUpdateView){
		if (width != this.mBorderWidth
				&& width >= 0
				&& width <= Math.min(getWidth(), getHeight()) / 2){
			this.mBorderWidth = width;
			if (fUpdateView){
				invalidate();
			}
		}
	}

	/**
	 * 获取边缘颜色
	 * @return 边缘颜色(color-int)
	 */
	public int getBorderColor(){
		return this.mBorderColor;
	}
	/**
	 * 设置边缘颜色(自动重绘)
	 * @param colorid 边缘颜色(color-int)
	 * @see #setBorderColor(int colorid, boolean fUpdateView)
	 */
	public void setBorderColor(int colorid){
		setBorderColor(colorid, true);
	}
	/**
	 * 设置边缘颜色
	 * @param colorid 边缘颜色(color-int)
	 * @param fUpdateView 是否自动重绘
	 */
	public void setBorderColor(int colorid, boolean fUpdateView){
		if (colorid != this.mBorderColor){
			this.mBorderColor = colorid;
			if (fUpdateView){
				invalidate();
			}
		}
	}

	/**
	 * 获取圆角矩形弧度半径
	 * @return 弧度半径(像素)
	 */
	public int getRectRoundRadius(){
		return this.mRadius;
	}
	/**
	 * 设置圆角矩形弧度半径(自动重绘)
	 * @param radius 弧度半径(像素)
	 * @see #setRectRoundRadius(int radius, boolean fUpdateView)
	 */
	public void setRectRoundRadius(int radius){
		setRectRoundRadius(radius, true);
	}
	/**
	 * 设置圆角矩形弧度半径
	 * @param radius 弧度半径(像素)
	 * @param fUpdateView 是否自动重绘
	 */
	public void setRectRoundRadius(int radius, boolean fUpdateView){
		if (this.mType == TYPE_ROUNDED
				&& radius != this.mRadius
				&& radius >= 0
				&& radius <= Math.min(getWidth(), getHeight()) / 2){
			this.mRadius = radius;
			if (fUpdateView){
				invalidate();
			}
		}
	}

	/**
	 * 设置图像为纯色(自动重绘)
	 * @param colorid color-int
	 */
	public void setImageColor(int colorid){
		setImageColor(colorid, true);
	}
	/**
	 * 设置图像为纯色
	 * @param colorid color-int
	 * @param fUpdateView 是否自动重绘
	 */
	public void setImageColor(int colorid, boolean fUpdateView){
		if (!mIsUseImageColor || colorid != mImageColor){
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
	public void resetImageColor(){
		mIsUseImageColor = false;
		mImageColor = Color.TRANSPARENT;
	}
}
