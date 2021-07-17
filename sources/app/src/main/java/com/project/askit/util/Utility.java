package com.project.askit.util;

import android.content.Context;
import android.content.res.Resources;

import com.project.askit.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utility {

    private final static int SECOND = 1;
    private final static int MINUTE = 60 * SECOND;
    private final static int HOUR = 60 * MINUTE;
    private final static int DAY = 24 * HOUR;
    private final static int MONTH = 30 * DAY;
    private final static int YEAR = 12 * MONTH;

    public static String getRelativeTime(String rawDate, Context context) {

        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(rawDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        long timeInMillis = calendar.getTimeInMillis();
        long currentTimeInMillis = System.currentTimeMillis();
        int secondsPassed = (int) ((currentTimeInMillis - timeInMillis) / 1000);

        // Get seconds
        if (secondsPassed < Utility.MINUTE)
            return secondsPassed + " " + context.getString(R.string.seconds_ago);

        // Get within 2 minutes
        if (secondsPassed < 2 * Utility.MINUTE)
            return context.getString(R.string.a_minute_ago);

        // Get minutes
        if (secondsPassed < Utility.HOUR)
            return (int) (secondsPassed / MINUTE) + " " + context.getString(R.string.minutes_ago);

        // Get within 1 hour
        if (secondsPassed < 2 * Utility.HOUR)
            return context.getString(R.string.an_hour_ago);

        // Get hours
        if (secondsPassed < Utility.DAY)
            return (int) (secondsPassed / HOUR) + " " + context.getString(R.string.hours_ago);

        // Get within 2 days
        if (secondsPassed < 2 * Utility.DAY)
            return context.getString(R.string.yesterday);

        // Get days
        if (secondsPassed < Utility.MONTH)
            return (int) (secondsPassed / DAY) + " " + context.getString(R.string.days_ago);

        // Get months
        if (secondsPassed < Utility.YEAR)
            return (int) (secondsPassed / MONTH) + " " + context.getString(R.string.months_ago);

        // Get within 2 years
        if (secondsPassed < 2 * Utility.YEAR)
            return context.getString(R.string.a_year_ago);

        // Get years
        return (int) (secondsPassed / YEAR) + " " + context.getString(R.string.years_ago);
    }

    public static String getPrettyDateFromString(String rawDate) {

        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(rawDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] shortMonths = new DateFormatSymbols(Locale.ENGLISH).getShortMonths();

        return shortMonths[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.DAY_OF_MONTH) + ", " + calendar.get(Calendar.YEAR);
    }

    public static String getPrettyTimeFromString(String rawDate) {

        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(rawDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }


}
