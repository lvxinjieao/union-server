package com.u8.server.utils;

import com.u8.server.log.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ant on 2015/4/24.
 */
public class TimeUtils {

    public final static String FORMATER_1 = "yyyyMMddHHmmss";
    public static String FORMATER_2 = "yyyyMMddHHmmssSSS";
    public static String FORMATER_3 = "yyyy-MM-dd HH:mm:ss";
    public static String FORMATER_4 = "yyyyMMdd";
    public static String FORMATER_5 = "yyyy/MM/dd";
    public static String FORMATER_6 = "MM/dd";
    public static String FORMATER_7 = "yyyy-MM-dd";

    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new ConcurrentHashMap<String, ThreadLocal<SimpleDateFormat>>();
    private static final Object lockObj = new Object();


    public static String format_yyyyMMdd(Date time) {


        return getSdf(FORMATER_4).format(time);
    }

    public static String format_yyyyMMddHHmmss(Date time) {

        return getSdf(FORMATER_1).format(time);
    }

    public static String format_yyyyMMddHHmmssSSS(Date time) {

        return getSdf(FORMATER_2).format(time);
    }

    public static String format(Date time, String fmt) {

        return getSdf(fmt).format(time);
    }

    public static String format_default(Date time) {

        return getSdf(FORMATER_3).format(time);
    }


    public static Object parse_default(String time) {
        try {
            return getSdf(FORMATER_3).parseObject(time);
        } catch (ParseException e) {
            Log.e(e.getMessage(), e);
        }

        return null;
    }

    public static Object parseTime(String time, String format) {
        try {
            return getSdf(format).parseObject(time);
        } catch (ParseException e) {
            Log.e(e.getMessage(), e);
        }

        return null;
    }


    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    System.out.println("put new sdf of pattern " + pattern + " to map");

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {

                        @Override
                        protected SimpleDateFormat initialValue() {
                            System.out.println("thread: " + Thread.currentThread() + " init pattern: " + pattern);
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }

        return tl.get();
    }

    public static boolean sameYear(Date date1, Date date2) {

        if (date1 == null || date2 == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        return calendar.get(Calendar.YEAR) - calendar.get(Calendar.YEAR) == 0;
    }

    public static Date dateSub(Date date, int val) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -val);
        date = calendar.getTime();
        return date;
    }

    public static Date dateAdd(Date date, int val) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, val);
        date = calendar.getTime();
        return date;
    }

    public static Date minuteAdd(Date date, int val) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, val);
        date = calendar.getTime();
        return date;
    }

    public static Date lastDay() {
        return dateSub(new Date(), 1);
    }

    public static Date nextDay() {
        return dateAdd(new Date(), 1);
    }

    public static Date nextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        return date;
    }

    public static Date dateBegin(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis() - millisecond);

        return cal.getTime();
    }

    public static Date dateEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis() - millisecond);

        cal.setTimeInMillis(cal.getTimeInMillis() + 23 * 60 * 60 * 1000 + 59 * 60 * 1000 + 59 * 1000);

        return cal.getTime();
    }
}
