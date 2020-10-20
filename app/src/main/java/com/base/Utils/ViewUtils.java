package com.base.Utils;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;

import java.io.ObjectStreamException;
import java.util.ArrayList;

public class ViewUtils {
    private ViewUtils(){}
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final ViewUtils instance = new ViewUtils();
    }
    /**
     * 单一实例
     */
    public static ViewUtils getInstance() {
        return ViewUtils.SingletonHolder.instance;
    }
    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return ViewUtils.SingletonHolder.instance;
    }
    /**
     * 检测是否被遮住显示不全
     * @return false 未被遮住 true 被遮住
     */
    public boolean isCover(View view) {
        boolean cover = false;
        Rect rect = new Rect();
        cover = view.getGlobalVisibleRect(rect);
        if (cover) {
            if (rect.width() >= view.getMeasuredWidth() && rect.height() >= view.getMeasuredHeight()) {
                return !cover;
            }
        }
        return true;
    }
    /**

     * 根据坐标获取相对应的子控件<br>
     * 在Activity使用
     *
     * @param activity
     * @param x 坐标
     * @param y 坐标
     * @return 目标View
     */
    public View getViewAtActivity(Activity activity,int x, int y) {
        // 从Activity里获取容器
        View root =activity.getWindow().getDecorView();
        return findViewByXY(root, x, y);
    }

    /**
     * 根据坐标获取相对应的子控件<br>
     * 在重写ViewGroup使用
     *
     * @param x 坐标
     * @param y 坐标
     * @return 目标View
     */
    public View getViewAtViewGroup(ViewGroup viewGroup,int x, int y) {
        return findViewByXY(viewGroup, x, y);
    }

    private View findViewByXY(View view, int x, int y) {
        View targetView = null;
        if (view instanceof ViewGroup) {
            // 父容器,遍历子控件
            ViewGroup v = (ViewGroup) view;
            for (int i = 0; i < v.getChildCount(); i++) {
                targetView = findViewByXY(v.getChildAt(i), x, y);
                if (targetView != null) {
                    break;
                }
            }
        } else {
            targetView = getTouchTarget(view, x, y);
        }
        return targetView;

    }

    private View getTouchTarget(View view, int x, int y) {
        View targetView = null;
        // 判断view是否可以聚焦
        ArrayList<View> TouchableViews = view.getTouchables();
        for (View child : TouchableViews) {
            if (isTouchPointInView(child, x, y)) {
                targetView = child;
                break;
            }
        }
        return targetView;
    }

    private boolean isTouchPointInView(View view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (view.isClickable() && y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }
    /**
     * 创建背景颜色
     *
     * @param color       填充色
     * @param strokeColor 线条颜色
     * @param strokeWidth 线条宽度  单位px
     * @param radius      角度  px
     */
    public  GradientDrawable createShape(@ColorInt int color, @ColorInt int strokeColor, int strokeWidth, float radius) {
        try {
            GradientDrawable radiusBg = new GradientDrawable();
            //设置Shape类型
            radiusBg.setShape(GradientDrawable.RECTANGLE);
            //设置填充颜色
            radiusBg.setColor(color);
            //设置线条粗心和颜色,px
            radiusBg.setStroke(strokeWidth, strokeColor);
            //设置圆角角度,如果每个角度都一样,则使用此方法
            radiusBg.setCornerRadius(radius);
            return radiusBg;
        } catch (Exception e) {
            return new GradientDrawable();
        }
    }
    /**
     * 创建背景颜色
     *
     * @param color       填充色
     * @param strokeColor 线条颜色
     * @param strokeWidth 线条宽度  单位px
     * @param radius      角度  px,长度为4,分别表示左上,右上,右下,左下的角度
     */
    public  GradientDrawable createShape(@ColorInt int color, @ColorInt int strokeColor, int strokeWidth, float radius[]) {
        try {
            GradientDrawable radiusBg = new GradientDrawable();
            //设置Shape类型
            radiusBg.setShape(GradientDrawable.RECTANGLE);
            //设置填充颜色
            radiusBg.setColor(color);
            //设置线条粗心和颜色,px
            radiusBg.setStroke(strokeWidth, strokeColor);
            //每连续的两个数值表示是一个角度,四组:左上,右上,右下,左下
            if (radius != null && radius.length == 4) {
                radiusBg.setCornerRadii(new float[]{radius[0], radius[0], radius[1], radius[1], radius[2], radius[2], radius[3], radius[3]});
            }
            return radiusBg;
        } catch (Exception e) {
            return new GradientDrawable();
        }
    }

    /**
     * 创建背景颜色
     * @param color  填充色
     * @param radius  角度
     * @return
     */
    public GradientDrawable createShape(@ColorInt int color , int radius){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(radius);//设置4个角的弧度
        drawable.setColor(color);// 设置颜色
        return drawable;

    }
}
