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
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by Smartown on 2017/7/19.
 */
public class FreeScrollView extends HorizontalScrollView {
//    private GestureDetector gestureDetector;
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
    private float mRadius=100;//圆角角度

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
        super.dispatchDraw(canvas);
        RectF mBrounds = new RectF();
        mBrounds.set(scrollBarLeft, getHeight() - scrollBarHeight, scrollBarLeft + scrollBarWidth, getHeight());
//        canvas.drawRect(scrollBarLeft, getHeight() - scrollBarHeight, scrollBarLeft + scrollBarWidth, getHeight(), mPaint);
        canvas.drawRoundRect(mBrounds, mRadius, mRadius, mPaint);
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
        //scroll view 的可滚动水平范围是所有子视图的宽度总合
        mWidth=computeHorizontalScrollRange();
        //滑动条的滑动范围 scrollBarLeft ~ mScreenWitdh-scroolBarWidth-scrollBarStartDis-scrollBarStartDis
        float rangeBar = mScreenWidth - scrollBarWidth - scrollBarStartDis * 2;
        //水平滑动栏的滑动范围
        float rangeScrollView = mWidth - mScreenWidth;
        //滑动比例
        xPos = rangeBar / (rangeScrollView * 1.0f);
    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#CCCCCC"));
        mPaint.setStyle(Paint.Style.FILL);
        mScreenWidth = Util.getScreenWidth(getResources());

        if (attrs!=null){
            TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.FreeScrollView);
            scrollBarLeft = typedArray.getDimensionPixelSize(R.styleable.FreeScrollView_scrollBarLeft,0);
            scrollBarWidth = typedArray.getDimensionPixelSize(R.styleable.FreeScrollView_scrollBarWidth,50);
            scrollBarHeight = typedArray.getDimensionPixelSize(R.styleable.FreeScrollView_scrollBarHeight,20);
            scrollBarStartDis = typedArray.getDimensionPixelSize(R.styleable.FreeScrollView_scrollBarStartDis,0);
            mRadius = typedArray.getDimensionPixelSize(R.styleable.FreeScrollView_mRadius,0);
        }
//        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
//            @Override
//            public boolean onDown(MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public void onShowPress(MotionEvent e) {
//
//            }
//            //由于FreeScrollView拦截了TouchEvent，所以要在FreeScrollView处理点击事件，
//            //通过计算坐标来定位点击的是哪个单元格，点击处理顺序：
//            //FreeScrollView.onSingleTapUp() -> TableLayout.onClick()  -> TableLayout.onClick() -> TableColumn.onClick()
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                View view = getChildAt(0);
//                if (view instanceof TableLayout) {
//                    ((TableLayout) view).onClick(e.getX() + getScrollX(), e.getY() + getScrollY());
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                View view = getChildAt(0);
//                int childHeight = view.getHeight();
//                int childWidth = view.getWidth();
//                int toX, toY;
//                if (distanceX > 0) {
//                    if (childWidth > getWidth()) {
//                        if (getScrollX() + getWidth() >= childWidth) {
//                            toX = childWidth - getWidth();
//                        } else {
//                            toX = (int) (getScrollX() + distanceX);
//                        }
//                    } else {
//                        toX = 0;
//                    }
//                } else {
//                    if (getScrollX() + distanceX < 0) {
//                        toX = 0;
//                    } else {
//                        toX = (int) (getScrollX() + distanceX);
//                    }
//                }
//                if (distanceY > 0) {
//                    if (childHeight > getHeight()) {
//                        if (getScrollY() + getHeight() >= childHeight) {
//                            toY = childHeight - getHeight();
//                        } else {
//                            toY = (int) (getScrollY() + distanceY);
//                        }
//                    } else {
//                        toY = 0;
//                    }
//                } else {
//                    if (getScrollY() + distanceY < 0) {
//                        toY = 0;
//                    } else {
//                        toY = (int) (getScrollY() + distanceY);
//                    }
//                }
//                scrollTo(toX, toY);
//                return false;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//
//            }
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                return false;
//            }
//        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
//        gestureDetector.onTouchEvent(event);
//        return true;
    }

}
