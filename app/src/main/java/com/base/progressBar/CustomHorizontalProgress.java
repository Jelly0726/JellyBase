package com.base.progressBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ProgressBar;

import com.jelly.jellybase.R;


/**
 * Created by user on 2017-8-8.
 *
 * 直接继承ProgressBar可以实现进度额保存
 *
 * 带数字进度
 */

public class CustomHorizontalProgress extends ProgressBar{

    //默认值
    private static final int DEAFUALT_PROGRESS_UNREACH_HEIGHH = 10;//dp
    protected static final int DEAFUALT_PROGRESS_UNREACH_CORLOR = 0xFFD3D6DA;
    protected static final int DEAFUALT_PROGRESS_REACH_HEIGHH = 10;//dp
    protected static final int DEAFUALT_PROGRESS_REACH_CORLOR = 0xFFFC00D1;
    protected static final int DEAFUALT_PROGRESS_TEXT_SIZE = 10;//sp
    protected static final int DEAFUALT_PROGRESS_TEXT_CORLOR = 0xFFD3D6DA;
    protected static final int DEAFUALT_PROGRESS_TEXT_OFFSET = 10;//dp
    protected static final int DEAFUALT_PROGRESS_VIEW_WIDTH = 200;//进度条默认宽度

    protected int HorizontalProgresUnReachColor;//不能用static修饰,不然多个View会共用此属性
    private int HorizontalProgresUnReachHeight;
    protected int HorizontalProgresReachColor;
    private int HorizontalProgresReachHeight;
    protected int HorizontalProgresTextColor;
    protected int HorizontalProgresTextSize;
    protected int HorizontalProgresTextOffset;
    private int mTextGravity = Gravity.CENTER;//文本位置
    private int mProgresGravity = Gravity.TOP;//进度条位置
    /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/
    //此处可根据自己需要修改大小
    private float[] mRadiusArray =null;
    public int getHorizontalProgresUnReachColor() {
        return HorizontalProgresUnReachColor;
    }

    public void setHorizontalProgresUnReachColor(int horizontalProgresUnReachColor) {
        HorizontalProgresUnReachColor = horizontalProgresUnReachColor;
    }

    public int getHorizontalProgresReachColor() {
        return HorizontalProgresReachColor;
    }

    public void setHorizontalProgresReachColor(int horizontalProgresReachColor) {
        HorizontalProgresReachColor = horizontalProgresReachColor;
    }
    public String getLeftText() {
        return leftText;
    }

    public void setLeftText(@NonNull String leftText) {
        this.leftText = leftText;
    }

    public String getMiddleText() {
        return middleText;
    }

    public void setMiddleText(@NonNull String middleText) {
        this.middleText = middleText;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(@NonNull String rightText) {
        this.rightText = rightText;
    }
    public String getFollowText() {
        return followText;
    }

    public void setFollowText(@NonNull String followText) {
        this.followText = followText;
    }

    private String leftText="";//左边文字
    private String middleText="";//中间文字
    private String rightText="";//右边文字
    private String followText="";//跟随文字

    protected Paint mPaint = new Paint();
    public CustomHorizontalProgress(Context context) {
        this(context,null);
    }

    public CustomHorizontalProgress(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CustomHorizontalProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getStyleabletAttr(attrs);
        mPaint.setTextSize(HorizontalProgresTextSize);//设置画笔文字大小,便于后面测量文字宽高
        mPaint.setColor(HorizontalProgresTextColor);
    }


    /**
     * 获取自定义属性
     */
    protected void getStyleabletAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomHorizontalProgresStyle);
        HorizontalProgresUnReachColor = typedArray.getColor(R.styleable.CustomHorizontalProgresStyle_HorizontalProgresUnReachColor,DEAFUALT_PROGRESS_UNREACH_CORLOR);
        HorizontalProgresReachColor = typedArray.getColor(R.styleable.CustomHorizontalProgresStyle_HorizontalProgresReachColor,DEAFUALT_PROGRESS_REACH_CORLOR);
        //将sp、dp统一转换为sp
        HorizontalProgresReachHeight = (int) typedArray.getDimension(R.styleable.CustomHorizontalProgresStyle_HorizontalProgresReachHeight,dp2px(getContext(),DEAFUALT_PROGRESS_REACH_HEIGHH));
        HorizontalProgresUnReachHeight = (int) typedArray.getDimension(R.styleable.CustomHorizontalProgresStyle_HorizontalProgresUnReachHeight,dp2px(getContext(),DEAFUALT_PROGRESS_UNREACH_HEIGHH));
        HorizontalProgresTextColor = typedArray.getColor(R.styleable.CustomHorizontalProgresStyle_HorizontalProgresTextColor,DEAFUALT_PROGRESS_TEXT_CORLOR);
        HorizontalProgresTextSize = (int) typedArray.getDimension(R.styleable.CustomHorizontalProgresStyle_HorizontalProgresTextSize,sp2px(getContext(),DEAFUALT_PROGRESS_TEXT_SIZE));
        HorizontalProgresTextOffset = (int) typedArray.getDimension(R.styleable.CustomHorizontalProgresStyle_HorizontalProgresTextOffset,DEAFUALT_PROGRESS_TEXT_OFFSET);
        float mRadius = typedArray.getDimensionPixelSize(R.styleable.CustomHorizontalProgresStyle_HorizontalProgressRadius, 0);
        if (mRadius>0){
            mRadiusArray = new float[]{
                    mRadius,
                    mRadius,
                    mRadius,
                    mRadius,
                    mRadius,
                    mRadius,
                    mRadius,
                    mRadius};
        }else {
            float mTopLeftRadius = typedArray.getDimensionPixelSize(R.styleable.CustomHorizontalProgresStyle_HorizontalProgressTopLeftRadius,0);
            float mTopRightRadius = typedArray.getDimensionPixelSize(R.styleable.CustomHorizontalProgresStyle_HorizontalProgressTopRightRadius,0);
            float mBottomLeftRadius = typedArray.getDimensionPixelSize(R.styleable.CustomHorizontalProgresStyle_HorizontalProgressBottomLeftRadius,0);
            float mBottomRightRadius = typedArray.getDimensionPixelSize(R.styleable.CustomHorizontalProgresStyle_HorizontalProgressBottomRightRadius,0);
            mRadiusArray = new float[]{
                    mTopLeftRadius,
                    mTopLeftRadius,
                    mTopRightRadius,
                    mTopRightRadius,
                    mBottomLeftRadius,
                    mBottomLeftRadius,
                    mBottomRightRadius,
                    mBottomRightRadius};
        }
        mTextGravity = typedArray.getInt(R.styleable.CustomHorizontalProgresStyle_HorizontalProgressTextGravity, mTextGravity);
        mProgresGravity = typedArray.getInt(R.styleable.CustomHorizontalProgresStyle_HorizontalProgressGravity, mProgresGravity);
        leftText=typedArray.getString(R.styleable.CustomHorizontalProgresStyle_ProgressLeftText);
        if (TextUtils.isEmpty(leftText)){
            leftText="";
        }
        rightText=typedArray.getString(R.styleable.CustomHorizontalProgresStyle_ProgressRightText);
        if (TextUtils.isEmpty(rightText)){
            rightText="";
        }
        middleText=typedArray.getString(R.styleable.CustomHorizontalProgresStyle_ProgressMiddleText);
        if (TextUtils.isEmpty(middleText)){
            middleText="";
        }
        followText=typedArray.getString(R.styleable.CustomHorizontalProgresStyle_ProgressFollowText);
        if (TextUtils.isEmpty(followText)){
            followText="";
        }
        typedArray.recycle();//记得加这句
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);//计算宽高
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width,height);//设置宽高
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();//save、restore 图层的保存和回滚相关的方法 详见 http://blog.csdn.net/tianjian4592/article/details/45234419
        if (mProgresGravity==Gravity.CENTER){
            canvas.translate(0,getHeight()/2);//移动图层到垂直居中位置
        }else if (mProgresGravity==Gravity.TOP){
            canvas.translate(0,HorizontalProgresReachHeight/2);
        }else if (mProgresGravity==Gravity.BOTTOM){
            canvas.translate(0,getHeight()-HorizontalProgresReachHeight/2);
        }
        float radio = getProgress()*1.0f/getMax();
        float realWidth = getWidth() - getPaddingLeft() - getPaddingRight()  ;//实际宽度
        float progressO  = radio * realWidth ;
        float progressX  = radio * (realWidth-0*3) ;
        //绘制未做走完的进度
        if (progressO  <
                getWidth() - getPaddingLeft() - getPaddingRight()){//进度走完了,不再画未走完的
            float[] bitmapRadius={
                    0.0f,
                    0.0f,
                    mRadiusArray[2],
                    mRadiusArray[3],
                    mRadiusArray[4],
                    mRadiusArray[5],
                    0.0f,
                    0.0f,
            };
            mPaint.setColor(HorizontalProgresUnReachColor);
            mPaint.setStrokeWidth(HorizontalProgresUnReachHeight);
            RectF mRectF2 = new RectF(progressX,
                    getPaddingTop()-HorizontalProgresUnReachHeight/2,
                    realWidth,HorizontalProgresUnReachHeight/2);//圆角 int left, int top, int right, int bottom
//            canvas.drawRoundRect(mRectF2,HorizontalProgressRadius,HorizontalProgressRadius,mPaint);//圆角矩形
            Path mBitmapPath = new Path();
            mBitmapPath.addRoundRect(mRectF2, bitmapRadius, Path.Direction.CW);
            canvas.drawPath(mBitmapPath,mPaint);
            //progressO  = radio * (realWidth+HorizontalProgressRadius);
        }
        float[] bitmapRadius={
                mRadiusArray[0],
                mRadiusArray[1],
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                mRadiusArray[6],
                mRadiusArray[7],
        };
        //绘制走完的进度线
        mPaint.setColor(HorizontalProgresReachColor);
        mPaint.setStrokeWidth(HorizontalProgresReachHeight);
        RectF mRectF = new RectF(getPaddingLeft(),getPaddingTop()-HorizontalProgresReachHeight/2,
                (int)progressO,HorizontalProgresReachHeight/2);//圆角 int left, int top, int right, int bottom
//        canvas.drawRoundRect(mRectF,HorizontalProgressRadius,HorizontalProgressRadius,mPaint);//圆角矩形
        Path mBitmapPath = new Path();
        mBitmapPath.addRoundRect(mRectF, bitmapRadius, Path.Direction.CW);
        canvas.drawPath(mBitmapPath,mPaint);
        //绘制文字
        mPaint.setColor(HorizontalProgresTextColor);
        mPaint.setTextSize(HorizontalProgresTextSize);

        int y = (int) -((mPaint.descent() + mPaint.ascent())/2);//文字居中
        canvas.drawText(leftText,getPaddingLeft()+dp2px(getContext(),5)
                ,getPaddingTop() + y,mPaint);

        int middleTextWidth = (int) mPaint.measureText(middleText);//The width of the text
        canvas.drawText(middleText,realWidth/2-middleTextWidth/2
                ,getPaddingTop() + y,mPaint);

        int rightTextWidth = (int) mPaint.measureText(rightText);//The width of the text
        canvas.drawText(rightText,realWidth-rightTextWidth-dp2px(getContext(),5)
                ,getPaddingTop() + y,mPaint);
        int followTextWidth = (int) mPaint.measureText(followText);//The width of the text
        if (mTextGravity == Gravity.CENTER) {
            canvas.drawText(followText, (int) progressO - followTextWidth - dp2px(getContext(), 5)
                    , getPaddingTop() + y, mPaint);
        }else if (mTextGravity == Gravity.TOP) {
            canvas.drawText(followText, (int) progressO
                    ,-(HorizontalProgresTextSize/2), mPaint);
        }else if (mTextGravity == Gravity.BOTTOM) {
            canvas.drawText(followText, (int) progressO
                    ,getPaddingTop() +y+getPaddingBottom()+HorizontalProgresTextSize, mPaint);
        }
        canvas.restore();
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }



    /**
     * Determines the width of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    protected int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = dp2px(getContext(),DEAFUALT_PROGRESS_VIEW_WIDTH);//
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    /**
     * Determines the height of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    protected int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            //此处高度为走完的进度高度和未走完的机大以及文字的高度的最大值
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());//得到字的高度有二种方式,第一种是React,第二种这个
            result = Math.max(textHeight,Math.max(HorizontalProgresReachHeight,HorizontalProgresUnReachHeight)) + getPaddingTop()
                    + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }



}
