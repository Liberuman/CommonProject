package com.sxu.commonproject.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * Created by juhg on 16/2/17.
 */
public class AndroidPlatformUtil {

    /**
     * 获取手机的DeviceID；
     * @param context
     * @return
     */
    public static String getPhoneDeviceId(Context context) {
        TelephonyManager telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telManager == null ? null : telManager.getDeviceId();
    }

    /**
     * 获取手机的Mac地址
     * @param context
     * @return
     */
    public static String getWifiMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.getConnectionInfo() != null) {
            return wifiManager.getConnectionInfo().getMacAddress();
        }

        return null;
    }

    /**
     * 获取屏幕的宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕的高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕客户区域的高度（除状态栏）
     * @param context
     * @return
     */
    public static int getClientHeight(Context context) {
        return getScreenHeight(context) - getStatusBarHeight(context);
    }

    /**
     * 获取屏幕状态栏的高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Rect clientRect = new Rect();
        if (context instanceof Activity) {
            ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(clientRect);
            return clientRect.top;
        }

        return 0;
    }

    /**
     * dp转换为px
     * @param context
     * @param dp
     * @return
     */
    public static int dpToPx(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    /**
     * px转换为dp
     * @param context
     * @param px
     * @return
     */
    public static int pxTodp(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(px / scale + 0.5f);
    }

    /**
     * 获取应用的版本号
     * @param context
     * @return
     */
    public static String getVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取版本的code
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Integer.MAX_VALUE;
    }

    /**
     * 获取渠道号
     * @param context
     * @return
     */
    public static String getChannel(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm != null) {
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (appInfo != null && appInfo.metaData != null) {
                    return appInfo.metaData.get("channelNo") + "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }
}
