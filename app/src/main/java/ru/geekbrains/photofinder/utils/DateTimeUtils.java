package ru.geekbrains.photofinder.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

    public static String convertPrefDataToUnix(String MMddYYYY) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("MM//dd/yyyy");
        Date date = dateFormat.parse(MMddYYYY);
        long unixTime = date.getTime() / 1000;
        return String.valueOf(unixTime);
    }
}
