package ru.geekbrains.photofinder.utils;


import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.geekbrains.photofinder.R;

public class DateTimeUtils {
    public static String convertPrefDataToUnix(Context context, String inboxDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(context.getString(
                R.string.pref_date_picker_date_format), Locale.getDefault());
        Date date = dateFormat.parse(inboxDate);
        long unixTime = date.getTime() / 1000;
        return String.valueOf(unixTime);
    }
}
