package com.sxu.commonproject.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by juhg on 16/3/16.
 */
public class TimeFormatUtil {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60;
    private static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;

    /**
     * 将字符串的时间戳转换成long类型
     * @param time
     * @return
     */
    public static long strTimeToLong(String time) {
        if (!TextUtils.isEmpty(time)) {
            try {
                return Long.parseLong(time);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
        return 0;
    }

    /**
     * 讲时间戳进行格式化，具体规则如下：
     * 今天：hh:mm; 昨天：昨天; 一周内：星期; 今年：MM月dd日; 其他：yyyy-MM-dd
     * @param milliSecond
     * @return
     */
    public static String getTimeDesc(long milliSecond) {
        Date oldTime = new Date(milliSecond);
        Date currentTime = new Date();
        SimpleDateFormat sdf = null;
        String dateDesc = "";

        if (currentTime.getYear() == oldTime.getYear()) {
            if (currentTime.getMonth() == oldTime.getMonth()) {
                if (currentTime.getDate() == oldTime.getDate()) {
                    sdf = new SimpleDateFormat("HH:mm");
                    dateDesc = sdf.format(oldTime);
                } else if (currentTime.getDate() - oldTime.getDate() == 1) {
                    dateDesc = "昨天";
                } else if (currentTime.getDate() - oldTime.getDate() < 7){
                    sdf = new SimpleDateFormat("EEEE");
                    dateDesc = sdf.format(oldTime);
                } else {
                    sdf = new SimpleDateFormat("MM-dd");
                    dateDesc = sdf.format(oldTime);
                }
            } else {
                sdf = new SimpleDateFormat("MM-dd");
                dateDesc = sdf.format(oldTime);
            }
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateDesc = sdf.format(oldTime);
        }

        return dateDesc;
    }
}
