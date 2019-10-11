package com.base.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.base.appManager.BaseApplication;
import com.base.applicationUtil.AppUtils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 80热敏纸最大打印宽度 292.0
 * 一个空格宽度 		3.0
 * 一个中文宽度 		12.0
 * 一个数字宽度 		7.0
 * 一个中文符号 		12.0 （：）
 * 一个英文符号 		3.0  （.）
 * 一个英文小写字符宽度	7.0（a）
 * 一个英文大写字符宽度	8.0 （A）
 *
 * &#32; == 普通的英文半角空格
 * &#160; == &nbsp; == &#xA0; == no-break space （普通的英文半角空格但不换行）
 * &#12288; == 中文全角空格 （一个中文宽度）
 * &#8194; == &ensp; == en空格 （半个中文宽度，但两个空格比一个中文略大）
 * &#8195; == &emsp; == em空格 （一个中文宽度，但用起来会比中文字宽一点点）
 * &#8197; == 四分之一em空格 （四分之一中文宽度）
 * &#8201;（窄空格：）
 * &#8230;（省略号）
 * \u3000\u3000（首行缩进）
 * \u3000（全角空格(中文符号)）
 * \u0020（半角空格(英文符号)）
 */
public class StringUtil {
    // 存放国标一级汉字不同读音的起始区位码
    static final int GB_SP_DIFF = 160;
    // 存放国标一级汉字不同读音的起始区位码对应读音
    static final int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106
            , 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5600 };
    static final char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',  'y', 'z' };
    /***
     * 获取url 指定name的value;
     * @param url
     * @param name
     * @return
     */
    public static String getValueByName(String url, String name) {
        String result = "";
        int index = url.indexOf("?");
        String temp = url.substring(index + 1);
        String[] keyValue = temp.split("&");
        for (String str : keyValue) {
            if (str.contains(name)) {
                result = str.replace(name + "=", "");
                break;
            }
        }
        return result;
    }
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
        if (isEmpty(paramString))return false;
        String regExp = "^((13[0-9])|(15[0-9])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(14[4-9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(paramString);
        return m.matches();
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
    /**
     * 是否是英文
     * @return
     */
    public static boolean isEnglish(String charaString){
        return charaString.matches("^[a-zA-Z]*");
    }
    public static boolean isChinese(String str){
        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if(m.find())
            return true;
        else
            return false;
    }

    /**
     * 汉字转拼音
     * @param characters
     * @return
     */
    public static String getSpells(String characters) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < characters.length(); i++) {
            char ch = characters.charAt(i);
            if ((ch >> 7) == 0) {
                // 判断是否为汉字，如果左移7位为0就不是汉字，否则是汉字
                buffer.append(ch);
            } else {
                char spell = getFirstLetter(ch);
                buffer.append(String.valueOf(spell));
            }
        }
        return buffer.toString().toLowerCase();
    }
    // 获取一个汉字的首字母
    public static Character getFirstLetter(char ch) {
        byte[] uniCode = null;
        try {
            uniCode = String.valueOf(ch).getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        if (uniCode[0] < 128 && uniCode[0] > 0) {
// 非汉字
            return ch;
        } else {
            return convert(uniCode);
        }
    }
    /**
     * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
     * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
     */
    static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i] && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }

    /**
     * List 深度复制（重新分配地址）
     * @param src
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> List<T> deepCopy(List<T> src){
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = null;
        @SuppressWarnings("unchecked")
        List<T> dest = null;
        try {
            in = new ObjectInputStream(byteIn);
            dest = (List<T>) in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dest==null?new ArrayList<T>():dest;
    }
    /**
     * @return 返回指定的文字宽度
     */
    public static float getFontWidth(String text) {
        return new Paint().measureText(text);
    }
    /**
     * @return 返回指定的文字高度
     */
    public static float getFontHeight(String text) {
        Paint.FontMetrics fm = new Paint().getFontMetrics();
        //文字基准线的下部距离-文字基准线的上部距离 = 文字高度
        return fm.descent - fm.ascent;
    }
    /**
     * List转String以","隔开
     * 采用Stringbuilder.append()的方式追加
     * @param mList
     * @return
     */
    public static String listToString(List<String> mList) {
        final String SEPARATOR = ",";
        // mList = Arrays.asList("AAA", "BBB", "CCC");
        StringBuilder sb = new StringBuilder();
        String convertedListStr = "";
        if (null != mList && mList.size() > 0) {
            for (String item : mList) {
                sb.append(item);
                sb.append(SEPARATOR);
            }
            convertedListStr = sb.toString();
            convertedListStr = convertedListStr.substring(0, convertedListStr.length()
                    - SEPARATOR.length());
            return convertedListStr;
        } else return "";
    }

    /**
     * 在textView前后添加图标
     * @param index      前后 0前，1后
     * @param text       文本
     * @param icon       图标
     * @param size       图标大小单位dp
     * @return
     */
    public static SpannableString setIconToText(int index,String text,int icon,int size){
        if (index==0) {
            SpannableString sp = new SpannableString("   " + text);
            //获取一张图片
            Drawable drawable = ContextCompat.getDrawable(BaseApplication.getInstance(), icon);
            drawable.setBounds(0, 0, AppUtils.dipTopx(BaseApplication.getInstance(), size),
                    AppUtils.dipTopx(BaseApplication.getInstance(), size));
            //居中对齐imageSpan
            CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawable);
            /**
             * 解释一下0,1： 开始位置从0开始，到第一个位置结束。 如果大家想在最后面加上图标，可以把0换成字符串长度-1，
             * 把1换成字符串长度，切记要在后面加一个空格占位，否则会切割掉你原本的字符串哦。
             */
            sp.setSpan(imageSpan, 0, 1, ImageSpan.ALIGN_BASELINE);
            return sp;
        }else {
            SpannableString sp = new SpannableString(text+"   ");
            //获取一张图片
            Drawable drawable = ContextCompat.getDrawable(BaseApplication.getInstance(), icon);
            drawable.setBounds(0, 0, AppUtils.dipTopx(BaseApplication.getInstance(), size),
                    AppUtils.dipTopx(BaseApplication.getInstance(), size));
            //居中对齐imageSpan
            CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawable);
            /**
             * 解释一下0,1： 开始位置从0开始，到第一个位置结束。 如果大家想在最后面加上图标，可以把0换成字符串长度-1，
             * 把1换成字符串长度，切记要在后面加一个空格占位，否则会切割掉你原本的字符串哦。
             */
            sp.setSpan(imageSpan, -1, text.length(), ImageSpan.ALIGN_BASELINE);
            return sp;
        }
    }
    /**
     *  这个是一个可以垂直居中的ImageSpan
     */
    static class CenterAlignImageSpan extends ImageSpan {

        public CenterAlignImageSpan(Drawable drawable) {
            super(drawable);
        }
        public CenterAlignImageSpan(Bitmap b) {
            super(b);
        }
        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                         @NonNull Paint paint) {
            Drawable b = getDrawable();
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
            int transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;//计算y方向的位移
            canvas.save();
            canvas.translate(x, transY);//绘制图片位移一段距离
            b.draw(canvas);
            canvas.restore();
        }
    }
    /**
     * 判断对象或对象数组中每一个对象是否为空:
     * 对象为null或{}，
     * 字符序列长度为0或null，
     * 集合类、Map为empty
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null)
            return true;
        if (obj instanceof CharSequence)
            return ((CharSequence) obj).length() == 0;
        if (((String)obj).toLowerCase().equals("null")
                &&((String)obj).trim().length()<=0){
            return true;
        }
        if (obj instanceof Collection)
            return ((Collection) obj).isEmpty();

        if (obj instanceof Map)
            return ((Map) obj).isEmpty();

        if (obj instanceof Object[]) {
            Object[] object = (Object[]) obj;
            if (object.length == 0) {
                return true;
            }
            boolean empty = true;
            for (int i = 0; i < object.length; i++) {
                if (!isEmpty(object[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        String str = ObjectUtils.toString(obj, "");
        return StringUtils.isNotBlank(str);
    }
    public static void main(String[] arg){
//        System.out.println(NativeUtils.getNativeString());
//        String ptCasinoMsg = "qwe123wer45.fadsf56hudh55.55fhsj6.00dj";
////        String ptCasinoMsg = "qwefhsj.dj";
////        String ptCasinoMsg = "日单量：100 | 实付金额：5000.0 | 订单金额：57000.34 | 优惠金额：9000";
//        String [] amounts = extractAmountMsg(ptCasinoMsg);
//        for (String i:amounts){
//            System.out.println("金额："+i);
//        }
//        System.out.println(isMobileNO("14600087240"));
        System.out.println("width="+StringUtil.getFontWidth("                     "));
        System.out.println("width="+StringUtil.getFontWidth("        "));
        System.out.println("width="+StringUtil.getFontWidth("       "));
    }
}
