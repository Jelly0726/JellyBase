package com.base.view;

/**
 * Created by Administrator on 2017/4/7.
 * 自定义悬浮按钮
 */

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import com.base.applicationUtil.AppUtils;

public class FloatingDraftButton extends FloatingActionButton {

    private int screenWidth;
    private int screenHeight;
    private int screenWidthHalf;
    private int statusHeight;
    //是否停靠屏幕边缘停
    private boolean isScreenEdge=true;

    public FloatingDraftButton(Context context) {
        super(context);
        init();
    }

    public FloatingDraftButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingDraftButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        screenWidth= AppUtils.getScreenWidth(getContext());
        screenWidthHalf=screenWidth/2;
        screenHeight=AppUtils.getScreenHeight(getContext());
        statusHeight=AppUtils.getStatusHeight((Activity) getContext());
    }
    public void setScreenEdge(boolean screenEdge) {
        isScreenEdge = screenEdge;
    }
    private int lastX;
    private int lastY;

    private boolean isDrag;
    private long downTime;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isDrag=false;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX=rawX;
                lastY=rawY;
                downTime= System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                isDrag=true;
                //计算手指移动了多少
                int dx=rawX-lastX;
                int dy=rawY-lastY;
//                //这里修复一些华为手机无法触发点击事件的问题
//                int distance= (int) Math.sqrt(dx*dx+dy*dy);
//                if(distance<=0){
//                    isDrag=false;
//                    break;
//                }
                float x=getX()+dx;
                float y=getY()+dy;
                //检测是否到达边缘 左上右下
                x=x<0?0:x>screenWidth-getWidth()?screenWidth-getWidth():x;
                y=y<statusHeight?statusHeight:y+getHeight()>screenHeight?screenHeight-getHeight():y;
                setX(x);
                setY(y);
                lastX=rawX;
                lastY=rawY;
                break;
            case MotionEvent.ACTION_UP:
                //这里修复一些华为手机无法触发点击事件的问题
                long upTime= System.currentTimeMillis();
                if((upTime-downTime)<200){
                    isDrag=false;
                }
                if(isDrag){
                    //恢复按压效果
                    setPressed(false);
                    if(isScreenEdge) {
                        //========移动到屏幕边缘
                        if (rawX >= screenWidthHalf) {
                            animate().setInterpolator(new DecelerateInterpolator())
                                    .setDuration(500)
                                    .xBy(screenWidth - getWidth() - getX())
                                    .start();
                        } else {
                            ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
                            oa.setInterpolator(new DecelerateInterpolator());
                            oa.setDuration(500);
                            oa.start();
                        }
                        //========移动到屏幕边缘
                    }
                }
                break;
        }
        //如果是拖拽则消耗事件，否则正常传递即可。
        return isDrag || super.onTouchEvent(event);
    }
}