package com.lsy.java.utils.dates;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Joda-Time 日期时间工具类
 */
public class JodaDateTimeUtils {
    
    // 常用日期格式
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 获取当前日期时间
     */
    public static DateTime now() {
        return DateTime.now();
    }
    
    /**
     * 日期格式化
     */
    public static String format(DateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toString(pattern);
    }
    
    /**
     * 字符串转日期时间
     */
    public static DateTime parse(String dateStr, String pattern) {
        if (dateStr == null || pattern == null) {
            return null;
        }
        return DateTime.parse(dateStr, DateTimeFormat.forPattern(pattern));
    }
    
    /**
     * Date 转 DateTime
     */
    public static DateTime toDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return new DateTime(date);
    }
    
    /**
     * DateTime 转 Date
     */
    public static Date toDate(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toDate();
    }
    
    /**
     * 获取两个日期之间的天数
     */
    public static int daysBetween(DateTime start, DateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return Days.daysBetween(start, end).getDays();
    }
    
    /**
     * 获取两个日期之间的小时数
     */
    public static int hoursBetween(DateTime start, DateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return Hours.hoursBetween(start, end).getHours();
    }
    
    /**
     * 获取两个日期之间的分钟数
     */
    public static int minutesBetween(DateTime start, DateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return Minutes.minutesBetween(start, end).getMinutes();
    }
    
    /**
     * 日期加减天数
     */
    public static DateTime plusDays(DateTime dateTime, int days) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusDays(days);
    }
    
    /**
     * 日期加减小时
     */
    public static DateTime plusHours(DateTime dateTime, int hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusHours(hours);
    }
    
    /**
     * 日期加减分钟
     */
    public static DateTime plusMinutes(DateTime dateTime, int minutes) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusMinutes(minutes);
    }
    
    /**
     * 获取指定日期的开始时间（00:00:00）
     */
    public static DateTime getStartOfDay(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.withTimeAtStartOfDay();
    }
    
    /**
     * 获取指定日期的结束时间（23:59:59）
     */
    public static DateTime getEndOfDay(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.millisOfDay().withMaximumValue();
    }
    
    /**
     * 判断是否是同一天
     */
    public static boolean isSameDay(DateTime date1, DateTime date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.toLocalDate().equals(date2.toLocalDate());
    }
    
    /**
     * 获取指定日期是星期几（1-7，1代表星期一）
     */
    public static int getDayOfWeek(DateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.getDayOfWeek();
    }
    
    /**
     * 获取指定日期是当月的第几天
     */
    public static int getDayOfMonth(DateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.getDayOfMonth();
    }
    
    /**
     * 获取指定日期是当年的第几天
     */
    public static int getDayOfYear(DateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.getDayOfYear();
    }
    
    /**
     * 获取指定日期所在月份的天数
     */
    public static int getDaysInMonth(DateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.dayOfMonth().getMaximumValue();
    }
    
    /**
     * 获取指定日期所在年份的天数
     */
    public static int getDaysInYear(DateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.dayOfYear().getMaximumValue();
    }
}