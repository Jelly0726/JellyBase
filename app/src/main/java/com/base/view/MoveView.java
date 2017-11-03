package com.base.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;

import com.base.ToolUtil.UtilTools;


/**
 * Created by Administrator on 2017/5/7.
 */

public class MoveView extends View {
    private final String TAG = MoveView.class.getName();
    private static final int STATE_DEFAULT = 0;                                                   // 默认,无法拖拽
    private static final int STATE_DRAG = 1;                                                      // 拖拽
    private static final int STATE_MOVE = 2;                                                      // 移动
    private static final int STATE_DISMISS = 3;                                                   // 消失

    private int mMoveStatus = 0;
    private int mWidth, mHeight, mBubbleCenterX, mBubbleCenterY, mCircleCenterX, mCircleCenterY;
    private int mState;                                                                             // 气泡状态
    private float mMaxD, mDistance;
    private float mBubbleRadius, mCircleRadius;                                                     // 气泡,拖拽球
    private float sCIRCLE_RADIUS = 50;                                                              // 拖拽球半径(dp)
    private final float sLINE_DISTANCE = 200;                                                       // 最大拖拽距离(dp)
    private Paint mBubblePaint;                                                                     // 气泡画笔
    private Path mBezierPath;                                                                       // 贝塞尔曲线
    private float mControlX, mControlY
            , mCircleStartX, mCircleStartY, mCircleEndX, mCircleEndY
            , mBubbleStartX, mBubbleStartY, mBubbleEndX, mBubbleEndY;                               // 二阶贝赛尔曲线 参数



    Scroller mScroller;

    public MoveView(Context context){
        super(context);
        init(context);
    }
    public MoveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MoveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mScroller = new Scroller(context);
        mHeight = getBackground().getIntrinsicHeight();
        mWidth = getBackground().getIntrinsicWidth();
        mMaxD = UtilTools.dip2px(getContext(), sLINE_DISTANCE);
//        mCircleRadius = UtilTools.dip2px(getContext(), sCIRCLE_RADIUS);
        mBubblePaint = new Paint();                                                                 // 创建画笔
        mBubblePaint.setColor(Color.RED);
        mBubblePaint.setStyle(Paint.Style.STROKE);                                                 // 填充模式 描边
        mBubblePaint.setStrokeWidth(10);
        mBezierPath = new Path();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (mState != STATE_DISMISS){
//                    d = (float) Math.hypot(event.getX()-mBubbleCenterX, event.getY()-mBubbleCenterY);
                    mState = STATE_DRAG;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mState != STATE_DEFAULT){
                    mBubbleCenterX = (int) event.getX();
                    mBubbleCenterY = (int) event.getY();
                    // 计算气泡圆心与粘性小球圆心的距离
                    mDistance = (float) Math.hypot(mBubbleCenterX-mCircleCenterX, mBubbleCenterY-mCircleCenterY);
                    if (mState == STATE_DRAG){      // 如果可拖拽
                        // 间距小于可粘接的最大距离
                        if (mDistance < mMaxD){
                            // 粘接球半径渐渐变小
                            mCircleRadius = (1 - mDistance / mMaxD ) * sCIRCLE_RADIUS;
                        }else {
                            mState = STATE_MOVE;
                        }
                    }
                    invalidate();
                }
                mMoveStatus = 1;
                break;
            case MotionEvent.ACTION_UP:
                // 正在拖拽时松开手指,气泡恢复原来位置并颤动一下
                if (mState == STATE_DRAG){
                    setBubbleRestoreAnim();
                }else if (mState == STATE_MOVE){            // 超过拖拽距离
                    // 如果在移动状态下间距回到两倍半径之内,气泡不取消
                    if (mDistance < 2 * mBubbleRadius){
                        setBubbleRestoreAnim();
                    }else {
                        // 气泡消失
                        setBubbleDismissAnim();
                    }
                }
                if (mMoveStatus == 1){
                    return true;
                }else {
                    break;
                }
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBubbleCenterX = w/2;
        mBubbleCenterY = h/2;
        mCircleCenterX = mBubbleCenterX;
        mCircleCenterY = mBubbleCenterY;
        mBubbleRadius = Math.min(mBubbleCenterX, mBubbleCenterY);
        sCIRCLE_RADIUS = mBubbleRadius/2;
        mCircleRadius = sCIRCLE_RADIUS;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(mWidth, mHeight);
        }else if (widthSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(mWidth, heightSpecSize);
        }else if (heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize, mHeight);
        }
    }

    /***
     * 气泡恢复并颤抖
     */
    private void setBubbleRestoreAnim(){
        ValueAnimator anim = ValueAnimator.ofObject(new PointFEvaluator()
                , new PointF(mBubbleCenterX, mBubbleCenterY), new PointF(mCircleCenterX, mCircleCenterY));
        anim.setDuration(200);
        // 使用OvershootInterpolator差值器达到颤动效果
        anim.setInterpolator(new OvershootInterpolator(5));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF curPoint = (PointF) animation.getAnimatedValue();
                mBubbleCenterX = (int) curPoint.x;
                mBubbleCenterY = (int) curPoint.y;
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束后状态改为默认
                mState = STATE_DEFAULT;
            }
        });
        anim.start();
    }

    /***
     * 气泡消失
     */
    private void setBubbleDismissAnim(){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画拖拽气泡
        canvas.drawCircle(mBubbleCenterX, mBubbleCenterY, mBubbleRadius, mBubblePaint);
        if (mState == STATE_DRAG){
            // 画粘结小球
            canvas.drawCircle(mCircleCenterX, mCircleCenterY, mCircleRadius, mBubblePaint);
            // 计算二阶贝赛尔曲线的起点,终点和控制点
            calculateBezierCoordinate();
            // 画二阶贝塞尔曲线
            mBezierPath.reset();
            mBezierPath.moveTo(mCircleStartX, mCircleStartY);
            mBezierPath.quadTo(mControlX, mControlY, mBubbleEndX, mBubbleEndY);
            mBezierPath.lineTo(mBubbleStartX, mBubbleStartY);
            mBezierPath.quadTo(mControlX, mControlY, mCircleEndX, mCircleEndY);
            mBezierPath.close();
            canvas.drawPath(mBezierPath, mBubblePaint);
        }
        //画消息个数(暂不实现)
    }

    /***
     * 计算二阶贝赛尔曲线的起点,终点,控制点
     */
    private void calculateBezierCoordinate(){
        // 控制点坐标为两圆心连线中点
        mControlX = (mCircleCenterX + mBubbleCenterX) / 2;
        mControlY = (mCircleCenterY + mBubbleCenterY) / 2;
        // 两条二阶贝塞尔曲线的起点和终点
        float sin = (mCircleCenterY - mBubbleCenterY) / mDistance;
        float cos = (mBubbleCenterX - mCircleCenterX) / mDistance;

        mCircleStartX = mCircleCenterX - mCircleRadius * sin;
        mCircleStartY = mCircleCenterY - mCircleRadius * cos;
        mCircleEndX = mCircleCenterX + mCircleRadius * sin;
        mCircleEndY = mCircleCenterY + mCircleRadius * cos;

        mBubbleStartX = mBubbleCenterX + mBubbleRadius * sin;
        mBubbleStartY = mBubbleCenterY + mBubbleRadius * cos;
        mBubbleEndX = mBubbleCenterX - mBubbleRadius * sin;
        mBubbleEndY = mBubbleCenterY - mBubbleRadius * cos;
    }
	/***
     * PointF动画估值器
     */
    public class PointFEvaluator implements TypeEvaluator<PointF>{
        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            float x = startValue.x + fraction * (endValue.x - startValue.x);
            float y = startValue.y + fraction * (endValue.y - startValue.y);
            return new PointF(x,y);
        }
    }
}
