package ru.geekbrains.photofinder.utils;


import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.geekbrains.photofinder.R;

public class DateTimeUtils {
    public static String convertPrefDataToUnix(Context context, String inboxDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(context.getString(
                R.string.pref_date_picker_date_format), Locale.getDefault());
        Date date = dateFormat.parse(inboxDate);
        long unixTime = date.getTime() / context.getResources().getInteger(
                R.integer.millisecond_in_second);
        return String.valueOf(unixTime);
    }

    public static String convertUnixDate(Context context, long date) {
        DateFormat f = new SimpleDateFormat(context.getString(R.string.date_format), Locale.getDefault());
        String convertDate = f.format(date);
        return convertDate;
    }
}
