package cn.bingoogolapple.basenote.util;

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
    private static final SimpleDateFormat sHourMinuteSdf = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat sYearMonthDayHourMinuteSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private CalendarUtil() {
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"), Locale.CHINA);
    }

    public static Calendar getZeroSecondCalendar() {
        Calendar calendar = getCalendar();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static String formatDisplayTime(long milliseconds) {
        Calendar ultimateCalendar = getCalendar();
        ultimateCalendar.setTimeInMillis(milliseconds);

        Calendar yesterday = getCalendar();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        Calendar today = getCalendar();

        Calendar tomorrow = getCalendar();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        if (ultimateCalendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "昨天 " + formatHourMinute(ultimateCalendar.getTime());
        } else if (ultimateCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "今天 " + formatHourMinute(ultimateCalendar.getTime());
        } else if (ultimateCalendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && ultimateCalendar.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
            return "明天 " + formatHourMinute(ultimateCalendar.getTime());
        } else {
            return formatYearMonthDayHourMinute(ultimateCalendar.getTime());
        }
    }

    public static String formatHourMinute(Date date) {
        return sHourMinuteSdf.format(date);
    }

    public static String formatYearMonthDayHourMinute(Date date) {
        return sYearMonthDayHourMinuteSdf.format(date);
    }
}