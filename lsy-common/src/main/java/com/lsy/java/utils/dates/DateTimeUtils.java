package com.lsy.java.utils.dates;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class DateTimeUtils {

    private static Map<String, DateTimeFormatter> dateTimeFormatterMap = new HashMap<>();

    private static ZoneId defaultZone = ZoneId.systemDefault();

    @Deprecated
    private static final String zeroTimeSuffix = " 00:00:00.000";

    private DateTimeUtils() {
    }

    public static DateTimeFormatter getDateTimeFormatter(String pattern) {
        DateTimeFormatter f = dateTimeFormatterMap.get(pattern);
        if (f == null) {
            f = DateTimeFormatter.ofPattern(pattern);
            dateTimeFormatterMap.put(pattern, f);
        }
        return f;
    }

    public static long toMillis(LocalDate ld) {
        LocalDateTime ldt = ld.atStartOfDay();
        return toMillis(ldt);
    }

    public static long toMillis(LocalDateTime ldt) {
        return ldt.atZone(defaultZone).toInstant().toEpochMilli();
    }

    @Deprecated
    public static long toMillis(String dateTime, String... pattern) {
        if (dateTime.length() == 10) {
            dateTime += zeroTimeSuffix;
        }
        String p = Consts.DP.DP23;
        if (pattern.length != 0) {
            p = pattern[0];
        }
        DateTimeFormatter f = getDateTimeFormatter(p);
        LocalDateTime ldt = LocalDateTime.parse(dateTime, f);
        return toMillis(ldt);
    }

    public static long toMillis(String dateTime, String pattern) {
        DateTimeFormatter f = getDateTimeFormatter(pattern);
        LocalDateTime ldt = LocalDateTime.parse(dateTime, f);
        return toMillis(ldt);
    }

    public static LocalDate transform(Date date) {
        return date.toInstant().atZone(defaultZone).toLocalDate();
    }

    public static LocalDateTime transform(long l) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), defaultZone);
    }

    public static LocalDateTime localDateTimeFrom(Date date) {
        return date.toInstant().atZone(defaultZone).toLocalDateTime();
    }

    public static Date from(Instant i) {
        return new Date(i.toEpochMilli());
    }

    public static Date from(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(defaultZone).toInstant());
    }

    public static Date from(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(defaultZone).toInstant());
    }

    @Deprecated
    public static String convert(long mills, String... pattern) {
        String p = Consts.DP.DP10;
        if (pattern.length != 0) {
            p = pattern[0];
        }
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(mills), defaultZone);
        DateTimeFormatter f = getDateTimeFormatter(p);
        return ldt.format(f);
    }

    public static String convert(long mills, String pattern) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(mills), defaultZone);
        DateTimeFormatter f = getDateTimeFormatter(pattern);
        return ldt.format(f);
    }

    @Deprecated
    public static String convert(LocalDate date, String... pattern) {
        String p = Consts.DP.DP10;
        if (pattern.length != 0) {
            p = pattern[0];
        }
        DateTimeFormatter f = getDateTimeFormatter(p);
        return date.format(f);
    }

    public static String convert(LocalDate date, String pattern) {
        DateTimeFormatter f = getDateTimeFormatter(pattern);
        return date.format(f);
    }

    @Deprecated
    public static String convert(LocalDateTime localDateTime, String... pattern) {
        String p = Consts.DP.DP23;
        if (pattern.length != 0) {
            p = pattern[0];
        }
        DateTimeFormatter f = getDateTimeFormatter(p);
        return localDateTime.format(f);
    }

    public static String convert(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter f = getDateTimeFormatter(pattern);
        return localDateTime.format(f);
    }

    public static List<String> datesBetween(String start, String end) {
        LocalDate sd = LocalDate.parse(start);
        LocalDate ed = LocalDate.parse(end);
        long dist = ChronoUnit.DAYS.between(sd, ed);
        if (dist == 0) {
            return Collections.emptyList();
        } else if (dist < 0) {
            LocalDate d = ed;
            ed = sd;
            sd = d;
            dist = Math.abs(dist);
        }
        long max = dist + 1;
        return Stream.iterate(sd, d -> {
            return d.plusDays(1);
        }).limit(max).map(LocalDate::toString).collect(Collectors.toList());
    }

    public static List<LocalDate> datesBetween(LocalDate sd, LocalDate ed) {
        long numOfDaysBetween = ChronoUnit.DAYS.between(sd, ed);
        return IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween).mapToObj(sd::plusDays).collect(Collectors.toList());
    }

    public static LocalDate beforeNow(long offsetDays) {
        return LocalDate.now().minusDays(offsetDays);
    }

    public static LocalDateTime beforeNowNoTime(long offsetDays) {
        return LocalDate.now().minusDays(offsetDays).atStartOfDay();
    }

    @Deprecated
    public static LocalDateTime time2zero(LocalDateTime ldt) {
        return ldt.withHour(0).withMinute(0).withSecond(0).with(ChronoField.MILLI_OF_SECOND, 0);
    }

    public static long get10sTimeWinStart(int n) {
        LocalDateTime now = LocalDateTime.now().with(ChronoField.MILLI_OF_SECOND, 0);
        int sec = now.getSecond();
        long interval;
        if (sec > 49) {
            interval = sec - 50;
        } else if (sec > 39) {
            interval = sec - 40;
        } else if (sec > 29) {
            interval = sec - 30;
        } else if (sec > 19) {
            interval = sec - 20;
        } else if (sec > 9) {
            interval = sec - 10;
        } else {
            interval = sec;
        }
        long millis = toMillis(now);
        return millis - interval * 1000 - (n - 1) * 10L * 1000;
    }

    class Consts {

        private Consts() {
        }

        public static final class S {
            public static final String DEFAULT = "default";
            public static final String TRUE = "true";
            public static final String FALSE = "false";
            public static final String TRUE1 = "1";
            public static final String FALSE0 = "0";

            public static final String EMPTY = "";
            public static final String SPACE_STR = " ";
            public static final char SPACE = ' ';
            public static final String TWO_SPACE_STR = "  ";

            public static final char COMMA = ',';
            public static final char COLON = ':';
            public static final char FORWARD_SLASH = '/';
            public static final String FORWARD_SLASH_STR = "/";
            public static final char BACK_SLASH = '\\';
            public static final char DOT = '.';
            public static final char SEMICOLON = ';';
            public static final char QUESTION = '?';
            public static final char DOUBLE_QUOTE = '"';
            public static final char SINGLE_QUOTE = '\'';
            public static final char ASTERISK = '*';
            public static final char DASH = '-';
            public static final char UNDER_LINE = '_';
            public static final char EQUAL = '=';
            public static final char AT = '@';
            public static final char LEFT_SQUARE_BRACKET = '[';
            public static final char RIGHT_SQUARE_BRACKET = ']';
            public static final char LEFT_BRACE = '{';
            public static final char RIGHT_BRACE = '}';
            public static final char SQUARE = '^';
            public static final char HASH = '#';
            public static final char AND = '&';
            public static final char OR = '|';
            public static final char LF = '\n';
            public static final char TAB = '\t';
            public static final char NUL = '\u0000';

            /*
            private static  final  char    c0                    =  SystemUtils.IS_OS_WINDOWS ? S.BACK_SLASH : S.FORWARD_SLASH;
            public  static  final  char    PATH_SEPARATOR        =  c0;
            */
            public static final String LINE_SEPARATOR = System.lineSeparator();
            public static final String COMMA_SPACE = ", ";
            public static final String HTTP_PROTOCOL_PREFIX = "http://";
        }

        public static final class C {
            public static final String UTF8 = "UTF-8";
            public static final String GBK = "GBK";
            public static final String ISO88591 = "ISO8859-1";
        }

        public static final class DP {
            public static final String DP10 = "yyyy-MM-dd";
            public static final String DP14 = "yyyyMMddHHmmss";
            public static final String DP19 = "yyyy-MM-dd HH:mm:ss";
            public static final String DP23 = "yyyy-MM-dd HH:mm:ss.SSS";
            public static final byte MILLS_LEN = 13;
        }

        public static final class P {
            public static final String LOCAL = "local";
            public static final String DEV = "dev";
            public static final String TEST = "test";
            public static final String FAT = "fat";
            public static final String PREPROD = "preprod";
            public static final String UAT = "uat";
            public static final String PRO = "pro";
            public static final String PROD = "prod";
        }

        public static final class UN {
            public static final int KB = 1024;
            public static final int MB = 1024 * KB;
            public static final int GB = 1024 * MB;
        }

        public static final String HTTP_SERVER = "httpServer";
        public static final String HTTP_CLIENT = "httpClient";
        public static final String MYSQL = "mysql";
        public static final String REDIS = "redis";
        public static final String CODIS = "codis";
        public static final String MONGO = "mongo";
        public static final String KAFKA = "kafka";
        public static final String ELASTICSEARCH = "elasticsearch";
        public static final String SCHED = "sched";
        public static final String R2DBC = "r2dbc";
        public static final String TRACE_ID = "traceId";
    }

}
