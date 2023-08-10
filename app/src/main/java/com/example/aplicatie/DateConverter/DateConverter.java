package com.example.aplicatie.DateConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private static final SimpleDateFormat formatterReminder = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());

    public static Date fromString(String date) {
        try{
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String fromDate(Date date) {
        if(date == null) {
            return null;
        }
        return formatter.format(date);
    }

    public static String fromReminder(Date date) {
        if(date == null) {
            return null;
        }
        return formatterReminder.format(date);
    }
}
