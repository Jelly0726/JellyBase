package com.jelly.Utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 时间的工具类,方法不足可参阅{@link Calendar}或者{org.apache.commons.lang.time}包下类.
 * 
 * @author Hal
 * @version 1.0 ,before 2012-9-12
 * @see Calendar
 * @see SimpleDateFormat
 */
public class TimeUtil {

    /** 中文时间格式，年月：值为{@value} */
    public static final String DF_SIMPLE_YM = "yyyyMM";
    
    /** 中文时间格式，年月日：值为{@value} */
    public static final String DF_SIMPLE_YMD = "yyyyMMdd";
    
    /** 中文时间格式，年月日时分秒：值为{@value} */
    public static final String DF_SIMPLE_YMDHMS = "yyyyMMddHHmmss";
    
    /** 中文时间格式，年月日时分毫秒：值为{@value} */
    public static final String DF_SIMPLE_YMDHMSMS = "yyyyMMddHHmmssSSS";
    
    /** 中文时间格式，年月日时分秒：值为{@value} */
    public static final String DF_YMDHMS = "yyyy-MM-dd_HH_mm_ss";
    
    /** 中文时间格式，年-月：值为{@value} */
    public static final String DF_ZH_YM = "yyyy-MM";
    
    /** 中文时间格式，年-月-日：值为{@value} */
    public static final String DF_ZH_YMD = "yyyy-MM-dd";
    
    /** yyyy/MM/dd */
    public static final String DF_ZH_YMD1 = "yyyy/MM/dd";
    
    /** yyyy.MM.dd */
    public static final String DF_ZH_YMD2 = "yyyy.MM.dd";
    
    /** 中文时间格式，时:分:秒：值为{@value} */
    public static final String DF_ZH_HMS = "HH:mm:ss";
    
    /** 中文时间格式，年-月-日 时:分:秒： 值为{@value} */
    public static final String DF_ZH_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    
    /** yyyy/MM/dd HH:mm:ss */
    public static final String DF_ZH_YMDMHS1 = "yyyy/MM/dd HH:mm:ss";
    
    /** yyyy.MM.dd HH:mm:ss */
    public static final String DF_ZH_YMDHMS2 = "yyyy.MM.dd HH:mm:ss";
    
    /** 中文时间格式，年-月-日 时:分:秒,毫秒： 值为{@value} */
    public static final String DF_ZH_YMDHMS_SSS = "yyyy-MM-dd HH:mm:ss,SSS";
    
    /** yyyy-MM-dd HH:mm */
    public static final String DF_ZH_YMDHM = "yyyy-MM-dd HH:mm";
    
    /** yyyy/MM/dd HH:mm */
    public static final String DF_ZH_YMDHM1 = "yyyy/MM/dd HH:mm";
    
    /** yyyy.MM.dd HH:mm */
    public static final String DF_ZH_YMDHM2 = "yyyy.MM.dd HH:mm";
    
    // 值带中文用"{@value}",生成javadoc时会乱码
    /** 中文时间格式，年-月-日：值为 yyyy年MM月dd日 */
    public static final String DF_ZHCN_YMD = "yyyy年MM月dd日";
    
    /** 中文时间格式， yyyy年MM月dd日HH时mm分： 值为 yyyy年MM月dd日HH时mm分 */
    public static final String DF_ZHCN_YMDHM = "yyyy年MM月dd日HH时mm分";
    
    /** 中文时间格式， yyyy年MM月dd日HH时mm分ss秒： 值为 yyyy年MM月dd日HH时mm分ss秒 */
    public static final String DF_ZHCN_YMDHMS = "yyyy年MM月dd日HH时mm分ss秒";
    
    /** 中文时间格式， MM月dd日HH时mm分： 值为 MM月dd日HH时mm分 */
    public static final String DF_ZHCN_MDHM = "MM月dd日HH时mm分";
    
    /** 中文时间格式， MM月dd日HH时mm分： 值为 MM月dd日 */
    public static final String DF_ZHCN_MD = "MM月dd日";
    
    /**
     * 常用字符串模式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static final String[] COMMON_PATTERNS = new String[] {DF_ZH_YMD, DF_ZH_YMDHMS, DF_ZH_YMDHM, DF_ZH_YMD1, DF_ZH_YMDMHS1, DF_ZH_YMDHM1, DF_ZH_YMD2, DF_ZH_YMDHMS2, DF_ZH_YMDHM2};
    
    // *******************************字符串 转 日期对象 方法开始*************************************************
    /**
     * 将日期字符串按指定字符串{@link SimpleDateFormat} 解析成日期对象返回.
     * 
     * @see SimpleDateFormat
     * @param dateString 日期字符串,格式与dateFormat保持一致.
     * @param dateFormat 按{@link SimpleDateFormat }规范书写.
     * @return Date
     * @throws ParseException
     */
    public static Date stringToDate(String dateString, String dateFormat)
        throws ParseException {
        return new SimpleDateFormat(dateFormat).parse(dateString);
    }
    
    /**
     * 将日期字符串按{@link #DF_ZH_YMDHMS}(参见{@link SimpleDateFormat} )解析成日期对象返回.
     * 
     * @param dateString 日期字符串,格式与dateFormat保持一致.
     * @return Date
     * @throws ParseException
     * @see #stringToDate(String, String)
     */
    public static Date stringToDateYMDHMS(String dateString)
        throws ParseException {
        return stringToDate(dateString, DF_ZH_YMDHMS);
    }
    
    // *******************************日期对象 转 字符串 方法开始*************************************************
    /**
     * 将日期按指定字符串{@link SimpleDateFormat} 解析成字符串返回.
     * 
     * @param date Date日期.
     * @param dateFormat 返回字符串中的日期格式{@link SimpleDateFormat}.
     * @return String
     * 
     */
    public static String dateToString(Date date, String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(date);
    }
    
    /**
     * 将日期按{@link #DF_ZH_YMD}格式 解析成字符串返回.
     * 
     * @param date Date日期.
     * @return String
     * @see #dateToString(Date, String)
     */
    public static String dateToStringYMD(Date date) {
        return dateToString(date, DF_ZH_YMD);
    }
    
    /**
     * 将日期按{@link #DF_ZH_YMDHMS}格式 解析成字符串返回.
     * 
     * @param date Date日期.
     * @return String
     * @see #dateToString(Date, String)
     */
    public static String dateToStringYMDHMS(Date date) {
        return dateToString(date, DF_ZH_YMDHMS);
    }
    
    /**
     * 转换日期格式(将源日期格式字符串，转换为目标格式日期)
     * 
     * @param dateStr
     * @param srcFmt
     * @param targetFmt
     * @return
     * @author Hal
     * @throws ParseException
     * @date 2016年11月17日 下午4:23:55
     */
    public static String transformFormat(String dateStr, String srcFmt, String targetFmt)
        throws ParseException {
        if (null == dateStr || "".equals(dateStr)) {
            return "";
        }
        Date d = stringToDate(dateStr, srcFmt);
        return dateToString(d, targetFmt);
    }
    
    // *******************************提供简便方法,加减日期年月日、时分秒 方法开始*************************************************
    /**
     * 指定时间 年 向前(过去,{@code movNum}为负) 或 向后(未来,{@code movNum}为正) 平移{@code movNum} ,返回移动后的Date
     * 
     * @param date 指定的日期
     * @param movNum 移动的长度
     * @return Date
     */
    public static Date movDateForYear(Date date, int movNum) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(date);
        gcal.add(GregorianCalendar.YEAR, movNum);
        return gcal.getTime();
    }
    
    /**
     * 指定时间 月 向前(过去,{@code movNum}为负) 或 向后(未来,{@code movNum}为正) 平移{@code movNum} ,,返回移动后的Date
     * 
     * @param date 指定的日期
     * @param movNum 移动的长度
     * @return Date
     */
    public static Date movDateForMonth(Date date, int movNum) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(date);
        gcal.add(GregorianCalendar.MONTH, movNum);
        return gcal.getTime();
    }
    
    /**
     * 指定时间 天 向前(过去,{@code movNum}为负) 或 向后(未来,{@code movNum}为正) 平移{@code movNum} ,,返回移动后的Date
     * 
     * @param date 指定的日期
     * @param movNum 移动的长度
     * @return Date
     */
    public static Date movDateForDay(Date date, int movNum) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(date);
        gcal.add(GregorianCalendar.DATE, movNum);
        return gcal.getTime();
    }
    
    /**
     * 指定时间 小时 向前(过去,{@code movNum}为负) 或 向后(未来,{@code movNum}为正) 平移{@code movNum} ,,返回移动后的Date
     * 
     * @param date 指定的日期
     * @param movNum 移动的长度
     * @return Date
     */
    public static Date movDateForHour(Date date, int movNum) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(date);
        gcal.add(GregorianCalendar.HOUR_OF_DAY, movNum);
        return gcal.getTime();
    }
    
    /**
     * 指定时间 分钟 向前(过去,{@code movNum}为负) 或 向后(未来,{@code movNum}为正) 平移{@code movNum} ,返回移动后的Date
     * 
     * @param date 指定的日期
     * @param movNum 移动的长度
     * @return Date
     */
    public static Date movDateForMinute(Date date, int movNum) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(date);
        gcal.add(GregorianCalendar.MINUTE, movNum);
        return gcal.getTime();
    }

    /**
     * 获取当前时间之前或之后几分钟 minute
     * @param minute
     * @return
     * by hzr
     */
    public static String getTimeByMinute(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }

    /**
     * 指定时间 秒 向前(过去,{@code movNum}为负) 或 向后(未来,{@code movNum}为正) 平移{@code movNum} ,返回移动后的Date
     * 
     * @param date 指定的日期
     * @param movNum 移动的长度
     * @return Date
     */
    public static Date movDateForSecond(Date date, int movNum) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(date);
        gcal.add(GregorianCalendar.SECOND, movNum);
        return gcal.getTime();
    }
    
    // *******************************提供简便方法,计算时间间隔 方法开始*************************************************
    /**
     * 计算两个时间的时间间隔(毫秒)数.
     * 
     * @param beginDate
     * @param endDate
     * @return milliSeconds
     */
    public static long countMilliSeconds(Date beginDate, Date endDate) {
        long ms = beginDate.getTime() - endDate.getTime();
        
        // 若为负数,转正
        if (ms < 0) {
            ms = (-1) * ms;
        }
        return ms;
    }
    
    /**
     * 计算两个时间的时间间隔(秒)数.
     * 
     * @param beginDate
     * @param endDate
     * @return Second
     */
    public static long countSeconds(Date beginDate, Date endDate) {
        long ms = countMilliSeconds(beginDate, endDate);
        
        return ms / 1000;
    }
    
    /**
     * 计算两个时间的时间间隔(分钟)数.
     * 
     * @param beginDate
     * @param endDate
     * @return Minute
     */
    public static long countMinutes(Date beginDate, Date endDate) {
        long ms = countSeconds(beginDate, endDate);
        
        return ms / 60;
    }
    
    /**
     * 计算两个时间的时间间隔(小时)数.
     * 
     * @param beginDate
     * @param endDate
     * @return Hour
     */
    public static long countHours(Date beginDate, Date endDate) {
        long ms = countMinutes(beginDate, endDate);
        
        return ms / 60;
    }
    
    /**
     * 计算两个时间的时间间隔(天)数.
     * 
     * @param beginDate
     * @param endDate
     * @return Day
     */
    public static long countDays(Date beginDate, Date endDate) {
        long ms = countHours(beginDate, endDate);
        
        return ms / 24;
    }
    
    /**
     * 计算两个时间的时间间隔(天)数.<br/>
     * 忽略时间(时分秒)<br/>
     * 
     * @param beginDate
     * @param endDate
     * @return Day
     * @throws ParseException
     */
    public static long countDaysIgnoreTime(Date beginDate, Date endDate)
        throws ParseException {
        return countDays(stringToDate(dateToString(beginDate, DF_ZH_YMD), DF_ZH_YMD), stringToDate(dateToString(endDate, DF_ZH_YMD), DF_ZH_YMD));
    }
    
    /**
     * 计算两时间段内耗时，并格式化成：dd天HH时mm分ss秒返回
     * 
     * @param begin 开始时间
     * @param end 结束时间
     * @return String
     */
    public static String parseUseTime(Date begin, Date end) {
        if (begin != null && end != null) {
            long ms = countMilliSeconds(begin, end);
            return parseUseTime(ms);
        } else {
            return "";
        }
    }
    
    /**
     * 通过指定耗时（毫秒）格式化成：dd天HH时mm分ss秒
     * 
     * @param useTime
     * @return String
     */
    public static String parseUseTime(long useTime) {
        long s = 1000;
        long m = s * 60;
        long h = m * 60;
        long d = h * 24;
        String dd = Long.toString(useTime / d);
        String HH = Long.toString((useTime % d) / h);
        String mm = Long.toString(((useTime % d) % h) / m);
        String ss = Long.toString((((useTime % d) % h) % m) / s);
        return dd + "天" + HH + "时" + mm + "分" + ss + "秒";
    }

    
    // *******************************提供简便方法,获取常用格式日期字符串 方法开始*************************************************
    /**
     * 以 {@link #DF_ZH_HMS}格式返回现在时间字符串格式.
     * 
     * @return String
     * @see #dateToString(Date, String)
     */
    public static String getNowTime() {
        return getNowString(DF_ZH_HMS);
    }
    
    /**
     * 以 {@link #DF_ZH_YMDHMS}格式返回现在时间日期字符串格式.
     * 
     * @return String
     * @see #dateToString(Date, String)
     */
    public static String getNowDateTime() {
        return getNowString(DF_ZH_YMDHMS);
    }
    
    /**
     * 以 {@link #DF_ZHCN_YMDHMS}格式返回现在时间日期字符串格式.
     * 
     * @return String
     * @see #dateToString(Date, String)
     */
    public static String getNowDateTimeZhCN() {
        return getNowString(DF_ZHCN_YMDHMS);
    }
    
    /**
     * 获取现在时间字符串.
     * 
     * @param format 指定字符串格式
     * @return
     * @author Hal
     * @date 2016年11月17日 下午4:35:16
     */
    public static String getNowString(String format) {
        return dateToString(getNowDate(), format);
    }
    
    /**
     * 
     * 获取当前时间对象.
     * 
     * @modify : liuh 2017年1月16日
     * @return java.util.Date
     */
    public static Date getNowDate(){
        return new Date();
    }
    
    // ************获取Date对象中的年、月、日、时、分、秒、毫秒,不调用Date对象中要删除的方法 开始**************
    /**
     * 获取{@code d}中的年.
     * 
     * @param d 时间
     * @return 返回 {@code d}中的年
     */
    public static int getDateYear(Date d) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(d);
        return cal.get(Calendar.YEAR);
    }
    
    /**
     * 获取{@code d}中的月份.
     * 
     * @param d 时间
     * @return 返回 {@code d}中的月份
     */
    public static int getDateMonth(Date d) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(d);
        return cal.get(Calendar.MONTH) + 1;
    }
    
    /**
     * 获取{@code d}为星期几（星期一为第一天）.
     * 
     * @param d
     * @return
     */
    public static int getDateWeek(Date d) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        
        cal.setTime(d);
        
        int idx = cal.get(Calendar.DAY_OF_WEEK);
        /**
         * 默认取值 : SUNDAY = 1 MONDAY = 2 TUESDAY = 3 WEDNESDAY = 4 THURSDAY = 5 FRIDAY = 6 SATURDAY = 7 我们需要将之替换成习惯 1=星期一... 故作如下处理
         */
        
        if (idx == 1) {
            idx = 7;
        } else {
            idx--;
        }
        
        return idx;
    }
    
    /**
     * 获取{@code d}中的日期.
     * 
     * @param d 时间
     * @return 返回 {@code d}中的日期
     */
    public static int getDateDay(Date d) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(d);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * 获取{@code d}中的小时.
     * 
     * @param d 时间
     * @return 返回 {@code d}中的小时
     */
    public static int getDateHour(Date d) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(d);
        return cal.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * 获取{@code d}中的分钟.
     * 
     * @param d 时间
     * @return 返回 {@code d}中的分钟
     */
    public static int getDateMinute(Date d) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(d);
        return cal.get(Calendar.MINUTE);
    }
    
    /**
     * 获取{@code d}中的秒.
     * 
     * @param d 时间
     * @return 返回 {@code d}中的秒
     */
    public static int getDateSecond(Date d) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(d);
        return cal.get(Calendar.SECOND);
    }
    
    /**
     * 将小时数换算成返回以毫秒为单位的时间
     * 
     * @param hours 小时数
     * @return long
     */
    public static long getMicroSec(BigDecimal hours) {
        BigDecimal bd = hours.multiply(new BigDecimal(3600 * 1000));
        return bd.longValue();
    }
    
    /**
     * 获得当前月份的天数
     * 
     * @return int
     */
    public static int getCurentMonthDays() {
        return getMonthDays(getNowDate());
    }
    
    /**
     * 获得{@code d}所在月份的天数
     * 
     * @param d 日期
     * @return int
     */
    public static int getMonthDays(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(getNowDate());
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * 获取星期第一天日期对象.
     * 
     * @param date
     * @return Date
     */
    public static Date getWeekFirstDay(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        // 设置一个星期的第一天为星期1，默认是星期日
        cal.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        
        cal.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
        
        return cal.getTime();
    }
    
    /**
     * 获取该月第一天日期对象.
     * 
     * @param date
     * @return Date
     */
    public static Date getMonthFirstDay(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        // 设置一个星期的第一天为星期1，默认是星期日
        cal.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        
        cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        
        return cal.getTime();
    }
    
    /**
     * 通过日期字符串获取日期格式标识符 如："2013-04-11" 返回 "yyyy-MM-dd"
     * 
     * @param dataStr
     * @return String
     */
    public static String getDateFormat(String dataStr) {
        // 简单算法解决几个常用的格式转换
        String spDay = "-?";
        String spTime = ":?";
        String sp = "\\s";
        String yyyy = "\\d{4}";
        String MM = "\\d{1,2}";
        String dd = "\\d{1,2}";
        String HH = "\\d{1,2}";
        String mm = "\\d{1,2}";
        String ss = "\\d{1,2}";
        if (Pattern.matches(yyyy + spDay + MM, dataStr)) {
            return "yyyy-MM";
        } else if (Pattern.matches(yyyy + spDay + MM + spDay + dd, dataStr)) {
            return "yyyy-MM-dd";
        } else if (Pattern.matches(yyyy + spDay + MM + spDay + dd + sp + HH + spTime + mm, dataStr)) {
            return "yyyy-MM-dd HH:mm";
        } else if (Pattern.matches(yyyy + spDay + MM + spDay + dd + sp + HH + spTime + mm + spTime + ss, dataStr)) {
            return "yyyy-MM-dd HH:mm:ss";
        } else if (Pattern.matches(HH + spTime + mm + spTime + ss, dataStr)) {
            return "HH:mm:ss";
        } else if (Pattern.matches(HH + spTime + mm, dataStr)) {
            return "HH:mm";
        }
        return null;
    }
    
    /**
     * 获取当天0时0分0秒0毫秒的日期对象.
     * 
     * @param date
     * @return
     */
    public static Date getZeroOfDay(Date date) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        cd.set(Calendar.HOUR_OF_DAY, 0);
        cd.set(Calendar.MINUTE, 0);
        cd.set(Calendar.SECOND, 0);
        cd.set(Calendar.MILLISECOND, 0);
        
        long zero = cd.getTimeInMillis();
        
        return new Date(zero);
    }
    
    /**
     * 获取当天23时59分59秒999毫秒的日期对象.
     * 
     * @param date
     * @return
     */
    public static Date getLastMillisecondsOfDay(Date date) {
        Date zero = getZeroOfDay(date);
        
        // (1000*3600*24)=86400000 一天的毫秒数 -1 = 86399999
        long last = zero.getTime() + 86399999;
        
        return new Date(last);
    }
    
    /**
     * 通过月份序号，获取月份英文名.
     * 
     * @param idx 月份序号,参数仅支持1-12
     * @return
     */
    public static String getMonthName_EN(int idx) {
        String[] names = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return names[idx - 1];
    }
    
    /**
     * 通过月份序号，获取月份中文名.
     * 
     * @param idx 月份序号,参数仅支持1-12
     * @return
     */
    public static String getMonthName_ZHCN(int idx) {
        String[] names = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
        return names[idx - 1];
    }
    
    /**
     * 通过星期序号，获取星期英文名.
     * 
     * @param idx 星期序号,参数仅支持1-7
     * @return
     */
    public static String getWeekName_EN(int idx) {
        String[] names = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        return names[idx - 1];
    }
    
    /**
     * 通过星期序号，获取星期中文名.
     * 
     * @param idx 星期序号,参数仅支持1-7
     * @return
     */
    public static String getWeekName_ZHCN(int idx) {
        String[] names = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};
        return names[idx - 1];
    }
    
    /**
     * 过滤List里面的时间对象，并转换为指定格式的时间字符串
     * 
     * @param list 要处理的list对象
     * @param dateFormat 需要转换的日期格式
     */
    @SuppressWarnings("rawtypes")
    public static List convertListDateToString(List<Map<String, Object>> list, String dateFormat) {
        if (list == null)
            return new ArrayList();
        ;
        if (dateFormat == null || "".equals(dateFormat)) {
            dateFormat = DF_ZH_YMDHMS;
        }
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> row = list.get(i);
            for (Map.Entry<String, Object> col : row.entrySet()) {
                if (col.getValue() instanceof Date) {
                    String dateStr = TimeUtil.dateToString((Date)col.getValue(), dateFormat);
                    col.setValue(dateStr);
                }
            }
        }
        return list;
    }


    public static Date DateFormatDateYMD(Date date){

        SimpleDateFormat sdf=new SimpleDateFormat(TimeUtil.DF_ZH_YMD);
        String stringDate  =  TimeUtil.dateToString(date, TimeUtil.DF_ZH_YMD);
        try {
            return   sdf.parse(stringDate);
        } catch (ParseException e) {
            return  null;
        }
    }

    /**
     * 获取指定格式的日期
     * @param dateType
     * @return
     */
    public static String getFormatTime(String dateType){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(dateType);
        return sdf.format(date);
    }

    /**
     * @param oldTime 给的时间
     * @param newTime 当前时间
     * @return 1 ：比今天时间更大  -1  老时间比今天时间更小
     * @throws ParseException 转换异常
     */
    public static int isYeaterday(Date oldTime,Date newTime) throws ParseException{
        if(newTime==null){
            newTime=new Date();
        }
        //将下面的 理解成  yyyy-MM-dd HH:mm:ss 更好理解点
        SimpleDateFormat format = new SimpleDateFormat(DF_ZH_YMDHMS);
        String todayStr = format.format(newTime);
        Date today = format.parse(todayStr);
        String oldDayStr = format.format(oldTime);
        Date oldDay = format.parse(oldDayStr);
        // 86400000=24*60*60*1000 五天
        if((today.getTime()-oldDay.getTime())>0) {//大于于今天
            return 1;
        } else { //小于今天
            return -1;
        }
    }

    /**
     * 得到几天后的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)+day);
        return now.getTime();
    }

    /**
     * 时间格式转化
     * @param date
     * @param oldPattern
     * @param newPattern
     * @return
     * create by hzr
     */
    public final String StringPattern(String date, String oldPattern, String newPattern) {
        if (date == null || oldPattern == null || newPattern == null)
            return "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(oldPattern);        // 实例化模板对象
        SimpleDateFormat sdf2 = new SimpleDateFormat(newPattern);        // 实例化模板对象
        Date d = null ;
        try{
            d = sdf1.parse(date) ;   // 将给定的字符串中的日期提取出来
        }catch(Exception e){            // 如果提供的字符串格式有错误，则进行异常处理
            e.printStackTrace() ;       // 打印异常信息
        }
        return sdf2.format(d);
    }

    /**
     * 将时间转换为时间戳
     * @param s
     * @return
     * @throws ParseException
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /**
     * 将时间戳转换为时间
     * @param s
     * @return
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 获取当前时间之前或之后几小时 hour
     * @param hour
     * @return
     */
    public static String getTimeByHour(int hour) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hour);

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());

    }

    /**
     * @param oldTime 给的时间
     * @param newTime 当前时间
     * @return 1 ：比今天时间更大  -1  老时间比今天时间更小
     * @throws ParseException 转换异常
     */
    public static int isEqual(Date oldTime,Date newTime) throws ParseException{
        if(newTime==null){
            newTime=new Date();
        }
        //将下面的 理解成  yyyy-MM-dd HH:mm:ss 更好理解点
        SimpleDateFormat format = new SimpleDateFormat(DF_ZH_YMD);
        String todayStr = format.format(newTime);
        Date today = format.parse(todayStr);
        String oldDayStr = format.format(oldTime);
        Date oldDay = format.parse(oldDayStr);
        // 86400000=24*60*60*1000 五天
        if((today.getTime()-oldDay.getTime())>0) {//大于于今天
            return 1;
        } else if ((today.getTime()-oldDay.getTime()) == 0){ //小于今天
            return 0;
        }else {
            return -1;
        }
    }

    public boolean compare(String time1,String time2) throws ParseException {
        //如果想比较日期则写成"yyyy-MM-dd"就可以了
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //将字符串形式的时间转化为Date类型的时间
        Date a=sdf.parse(time1);
        Date b=sdf.parse(time2);
        //Date类的一个方法，如果a早于b返回true，否则返回false
        if(a.before(b))
            return true;
        else
            return false;
		/*
		 * 如果你不喜欢用上面这个太流氓的方法，也可以根据将Date转换成毫秒
		if(a.getTime()-b.getTime()<0)
			return true;
		else
			return false;
		*/
    }

    public static void main(String[] args) throws ParseException {
//        Date d1 = new Date(System.currentTimeMillis()-1000);
//        Date d2 = new Date(System.currentTimeMillis());
//        System.out.println(d1.compareTo(d2));
        String beginWorkDate = "8:00:00";//上班时间
        String endWorkDate = "12:00:00";//下班时间
        SimpleDateFormat sdf = new SimpleDateFormat(TimeUtil.DF_ZH_HMS);
        long a = TimeUtil.countMinutes(sdf.parse(beginWorkDate),sdf.parse(endWorkDate));
        System.out.println(a/30);

        String beginRestDate = "8:30:00";//休息时间
        String endRestDate = "9:00:00";
        long e = TimeUtil.countMinutes(sdf.parse(beginWorkDate),sdf.parse(beginRestDate));
        long f = TimeUtil.countMinutes(sdf.parse(beginWorkDate),sdf.parse(endRestDate));

        String beginVactionDate = "10:00:00";//请假时间
        String endVactionDate = "11:00:00";
        long b= TimeUtil.countMinutes(sdf.parse(beginWorkDate),sdf.parse(beginVactionDate));
        long c= TimeUtil.countMinutes(sdf.parse(beginWorkDate),sdf.parse(endVactionDate));

        String time = "9:00:00";//被别人预约的
        long d = TimeUtil.countMinutes(sdf.parse(beginWorkDate),sdf.parse(time));
        List<Integer> num = new ArrayList<>();
        num.add((int) (d/30));

        String myTime = "11:30:00";//我约的
        long g = TimeUtil.countMinutes(sdf.parse(beginWorkDate),sdf.parse(myTime));
        List<Integer> mynum = new ArrayList<>();
        mynum.add((int) (g/30));


        List<Date> yes = new ArrayList<>();//能约
        List<Date> no = new ArrayList<>();//不能约
        List<Date> my = new ArrayList<>();//我约的
        for (int i = 0; i < a/30; i ++) {
            Date times = TimeUtil.movDateForMinute(sdf.parse(beginWorkDate), i * 30);
            System.out.println(sdf.format(times));
            yes.add(times);
            if ((i >= e / 30 && i < f / 30) || (i >= b / 30 && i < c / 30)) {
                //休息时间,请假时间
                no.add(times);
                yes.remove(times);
                continue;
            }
            for (Integer n : num) {
                if (i == n) {
                    //被预约时间
                    no.add(times);
                    yes.remove(times);
                }
            }
            for (Integer n : mynum) {
                if (i == n) {
                    //被预约时间
                    my.add(times);
                }
            }
        }
    }
}
