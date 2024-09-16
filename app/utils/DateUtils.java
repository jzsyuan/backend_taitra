package utils;

import play.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ilss0902 on 2017/7/13.
 */
public class DateUtils {

    public static String simpleFormat = "yyyy/MM/dd HH:mm:ss";

    public static Date stringToDate(String dateString, String format) {

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        try {
            Date date = sdf.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String stringToOtherFormat(String dateString, String fromFormat, String toFormat) {

        SimpleDateFormat sdf = new SimpleDateFormat(fromFormat);

        try {
            Date date = sdf.parse(dateString);

            return dateToString(date, toFormat);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String dateToString(Date date, String format) {

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        String str = sdf.format(date);
        return str;

    }

    public static String getNowInFormatter(String format) {

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        Date today = new Date();
        String reportDate = sdf.format(today);
        return reportDate;

    }

    public static Date dateWithExtraDay(Date nowDate, int day) {
        return new Date(nowDate.getTime() + (long) day * 24 * 60 * 60 * 1000);
    }

    public static Date dateWithExtraHour(Date nowDate, int hour) {
        return new Date(nowDate.getTime() + (long) hour * 60 * 60 * 1000);
    }

    public static Date dateWithExtraMin(Date nowDate, int min) {
        return new Date(nowDate.getTime() + (long) min * 60 * 1000);
    }

    public static Date dateWithExtraDayEnd(Date nowDate, int day) {
        String timestampFormat = "yyyy/MM/dd";
        String format = "yyyy/MM/dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(nowDate.getTime() + (long) (day + 1) * 24 * 60 * 60 * 1000);
        return new Date(DateUtils.stringToDate(simpleDateFormat.format(date), timestampFormat).getTime() - 1000);
    }

    public static String getDateStringOfRepublicOfChina(Date nowDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String dateString = simpleDateFormat.format(nowDate);
        String year = dateString.substring(0, 4);
        String otherString = dateString.substring(4);
        return String.format("%d%s", Integer.parseInt(year) - 1911, otherString);
    }

    //每個月的第一天日期
    public static Date getFirstMonthDay(Calendar calendar) {
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    //每個月的最後一天日期
    public static Date getLastMonthDay(Calendar calendar) {
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    //一天的開始時間 yyyy:MM:dd 00:00:00
    public static Date getBeginningOfTheDay() {
        Calendar calendar = new GregorianCalendar();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    //一天的结束時間 yyyy:MM:dd 23:59:59
    public static Date getEndOfTheDay() {
        Calendar calendar = new GregorianCalendar();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    public static String getWeekOfDay(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("u");
        return simpleDateFormat.format(date);
    }

    public static Date getShiftDate(Date baseDate, int field, int value) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(baseDate);
        calendar.add(field, value);

        return calendar.getTime();
    }

    public static int daysBetweenByMillis(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static Period daysBetween(Date startDate, Date endDate) {
        Period diff = Period.between(
                LocalDate.parse(dateToString(startDate, "yyyy-MM-dd")),
                LocalDate.parse(dateToString(endDate, "yyyy-MM-dd")));
        return diff;
    }

    public static Period monthsBetween(Date startDate, Date endDate) {
        Period diff = Period.between(
                LocalDate.parse(dateToString(startDate, "yyyy-MM-dd")),
                LocalDate.parse(dateToString(endDate, "yyyy-MM-dd")));
        return diff;
    }
}
