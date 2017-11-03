package com.base.applicationUtil;

import android.content.Context;

/**
 * 利用反射获取控件及资源
 */
public class CPResourceUtil {

    public static int getLayoutId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "layout", 
                paramContext.getPackageName()); 
    } 
 
    public static int getStringId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "string", 
                paramContext.getPackageName()); 
    } 
 
    public static int getDrawableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, 
                "drawable", paramContext.getPackageName()); 
    } 
     
    public static int getStyleId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, 
                "style", paramContext.getPackageName()); 
    } 
     
    public static int getId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,"id", paramContext.getPackageName()); 
    } 
     
    public static int getColorId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, 
                "color", paramContext.getPackageName()); 
    } 
    public static int getArrayId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, 
                "array", paramContext.getPackageName()); 
    }
    public static int getRawId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, 
                "raw", paramContext.getPackageName()); 
    } 
    public static int getStyleableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, 
                "styleable", paramContext.getPackageName()); 
    } 
}
