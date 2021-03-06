package ru.geekbrains.photofinder.utils;


import android.content.Context;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.geekbrains.photofinder.R;

public class DateTimeUtils {
    public static String convertPrefDataToUnix(Context context, String inboxDate) throws ParseException {
        if (!TextUtils.isEmpty(inboxDate)) {
            DateFormat dateFormat = new SimpleDateFormat(context.getString(
                    R.string.pref_date_picker_date_format), Locale.getDefault());
            Date date = dateFormat.parse(inboxDate);
            long unixTime = date.getTime() / context.getResources().getInteger(
                    R.integer.millisecond_in_second);
            return String.valueOf(unixTime);
        }
        return null;
    }

    public static String convertUnixDate(Context context, long date) {
     /*   DateFormat f = new SimpleDateFormat(context.getString(R.string.date_format), Locale.getDefault());
        String convertDate = f.format(date);
        return convertDate;*/

        Date d = new Date(date * 1000L);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(d);
    }
}
