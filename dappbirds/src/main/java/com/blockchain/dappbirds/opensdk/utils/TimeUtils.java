package com.blockchain.dappbirds.opensdk.utils;


import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static String getSpaceTime(Long millisecond) {
        if (getDay(millisecond).equals(getDay(System.currentTimeMillis() / 1000))) {
            return "今天\t\t" + getTime(millisecond);
        } else {
            return getDateTime(millisecond);
        }
    }

    public static String getDateTime(Long millisecond) {
        millisecond = millisecond * 1000;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(millisecond);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    public static String getTime(Long millisecond) {
        millisecond = millisecond * 1000;
        Date date = new Date(millisecond);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String fmt = dateFormat.format(date);
        return fmt;
    }

    public static String getDay(Long millisecond) {
        millisecond = millisecond * 1000;
        Date date = new Date(millisecond);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fmt = dateFormat.format(date);
        return fmt;
    }
}
