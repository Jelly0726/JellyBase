package com.jelly.view;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * 监听SoftKeyboard的弹出与隐藏状态，并通过接口方法通知出去。
 * 界面UI状态的变化通过ViewTreeObserver.OnGlobalLayoutListener接口来监听
 */
public class SoftKeyboardManager implements ViewTreeObserver.OnGlobalLayoutListener{
    public static final boolean DEBUG = false;
    public interface SoftKeyboardStateListener {
        void onSoftKeyboardOpened(int keyboardHeightInPx);
        void onSoftKeyboardClosed();
    }

    private final List<SoftKeyboardStateListener> listeners = new LinkedList<SoftKeyboardStateListener>();
    private final View activityRootView;
    private int lastSoftKeyboardHeightInPx;
    private boolean isSoftKeyboardOpened;

    public SoftKeyboardManager(View activityRootView) {
        this(activityRootView, false);
    }

    public SoftKeyboardManager(View activityRootView, boolean isSoftKeyboardOpened) {
        this.activityRootView = activityRootView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @Override

    public void onGlobalLayout() {

        final Rect r = new Rect();
        //r will be populated with the coordinates of your view that area still visible.
        activityRootView.getWindowVisibleDisplayFrame(r);
        final int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
        if (DEBUG){
            Log.d("SoftKeyboardStateHelper", "heightDiff:" + heightDiff);
        }
        if (!isSoftKeyboardOpened && heightDiff > 500) { // if more than 100 pixels， its probably a keyboard...
            isSoftKeyboardOpened = true;
            notifyOnSoftKeyboardOpened(heightDiff);
            //if (isSoftKeyboardOpened && heightDiff < 100)
        } else if (isSoftKeyboardOpened && heightDiff < 500) {
            isSoftKeyboardOpened = false;
            notifyOnSoftKeyboardClosed();
        }
    }

    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened) {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    public boolean isSoftKeyboardOpened() {
        return isSoftKeyboardOpened;
    }

    /**
     * Default value is zero (0)
     *
     * @return last saved keyboard height in px
     */

    public int getLastSoftKeyboardHeightInPx() {

        return lastSoftKeyboardHeightInPx;

    }

    public void addSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.add(listener);
    }

    public void removeSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.remove(listener);
    }

    public void dispose(){
        activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeightInPx) {
        this.lastSoftKeyboardHeightInPx = keyboardHeightInPx;
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeightInPx);
            }
        }
    }

    private void notifyOnSoftKeyboardClosed() {
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardClosed();
            }
        }
    }
}