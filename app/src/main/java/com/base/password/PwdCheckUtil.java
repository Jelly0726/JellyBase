package com.base.password;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS;
import static java.lang.Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS;
import static java.lang.Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT;
import static java.lang.Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
import static java.lang.Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A;
import static java.lang.Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B;

/**
 * 密码校验工具类
 */
public class PwdCheckUtil {
    /**
     * 校验密码
     * 1、长度不小于6位
     * 2、必须以字母开头
     * 3、必须包含特殊字符
     * 4、必须包含数字
     * @param pwd
     * @return 是否满足条件
     */
    public static boolean validPwd(String pwd){
        if(StringUtils.isEmpty(pwd)){
            return false;
        }
        if(pwd.length() < 6){
            return false;
        }
        if(pwd.matches("^[a-zA-z](.*)")
                && pwd.matches("(.*)[-`=\\\\\\[\\];',./~!@#$%^&*()_+|{}:\"<>?]+(.*)")
                && pwd.matches("(.*)\\d+(.*)")){
            return true;
        }
        return false;
    }
    /**
     * 校验密码
     1，不能全部是数字
     2，不能全部是字母
     3，必须是数字或字母
     4 长度要在6-16位之间
     * @param pwd
     * @return 是否满足条件
     */
    public static boolean validPwd2(String pwd){
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        if(StringUtils.isEmpty(pwd)){
            return false;
        }
        return pwd.matches(regex);
    }
    /**
     * 校验密码
     1，不能全部是数字
     2，不能全部是字母
     3，不能全部是特殊字符
     4，必须是数字、字母或特殊字符
     5 长度要在6-16位之间

     ^匹配正则的起始位置；
     $截止位置；
     \\S匹配任何非空白字符。等价于 [^ \f\n\r\t\v]；
     (?![0-9]+$)不全是数字；
     [a-zA-Z][a-zA-Z0-9\\S]必须以英文字母开始；
     +出现次数大于等于1；
     *出现次数大于0；

     *
     * @param pwd
     * @return 是否满足条件
     */
    public static boolean validPwd3(String pwd){
        String regex = "^(?![0-9]+$)(?![^0-9]+$)(?![a-zA-Z]+$)(?![^a-zA-Z]+$)(?![a-zA-Z0-9]+$)[a-zA-Z0-9\\S]{6,16}$";
        if(StringUtils.isEmpty(pwd)){
            return false;
        }
        return pwd.matches(regex);
    }

    /**
     * 必须包含数字、字母、特殊字符中的任意两种
     * @param pwd
     * @return
     */
    public static boolean validPwd4(String pwd){
        String regex = "^(?=.*[a-zA-Z0-9].*)(?=.*[a-zA-Z\\W].*)(?=.*[0-9\\W].*).{6,20}$";
        if(StringUtils.isEmpty(pwd)){
            return false;
        }
        return pwd.matches(regex);
    }

    /**
     * 获取密码复杂度等级（高、中、低）
     * @param pwd  密码
     * @return     （高、中、低）2 、1 、0
     */
    public static int getPswMeter(String pwd){
        //复杂（同时包含数字，字母，特殊符号）
        if (pwd.matches("^^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*_-]+$)(?![a-zA-z\\d]+$)(?![a-zA-z!@#$%^&*_-]+$)(?![\\d!@#$%^&*_-]+$)[a-zA-Z\\d!@#$%^&*_-]+$")){
            return 2;
        }
        //中级（包含字母和数字）
        if (pwd.matches("^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*]+$)[a-zA-Z\\d!@#$%^&*]+$")){
            return 1;
        }
        //简单（只包含数字或字母）
        if (pwd.matches("^(?:\\d+|[a-zA-Z]+|[!@#$%^&*]+)$")){
            return 0;
        }
        return 0;
    }
    /**
     *   判断EditText输入的数字、中文还是字母方法
     */
    public static void whatIsInput(Context context, EditText edInput) {
        String txt = edInput.getText().toString();

        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(txt);
        if (m.matches()) {
            Toast.makeText(context, "输入的是数字", Toast.LENGTH_SHORT).show();
        }
        p = Pattern.compile("[a-zA-Z]");
        m = p.matcher(txt);
        if (m.matches()) {
            Toast.makeText(context, "输入的是字母", Toast.LENGTH_SHORT).show();
        }
        p = Pattern.compile("[\u4e00-\u9fa5]");
        m = p.matcher(txt);
        if (m.matches()) {
            Toast.makeText(context, "输入的是汉字", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 是否含有数字
     * @param pwd
     * @return
     */
    public static boolean isDigit(String pwd){
        return  pwd.matches(".*\\d+.*");
    }
    /**
     * 判断一个字符串是否都为数字
     * @param strNum
     * @return
     */
    public static boolean isDigit2(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) strNum);
        return matcher.matches();
    }
    /**
     * 是否包含英文字母
     * @param pwd
     * @return
     */
    public static boolean isCase(String pwd){
        return  pwd.matches(".*[a-zA-Z]+.*");
    }
    /**
     * 是否都为英文字母
     * @param pwd
     * @return
     */
    public static boolean isCase2(String pwd){
        return  pwd.matches("[a-zA-Z]{1,}");
    }
    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
    /**
     * 是否含有中文字符
     * @param checkStr
     * @return
     */
    public static boolean isChina(String checkStr){
        if(!StringUtils.isEmpty(checkStr)){
            char[] checkChars = checkStr.toCharArray();
            for(int i = 0; i < checkChars.length; i++){
                char checkChar = checkChars[i];
                if(checkCharContainChinese(checkChar)){
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean checkCharContainChinese(char checkChar){
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(checkChar);
        if(CJK_UNIFIED_IDEOGRAPHS == ub || CJK_COMPATIBILITY_IDEOGRAPHS == ub
                || CJK_COMPATIBILITY_FORMS == ub || CJK_RADICALS_SUPPLEMENT == ub
                || CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A == ub
                || CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B == ub){
            return true;
        }
        return false;
    }
}
