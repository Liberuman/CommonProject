package com.sxu.commonproject.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by juhg on 16/3/16.
 */
public class TimeUtil {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60;
    private static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;

    public static String longTimeToStr(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(time));
    }

    public static Date strTimeToLong(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        return date != null ? date : null;
    }

    public static String getTimeDesc(Date oldTime) {
        Date currentTime = new Date();
        SimpleDateFormat sdf = null;
        if (currentTime.getDate() == oldTime.getDate()) {
            if (currentTime.getHours() == oldTime.getHours() && currentTime.getMinutes() - oldTime.getMinutes() < 5) {
                return "刚刚";
            } else  {
                sdf = new SimpleDateFormat("HH:mm");
                return sdf.format(oldTime);
            }
        } else if (currentTime.getDate() - oldTime.getDate() == 1) {
            return "昨天";
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(oldTime);
        }
    }
}
