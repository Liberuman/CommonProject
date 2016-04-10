package com.sxu.commonproject.util;

/********************************************************************************
 *
 * FileName: ShowMsgCountReceiver.java
 *
 * CopyRright: 2015 all right reserved by zhinanmao
 *
 * Author: Ju Honggang
 *
 * Date: 2015-07-21
 *
 * Description: 在图标上显示推送消息的数目
 *
 * Version: 2.4
 ********************************************************************************/

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import java.lang.reflect.Field;

public class ShowBadgeNumberUtil {

    /**
     * 应用启动页面的类名
     */
    private final static String LANCHER_ACTIVITY_NAME = "SplashActivity.class.getName()";

    /********************************************************************************
     * FunctionName: chooseShowWay
     *
     * Description: 选择显示桌面角标的实现过程
     *
     * Parameter: context(上下文信息) number(信息的条数) notification（推送的内容）
     *
     * Return：void
     ********************************************************************************/
    public static void chooseShowWay(Context context, int number, Notification notification) {
        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {            // 小米手机
            showByXiaomi(context, number, notification);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {    // 三星手机
            showBySamsung(context, number);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("sony")) {       // 索尼手机
            showBySony(context, number);
        } else {
            /**
             * NOthing
             */
        }
    }

    /********************************************************************************
     * FunctionName: showBySamsung
     *
     * Description: 小米手机应用图标显示消息个数的实现
     *
     * Parameter: context(上下文信息) number(信息的条数)
     *
     * Return：void
     ********************************************************************************/
    private static void showByXiaomi(Context context, int number, Notification notification) {
        Class miuiNotificationClass = null;
        boolean isVersionGreaterMiui6 = false;
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            // MIUI6之后的版本采用反射调用的方式来实现
            miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, 5);
            field = notification.getClass().getDeclaredField("extraNotification");
            field.setAccessible(true);
            field.set(notification, miuiNotification);
            isVersionGreaterMiui6 = true;
        } catch (Exception e) {
            e.printStackTrace();

            // MIUI6之前的版本通过广播的方式来实现
            Intent intent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            intent.putExtra("android.intent.extra.update_application_component_name", context.getPackageName() + "/"+ LANCHER_ACTIVITY_NAME);
            intent.putExtra("android.intent.extra.update_application_message_text", number);
            context.sendBroadcast(intent);
        } finally {
            if (isVersionGreaterMiui6) {
                //notification.notify();
                //notificationManager.notify(0, notification);
            }
        }
    }

    /********************************************************************************
     * FunctionName: showBySamsung
     *
     * Description: 三星手机应用图标显示消息个数的实现
     *
     * Parameter: context(上下文信息) number(信息的条数)
     *
     * Return：void
     ********************************************************************************/
    private static void showBySamsung(Context context, int number) {
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", number);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", LANCHER_ACTIVITY_NAME);
        context.sendBroadcast(intent);
    }

    /********************************************************************************
     * FunctionName: showBySony
     *
     * Description: 索尼手机应用图标显示消息个数的实现
     *
     * Parameter: context(上下文信息) number(信息的条数)
     *
     * Return：void
     ********************************************************************************/
    private static void showBySony(Context context, int number) {
        Intent intent = new Intent();
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", LANCHER_ACTIVITY_NAME);
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", number);
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
        intent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        context.sendBroadcast(intent);
    }
}
