package com.base.Utils;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtil {
    /**
     * 隐藏字符
     * @param str    要替换的字符串
     * @param star   要替换的开始位置
     * @param end    要替换的结束位置
     * @param ss     替换的字符
     * @return
     */
    public static String getReplace(@NonNull String str , int star, int end, String ss){
        String sr =str.trim().replaceAll(" ", "");
        int len = sr.length();
        if (star<1 || star>len ||end<1 ||end<star ||end>len){
            return  str;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i>=star-1 &&i <=end-1)
                builder.append(ss);
            else
                builder.append(sr.charAt(i));
        }
        return builder.toString();
    }
    /**
     * 隐藏字符
     * @param str    要替换的字符串
     * @param star   要替换的开始位置
     * @param end    要替换的结束位置
     * @return
     */
    public static String getReplace(@NonNull String str ,int star,int end){
        String sr =str.trim().replaceAll(" ", "");
        int len = sr.length();
        if (star<1 || star>len ||end<1 ||end<star ||end>len){
            return  str;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i>=star-1 && i <=end-1)
                builder.append("*");
            else
                builder.append(sr.charAt(i));
        }
        return builder.toString();
    }
    /**
     *
     * @param str 需要正则表达式判断的字符串
     * @param regex 正则表达式
     * @param bool 是否区分大小写
     * @return
     */
    public static boolean like(String str,String regex,boolean bool)
    {
        regex = regex.replaceAll("\\*", ".*");
        regex = regex.replaceAll("\\?", ".");
        Pattern pattern = Pattern.compile(regex,bool?Pattern.CASE_INSENSITIVE:0);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }
    /**
     * 判断邮箱是否合法
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
//      Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }
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
     * 判断字符串是否全为汉字
     * @param string      待判定的字符串
     * @return
     */
    public static boolean isAllChina(String string){
        String reg = "[\\u4e00-\\u9fa5]+";
        return string.matches(reg);
    }

    /**
     * 提取字符串中的汉字
     * @param string     源字符串
     * @return           全是汉字的字符串
     */
    public static String getChina(String string){
        String reg = "[^\u4e00-\u9fa5]";
        string = string.replaceAll(reg, " ");
        return string;
    }

    /**
     * 判断字符串中是否含有汉字
     * @param string     源字符串
     * @return  true:无汉字  false:有汉字
     */
    public static boolean isContainChina(String string){
        return (string.length() == string.getBytes().length);
    }

    /**
     * 获取字符串中汉字的个数
     * @param string      源字符串
     * @return           汉字的个数
     */
    public static int haveChinaNum(String string){
        int count = 0;
        String reg = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(string);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count;
    }
}
