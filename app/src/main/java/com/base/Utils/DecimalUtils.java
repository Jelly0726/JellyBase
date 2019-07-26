package com.base.Utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 金额工具类
 */
public class DecimalUtils {
    /**
     *保留两位小数
     * @param amount
     * @return
     */
    public static float getTwoDecimal(float amount){
//        BigDecimal bg = new BigDecimal(amount);
//        amount = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

        DecimalFormat df= new DecimalFormat("######0.00");
        return Float.parseFloat(df.format(amount));
    }
    /**
     *保留两位小数
     * @param amount
     * @return
     */
    public static String getTwoDecimal1(float amount){
//        BigDecimal bg = new BigDecimal(amount);
//        amount = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        DecimalFormat df= new DecimalFormat("######0.00");
        return df.format(amount);
    }
    /**
     *保留两位小数
     * @param amount
     * @return
     */
    public static double getTwoDecimal(double amount){
//        BigDecimal bg = new BigDecimal(amount);
//        amount = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        DecimalFormat df= new DecimalFormat("######0.00");
        return Double.parseDouble(df.format(amount));
    }
    /**
     *保留两位小数
     * @param amount
     * @return
     */
    public static String getTwoDecimal1(double amount){
//        BigDecimal bg = new BigDecimal(amount);
//        amount = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        DecimalFormat df= new DecimalFormat("######0.00");
        return df.format(amount);
    }
    /**
     *保留多位小数
     * @param amount  原数据
     * @param num     小数位
     * @return
     */
    public static float getDecimal(float amount,int num){
        if (num<=0){
            return amount;
        }
//        BigDecimal bg = new BigDecimal(amount);
//        amount = bg.setScale(num, BigDecimal.ROUND_HALF_UP).floatValue();
        String deci="######0.";
        for (int i=0;i<num;i++){
            deci+="0";
        }
        DecimalFormat df= new DecimalFormat(deci);
        return Float.parseFloat(df.format(amount));
    }
    /**
     *保留多位小数
     * @param amount  原数据
     * @param num     小数位
     * @return
     */
    public static String getDecimal1(float amount,int num){
//        BigDecimal bg = new BigDecimal(amount);
//        amount = bg.setScale(num, BigDecimal.ROUND_HALF_UP).floatValue();
        if(num == 0){
            return new DecimalFormat("0").format(amount);
        }
        String formatStr = "#####0.";
        for(int i=0;i<num;i++){
            formatStr = formatStr + "0";
        }
        return new DecimalFormat(formatStr).format(amount);
    }
    /**
     *保留多位小数
     * @param amount  原数据
     * @param num     小数位
     * @return
     */
    public static double getDecimal(double amount,int num){
        if (num<=0){
            return amount;
        }
//        BigDecimal bg = new BigDecimal(amount);
//        amount = bg.setScale(num, BigDecimal.ROUND_HALF_UP).doubleValue();
        String deci="######0.";
        for (int i=0;i<num;i++){
            deci+="0";
        }
        DecimalFormat df= new DecimalFormat(deci);
        return Double.parseDouble(df.format(amount));
    }
    /**
     *保留多位小数
     * @param amount  原数据
     * @param num     小数位
     * @return
     */
    public static String getDecimal1(double amount,int num){
//        BigDecimal bg = new BigDecimal(amount);
//        amount = bg.setScale(num, BigDecimal.ROUND_HALF_UP).doubleValue();
        if(num == 0){
            return new DecimalFormat("0").format(amount);
        }
        String formatStr = "#####0.";
        for(int i=0;i<num;i++){
            formatStr = formatStr + "0";
        }
        return new DecimalFormat(formatStr).format(amount);
    }
    /**
     * 数值舍入操作
     * @param amount
     * @param type  取BigDecimal里的常量
     * @return
     */
    public static String getBigDecimal(double amount,int type){
        String result="";
        BigDecimal bg3 = new BigDecimal(amount);
        switch (type){
            case BigDecimal.ROUND_UP://向上取整
                result=String.valueOf(bg3.setScale(2, BigDecimal.ROUND_UP).doubleValue());
                break;
            case BigDecimal.ROUND_DOWN://向下取整
                result=String.valueOf(bg3.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                break;
            case BigDecimal.ROUND_HALF_UP://四舍五入
                result=String.valueOf(bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                break;
            case BigDecimal.ROUND_HALF_DOWN://五舍六入
                result=String.valueOf(bg3.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue());
                break;
        }
        return result;
    }
    /**
     * 小数点末尾字体变小
     */
    public static SpannableString changTVsize(String value) {
        SpannableString spannableString = new SpannableString(value);
        if (value.contains(".")) {
            spannableString.setSpan(new RelativeSizeSpan(0.6f),
                    value.indexOf("."), value.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }
    /**
     * 金钱数字保留4位小数且三位三位的隔开
     */
    public static String changFormat(String value) {
        NumberFormat nf = new DecimalFormat("#,###.####");
        Double d = 554545.4545454;
        String str = nf.format(d);
        return str;
    }
}
