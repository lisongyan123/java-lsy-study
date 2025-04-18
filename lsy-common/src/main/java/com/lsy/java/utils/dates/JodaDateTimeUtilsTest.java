package com.lsy.java.utils.dates;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * JodaDateTimeUtils 测试类
 */
public class JodaDateTimeUtilsTest {
    
    public static void main(String[] args) {
        // 1. 测试获取当前时间
        System.out.println("1. 当前时间：" + JodaDateTimeUtils.now());
        
        // 2. 测试日期格式化
        DateTime now = JodaDateTimeUtils.now();
        System.out.println("2. 日期格式化：");
        System.out.println("   日期格式：" + JodaDateTimeUtils.format(now, JodaDateTimeUtils.PATTERN_DATE));
        System.out.println("   时间格式：" + JodaDateTimeUtils.format(now, JodaDateTimeUtils.PATTERN_TIME));
        System.out.println("   日期时间格式：" + JodaDateTimeUtils.format(now, JodaDateTimeUtils.PATTERN_DATETIME));
        
        // 3. 测试字符串转日期
        String dateStr = "2024-03-20 10:30:00";
        DateTime parsedDate = JodaDateTimeUtils.parse(dateStr, JodaDateTimeUtils.PATTERN_DATETIME);
        System.out.println("3. 字符串转日期：" + parsedDate);
        
        // 4. 测试 Date 和 DateTime 互转
        Date date = new Date();
        DateTime dateTime = JodaDateTimeUtils.toDateTime(date);
        Date convertedDate = JodaDateTimeUtils.toDate(dateTime);
        System.out.println("4. Date 和 DateTime 互转：");
        System.out.println("   原始 Date：" + date);
        System.out.println("   转换为 DateTime：" + dateTime);
        System.out.println("   转回 Date：" + convertedDate);
        
        // 5. 测试日期计算
        DateTime date1 = JodaDateTimeUtils.now();
        DateTime date2 = JodaDateTimeUtils.plusDays(date1, 5);
        System.out.println("5. 日期计算：");
        System.out.println("   两个日期之间的天数：" + JodaDateTimeUtils.daysBetween(date1, date2));
        System.out.println("   两个日期之间的小时数：" + JodaDateTimeUtils.hoursBetween(date1, date2));
        System.out.println("   两个日期之间的分钟数：" + JodaDateTimeUtils.minutesBetween(date1, date2));
        
        // 6. 测试日期加减
        DateTime tomorrow = JodaDateTimeUtils.plusDays(now, 1);
        DateTime nextHour = JodaDateTimeUtils.plusHours(now, 1);
        DateTime nextMinute = JodaDateTimeUtils.plusMinutes(now, 1);
        System.out.println("6. 日期加减：");
        System.out.println("   明天：" + tomorrow);
        System.out.println("   一小时后：" + nextHour);
        System.out.println("   一分钟后：" + nextMinute);
        
        // 7. 测试日期边界
        DateTime startOfDay = JodaDateTimeUtils.getStartOfDay(now);
        DateTime endOfDay = JodaDateTimeUtils.getEndOfDay(now);
        System.out.println("7. 日期边界：");
        System.out.println("   今天的开始时间：" + startOfDay);
        System.out.println("   今天的结束时间：" + endOfDay);
        
        // 8. 测试日期比较
        boolean isSameDay = JodaDateTimeUtils.isSameDay(date1, date2);
        System.out.println("8. 日期比较：");
        System.out.println("   是否是同一天：" + isSameDay);
        
        // 9. 测试日期信息获取
        System.out.println("9. 日期信息：");
        System.out.println("   星期几（1-7）：" + JodaDateTimeUtils.getDayOfWeek(now));
        System.out.println("   当月第几天：" + JodaDateTimeUtils.getDayOfMonth(now));
        System.out.println("   当年第几天：" + JodaDateTimeUtils.getDayOfYear(now));
        System.out.println("   当月天数：" + JodaDateTimeUtils.getDaysInMonth(now));
        System.out.println("   当年天数：" + JodaDateTimeUtils.getDaysInYear(now));
    }
}