package com.sxu.commonproject.util;

import java.text.DecimalFormat;

/**
 * Created by juhg on 16/2/26.
 */
public class DistanceFormatUtil {

    public static String getDistance(float distance) {
        return distance + "千米";
    }

    public static String getFormatDistance(float distance) {
        LogUtil.i("distance=====" + distance);
        DecimalFormat df = new DecimalFormat("0.0");
        if (distance > 1000) {
            LogUtil.i("distance format=====" + distance/1000.0f);
            return df.format(distance/1000.0f) + "公里";
        } else {
            return df.format(distance) + "米";
        }
    }
}
