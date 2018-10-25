package com.base.Utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.base.appManager.BaseApplication;
import com.base.applicationUtil.AppUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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
        if (TextUtils.isEmpty(str)||TextUtils.isEmpty(ss))return str;
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
        if (TextUtils.isEmpty(str))return str;
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
        if (TextUtils.isEmpty(str)||TextUtils.isEmpty(regex))return false;
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
        if (TextUtils.isEmpty(string))return false;
        String reg = "[\\u4e00-\\u9fa5]+";
        return string.matches(reg);
    }

    /**
     * 提取字符串中的汉字
     * @param string     源字符串
     * @return           全是汉字的字符串
     */
    public static String getChina(String string){
        if (TextUtils.isEmpty(string))return string;
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
        if (TextUtils.isEmpty(string))return false;
        return (string.length() == string.getBytes().length);
    }

    /**
     * 获取字符串中汉字的个数
     * @param string      源字符串
     * @return           汉字的个数
     */
    public static int haveChinaNum(String string){
        if (TextUtils.isEmpty(string))return 0;
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
    /**
     * 判断字符串是不是手机号码
     * @param paramString
     * @return
     */
    public static boolean isMobileNO(String paramString) {
        if (TextUtils.isEmpty(paramString))return false;
        return Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-1,5-9]))\\d{8}$").matcher(paramString).matches();
    }

    /**
     * 提取字符串中的数字（整数，flot,double）类型的金额
     * @param ptCasinoMsg  源字符串
     * @return
     */
    public static String[] extractAmountMsg(String ptCasinoMsg){
       List str=new ArrayList();
        ptCasinoMsg = ptCasinoMsg.replaceAll("[^\\d.]+"," ");
        String [] amounts = ptCasinoMsg.split(" ");
        for(int i=0;i<amounts.length;i++){
            Pattern p=Pattern.compile("(\\d+\\.\\d+)");
            Matcher m=p.matcher(amounts[i]);
            if(m.find()){
                str.add(m.group(1));
            }else{
                p= Pattern.compile("(\\d+)");
                m=p.matcher(amounts[i]);
                if(m.find()){
                    str.add(m.group(1));
                }
            }
        }
        String[] strings = new String[str.size()];
        str.toArray(strings);
        return strings;
    }
    /**
     * 关键字高亮显示
     *
     * @param target  需要高亮的关键字
     * @param text	     需要显示的文字
     * @param color      颜色
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     */
    public static SpannableStringBuilder highlight(String text, String target, int color) {
        if (TextUtils.isEmpty(text)||TextUtils.isEmpty(target))return null;
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            span = new ForegroundColorSpan(BaseApplication.getInstance().getResources().getColor(color));// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }
    /**
     * 根据值, 设置spinner默认选中:
     * @param spinner
     * @param value
     */
    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value, String modle){
        if (TextUtils.isEmpty(value)||TextUtils.isEmpty(modle))return;
        BaseAdapter apsAdapter= (BaseAdapter) spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        try {
            for (int i = 0; i < k; i++) {
                Object obj = apsAdapter.getItem(i);
                Class cla = obj.getClass();
                Field f = cla.getDeclaredField(modle);// 属性的值
                f.setAccessible(true);    // Very Important
                Object valu = (Object) f.get(obj);
                //Log.i("msg","value="+value+" valu="+valu);
                if (value.trim().equals(valu.toString().trim())) {
                    spinner.setSelection(i,true);// 默认选中项
                    break;
                }
            }
        }catch (Exception e){
            //Log.i("msg","e="+e.toString());
        }
    }
    /**
     * 添加选中下划线
     * @param activity  activity
     * @param button    选中的按钮  Button
     * @param linearLayout  父容器 LinearLayout
     */
    public static void setTabSelected(Activity activity, Button button, LinearLayout linearLayout) {
        Drawable selectedDrawable = ResourceReader.readDrawable(activity,
                CPResourceUtil.getDrawableId(activity,"radiobutton_bottom_line"));
        int screenWidth = AppUtils.getScreenSize(activity)[0];
        int size = linearLayout.getChildCount();
        int right =(int)((screenWidth /size)*0.6);
        //前两个是组件左上角在容器中的坐标 后两个是组件的宽度和高度
        selectedDrawable.setBounds(0, 0,right, AppUtils.dipTopx(activity,1));
        button.setSelected(true);
        button.setCompoundDrawables(null, null, null, selectedDrawable);
        for (int i = 0; i < size; i++) {
            if (button.getId() != linearLayout.getChildAt(i).getId()) {
                linearLayout.getChildAt(i).setSelected(false);
                ((Button) linearLayout.getChildAt(i)).setCompoundDrawables(null, null, null, null);
            }
        }
    }
    public static void main(String[] arg){
//        System.out.println(NativeUtils.getNativeString());
        String ptCasinoMsg = "qwe123wer45.fadsf56hudh55.55fhsj6.00dj";
//        String ptCasinoMsg = "日单量：100 | 实付金额：5000.0 | 订单金额：57000.34 | 优惠金额：9000";
        String [] amounts = extractAmountMsg(ptCasinoMsg);
        for (String i:amounts){
            System.out.println("金额："+i);
        }
    }
}
