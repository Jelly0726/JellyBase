package com.base.Utils;

import android.graphics.Rect;
import android.view.View;

import java.io.ObjectStreamException;

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
}
