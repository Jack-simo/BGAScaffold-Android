package cn.bingoogolapple.scaffolding.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 下午12:55
 * 描述:
 */
public class CalendarUtil {
    private static final SimpleDateFormat sHourMinuteSdf = new SimpleDateFormat("HH:mm", Locale.CHINESE);
    private static final SimpleDateFormat sMonthDayHourMinuteSdf = new SimpleDateFormat("MM/dd HH:mm", Locale.CHINESE);
    private static final SimpleDateFormat sYearMonthDayHourMinuteSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
    private static final SimpleDateFormat sYearMonthDaySdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
    private static final SimpleDateFormat sYearMonthDayWeekSlashSdf = new SimpleDateFormat("yyyy-MM-dd E", Locale.CHINESE);
    private static final SimpleDateFormat sYearMonthDayWeekSdf = new SimpleDateFormat("yyyy/MM/dd E", Locale.CHINESE);
    private static final SimpleDateFormat sChineseYearMonthDaySdf = new SimpleDateFormat("yyyy年MM月dd", Locale.CHINESE);
    private static final SimpleDateFormat sChineseYearMonthDayWeekSdf = new SimpleDateFormat("yyyy年MM月dd E", Locale.CHINESE);
    private static final long MINUTE_MILLISECONDS = 60 * 1000;
    private static final long HOUR_MILLISECONDS = 60 * MINUTE_MILLISECONDS;
    private static final long DAY_MILLISECONDS = 24 * HOUR_MILLISECONDS;

    private CalendarUtil() {
    }

    /**
     * 将时，分，秒，以及毫秒值设置为0
     *
     * @param calendar
     */
    public static void zeroFromHour(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 将时间戳转换成当天零点的时间戳
     *
     * @param milliseconds
     * @return
     */
    public static Calendar zeroFromHour(long milliseconds) {
        Calendar calendar = getCalendar(milliseconds);
        zeroFromHour(calendar);
        return calendar;
    }

    /**
     * 将时间戳转换成当天零点的时间戳
     *
     * @param milliseconds
     * @return
     */
    public static long zeroFromHourMilliseconds(long milliseconds) {
        return zeroFromHour(milliseconds).getTimeInMillis();
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"), Locale.CHINA);
    }

    public static Calendar getCalendar(long milliseconds) {
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(milliseconds);
        return calendar;
    }

    /**
     * 获取当前时间零秒零毫秒的时间戳
     *
     * @return
     */
    public static Calendar getZeroSecondCalendar() {
        Calendar calendar = getCalendar();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 获取今天零点零小时零分零秒零毫秒的时间戳
     *
     * @return
     */
    public static long getTodayZeroTimeInMillis() {
        Calendar calendar = getZeroSecondCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取明天零点零小时零分零秒零毫秒的时间戳
     *
     * @return
     */
    public static long getTomorrowZeroTimeInMillis() {
        Calendar calendar = getZeroSecondCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 格式化详细的日期时间
     *
     * @param milliseconds
     * @return
     */
    public static CharSequence formatDetailDisplayTime(long milliseconds) {
        Calendar ultimateCalendar = getCalendar();
        ultimateCalendar.setTimeInMillis(milliseconds);

        Calendar theDayBeforeYesterday = getCalendar();
        theDayBeforeYesterday.add(Calendar.DAY_OF_YEAR, -2);

        Calendar yesterday = getCalendar();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        Calendar today = getCalendar();

        Calendar tomorrow = getCalendar();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        Calendar theDayAfterTomorrow = getCalendar();
        theDayAfterTomorrow.add(Calendar.DAY_OF_YEAR, 2);

        if (ultimateCalendar.get(Calendar.YEAR) == theDayBeforeYesterday.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == theDayBeforeYesterday.get(Calendar.DAY_OF_YEAR)) {
            return "前天 " + formatHourMinute(ultimateCalendar.getTime());
        } else if (ultimateCalendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "昨天 " + formatHourMinute(ultimateCalendar.getTime());
        } else if (ultimateCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            long differenceTime = today.getTimeInMillis() - ultimateCalendar.getTimeInMillis();
            if (differenceTime >= 0 && differenceTime < MINUTE_MILLISECONDS) {
                return "刚刚";
            } else if (differenceTime >= 0 && differenceTime < HOUR_MILLISECONDS) {
                return (new Long(today.getTimeInMillis() / MINUTE_MILLISECONDS).intValue() - new Long(ultimateCalendar.getTimeInMillis() / MINUTE_MILLISECONDS).intValue()) + "分钟前";
            } else {
                return "今天 " + formatHourMinute(ultimateCalendar.getTime());
            }
        } else if (ultimateCalendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
            return "明天 " + formatHourMinute(ultimateCalendar.getTime());
        } else if (ultimateCalendar.get(Calendar.YEAR) == theDayAfterTomorrow.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == theDayAfterTomorrow.get(Calendar.DAY_OF_YEAR)) {
            return "后天 " + formatHourMinute(ultimateCalendar.getTime());
        } else {
            return formatYearMonthDayHourMinute(ultimateCalendar.getTime());
        }
    }

    /**
     * 格式化详细的日期
     *
     * @param milliseconds
     * @return
     */
    public static CharSequence formatDetailDisplayDate(long milliseconds) {
        Calendar ultimateCalendar = getCalendar();
        ultimateCalendar.setTimeInMillis(milliseconds);

        Calendar theDayBeforeYesterday = getCalendar();
        theDayBeforeYesterday.add(Calendar.DAY_OF_YEAR, -2);

        Calendar yesterday = getCalendar();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        Calendar today = getCalendar();

        Calendar tomorrow = getCalendar();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        Calendar theDayAfterTomorrow = getCalendar();
        theDayAfterTomorrow.add(Calendar.DAY_OF_YEAR, 2);

        if (ultimateCalendar.get(Calendar.YEAR) == theDayBeforeYesterday.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == theDayBeforeYesterday.get(Calendar.DAY_OF_YEAR)) {
            return "前天";
        } else if (ultimateCalendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "昨天";
        } else if (ultimateCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "今天";
        } else if (ultimateCalendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
            return "明天";
        } else if (ultimateCalendar.get(Calendar.YEAR) == theDayAfterTomorrow.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == theDayAfterTomorrow.get(Calendar.DAY_OF_YEAR)) {
            return "后天";
        } else {
            return formatYearMonthDay(ultimateCalendar.getTime());
        }
    }

    public static String formatHourMinute(Date date) {
        return sHourMinuteSdf.format(date);
    }

    public static String formatHourMinute(long milliseconds) {
        return formatHourMinute(new Date(milliseconds));
    }


    public static String formatMonthDayHourMinute(Date date) {
        return sMonthDayHourMinuteSdf.format(date);
    }

    public static String formatMonthDayHourMinute(long milliseconds) {
        return formatMonthDayHourMinute(new Date(milliseconds));
    }


    public static String formatYearMonthDayHourMinute(Date date) {
        return sYearMonthDayHourMinuteSdf.format(date);
    }

    public static String formatYearMonthDayHourMinute(long milliseconds) {
        return formatYearMonthDayHourMinute(new Date(milliseconds));
    }

    public static String formatYearMonthDay(Date date) {
        return sYearMonthDaySdf.format(date);
    }

    public static String formatYearMonthDay(long milliseconds) {
        return formatYearMonthDay(new Date(milliseconds));
    }

    public static String formatYearMonthDayWeek(Date date) {
        return sYearMonthDayWeekSdf.format(date);
    }

    public static String formatYearMonthDayWeek(long milliseconds) {
        return formatYearMonthDayWeek(new Date(milliseconds));
    }

    public static String formatYearMonthDayWeekSlash(Date date) {
        return sYearMonthDayWeekSlashSdf.format(date);
    }

    public static String formatYearMonthDayWeekSlash(long milliseconds) {
        return formatYearMonthDayWeekSlash(new Date(milliseconds));
    }


    public static long parseYearMonthDay(String date) {
        try {
            return sYearMonthDaySdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long parseYearMonthDayHourMinute(String time) {
        try {
            return sYearMonthDayHourMinuteSdf.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String formatChineseYearMonthDay(Date date) {
        return sChineseYearMonthDaySdf.format(date);
    }

    public static String formatChineseYearMonthDay(long milliseconds) {
        return formatChineseYearMonthDay(new Date(milliseconds));
    }

    public static String formatChineseYearMonthDayWeek(Date date) {
        return sChineseYearMonthDayWeekSdf.format(date);
    }

    public static String formatChineseYearMonthDayWeek(long milliseconds) {
        return formatChineseYearMonthDayWeek(new Date(milliseconds));
    }

    public static int daysBetween(long start, long end) {
        return new Long(end / DAY_MILLISECONDS).intValue() - new Long(start / DAY_MILLISECONDS).intValue();
    }

    public static int daysBetweenToday(long start) {
        return daysBetween(start, getCalendar().getTimeInMillis());
    }
}