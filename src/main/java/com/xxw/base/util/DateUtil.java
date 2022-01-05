package com.xxw.base.util;

import com.xxw.base.dao.DateHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 唐少峰
 * @title 时间工具类
 * @date 2015年12月7日 下午3:26:58
 * @description
 */
public class DateUtil {

    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);


    /**
     * 日期格式yyyyMMddHHmm
     */
    public static final String yyyy_MM_dd_HH_mm_IOS = "yyyyMMddHHmm";
    /**
     * 日期格式(yyyyMMddHHmmss)
     */
    public static final String YMDHMS = "yyyyMMddHHmmss";
    /**
     * 日期格式(yyyy-MM-dd HH:mm:ss.S)
     **/
    public static final String YMDHMSS = "yyyy-MM-dd HH:mm:ss.ss";
    /**
     * 日期格式(yyyy-MM-dd HH:mm:ss)
     **/
    public static final String STRIPING_YMD_COLON_HM = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式(yyyy-MM-dd HH:mm:ss)
     **/
    public static final String yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 日期格式(yyyy-MM-dd HH:mm)
     **/
    public static final String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";

    /**
     * 日期格式(yyyy-MM-dd HH)
     **/
    public static final String yyyy_MM_dd_HH = "yyyy-MM-dd HH";

    /**
     * 日期格式(yyyy-MM-dd)
     **/
    public static final String yyyy_MM_dd = "yyyy-MM-dd";

    /**
     * 日期格式(yyyyMMddHHmmssSSS)
     */
    public static final String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

    /**
     * 日期格式yyyyMMdd
     */
    public static final String yyyyMMdd = "yyyyMMdd";

    /**
     * 日期格式yyyy
     */
    public static final String yyyy = "yyyy";

    /**
     * 日期格式HHmm
     */
    public static final String HHmm = "HHmm";
    
    /**
     * 日期格式HHmmss
     */
    public static final String HHmmss="HHmmss";

    /**
     * String转Date
     *
     * @param source
     * @param format
     * @return
     */
    public static Date strToDate(String source, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(source);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Date转String
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToStr(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前时间的GMT日期
     */
    public static String dateTransformBetweenTimeZoneDefault() {
        return dateTransformBetweenTimeZone(new Date(), new SimpleDateFormat(DateUtil.yyyy_MM_dd_HH_mm_IOS), TimeZone.getDefault(), TimeZone.getTimeZone("Asia/Shanghai"));
    }

    /**
     * 获取当前时间的GMT日期
     */
    public static String dateTransformBetweenTimeZoneDefault(String formatter) {
        return dateTransformBetweenTimeZone(new Date(), new SimpleDateFormat(formatter), TimeZone.getDefault(), TimeZone.getTimeZone("Asia/Shanghai"));
    }

    /**
     * 获取当前时间格式：yyyyMMddHHmmssSSS
     *
     * @return
     */
    public static String now() {
        return getTime(DateUtil.yyyyMMddHHmmssSSS);
    }

    /**
     * 获取当前时间格式：yyyyMMddHHmmssSSS
     *
     * @return
     */
    public static String nowDate() {
        return getTime(DateUtil.YMDHMS);
    }

    public static String nowDateTime() {
        return getTime(DateUtil.yyyy_MM_dd_HH_mm_ss_SSS);
    }

    /**
     * 获取对应格式的时间
     *
     * @param format
     * @return
     */
    public static String getTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);//设置日期格式
        return df.format(new Date());
    }

    /**
     * 获取指定月的前一月（年）或后一月（年）
     *
     * @param dateStr
     * @param addYear
     * @param addMonth
     * @param addDate
     * @return 输入的时期格式为yyyy-MM-dd HH:mm:ss，输出的日期格式为yyyy-MM-dd HH:mm:ss
     * @throws Exception
     */
    public static String getCustomTime(String dateStr, int addYear, int addMonth, int addDate) {
        String dateTmp = nowDateTime();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss_SSS);
            Date sourceDate = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(sourceDate);
            cal.add(Calendar.YEAR, addYear);
            cal.add(Calendar.MONTH, addMonth);
            cal.add(Calendar.DATE, addDate);
            SimpleDateFormat returnSdf = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss_SSS);
            dateTmp = returnSdf.format(cal.getTime());
        } catch (Exception e) {
            logger.error("获取时间错误", e);
        }
        return dateTmp;
    }

    public static String getBeforeOneMonthTime() {
        return getCustomTime(nowDateTime(), 0, -1, 0);
    }

    /**
     * 时间戳转GMT日期
     */
    public static String dateTransformBetweenTimeZoneDefault(long timestamp) {
        String dates = new SimpleDateFormat(DateUtil.YMDHMS).format(new Date(timestamp * 1000));
        DateFormat sdf = DateHelper.formatFormat(DateUtil.YMDHMS);
        Date date = null;
        try {
            date = sdf.parse(dates);
        } catch (ParseException e) {
            logger.error("时间戳转GMT日期错误", e);
        }
        return dateTransformBetweenTimeZone(date, new SimpleDateFormat(DateUtil.YMDHMS), TimeZone.getDefault(), TimeZone.getTimeZone("GMT"));
    }

    /**
     * 获取GMT日期
     */
    public static String dateTransformBetweenTimeZone(Date sourceDate, DateFormat formatter, TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        Long targetTime = sourceDate.getTime() - sourceTimeZone.getRawOffset() + targetTimeZone.getRawOffset();
        return getTime(new Date(targetTime), formatter);
    }

    /**
     * 将当地当前时间转换成GMT时间，并转换为yyyyMMddHHmmss格式
     */
    public static String getGMT14() {
        Long targetTime = new Date().getTime() - TimeZone.getDefault().getRawOffset() + TimeZone.getTimeZone("GMT").getRawOffset();
        return getTime(new Date(targetTime), new SimpleDateFormat(YMDHMS));
    }

    public static String getTime(Date date, DateFormat formatter) {
        return formatter.format(date);
    }

    public static String getTime(Date date, String formatter) {
        return new SimpleDateFormat(formatter).format(date);
    }


    /**
     * 将格式如"20160513040100"转为如"2016年05月13日04时01分"
     *
     * @param dates
     * @return
     * @author MaoXY
     * @date 2016年5月13日 下午2:39:33
     * @description
     */
    public static String str2Date(String dates) {
        StringBuffer sbBuffer = null;
        if (StringUtils.isEmpty(dates)) {
            return "";
        }
        if (dates.length() >= 12) {
            sbBuffer = new StringBuffer();
            sbBuffer.append(dates, 0, 4).append("年");
            dates = dates.substring(4);
            sbBuffer.append(dates, 0, 2).append("月");
            dates = dates.substring(2);
            sbBuffer.append(dates, 0, 2).append("日");
            dates = dates.substring(2);

            sbBuffer.append(" ");
            sbBuffer.append(dates, 0, 2).append(":");
            dates = dates.substring(2);
            sbBuffer.append(dates, 0, 2);
        }
        return sbBuffer.toString();
    }

    /**
     * 日期的小时加减
     *
     * @param date      待计算的日期
     * @param hour      要加(正数)，或减(负数)的数值
     * @param formatStr 格式
     * @return 计算后的格式化内容
     * @author MaoXY
     * @date 2016年5月24日 上午9:54:55
     * @description
     */
    public static String hourCompute(String date, int hour, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        Date dt = null;
        String result = null;
        try {
            date = date.length() >= 14 ? date.substring(0, 14) : date;
            dt = sdf.parse(date);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.HOUR, hour);
            Date dt1 = rightNow.getTime();
            result = sdf.format(dt1);
        } catch (ParseException e) {
            logger.error("logger.hourCompute execute exception：", e);
        }
        return result;
    }

    /**
     * 日期的天数加减
     *
     * @param date      待计算的日期
     * @param day       要加(正数)，或减(负数)的数值
     * @param formatStr 格式
     * @return 计算后的格式化内容
     * @author 冯仕良
     * @date 2016年7月11日 上午9:54:55
     * @description
     */
    public static String dayCompute(String date, int day, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        Date dt = null;
        String result = null;
        try {
            date = date.length() >= 12 ? date.substring(0, 12) : date;
            dt = sdf.parse(date);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.DATE, day);
            Date dt1 = rightNow.getTime();
            result = sdf.format(dt1);
        } catch (ParseException e) {
            logger.error("logger.hourCompute execute exception：", e);
        }
        return result;
    }

    /**
     * 日期的分钟的加减
     *
     * @param date      待计算的日期
     * @param minute    要加(正数)，或减(负数)的数值
     * @param formatStr 格式
     * @return 计算后的格式化内容
     * @author 冯仕良
     * @date 2016年7月11日 上午9:54:55
     * @description
     */
    public static String minuteCompute(String date, int minute, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        Date dt = null;
        String result = null;
        try {
            dt = sdf.parse(date);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.MINUTE, minute);
            Date dt1 = rightNow.getTime();
            result = sdf.format(dt1);
        } catch (ParseException e) {
            logger.error("logger.hourCompute execute exception：", e);
        }
        return result;
    }

    /**
     * @param date
     * @param hour
     * @param formatStr
     * @return
     * @author MaoXY
     * @date 2016年5月24日 上午10:45:40
     * @description
     */
    public static String hourCompute2Date(String date, int hour, String formatStr) {
        String dates = hourCompute(date, hour, formatStr);
        if (!StringUtils.isEmpty(dates)) {
            return str2Date(dates);
        }
        return "";
    }

    /**
     * @Author:ivan
     * @param:
     * @Description: 获得“今天”零点时间戳 获得2点的加上2个小时的毫秒数就行
     * @Date:2020/05/12 0012
     */
    public static Long getTodayZeroPointTimestamps() {
        Long currentTimestamps = System.currentTimeMillis();
        Long oneDayTimestamps = Long.valueOf(60 * 60 * 24 * 1000);
        return currentTimestamps - (currentTimestamps + 60 * 60 * 8 * 1000) % oneDayTimestamps;
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        return timeStamp2Date(Long.valueOf(seconds) * 1000, format);
    }


    /**
     * 时间戳转换成日期格式字符串
     *
     * @param timeStamp 精确到毫秒的字符串
     * @return
     */
    public static String timeStamp2Date(Long timeStamp) {
        return timeStamp2Date(timeStamp, null);
    }


    /**
     * 时间戳转换成日期格式字符串
     *
     * @param timeStamp 精确到毫秒的字符串
     * @param format
     * @return
     */
    public static String timeStamp2Date(Long timeStamp, String format) {
        if (format == null || format.isEmpty()) {
            format = yyyy_MM_dd_HH_mm_ss_SSS;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(timeStamp)));
    }


    /**
     * 日期格式字符串转换成时间戳，精确到毫秒
     *
     * @param date_str 字符串, 日期如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Long date2TimeStamp(String date_str) {
        return date2TimeStamp(date_str, yyyy_MM_dd_HH_mm_ss_SSS);
    }

    /**
     * 日期格式字符串转换成时间戳，精确到毫秒
     *
     * @param date_str 字符串日期
     * @param format   如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Long date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date_str).getTime();
        } catch (Exception e) {
            logger.error("日期格式转换为时间戳错误", e);
        }
        return 0L;
    }

    /**
     * yyyyMMddHHmmssSSS 日期格式字符串转换成时间戳，精确到毫秒
     *
     * @param date_str 字符串日期
     * @return
     */
    public static Long commonDateStr2TimeStamp(String date_str) {
        return date2TimeStamp(date_str, yyyyMMddHHmmssSSS);
    }


    /**
     * 取得当前时间戳（精确到秒）
     *
     * @return
     */
    public static String timeStamp() {
        long time = System.currentTimeMillis();
        String t = String.valueOf(time / 1000);
        return t;
    }

    /**
     * 获取当前时间前n分钟的时间戳
     *
     * @param minute
     * @return
     */
    public static long getAgoTime(int minute) {
        Calendar beforeTime = Calendar.getInstance();
        // n分钟之前的时间
        beforeTime.add(Calendar.MINUTE, -minute);
        return beforeTime.getTimeInMillis();
    }


    /**
     * 按照天切割时间区间
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @param intervalDays 切割间隔
     */
    public static List<DateSplit> splitByDay(Long startTime, Long endTime, int intervalDays) {
        if (endTime <= startTime) {
            return null;
        }
        List<DateSplit> dateSplits = new ArrayList<>(256);

        DateSplit param = new DateSplit();
        param.setStartDateTime(startTime);
        param.setEndDateTime(addDays(startTime, intervalDays));
        while (true) {
            param.setStartDateTime(startTime);
            Long tempEndTime = addDays(startTime, intervalDays);
            if (tempEndTime >= endTime) {
                tempEndTime = endTime;
            }
            param.setEndDateTime(tempEndTime);

            dateSplits.add(new DateSplit(param.getStartDateTime(), param.getEndDateTime()));

            startTime = addDays(startTime, intervalDays);
            if (startTime >= endTime) {
                break;
            }
            if (param.getEndDateTime() >= endTime) {
                break;
            }
        }
        return dateSplits;
    }

    /**
     * 按照小时切割时间区间
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @param intervalHours 切割间隔
     */
    public static List<DateSplit> splitByHour(Long startTime, Long endTime, int intervalHours) {
        if (endTime <= startTime) {
            return null;
        }

        List<DateSplit> dateSplits = new ArrayList<>(256);

        DateSplit param = new DateSplit();
        param.setStartDateTime(startTime);
        param.setEndDateTime(addHours(startTime, intervalHours));
        while (true) {
            param.setStartDateTime(startTime);
            Long tempEndTime = addHours(startTime, intervalHours);
            if (tempEndTime >= endTime) {
                tempEndTime = endTime;
            }
            param.setEndDateTime(tempEndTime);

            dateSplits.add(new DateSplit(param.getStartDateTime(), param.getEndDateTime()));

            startTime = addHours(startTime, intervalHours);
            if (startTime >= endTime) {
                break;
            }
            if (param.getEndDateTime() >= endTime) {
                break;
            }
        }
        return dateSplits;
    }

    /**
     * 按照分钟切割时间区间
     *
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @param intervalMinutes 切割间隔
     */
    public static List<DateSplit> splitByMinute(Long startTime, Long endTime, int intervalMinutes) {
        if (endTime <= startTime) {
            return null;
        }
        List<DateSplit> dateSplits = new ArrayList<>(256);

        DateSplit param = new DateSplit();
        param.setStartDateTime(startTime);
        param.setEndDateTime(addMinute(startTime, intervalMinutes));
        while (true) {
            param.setStartDateTime(startTime);
            Long tempEndTime = addMinute(startTime, intervalMinutes);
            if (tempEndTime >= endTime) {
                tempEndTime = endTime;
            }
            param.setEndDateTime(tempEndTime);

            dateSplits.add(new DateSplit(param.getStartDateTime(), param.getEndDateTime()));

            startTime = addMinute(startTime, intervalMinutes);
            if (startTime >= endTime) {
                break;
            }
            if (param.getEndDateTime() >= endTime) {
                break;
            }
        }
        return dateSplits;
    }

    /**
     * 按照秒切割时间区间
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @param intervalSeconds 切割间隔
     */
    public static List<DateSplit> splitBySecond(Long startTime, Long endTime, int intervalSeconds) {
        if (endTime <= startTime) {
            return null;
        }
        List<DateSplit> dateSplits = new ArrayList<>(256);

        DateSplit param = new DateSplit();
        param.setStartDateTime(startTime);
        param.setEndDateTime(addSeconds(startTime, intervalSeconds));
        while (true) {
            param.setStartDateTime(startTime);
            Long tempEndTime = addSeconds(startTime, intervalSeconds);
            if (tempEndTime >= endTime) {
                tempEndTime = endTime;
            }
            param.setEndDateTime(tempEndTime);

            dateSplits.add(new DateSplit(param.getStartDateTime(), param.getEndDateTime()));

            startTime = addSeconds(startTime, intervalSeconds);
            if (startTime >= endTime) {
                break;
            }
            if (param.getEndDateTime() >= endTime) {
                break;
            }
        }
        return dateSplits;
    }

    public static Long addDays(Long date, int days) {
        return add(date, Calendar.DAY_OF_MONTH, days);
    }

    public static Long addHours(Long date, int hours) {
        return add(date, Calendar.HOUR_OF_DAY, hours);
    }

    public static Long addMinute(Long date, int minute) {
        return add(date, Calendar.MINUTE, minute);
    }

    public static Long addSeconds(Long date, int second) {
        return add(date, Calendar.SECOND, second);
    }

    private static Long add(final Long date, final int calendarField, final int amount) {
        final Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        c.add(calendarField, amount);
        return c.getTimeInMillis();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DateSplit {
        private Long startDateTime;
        private Long endDateTime;

        public String getStartDateTimeStr(String format) {
            return timeStamp2Date(startDateTime, format);
        }

        public String getEndDateTimeStr(String format) {
            return timeStamp2Date(endDateTime, format);
        }

        public boolean isBetweenStartAndEnd(Long time) {
            return time >= startDateTime && time < endDateTime;
        }
    }

    /**
     * 获取指定日期0时0分0秒
     *
     * @param time
     */
    public static String get0Hours(String time) {
        //2020 07 12 14 18 56 863
        return time.substring(0, 8) + "000000000";
    }

    /**
     * 获取指定日期23时59分59秒
     *
     * @param time
     */
    public static String get23Hours(String time) {
        //2020 07 12 14 18 56 863
        return time.substring(0, 8) + "235959000";
    }

    /**
     * 获取指定日期0分0秒
     *
     * @param time
     */
    public static String get0Minutes(String time) {
        //2020 07 12 14 18 56 863
        return time.substring(0, 10) + "0000000";
    }

    /**
     * 获取指定日期59分59秒
     *
     * @param time
     */
    public static String get59Minutes(String time) {
        //2020 07 12 14 18 56 863
        return time.substring(0, 10) + "5959000";
    }

    /**
     * 筛选周末或工作日
     * dayType 1工作日 2周末
     * @param dateSplits
     */
    public static List<DateSplit> filterWeekend(List<DateSplit> dateSplits, int dayType) {
        List<DateSplit> collect = dateSplits.stream().filter(a -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(a.getStartDateTime()));
            if (1 == dayType) {
                return calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY;
            }else{
                return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
            }
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 将20200702101740转成2020-07-03 00:00:00类型
     */
    public static String timeStr2DateStr(String time){
        SimpleDateFormat format = new SimpleDateFormat(DateUtil.yyyy_MM_dd_HH_mm_ss_SSS);
        SimpleDateFormat fm = new SimpleDateFormat(DateUtil.YMDHMS);
        if (StringUtils.isNotNull(time)){
            try {
                String t=format.format(fm.parse(time));
                return t;
            } catch (ParseException e) {
                logger.error("日期格式yyyyMMddHHmmss转换错误", e);
            }
        }
        return null;
    }

    /**
     * 将20200702101740转成20200702类型
     */
    public static String timeStr3DateStr(String time){
        try {
            if (StringUtils.isNotNull(time)){
                String t=time.substring(0,8);
                return t;
            }
        } catch (Exception e) {
            logger.error("日期格式yyyyMMddHHmmss转换错误", e);
        }
        return null;
    }

    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat(yyyyMMdd);
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;

        String day=weekDays[w];
        switch (day){
            case "星期日":
                return "7";
            case "星期一":
                return "1";
            case "星期二":
                return "2";
            case "星期三":
                return "3";
            case "星期四":
                return "4";
            case "星期五":
                return "5";
            case "星期六":
                return "6";
            default:
                return null;
        }

    }

    /**
     * 根据日期和时间获取时间段分片集合
     *
     * @param startDate yyyyMMdd
     * @param startTime HHmmss
     * @param endDate yyyyMMdd
     * @param endTime HHmmss
     * @return 时间段集合 [{开始时间，结束时间}]
     */
    public static List<String[]> generateDateArray(String startDate, String startTime, String endDate, String endTime) {
        List<String[]> list = new ArrayList<>();
        Long startDateTemp = Long.parseLong(startDate);
        while (startDateTemp<=Long.parseLong(endDate)){
            String start = startDateTemp + startTime + "000";
            list.add(new String[]{start,startDateTemp+endTime+"000"});
            startDateTemp = Long.parseLong(timeStamp2Date(addDays(date2TimeStamp(start, yyyyMMddHHmmssSSS), 1),yyyyMMdd));
        }

        return list;
    }

    /**
     * 获取当前时间格式：yyyy
     *
     * @return
     */
    public static String nowYear() {
        return getTime(DateUtil.yyyy);
    }

    /**
     * 验证传入的时间段集合是否有重叠 startTime,endTime 例：0100,2020 或 202011092056000,202011092300000 等等
     * @param times
     */
    public static boolean isOverlapping(List<String> times) {
        for (int i = 0; i < times.size(); i++) {
            String startTime1 = times.get(i).split(",")[0];
            String endTime1 = times.get(i).split(",")[1];
            for (int j = 0; j < times.size(); j++) {
                if(i==j){
                    continue;
                }
                String startTime2 = times.get(j).split(",")[0];
                String endTime2 = times.get(j).split(",")[1];
                if((Long.parseLong(startTime2)>= Long.parseLong(startTime1)&&Long.parseLong(startTime2)<= Long.parseLong(endTime1)) || (Long.parseLong(endTime2)<= Long.parseLong(endTime1)&&Long.parseLong(endTime2)>= Long.parseLong(startTime1))){
                    return false;
                }
            }
        }
        return true;
    }


    public static void main(String[] args) {
        // System.out.println(hourCompute("201605240220", 8,
        // DateUtil.yyyy_MM_dd_HH_mm_IOS));
        // System.out.println(DateUtil.hourCompute2Date("201605240312", 8,
        // DateUtil.yyyy_MM_dd_HH_mm_IOS));
        // System.out.println(dateTransformBetweenTimeZoneDefault(1467188103));
        //System.out.println(dateTransformBetweenTimeZoneDefault(YMDHMS));
        //System.out.println(minuteCompute(dateTransformBetweenTimeZoneDefault(YMDHMS), -5, YMDHMS));
        // System.out.println(dayCompute("20160704000000", 7, "yyyyMMdd"));

//        System.out.println(DateUtil.strToDate("20200614190000", DateUtil.YMDHMS).after(new Date()));
//        long agoTime = DateUtil.getAgoTime(5);
//        System.out.println(agoTime);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        String format = sdf.format(agoTime);
//        System.out.println(format);

        Long s = commonDateStr2TimeStamp("20200711000000000");
        Long e = commonDateStr2TimeStamp("20200716235959000");
        List<DateSplit> dateSplits = DateUtil.splitByDay(s, e, 1);
        dateSplits = DateUtil.filterWeekend(dateSplits,2);
        for (DateUtil.DateSplit dateSplit : dateSplits) {
            System.out.println("时间区间: " + dateSplit.getStartDateTimeStr(yyyy_MM_dd_HH_mm) + " --->  " + dateSplit.getEndDateTimeStr(yyyy_MM_dd_HH_mm));

        }

    }

}