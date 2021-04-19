package com.jelly.baselibrary.SystemBar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class StatusBarUtil {
    private static boolean hasExecuteNotch = false;//
    private static boolean isNotch = false;//是否刘海屏
    private static int mBarSize = 0;//状态栏高度
    public final static int TYPE_MIUI = 0;
    public final static int TYPE_FLYME = 1;
    public final static int TYPE_M = 3;//6.0

    @IntDef({TYPE_MIUI,
            TYPE_FLYME,
            TYPE_M})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param colorId 颜色
     */
    public static void setStatusBarColor(Activity activity, int colorId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(colorId);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTintManager,需要先将状态栏设置为透明
            setTranslucentStatus(activity);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(activity);
            systemBarTintManager.setStatusBarTintEnabled(true);//显示状态栏
            systemBarTintManager.setStatusBarTintColor(colorId);//设置状态栏颜色
        }
    }

    /**
     * 设置状态栏透明
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //导航栏颜色也可以正常设置
            //window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
            //int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            //attributes.flags |= flagTranslucentNavigation;
            window.setAttributes(attributes);
        }
    }


    /**
     * 代码实现android:fitsSystemWindows
     *
     * @param activity
     */
    public static void setRootViewFitsSystemWindows(Activity activity, boolean fitSystemWindows) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup winContent = (ViewGroup) activity.findViewById(android.R.id.content);
            if (winContent.getChildCount() > 0) {
                ViewGroup rootView = (ViewGroup) winContent.getChildAt(0);
                if (rootView != null) {
                    rootView.setFitsSystemWindows(fitSystemWindows);
                }
            }
        }

    }


    /**
     * 设置状态栏深色浅色切换
     */
    public static boolean setStatusBarDarkTheme(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setStatusBarFontIconDark(activity, TYPE_M, dark);
            } else if (OSUtils.isMiui()) {
                setStatusBarFontIconDark(activity, TYPE_MIUI, dark);
            } else if (OSUtils.isFlyme()) {
                setStatusBarFontIconDark(activity, TYPE_FLYME, dark);
            } else {//其他情况
                return false;
            }

            return true;
        }
        return false;
    }

    /**
     * 设置 状态栏深色浅色切换
     */
    public static boolean setStatusBarFontIconDark(Activity activity, @ViewType int type, boolean dark) {
        switch (type) {
            case TYPE_MIUI:
                return setMiuiUI(activity, dark);
            case TYPE_FLYME:
                return setFlymeUI(activity, dark);
            case TYPE_M:
            default:
                return setCommonUI(activity, dark);
        }
    }

    //设置6.0 状态栏深色浅色切换
    public static boolean setCommonUI(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (dark) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                if (decorView.getSystemUiVisibility() != vis) {
                    decorView.setSystemUiVisibility(vis);
                }
                return true;
            }
        }
        return false;

    }

    //设置Flyme 状态栏深色浅色切换
    public static boolean setFlymeUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //设置MIUI 状态栏深色浅色切换
    public static boolean setMiuiUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            Class<?> clazz = activity.getWindow().getClass();
            @SuppressLint("PrivateApi") Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getDeclaredMethod("setExtraFlags", int.class, int.class);
            extraFlagField.setAccessible(true);
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取系统状态栏高度
     * @param context
     * @return
     */
    public static int getBarHeight(Context context) {
        int mBarSize = 0;
        try {
            //方法一
            Resources resources = context.getApplicationContext().getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                mBarSize = resources.getDimensionPixelSize(resourceId);
            } else {
                //方法二 通过反射
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField("status_bar_height")
                        .get(object).toString());
                mBarSize = context.getApplicationContext().getResources().getDimensionPixelSize(height);
            }
            //方法三  状态栏高度 = 屏幕高度 - 应用区高度 此方法需要在view加载进activity后有效
            //屏幕
//            DisplayMetrics dm = new DisplayMetrics();
//            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
//            //应用区域
//            Rect outRect1 = new Rect();
//            ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
//            if (!outRect1.isEmpty()) {
//                //状态栏高度=屏幕高度-应用区域高度
//                mBarSize = Math.max(mBarSize, dm.heightPixels - outRect1.height());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context = null;
            return mBarSize;
        }
    }

    /**
     * 获取状态栏高度（刘海屏则获取刘海高度）
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (mBarSize == 0) {
            int height = getBarHeight(context);
            int notchHeight = getNotchHeight((Activity) context);
            mBarSize = Math.max(height, notchHeight);
        }
        return mBarSize;
    }

    /**
     * 获取刘海高度
     */
    public static int getNotchHeight(Activity activity) {
        String manufacturer = android.os.Build.MANUFACTURER.toLowerCase();
        int mBarSize = 0;
        if (hasNotch(activity)) {
            //有刘海才获取高度 否则默认刘海高度是0
            if (manufacturer.equalsIgnoreCase("xiaomi")) {
                mBarSize = getBarHeight(activity);//小米刘海会比状态栏小 直接获取状态栏高度
            } else if (manufacturer.equalsIgnoreCase("huawei")
                    || manufacturer.equalsIgnoreCase("honour")) {
                mBarSize = getNotchSizeAtHuaWei(activity);
            } else if (manufacturer.equalsIgnoreCase("vivo")) {
                //VIVO是32dp
                mBarSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        32, activity.getApplication().getResources().getDisplayMetrics());
            } else if (manufacturer.equalsIgnoreCase("oppo")) {
                mBarSize = 80;//oppo当时是固定数值
            } else if (manufacturer.equalsIgnoreCase("smartisan")) {
                mBarSize = 82;//当时锤子PDF文档上是固定数值
            } else {
                //其他品牌手机
                if (activity != null && activity.getWindow() != null) {
                    View decorView = activity.getWindow().getDecorView();
                    if ((Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)) {
                        WindowInsets windowInsets = decorView.getRootWindowInsets();
                        if (windowInsets != null) {
                            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
                                DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                                if (displayCutout != null) {
                                    List<Rect> rects = displayCutout.getBoundingRects();
                                    if (rects != null && rects.size() > 1) {
                                        if (rects.get(0) != null) {
                                            mBarSize = rects.get(0).bottom;
                                            return mBarSize;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            mBarSize = getBarHeight(activity);
        }
        return mBarSize;
    }

    /**
     * 华为获取刘海高度
     * 获取刘海尺寸：width、height
     * int[0]值为刘海宽度 int[1]值为刘海高度
     */
    public static int getNotchSizeAtHuaWei(Activity activity) {
        int height = 0;
        try {
            ClassLoader cl = activity.getApplication().getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            int[] ret = (int[]) get.invoke(HwNotchSizeUtil);
            height = ret[1];
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }

    /**
     * 是否是刘海或是钻孔屏幕 全局只取一次
     *
     * @return
     */
    public static boolean hasNotch(Activity activity) {
        if (!hasExecuteNotch) {
            hasExecuteNotch = true;
            String manufacturer = android.os.Build.MANUFACTURER.toLowerCase();
            if (manufacturer.equalsIgnoreCase("xiaomi")) {
                isNotch= hasNotchAtXiaoMi(activity);
            } else if (manufacturer.equalsIgnoreCase("huawei")
                    || manufacturer.equalsIgnoreCase("honour")) {
                isNotch= hasNotchAtHuawei(activity);
            } else if (manufacturer.equalsIgnoreCase("vivo")) {
                isNotch= hasNotchAtVivo(activity);
            } else if (manufacturer.equalsIgnoreCase("oppo")) {
                isNotch= hasNotchAtOPPO(activity);
            } else if (manufacturer.equalsIgnoreCase("samsung")) {
                isNotch= hasNotchSamsung(activity);
            } else {
                isNotch= isOtherBrandHasNotch(activity);
            }
        }
        return isNotch;
    }

    /**
     * 小米刘海屏判断
     *
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static boolean hasNotchAtXiaoMi(Activity activity) {
        return getInt("ro.miui.notch", activity) == 1;
    }

    public static int getInt(String key, Activity activity) {
        int result = 0;
        if (android.os.Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            try {
                ClassLoader classLoader = activity.getApplication().getClassLoader();
                @SuppressWarnings("rawtypes")
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //参数类型
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
                Object[] params = new Object[2];
                params[0] = new String(key);
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 华为刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtHuawei(Activity activity) {
        boolean ret = false;
        try {
            ClassLoader classLoader = activity.getApplication().getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * OPPO刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtOPPO(Activity activity) {
        return activity.getApplication().getPackageManager()
                .hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    /**
     * VIVO刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtVivo(Activity activity) {
        boolean ret = false;
        try {
            ClassLoader classLoader = activity.getApplication().getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, 0x20);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 判断三星手机是否有刘海屏
     *
     * @return
     */
    private static boolean hasNotchSamsung(Activity activity) {
        if (android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            try {
                final Resources res = activity.getApplication().getResources();
                final int resId = res.getIdentifier("config_mainBuiltInDisplayCutout", "string", "android");
                final String spec = resId > 0 ? res.getString(resId) : null;
                return spec != null && !TextUtils.isEmpty(spec);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**其他品牌判断是否刘海屏
     * @param activity
     * @return
     */
    private static boolean isOtherBrandHasNotch(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            View decorView = activity.getWindow().getDecorView();
            if ((Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)) {
                WindowInsets windowInsets = decorView.getRootWindowInsets();
                if (windowInsets != null) {
                    if ((Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)) {
                        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                        if (displayCutout != null) {
                            List<Rect> rects = displayCutout.getBoundingRects();
                            if (rects != null && rects.size() > 0) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}