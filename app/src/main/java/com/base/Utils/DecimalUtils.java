package com.base.Utils;

import java.text.DecimalFormat;

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
        if (num<=0){
            return amount+"";
        }
//        BigDecimal bg = new BigDecimal(amount);
//        amount = bg.setScale(num, BigDecimal.ROUND_HALF_UP).floatValue();
        String deci="######0.";
        for (int i=0;i<num;i++){
            deci+="0";
        }
        DecimalFormat df= new DecimalFormat(deci);
        return df.format(amount);
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
        if (num<=0){
            return amount+"";
        }
//        BigDecimal bg = new BigDecimal(amount);
//        amount = bg.setScale(num, BigDecimal.ROUND_HALF_UP).doubleValue();
        String deci="######0.";
        for (int i=0;i<num;i++){
            deci+="0";
        }
        DecimalFormat df= new DecimalFormat(deci);
        return df.format(amount);
    }
}