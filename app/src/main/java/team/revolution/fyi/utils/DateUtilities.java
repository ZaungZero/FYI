package team.revolution.fyi.utils;


import android.annotation.SuppressLint;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtilities {
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat dateFormatForInvoiceNo = new SimpleDateFormat("ddMMyyyyhhmmss");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat day = new SimpleDateFormat("dd");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat month = new SimpleDateFormat("MM");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat year = new SimpleDateFormat("yyyy");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat hour_And_minute = new SimpleDateFormat("hh:mm");

    /**
     * Convert date to a simple format String
     */
    public static String dateToString(Date d) {
        return dateFormat.format(d);
    }

    public static String dateToStringWithTime(Date d) {
        return dateFormatWithTime.format(d);
    }


    public static String dateToStringForInvoiceNo(Date d) {
        return dateFormatForInvoiceNo.format(d);
    }

    public static String dayToString(Date d) {
        return day.format(d);
    }

    public static String monthToString(Date d) {
        return month.format(d);
    }

    public static String yearToString(Date d) {
        return year.format(d);
    }

    public static String hourAnMinuteToString(Date d) {
        return hour_And_minute.format(d);
    }

    public static Long dateToTimeInstance(String str_date) throws ParseException {
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = (Date) formatter.parse(str_date);
        assert date != null;
        long output = date.getTime() / 1000L;
        String str = Long.toString(output);
        return Long.parseLong(str) * 1000;
    }

    /*
    Returns Date - 1
     */

    public static Date previousDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar mCalendar = new GregorianCalendar(year, month, day);
        mCalendar.add(Calendar.DATE, -1);
        //Log.e("previousDate",mCalendar.getTime());
        return mCalendar.getTime();
    }

    /*
    Returns Date + 1
    */

    public static Date next8DaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar mCalendar = new GregorianCalendar(year, month, day);
        mCalendar.add(Calendar.DATE, 8);
        return mCalendar.getTime();
    }

    public static Date next7DaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar mCalendar = new GregorianCalendar(year, month, day);
        mCalendar.add(Calendar.DATE, 7);
        return mCalendar.getTime();
    }

    public static Date next6DaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar mCalendar = new GregorianCalendar(year, month, day);
        mCalendar.add(Calendar.DATE, 6);
        return mCalendar.getTime();
    }

    public static Date next5DaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar mCalendar = new GregorianCalendar(year, month, day);
        mCalendar.add(Calendar.DATE, 5);
        return mCalendar.getTime();
    }

    public static Date next4DaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar mCalendar = new GregorianCalendar(year, month, day);
        mCalendar.add(Calendar.DATE, 4);
        return mCalendar.getTime();
    }

    public static Date next3DaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar mCalendar = new GregorianCalendar(year, month, day);
        mCalendar.add(Calendar.DATE, 3);
        return mCalendar.getTime();
    }

    public static Date next2DaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar mCalendar = new GregorianCalendar(year, month, day);
        mCalendar.add(Calendar.DATE, 2);
        return mCalendar.getTime();
    }

    public static Long next8DaysTimeStamp() {
        Timestamp timestamp = new Timestamp(next8DaysDate().getTime());
        return timestamp.getTime();
    }

    public static Long next7DaysTimeStamp() {
        Timestamp timestamp = new Timestamp(next7DaysDate().getTime());
        return timestamp.getTime();
    }

    public static Long next6DaysTimeStamp() {
        Timestamp timestamp = new Timestamp(next6DaysDate().getTime());
        return timestamp.getTime();
    }

    public static Long next5DaysTimeStamp() {
        Timestamp timestamp = new Timestamp(next5DaysDate().getTime());
        return timestamp.getTime();
    }

    public static Long next4DaysTimeStamp() {
        Timestamp timestamp = new Timestamp(next4DaysDate().getTime());
        return timestamp.getTime();
    }

    public static Long next3DaysTimeStamp() {
        Timestamp timestamp = new Timestamp(next3DaysDate().getTime());
        return timestamp.getTime();
    }

    public static Long next2DaysTimeStamp() {
        Timestamp timestamp = new Timestamp(next2DaysDate().getTime());
        return timestamp.getTime();
    }

    public static Date nextDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar mCalendar = new GregorianCalendar(year, month, day);
        mCalendar.add(Calendar.DATE, 1);
        return mCalendar.getTime();
    }

    public static Date nextOneMonthFromCurrentDaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar mCalendar = new GregorianCalendar(2020, 11, 31);
        //mCalendar.add(Calendar.MONTH, 1);
        return mCalendar.getTime();
    }

    public static Long nextMonthTimeStamp() {
        Timestamp timestamp = new Timestamp(nextOneMonthFromCurrentDaysDate().getTime());
        return timestamp.getTime();
    }

    public static Date nextYear() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar mCalendar = new GregorianCalendar(year, month, day);
        mCalendar.add(Calendar.YEAR, 1);
        return mCalendar.getTime();
    }

    public static Long nextDayTimeStamp() {
        Timestamp timestamp = new Timestamp(nextDate().getTime());
        return timestamp.getTime();
    }

    public static Long nextYearTimeStamp() {
        Timestamp timestamp = new Timestamp(nextYear().getTime());
        return timestamp.getTime();
    }

    public static Long currentTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime();
    }

    public static String timeStampToDateTime(Long timeStamp) {
        return dateFormatWithTime.format(new Date(timeStamp));
    }

    public static String timeStampToDate(Long timeStamp) {
        return dateFormat.format(new Date(timeStamp));
    }
}