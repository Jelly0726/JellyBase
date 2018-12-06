package com.base.Utils;

import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MyDate {
	private MyDate(){}
	private static final String TAG = "MyDate";
	/**
	 * 时间格式：yyyy年MM月dd日
	 */
	public static final String DATE_FORMAT = "yyyy年MM月dd日";
	/**
	 * 时间格式：yyyy年MM月dd日HH时mm分ss秒
	 */
	public static final String DATE_FORMAT2 = "yyyy年MM月dd日HH时mm分ss秒";
	/**
	 * 时间格式：yyyy年MM月dd日HH时mm分
	 */
	public static final String DATE_FORMAT3 = "yyyy年MM月dd日HH时mm分";
	/**
	 * 时间格式：yyyy年MM月dd日HH时
	 */
	public static final String DATE_FORMAT4 = "yyyy年MM月dd日HH时";
	/**
	 * 时间格式：HH时mm分ss秒
	 */
	public static final String DATE_FORMAT5 = "HH时mm分ss秒";
	/**
	 * 时间格式：HH时mm分
	 */
	public static final String DATE_FORMAT6 = "HH时mm分";
	/**
	 * 时间格式：mm分ss秒
	 */
	public static final String DATE_FORMAT7 = "mm分ss秒";
	/**
	 * 时间格式：yyyy年MM月
	 */
	public static final String DATE_FORMAT8 = "yyyy年MM月";
	/**
	 * 时间格式：yyyy-MM-dd HH:mm:ss
	 */
	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 时间格式：yyyy-MM-dd EEE HH:mm
	 */
	public static final String TIME_FORMAT0 = "yyyy-MM-dd EEE HH:mm";
	/**
	 * 时间格式：yyyy-MM-dd EEE
	 */
	public static final String TIME_FORMAT1 = "yyyy-MM-dd EEE";
	/**
	 * 时间格式：yyyy-MM-dd HH:mm
	 */
	public static final String TIME_FORMAT2 = "yyyy-MM-dd HH:mm";
	/**
	 * 时间格式：yyyy-MM-dd HH
	 */
	public static final String TIME_FORMAT3 = "yyyy-MM-dd HH";
	/**
	 * 时间格式：yyyy-MM-dd
	 */
	public static final String TIME_FORMAT4 = "yyyy-MM-dd";
	/**
	 * 时间格式：HH:mm:ss
	 */
	public static final String TIME_FORMAT5 = "HH:mm:ss";
	/**
	 * 时间格式：HH:mm
	 */
	public static final String TIME_FORMAT6 = "HH:mm";
	/**
	 * 时间格式：mm:ss
	 */
	public static final String TIME_FORMAT7 = "mm:ss";
	/**
	 * 时间格式：yyyy-MM
	 */
	public static final String TIME_FORMAT8 = "yyyy-MM";
	/**
	 * 在之前
	 */
	public static final int TIME_BEFORE = 1;

	/**
	 * 在中间
	 */
	public static final int TIME_ING = 2;

	/**
	 * 在之后
	 */
	public static final int TIME_AFTER = 3;

	/**
	 * 异常
	 */
	public static final int TIME_ERROR = -1;
	/**
	 * 根据年 月 获取对应的月份 天数
	 * */
	public static int getDaysByYearMonth(int year, int month) {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month-1);
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 将字符串转为 yyyy-MM-dd EEE HH:mm
	 * @param time
	 * @return
	 */
	public static String getTimeToStamp(String time){
		SimpleDateFormat sdf =   new SimpleDateFormat(TIME_FORMAT2);
		try {
			Date date = sdf.parse(time);
			sdf = new SimpleDateFormat( TIME_FORMAT0 );
			return sdf.format(date);
		}catch (Exception e){
			e.printStackTrace();
		}
		return time;
	}
	/**
	 * 将字符串转为 yyyy-MM-dd EEE
	 * @param time
	 * @return
	 */
	public static String getTimeToStampDa(String time){
		SimpleDateFormat sdf =   new SimpleDateFormat( TIME_FORMAT4 );
		try {
			Date date = sdf.parse(time);
			sdf = new SimpleDateFormat( TIME_FORMAT1);
			return sdf.format(date);
		}catch (Exception e){
			e.printStackTrace();
		}
		return time;
	}
	/**
	 * 将字符串转为 yyyy
	 * @param time
	 * @return
	 */
	public static int getTimeToStampY(String time){
		SimpleDateFormat sdf =   new SimpleDateFormat( TIME_FORMAT2 );
		try {
			Date date = sdf.parse(time);
			sdf = new SimpleDateFormat( "yyyy" );
			return Integer.parseInt(sdf.format(date));
		}catch (Exception e){
			e.printStackTrace();
		}
		return 1;
	}
	/**
	 * 将字符串转为MM
	 * @param time
	 * @return
	 */
	public static int getTimeToStampM(String time){
		SimpleDateFormat sdf =   new SimpleDateFormat( TIME_FORMAT2 );
		try {
			Date date = sdf.parse(time);
			sdf = new SimpleDateFormat( "MM" );
			return Integer.parseInt(sdf.format(date));
		}catch (Exception e){
			e.printStackTrace();
		}
		return 1;
	}
	/**
	 * 将字符串转为 dd
	 * @param time
	 * @return
	 */
	public static int getTimeToStampD(String time){
		SimpleDateFormat sdf =   new SimpleDateFormat( TIME_FORMAT2);
		try {
			Date date = sdf.parse(time);
			sdf = new SimpleDateFormat( "dd" );
			return Integer.parseInt(sdf.format(date));
		}catch (Exception e){
			e.printStackTrace();
		}
		return 1;
	}
	/**
	 * 将字符串转为 HH
	 * @param time
	 * @return
	 */
	public static int getTimeToStampH(String time){
		SimpleDateFormat sdf =   new SimpleDateFormat( TIME_FORMAT2);
		try {
			Date date = sdf.parse(time);
			sdf = new SimpleDateFormat( "HH" );
			return Integer.parseInt(sdf.format(date));
		}catch (Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 将字符串转为 mm
	 * @param time
	 * @return
	 */
	public static int getTimeToStampMin(String time){
		SimpleDateFormat sdf =   new SimpleDateFormat(TIME_FORMAT2);
		try {
			Date date = sdf.parse(time);
			sdf = new SimpleDateFormat( "mm" );
			return Integer.parseInt(sdf.format(date));
		}catch (Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 获取当前年
	 * @return
	 */
	public static int getYear(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR);
	}
	/**
	 * 获取当前月
	 * @return
	 */
	public static int getMonth(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.MONTH)+1;
	}
	/**
	 * 获取当前天
	 * @return
	 */
	public static int getDay(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_MONTH);
	}
	/**
	 * 获取当前时(24)
	 * @return
	 */
	public static int getHour(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.HOUR_OF_DAY);
	}
	/**
	 * 获取当前分
	 * @return
	 */
	public static int getMinute(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.MINUTE);
	}
	/**
	 * 获取当前秒
	 * @return
	 */
	public static int getSecond(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.SECOND);
	}
	/**
	 * 获取当前毫秒
	 * @return
	 */
	public static int getMilliSecond(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.MILLISECOND);
	}

	/**
	 * 获取中文格式 年月日时分秒
	 * @return  {@link MyDate#DATE_FORMAT2}
	 */
	public static String getDateCN() {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT2);
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;// 2012年10月03日 23:41:31
	}

	/**
	 *  获取英文格式 年月日时分秒
	 * @return {@link MyDate#TIME_FORMAT}
	 */
	public static String getDateEN() {
		SimpleDateFormat format1 = new SimpleDateFormat(TIME_FORMAT);
		String date1 = format1.format(new Date(System.currentTimeMillis()));
		return date1;// 2012-10-03 23:41:31
	}

	/**
	 *  获取英文格式 时分
	 * @return  {@link MyDate#TIME_FORMAT6}
	 */
	public static String getDate() {
		SimpleDateFormat format2 = new SimpleDateFormat(TIME_FORMAT6);
		String date = format2.format(new Date(System.currentTimeMillis()));
		return date;
	}

	/**
	 *  获取英文格式 时分秒
	 * @return {@link MyDate#TIME_FORMAT5}
	 */
	public static String getDatemiao() {
		SimpleDateFormat format2 = new SimpleDateFormat(TIME_FORMAT5);
		String date = format2.format(new Date(System.currentTimeMillis()));
		return date;
	}
	/**
	 *  获取英文格式 年月日
	 * @return {@link MyDate#TIME_FORMAT4}
	 */
	public static String getDateDaEN() {
		SimpleDateFormat format3 = new SimpleDateFormat(TIME_FORMAT4);
		String date = format3.format(new Date(System.currentTimeMillis()));
		return date;//2012-10-03
	}
	/**
	 *  获取英文格式 年月
	 * @return {@link MyDate#TIME_FORMAT8}
	 */
	public static String getDateMonthEN() {
		SimpleDateFormat format3 = new SimpleDateFormat(TIME_FORMAT8);
		String date = format3.format(new Date(System.currentTimeMillis()));
		return date;//2012-10
	}
	/**
	 *  获取中文格式 年月
	 * @return {@link MyDate#DATE_FORMAT8}
	 */
	public static String getDateMonthCN() {
		SimpleDateFormat format3 = new SimpleDateFormat(DATE_FORMAT8);
		String date = format3.format(new Date(System.currentTimeMillis()));
		return date;//2012年10月
	}
	/**
	 *  获取中文格式 年月日
	 * @return {@link MyDate#DATE_FORMAT}
	 */
	public static String getDateDaCN() {
		SimpleDateFormat format4 = new SimpleDateFormat(DATE_FORMAT);
		String date = format4.format(new Date(System.currentTimeMillis()));
		return date;//2012年10月03日
	}
	/**
	 *  获取英文格式 年月日
	 *  @param da  把日期往后增加一天.整数往后推,负数往前移动
	 * @return {@link MyDate#TIME_FORMAT4}
	 */
	public static String getDateDaEN(int da){
		Date date=new Date();//取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE,da);//把日期往后增加一天.整数往后推,负数往前移动
		date=calendar.getTime(); //这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT4);
		String dateString = formatter.format(date);
		return dateString;
	}
	/**
	 *  获取中文格式 年月日
	 *  @param da  把日期往后增加一天.整数往后推,负数往前移动
	 * @return {@link MyDate#DATE_FORMAT}
	 */
	public static String getDateDaCN(int da){
		Date date=new Date();//取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE,da);//把日期往后增加一天.整数往后推,负数往前移动
		date=calendar.getTime(); //这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		String dateString = formatter.format(date);
		return dateString;
	}
	/* 将字符串转为时间戳 */
	public static String getTimeToStampCN(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT2,
				Locale.CHINA);
		Date date = new Date();
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String tmptime = String.valueOf(date.getTime()).substring(0, 10);

		return tmptime;
	}
	/* 将字符串转为时间戳 */
	public static String getTimeToStampEN(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT,
				Locale.CHINA);
		Date date = new Date();
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String tmptime = String.valueOf(date.getTime()).substring(0, 10);

		return tmptime;
	}

	/**
	 *  整数(秒数)转换为时分秒格式
	 代码如下:
	 */
	// a integer to xx:xx:xx
	public static String secToTimeEN(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
			}
		}
		return timeStr;
	}
	/**
	 *  整数(秒数)转换为时分秒格式
	 代码如下:
	 */
	// a integer to xx:xx:xx
	public static String secToTimeCN(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00分00秒";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = unitFormat(minute) + "分"+ unitFormat(second)+ "秒";
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99时59分59秒";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + "时" + unitFormat(minute) + "分" + unitFormat(second)+ "秒" ;
			}
		}
		return timeStr;
	}
	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}
	/**
	 * string型时间转换
	 *
	 * @param timeFormat 时间格式
	 * @param timestamp  时间
	 * @return 刚刚  x分钟  小时前  ...
	 */
	public static String convertTime(String timeFormat, String timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
		try {
			return convertTime(sdf.parse(timestamp).getTime());
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.getMessage());
			return null;
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage());
		}

		return timestamp;
	}

	/**
	 * string型时间转换
	 *
	 * @param timeFormat 时间格式
	 * @param timestamp  时间
	 * @return 刚刚  x分钟  小时前  ...
	 */
	public static String convertTime(String timeFormat, long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
		try {
			Date date = new Date();
			date.setTime(timestamp);
			return sdf.format(date);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.getMessage());
			return "";
		}
	}

	/**
	 * int型时间转换
	 *
	 * @param timestamp 时间
	 * @return 刚刚  x分钟  一天内  ...
	 */
	public static String convertTime(long timestamp) {
		String timeStr = null;

		long interval = (System.currentTimeMillis() - timestamp) / 1000;
		if (interval <= 60) { //1分钟内 服务端的时间 可能和本地的有区别 所以小于0的 对于这个情况全部都显示刚刚
			timeStr = "刚刚";
		} else if (interval < 60 * 60) { // 1小时内
			timeStr = (interval / 60 == 0 ? 1 : interval / 60) + "分钟前";
		} else if (interval < 24 * 60 * 60) { // 一天内
			timeStr = (interval / 60 * 60 == 0 ? 1 : interval / (60 * 60)) + "小时前";
		} else if (interval < 30 * 24 * 60 * 60) { // 天前
			timeStr = (interval / 24 * 60 * 60 == 0 ? 1 : interval / (24 * 60 * 60)) + "天前";
		} else if (interval < 12 * 30 * 24 * 60 * 60) { // 月前
			timeStr = (interval / 30 * 24 * 60 * 60 == 0 ? 1 : interval / (30 * 24 * 60 * 60)) + "月前";
		} else if (interval < 12 * 30 * 24 * 60 * 60) { // 年前
			timeStr = (interval / 12 * 30 * 24 * 60 * 60 == 0 ? 1 : interval / (12 * 30 * 24 * 60 * 60)) + "年前";
		} else {
			Date date = new Date();
			date.setTime(timestamp);
			timeStr = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date);
		}

		return timeStr;
	}

	/**
	 * int型时间转换 比较距离结束
	 * @param timestamp 时间
	 * @return 刚刚  x分钟  一天后  ...
	 */
	public static String convertEndTime(long timestamp) {
		String timeStr = null;

		long interval = (timestamp - System.currentTimeMillis()) / 1000;
		if (interval <= 60) { //1分钟内 服务端的时间 可能和本地的有区别 所以小于0的 对于这个情况全部都显示刚刚
			timeStr = "刚刚";
		} else if (interval < 60 * 60) { // 1小时内
			timeStr = (interval / 60 == 0 ? 1 : interval / 60) + "分钟后";
		} else if (interval < 24 * 60 * 60) { // 一天内
			timeStr = (interval / 60 * 60 == 0 ? 1 : interval / (60 * 60)) + "小时后";
		} else if (interval < 30 * 24 * 60 * 60) { // 天前
			timeStr = (interval / 24 * 60 * 60 == 0 ? 1 : interval / (24 * 60 * 60)) + "天后";
		} else if (interval < 12 * 30 * 24 * 60 * 60) { // 月前
			timeStr = (interval / 30 * 24 * 60 * 60 == 0 ? 1 : interval / (30 * 24 * 60 * 60)) + "月后";
		} else if (interval < 12 * 30 * 24 * 60 * 60) { // 年前
			timeStr = (interval / 12 * 30 * 24 * 60 * 60 == 0 ? 1 : interval / (12 * 30 * 24 * 60 * 60)) + "年后";
		} else {
			Date date = new Date();
			date.setTime(interval);
			timeStr = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date);
		}

		return timeStr;
	}
	/**
	 * 将Calendar型时间转为固定格式的时间字符串
	 * @param timeformat 时间格式
	 * @param calendar   时间
	 * @return timeformat
	 */
	public static String convertToTime(String timeformat, Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat(timeformat, Locale.getDefault());
		return sdf.format(calendar.getTime());
	}
	/**
	 * 将long型时间转为固定格式的时间字符串
	 *
	 * @param longTime 时间
	 * @return {@link MyDate#TIME_FORMAT}
	 */
	public static String convertToTime(long longTime) {
		return convertToTime(TIME_FORMAT, longTime);
	}

	/**
	 * 将long型时间转为固定格式的时间字符串
	 *
	 * @param timeformat 时间格式
	 * @param longTime   时间
	 * @return timeformat
	 */
	public static String convertToTime(String timeformat, long longTime) {
		return timeStamp2Date(longTime+"",timeformat);
	}

	/**
	 * 将long型时间转为固定格式的日期字符串
	 *
	 * @param longTime 时间
	 * @return {@link MyDate#DATE_FORMAT}
	 */
	public static String convertToDate(long longTime) {
		return timeStamp2Date(longTime+"",DATE_FORMAT);
	}

	/**
	 * 时间戳转换成日期格式字符串
	 * @param seconds 精确到秒的字符串
	 * @param format 返回的时间格式  默认{@link MyDate#TIME_FORMAT}
	 * @return
	 */
	public static String timeStamp2Date(String seconds,String format) {
		if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
			return "";
		}
		if(format == null || format.isEmpty()){
			format = TIME_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if(seconds.length()==10) {
			return sdf.format(new Date(Long.valueOf(seconds + "000")));
		}else if(seconds.length()==13) {
			return sdf.format(new Date(Long.valueOf(seconds)));
		}else {
			return "";
		}
	}
	/**
	 * long型时间转换 2013年7月3日 18:05(星期三)格式
	 *
	 * @param longTime 长整型时间
	 * @return 2013年7月3日 18:05(星期三)
	 */
	public static String convertDayOfWeek(long longTime) {
		final String format = "%d年%d月%d日 %s:%s(%s)";

		Calendar c = Calendar.getInstance(); // 日历实例
		c.setTime(new Date(longTime));

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int date = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		String h = hour > 9 ? String.valueOf(hour) : "0" + hour;
		int minute = c.get(Calendar.MINUTE);
		String m = minute > 9 ? String.valueOf(minute) : "0" + minute;
		return String.format(Locale.getDefault(), format, year, month + 1, date, h, m, converToWeek(c.get(Calendar.DAY_OF_WEEK)));
	}

	/**
	 * 转换数字的星期为字符串的星期
	 *
	 * @param w
	 * @return 星期x
	 */
	private static String converToWeek(int w) {
		String week = null;

		switch (w) {
			case 1:
				week = "星期日";
				break;
			case 2:
				week = "星期一";
				break;
			case 3:
				week = "星期二";
				break;
			case 4:
				week = "星期三";
				break;
			case 5:
				week = "星期四";
				break;
			case 6:
				week = "星期五";
				break;
			case 7:
				week = "星期六";
				break;
		}

		return week;
	}

	/**
	 * 计算时间是否在区间内
	 *
	 * @param time  time
	 * @param time1 time
	 * @param time2 time
	 * @return {@link MyDate#TIME_BEFORE}{@link MyDate#TIME_ING}{@link MyDate#TIME_AFTER}
	 */
	public static int betweenTime(long time, long time1, long time2) {
		if ((time+"").length()==10){
			time=Long.parseLong(time+"000");
		}
		if ((time1+"").length()==10){
			time1=Long.parseLong(time1+"000");
		}
		if ((time2+"").length()==10){
			time2=Long.parseLong(time2+"000");
		}
		if (time1 > time2) {  //时间1大
			long testTime = time1;
			time1 = time2;
			time2 = testTime;
		}

		//已经过去
		if (time1 > time) {
			return TIME_BEFORE;
		} else if (time2 < time) {
			return TIME_AFTER;
		} else {
			return TIME_ING;
		}
	}

	/**
	 *  将"yyyy-MM-dd HH:mm:ss"格式的时间转为天、时、分、秒形式
	 * @param time  "yyyy-MM-dd HH:mm:ss"格式的时间
	 * @return  天、时、分、秒
	 */
	public static String computeTimeDifference(String time) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date date = df.parse(time);
			return computeTimeDifference(date.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 *  将时间戳转为天、时、分、秒形式
	 * @param time  时间戳
	 * @return  天、时、分、秒
	 */
	public static String computeTimeDifference(long time) {
		if ((time+"").length()==10){
			time=Long.parseLong(time+"000");
		}
		long l = time - System.currentTimeMillis();
		if(l <= 0) {
			return "" + 0 + "天" + 0 + "小时" + 0 + "分" + 0 + "秒";
		}

		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		return "" + day + "天" + hour + "小时" + min + "分" + s + "秒";
	}
	/**
	 *  将时间戳转为天、时形式
	 * @param time  时间戳
	 * @return  天、时
	 */
	public static String timeFormat(long time){
		if ((time+"").length()==10){
			time=Long.parseLong(time+"000");
		}
		long l = time - System.currentTimeMillis();
		if(l <= 0) {
			return "" + 0 + "天" + 0 + "小时";
		}

		long diffHour = l/(1000 * 60 * 60);
		long day = l/(1000 * 60 *60 * 24);
		if (diffHour < 24){
			// 显示为小时
			return  "" + diffHour + "小时";
		} else {
			// 显示天
			return "" + day + "天";
		}
	}

	/**
	 * 去掉时间中的T字符
	 * @param timeT
	 * @return
	 */
	public static String reTimeT(String timeT){
		String time="00-00-00 00:00:00";
		try{
			if(timeT!=null) {
				time = timeT.replace("T", " ");
			}else{
				return time;
			}
		}catch (Exception e){
			return "00-00-00 00:00:00";
		}
		return time;
	}
	/***
	 * 获取当前时间 月、日、星期
	 * @return
	 */
	public static String getCurentDate(){
		String date, day;
		int today;
		Calendar calendar = Calendar.getInstance();
		today = calendar.get(Calendar.DAY_OF_WEEK);
		switch (today){
			case 2:
				day = "星期一";
				break;
			case 3:
				day = "星期二";
				break;
			case 4:
				day = "星期三";
				break;
			case 5:
				day = "星期四";
				break;
			case 6:
				day = "星期五";
				break;
			case 7:
				day = "星期六";
				break;
			case 8:
				day = "星期天";
				break;
			default:
				day = "日期错误";
				break;
		}
		date = calendar.get(Calendar.MONTH) + 1 + "月" + calendar.get(Calendar.DATE) + "日  " + day;
		return date;
	}
	public static void main(String[] arg){
		String time=timeStamp2Date(1544005522+"",null);
		System.out.println(time);
	}
}
