package com.xxw.base.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description:日期时间操作的工具类
 * @author MaoXY
 */
public class DateHelper {
	/** 日期格式(yyyy) */
	public static final String yyyy_EN = "yyyy";
	/** 日期格式(yyyy-MM-dd) */
	public static final String yyyy_MM_dd_EN = "yyyy-MM-dd";
	/** 日期格式(yyyy-MM-dd HH:mm) */
	public static final String yyyy_MM_dd_HH_mm_EN = "yyyy-MM-dd HH:mm";
	/** 日期格式(yyyyMMdd) */
	public static final String yyyyMMdd_EN = "yyyyMMdd";
	/** 日期格式(yyyy-MM) */
	public static final String yyyy_MM_EN = "yyyy-MM";
	/** 日期格式(yyyyMM) */
	public static final String yyyyMM_EN = "yyyyMM";
	/** 日期格式(yyyy-MM-dd HH:mm:ss) */
	public static final String yyyy_MM_dd_HH_mm_ss_EN = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 日期格式(yyyy-MM-dd HH:mm:ss sss)
	 */
	public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss sss";
	/**
	 * 日期格式(yyyyMMddHHmmsssss)
	 */
	public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmsssss";
	/** 日期格式(yyyyMMddHHmmss) */
	public static final String yyyyMMddHHmmss_EN = "yyyyMMddHHmmss";
	/** 日期格式(yyyy年MM月dd日) */
	public static final String yyyy_MM_dd_CN = "yyyy年MM月dd日";
	/** 日期格式(yyyy年MM月dd日HH时mm分ss秒) */
	public static final String yyyy_MM_dd_HH_mm_ss_CN = "yyyy年MM月dd日HH时mm分ss秒";
	/** 日期格式(yyyy年MM月dd日HH时mm分) */
	public static final String yyyy_MM_dd_HH_mm_CN = "yyyy年MM月dd日HH时mm分";
	/** 日期格式(yyyy年MM月dd日 HH:mm) */
	public static final String yyyy_MM_dd_HH_mm_IOS = "yyyy年MM月dd日 HH:mm";
	public static final String GTM_DATE_FROM = "EEE, d MMM yyyy HH:mm:ss 'GMT'";

	/** DateFormat缓存 */
	private static Map<String, DateFormat> dateFormatMap = new HashMap<>();

	/**
	 * 得到日期格式化对象
	 * 
	 * @param pattern
	 *            {@link String} the pattern describing the date and time format
	 * @return {@link DateFormat}
	 */
	public static DateFormat formatFormat(String pattern) {
		DateFormat df = dateFormatMap.get(pattern);
		if (df == null) {
			df = new SimpleDateFormat(pattern);
			dateFormatMap.put(pattern, df);
		}
		return df;
	}

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 *            the time value to be formatted into a time string
	 * @param pattern
	 *            {@link String} the pattern describing the date and time format
	 * @return {@link String} the formatted time string.
	 */
	public static String format(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		if (date != null) {
			String str = format.format(date);
			return str;
		} else {
			return null;
		}
	}

	/**
	 * 按照默认pattern的格式，转化source为Date类型 source必须是pattern的形式
	 * @param source {@link String} A String whose beginning should be parsed
	 * @param pattern {@link String} the pattern describing the date and time format
	 * @return {@link Date} the formatted time Date.
	 */
	public static Date parse(String source, String pattern) {
		try {
			if (source == null || source.equals("")) {
				return null;
			}
			DateFormat sdf = DateHelper.formatFormat(pattern);
			Date d = sdf.parse(source);
			return d;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 转化source为Date类型 yyyy_MM_dd_EN
	 * 
	 * @param source
	 *            {@link String} A String whose beginning should be parsed
	 * @return {@link Date} the formatted time Date.
	 */
	public static Date parse(String source) {
		return parse(source, yyyy_MM_dd_EN);
	}

	/**
	 * 将YYYYMMDD转换成Date日期
	 * 
	 * @param source
	 *            {@link String} A String whose beginning should be parsed
	 * @return {@link Date} the formatted time Date.
	 * @throws Exception
	 */
	public static Date transferDate(String source) throws Exception {
		if (source == null || source.length() < 1)
			return null;
		if (source.length() != 8)
			throw new Exception("日期格式错误");
		String con = "-";
		String yyyy = source.substring(0, 4);
		String mm = source.substring(4, 6);
		String dd = source.substring(6, 8);
		int month = Integer.parseInt(mm);
		int day = Integer.parseInt(dd);
		if (month < 1 || month > 12 || day < 1 || day > 31)
			throw new Exception("日期格式错误");
		String str = yyyy + con + mm + con + dd;
		return parse(str, DateHelper.yyyy_MM_dd_EN);
	}

	/**
	 * 得到当前yyyy-MM-dd格式的日期字符串
	 * 
	 * @return {@link String} 日期字符串
	 */
	public static String currentDate() {
		return format(new Date(), yyyy_MM_dd_EN);
	}

	/**
	 * 根据格式获取当前日期
	 * 
	 * @param pattern
	 *            {@link String} 日期格式
	 * @return 格式化后的日期
	 */
	public static String currentDate(String pattern) {
		return format(new Date(), pattern);
	}

	/**
	 * 比较两个"yyyy-MM-dd"格式的日期，之间相差多少毫秒,arg1-arg0
	 * 
	 * @param arg0
	 *            {@link String} 日期字符串
	 * @param arg1
	 *            {@link String} 日期字符串
	 * @return {@link Long} 相差毫秒数
	 */
	public static long compare(String arg0, String arg1) {
		Date d1 = parse(arg0);
		Date d2 = parse(arg1);
		return d2.getTime() - d1.getTime();
	}

	/**
	 * 将小时数换算成返回以毫秒为单位的时间
	 * 
	 * @param hours
	 *            {@link BigDecimal}
	 * @return {@link Long}
	 */
	public static long longValue(BigDecimal hours) {
		BigDecimal bd;
		bd = hours.multiply(new BigDecimal(3600 * 1000));
		return bd.longValue();
	}

	/**
	 * 获取当前日期years年后的一个(pattern)的字符串
	 * 
	 * @param years
	 *            {@link Integer} 年数
	 * @param pattern
	 *            {@link String} 返回的日期格式
	 * @return {@link String} 计算后的日期字符串
	 */
	public static String formatStringOfYear(int years, String pattern) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(new Date());
		now.add(Calendar.YEAR, years);
		return format(now.getTime(), pattern);
	}

	/**
	 * 将Calendar转换成pattern格式的字符串
	 * 
	 * @param date
	 *            {@link Calendar} 待转换的日期
	 * @param pattern
	 *            {@link String} 返回日期格式
	 * @return {@link String} 格式后的日期
	 */
	public static String dateToCalendarString(Calendar date, String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);// 设置你想要的格式
		return df.format(date.getTime());
	}

	/**
	 * 
	 * 获取当前时间的小时
	 * 
	 * @param date
	 *            {@link Date} 当前日期
	 * @return {@link Integer} 当前小时
	 */
	public static int getHour(Date date) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		return now.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取指定日期小时后的一个(formatStr)的字符串
	 * 
	 * @param date
	 *            {@link Date}
	 * @param hour
	 *            {@link Integer}
	 * @param formatStr
	 *            {@link String}
	 * @return {@link String}
	 */
	public static String getHourOfDay(String date, int hour, String formatStr) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(parse(date, formatStr));
		now.add(Calendar.HOUR, hour);
		return format(now.getTime(), formatStr);
	}

	/**
	 * 
	 * 获取当前日期的星期字符串
	 * 
	 * @param date
	 *            {@link Date} 当前日期
	 * @return {@link String} 当前星期的中文
	 */
	public static String getWeekOfDate(Date date) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	/**
	 * 
	 * 获取当前日期的星期Int
	 * 
	 * @param date
	 *            {@link Date}
	 * @return {@link Integer} 当前日期Int(星期天=7)
	 */
	public static int getIntWeekOfDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w == 0)
			w = 7;
		return w;
	}

	/**
	 * 获取19xx,20xx形式的年
	 * 
	 * @param date
	 *            {@link Date} 当前日期
	 * @return {@link Integer} 当前日期的年份 如：2013
	 */
	public static int getYear(Date date) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		return now.get(Calendar.YEAR);
	}

	/**
	 * 获取指定日期day天后的一个(formatStr)的字符串
	 * 
	 * @param date
	 *            {@link Date}
	 * @param day
	 *            {@link Integer}
	 * @param formatStr
	 *            {@link String}
	 * @return {@link String}
	 */
	public static String getDateOfDay(String date, int day, String formatStr) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(parse(date, formatStr));
		now.add(Calendar.DATE, day);
		return format(now.getTime(), formatStr);
	}

	/**
	 * 获取日期mon月后的一个(formatStr)的字符串
	 * 
	 * @param startTime
	 *            {@link String}
	 * @param months
	 *            {@link Integer}
	 * @param formatStr
	 *            {@link String}
	 * @return
	 */
	public static String getDateStringOfMon(String startTime, int months, String formatStr) {
		Calendar now = Calendar.getInstance();
		now.setTime(parse(startTime, yyyy_MM_EN));
		now.add(Calendar.MONTH, months);
		return format(now.getTime(), formatStr);
	}

	/**
	 * 得到两个年份区间段字符串
	 * 
	 * @param startDate
	 *            {@link String} 开始日期
	 * @param endDate
	 *            {@link String} 结束日期
	 * @param dateFroamt
	 *            {@link String} 日期格式字符串
	 * @return {@link List}
	 */
	public static List<String> printYear(String startDate, String endDate, String dateFormat) {
		List<String> dateStr = new ArrayList<>();
		// 日期格式化实体
		DateFormat format = formatFormat(dateFormat);
		Date startYear = parse(startDate, yyyy_EN);
		// 开始时间
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startYear);
		// 结束时间
		Date endYear = parse(endDate, yyyy_EN);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endYear);

		while (startCal.before(endCal)) {
			dateStr.add(format.format(startCal.getTime()));
			startCal.add(Calendar.YEAR, 1);
		}
		dateStr.add(format.format(startCal.getTime()));
		return dateStr;
	}

	/**
	 * 
	 * 获取两个日期之间相隔天数
	 * 
	 * @param toStr
	 *            到哪个时间
	 * @param fromStr
	 *            从哪个时间
	 * @param dateFormat
	 *            日期字符串格式
	 * @return
	 */
	public static int getTimeInterval(String toStr, String fromStr, String dateFormat) {
		long to = parse(toStr, dateFormat).getTime();
		long from = parse(fromStr, dateFormat).getTime();
		int interval = Integer.parseInt(String.valueOf((to - from) / (1000 * 60 * 60 * 24)));
		return interval;
	}

	/**
	 * 获取GTM时间格式字符串
	 * @param formatStr
	 * @return
	 */
	public static String getGTMDateStr(String formatStr) {
		return getGTMDateStr(new Date(), formatStr);
	}
	
	/**
	 * 获取GTM时间格式字符串
	 * 
	 * @param date
	 * @param formatStr
	 * @return
	 */
	public static String getGTMDateStr(Date date,String formatStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr, Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(date.getTime());
	}

	public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateHelper.yyyy_MM_dd_CN);
        String str = "20160524024556";
        Date dt = null;
        try {
            dt = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.HOUR, 8);
//        rightNow.add(Calendar.YEAR, -1);// 日期减1年
//        rightNow.add(Calendar.MONTH, 3);// 日期加3个月
//        rightNow.add(Calendar.DAY_OF_YEAR, 10);// 日期加10天
        Date dt1 = rightNow.getTime();
        String reStr = sdf.format(dt1);
        System.out.println(reStr);
	    
//	    System.out.println(getHourOfDay("20160523183327", -8, DateHelper.yyyy_MM_dd_HH_mm_IOS));
    }
	
}
