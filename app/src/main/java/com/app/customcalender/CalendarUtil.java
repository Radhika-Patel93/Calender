package com.app.customcalender;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CalendarUtil {


    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            java.util.Date date = myFormatter.parse(sj1);
            java.util.Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }


    /**
     * Determine the current date is the day of the week
     *
     * @param pTime Set the time to judge @Format as 2012-09-08
     * @return dayForWeek
     * @Exception An exception occurs
     */
    public static int getWeekNoFormat(String pTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static Calendar toDate(String pTime) {
        Log.d("CALENDAR: ", pTime + " ");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
            return c;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Parameter format：2012-12-1
     * return December 1
     *
     * @param date
     */
    public static String FormatDateMD(String date) {
        if (TextUtils.isEmpty(date)) {
            new Throwable();
        }
        String month = date.split("-")[1];
        if (month.equalsIgnoreCase("1")) {
            month = "JAN";
        } else if (month.equalsIgnoreCase("2")) {
            month = "FEB";
        } else if (month.equalsIgnoreCase("3")) {
            month = "MAR";
        } else if (month.equalsIgnoreCase("4")) {
            month = "APR";
        } else if (month.equalsIgnoreCase("5")) {
            month = "MAY";
        } else if (month.equalsIgnoreCase("6")) {
            month = "JUN";
        } else if (month.equalsIgnoreCase("7")) {
            month = "JUL";
        } else if (month.equalsIgnoreCase("8")) {
            month = "AUG";
        } else if (month.equalsIgnoreCase("9")) {
            month = "SEP";
        } else if (month.equalsIgnoreCase("10")) {
            month = "OCT";
        } else if (month.equalsIgnoreCase("11")) {
            month = "NOV";
        } else if (month.equalsIgnoreCase("12")) {
            month = "DEC";
        }
        String day = date.split("-")[2];
        return month + " " + day;
    }

    /**
     * Parameter format：2012-12-1
     * return December 1, 2012
     *
     * @param date
     */
    public static String FormatDateYMD(String date) {
        if (TextUtils.isEmpty(date)) {
            new Throwable();
        }
        String year = date.split("-")[0];
        String month = date.split("-")[1];
        String day = date.split("-")[2];
        return year + "-" + month + "-" + day + "";
    }

    /**
     * Determine the current date is the day of the week
     *
     * @param pTime Set the time to judge * Format as 2012-09-08
     * @return dayForWeek critical result
     * @Exception An exception occurs
     */
    @SuppressLint("SimpleDateFormat")
    public static String getWeekByFormat(String pTime) {
        String week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            week += "SUN";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            week += "MON";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            week += "TUE";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            week += "WED";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            week += "THUR";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            week += "FRI";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            week += "SAT";
        }
        return week;
    }

}
