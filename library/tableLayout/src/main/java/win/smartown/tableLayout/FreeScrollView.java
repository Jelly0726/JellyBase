package win.smartown.tableLayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by Smartown on 2017/7/19.
 */
public class FreeScrollView extends HorizontalScrollView {
    /**
     * 绘制滑动条的画笔
     */
    private Paint mPaint;
    private float saveDistance;//记录当前滑动的位置 避免重复画滚动条
    private float scrollBarLeft;//滑动条距离屏幕左边距的距离
    private float scrollBarWidth;//滑动条的宽度
    private float scrollBarHeight;//滑动条的高度
    private float mWidth = 0;//滑动栏的宽度
    private float mScreenWidth;//屏幕的高度
    private float scrollBarStartDis = 0;//滑动条开始时候距离屏幕左边的距离，假设滑动到最右边距离屏幕也为这个距离
    private float xPos;//滑动比例
    private float scrollBarRadius=100;//圆角角度
    private int scrollBarColor;//滚动条颜色

    private boolean isCheck;//是否点击
    private boolean isClick;//允许点击
    private long downTime;//记录按下的时间
    private int lastX;
    private int lastY;
    public FreeScrollView(Context context) {
        super(context);
        init(null);
    }

    public FreeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FreeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FreeScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    private void init(AttributeSet attrs) {
        if (attrs!=null){
            TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.FreeScrollView);
            scrollBarLeft = typedArray.getDimensionPixelSize(R.styleable.FreeScrollView_scrollBarLeft,0);
            scrollBarWidth = typedArray.getDimensionPixelSize(R.styleable.FreeScrollView_scrollBarWidth,50);
            scrollBarHeight = typedArray.getDimensionPixelSize(R.styleable.FreeScrollView_scrollBarHeight,20);
            scrollBarStartDis = typedArray.getDimensionPixelSize(R.styleable.FreeScrollView_scrollBarStartDis,0);
            scrollBarRadius = typedArray.getDimensionPixelSize(R.styleable.FreeScrollView_scrollBarRadius,0);
            isClick = typedArray.getBoolean(R.styleable.FreeScrollView_isClick,false);
            scrollBarColor = typedArray.getColor(R.styleable.FreeScrollView_scrollBarColor,Color.GRAY);
        }
        mPaint = new Paint();
        mPaint.setColor(scrollBarColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mScreenWidth = Util.getScreenWidth(getResources());
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            throw new RuntimeException("FreeScrollView must contain only one child!");
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    /**
     * 画底部滑动条，用onDraw()方法无效
     *
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        //放在super前是后景，相反是前景，前景会覆盖子布局
        super.dispatchDraw(canvas);
        iniDraw(canvas);

    }
    private void iniDraw(Canvas canvas){
        //scroll view 的可滚动水平范围是所有子视图的宽度总合
        mWidth=computeHorizontalScrollRange();
        //滑动条的滑动范围 scrollBarLeft ~ mScreenWitdh-scroolBarWidth-scrollBarStartDis-scrollBarStartDis
        float rangeBar = mScreenWidth - scrollBarWidth - scrollBarStartDis * 2;
        //水平滑动栏的滑动范围
        float rangeScrollView = mWidth - mScreenWidth;
        //滑动比例
        xPos = rangeBar / (rangeScrollView * 1.0f);
        Log.i("dispatchDraw","mScreenWidth="+mScreenWidth);
        Log.i("dispatchDraw","scrollBarWidth="+scrollBarWidth);
        Log.i("dispatchDraw","mWidth="+mWidth);
        Log.i("dispatchDraw","rangeBar="+rangeBar);
        Log.i("dispatchDraw","rangeScrollView="+rangeScrollView);
        Log.i("dispatchDraw","xPos="+xPos);
        Log.i("dispatchDraw","getHeight()="+getHeight());
        Log.i("dispatchDraw","scrollBarLeft="+scrollBarLeft);
        RectF mBrounds = new RectF();
        mBrounds.set(scrollBarLeft, getHeight() - scrollBarHeight, scrollBarLeft + scrollBarWidth, getHeight());
//        canvas.drawRect(scrollBarLeft, getHeight() - scrollBarHeight, scrollBarLeft + scrollBarWidth, getHeight(), mPaint);
        canvas.drawRoundRect(mBrounds, scrollBarRadius, scrollBarRadius, mPaint);
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //当快速抛动，手指离开屏幕时，就会出现l 与 oldl 重复出现的情况，
        //而且都是相邻的两次，原因还不知道，若是哪位朋友知道了可以告诉我
        //这种情况下不要去绘制滑动条
        if (saveDistance == l)
            return;
        else
            saveDistance = l;

        //滑动条的左边缘x坐标应该为HorizontalScrollView滑动的距离乘以滑动条的滑动比例，
        // 加上HorizontalScrollView滑动的距离
        // （因为滑动条也会跟着滑动，所以应该抵消滑动条被带着滑动的距离）
        scrollBarLeft += ((l - oldl) + (l - oldl) * xPos);
        //重新绘制
        invalidate();
    }

    /**
     * 设置子View的宽高，决定自身View的宽高，每次启动都会调用此方法
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isCheck =false;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX=rawX;
                lastY=rawY;
                downTime= System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                isCheck =false;
                //计算手指移动了多少
                int dx=rawX-lastX;
                int dy=rawY-lastY;

                lastX=rawX;
                lastY=rawY;

//               //这里修复一些华为手机无法触发点击事件的问题
                int distance= (int) Math.sqrt(dx*dx+dy*dy);
                if(distance<=0&&isClick){
                    isCheck =true;
                    break;
                }
                break;
            case MotionEvent.ACTION_UP:
                //这里修复一些华为手机无法触发点击事件的问题
                long upTime= System.currentTimeMillis();
                if((upTime-downTime)<100&&isClick){
                    isCheck =true;
                }
                break;
        }
        if (isCheck&&isClick){//点击事件则向下传递
            View view = getChildAt(0);
            if (view instanceof TableLayout) {
                ((TableLayout) view).onClick(event.getX() + getScrollX(), event.getY() + getScrollY());
            }
        }
        //如果是点击则消耗事件，否则正常传递即可。
        return isCheck || super.onTouchEvent(event);
//        return super.onTouchEvent(event);
//        return true;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }
}
