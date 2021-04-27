package com.jelly.baselibrary.appManager;

import android.app.Activity;

import com.jelly.baselibrary.AppCallBack;

import java.io.ObjectStreamException;
import java.lang.ref.WeakReference;
import java.util.Stack;

/**
 * activity堆栈式管理
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年10月30日 下午6:22:05
 */
public class AppManager {
    private static Stack<WeakReference<Activity>> activityStack;
    private static AppCallBack appCallBack;

    private AppManager() {
    }

    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder {
        private static final AppManager instance = new AppManager();
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        return SingletonHolder.instance;
    }

    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     *
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return SingletonHolder.instance;
    }

    public void setAppCallBack(AppCallBack appCallBack) {
        this.appCallBack = appCallBack;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<WeakReference<Activity>>();
        }
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        activityStack.add(activityWeakReference);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack == null) {
            return null;
        }
        Activity activity = activityStack.lastElement().get();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack == null) {
            return;
        }
        Activity activity = activityStack.lastElement().get();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            if (activityStack != null) {
                for (WeakReference<Activity> activitys : activityStack) {
                    if (activitys.get() == activity)
                        activityStack.remove(activitys);
                }
            }
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (activityStack == null) {
            return;
        }
        for (WeakReference<Activity> activity : activityStack) {
            if (activity.get().getClass().equals(cls)) {
                finishActivity(activity.get());
                break;
            }
        }
    }

    public boolean isRegistered(Class<?> clas) {
        if (activityStack == null) {
            return false;
        }
        for (WeakReference<Activity> activity : activityStack) {
            if (activity.get().getClass().equals(clas)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack == null) {
            return;
        }
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                if (null != activityStack.get(i).get())
                    activityStack.get(i).get().finish();
                break;
            }
        }
        activityStack.clear();
        activityStack = null;
    }

    /**
     * 获取指定的Activity
     *
     * @author kymjs
     */
    public Activity getActivity(Class<?> cls) {
        if (activityStack != null)
            for (WeakReference<Activity> activity : activityStack) {
                if (activity.get().getClass().equals(cls)) {
                    return activity.get();
                }
            }
        return null;
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            if (appCallBack != null)
                appCallBack.appExit();
        } catch (Exception e) {
        }
    }
}