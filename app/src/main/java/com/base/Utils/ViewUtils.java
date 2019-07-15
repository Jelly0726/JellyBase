package com.base.Utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

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
}
