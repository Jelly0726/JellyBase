package com.jelly.Utils;

import android.graphics.Color;

import com.jelly.log.LogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 颜色相关工具类
 */
public class ColorUtils {
    /**
     * 字符串非空判断
     * @param string
     * @return
     */
    public static boolean isEmpty(String string){
        if (string!=null){
            if (!string.toLowerCase().equals("null")
                    &&string.trim().length()>0){
                return false;
            }
        }
        return true;
    }
    /**
     * 判断字符串是不是颜色
     * @param paramString
     * @return
     */
    public static boolean isColor(String paramString) {
        if (isEmpty(paramString))return false;
        String regExp = "^#([0-9a-fA-F]{8}|[0-9a-fA-F]{6}|[0-9a-fA-F]{3})$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(paramString);
        LogUtils.i("paramString="+m.matches());
        return m.matches();
    }
    /**
     * 十进制的颜色值转化为十六进制的颜色值
     * @param color 十进制颜色
     * @return 16进制颜色字符串
     * */
    public static String toHexColor(int color) {
        String A,R, G, B;
        StringBuffer sb = new StringBuffer();
        A = Integer.toHexString(Color.alpha(color));
        R = Integer.toHexString(Color.red(color));
        G = Integer.toHexString(Color.green(color));
        B = Integer.toHexString(Color.blue(color));
        //判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
        A = A.length() == 1 ? "0" + A : A;
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        sb.append("#");
        sb.append(A);
        sb.append(R);
        sb.append(G);
        sb.append(B);
        LogUtils.i("16进制颜色="+sb.toString());
        return sb.toString();
    }
    /**
     * 透明度百分比和十六进制对应关系
     */
    private static void rgba() {
        System.out.println("透明度 | 十六进制");
        System.out.println("---- | ----");
        for (double i = 1; i >= 0; i -= 0.01) {
            i = Math.round(i * 100) / 100.0d;
            int alpha = (int) Math.round(i * 255);
            String hex = Integer.toHexString(alpha).toUpperCase();
            if (hex.length() == 1) hex = "0" + hex;
            int percent = (int) (i * 100);
            System.out.println(String.format("%d%% | %s", percent, hex));
        }
    }
    /**
     * 获取透明度百分比对应的十六进制
     * @param alp
     * @return
     */
    public static String rgba(double alp){
        alp = Math.round(alp * 100) / 100.0d;
        int alpha = (int) Math.round(alp * 255);
        String hex = Integer.toHexString(alpha).toUpperCase();
        if (hex.length() == 1) hex = "0" + hex;
        return hex;
    }
    public static void main(String[] arg){
        rgba();
    }
}
