package com.sxu.commonproject.util;

import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

/*******************************************************************************
 * FileName: NetworkUtil
 * <p/>
 * Description:
 * <p/>
 * Author: juhg
 * <p/>
 * Version: v1.0
 * <p/>
 * Date: 16/4/13
 * <p/>
 * Copyright: all rights reserved by zhinanmao.
 *******************************************************************************/
public class NetworkUtil {

    /**
     * 网络类型
     */
    public static enum NETWORK_TYPE {
        NETWORK_TYPE_WIFI,
        NETWORK_TYPE_2G,
        NETWORK_TYPE_3G,
        NETWORK_TYPE_4G,
        NETWORK_TYPE_UNKNOWN
    }

    /**
     * 运营商类型
     */
    public static enum CARRIER_TYPE {
        CARRIER_TYPE_CMCC,
        CARRIER_TYPE_CUCC,
        CARRIER_TYPE_CTCC,
        CARRIER_TYPE_UNKNOWN
    }

    /**
     * 判断当前是否已联网
     * @param context
     * @return
     */
    public static boolean isValidNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkinfo = cm.getActiveNetworkInfo();
            if (networkinfo != null) {
                return networkinfo.isConnected();
            }
        }

        return false;
    }

    /**
     * 判断手机网络是否可用
     * @param context
     * @return
     */
    public static boolean isValidMobieNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkinfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkinfo != null) {
                return networkinfo.isConnected();
            }
        }

        return false;
    }

    /**
     * 判断Wifi网络是否可用
     * @param context
     * @return
     */
    public static boolean isValidWifiNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkinfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkinfo != null) {
                return networkinfo.isConnected();
            }
        }

        return false;
    }

    /**
     * 获取SIM卡的运营商类型
     * @param context
     * @return
     */
    public static CARRIER_TYPE getCarrierType(Context context) {
        CARRIER_TYPE carrierType = CARRIER_TYPE.CARRIER_TYPE_UNKNOWN;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = null;
        if (telephonyManager != null) {
            IMSI = telephonyManager.getSubscriberId();
            if (IMSI != null) {
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                    carrierType = CARRIER_TYPE.CARRIER_TYPE_CMCC;
                } else if (IMSI.startsWith("46001")) {
                    carrierType = CARRIER_TYPE.CARRIER_TYPE_CUCC;
                } else if (IMSI.startsWith("46003")) {
                    carrierType = CARRIER_TYPE.CARRIER_TYPE_CTCC;
                } else {
                    carrierType = CARRIER_TYPE.CARRIER_TYPE_UNKNOWN;
                }
            }
        }

        return carrierType;
    }

    /**
     * 获取SIM卡的运营商类型
     * @param context
     * @return
     */
    public static String getCarrierName(Context context) {
        String carrierName = "UNKNOWN";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = null;
        if (telephonyManager != null) {
            IMSI = telephonyManager.getSubscriberId();
            if (IMSI != null) {
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                    carrierName = "CMCC";
                } else if (IMSI.startsWith("46001")) {
                    carrierName = "CUCC";
                } else if (IMSI.startsWith("46003")) {
                    carrierName = "CTCC";
                } else {
                    carrierName = "UNKNOWN";
                }
            }
        }

        return carrierName;
    }

    /**
     * 获取网络类型
     * @param context
     * @return
     */
    public static NETWORK_TYPE getNetworkType(Context context) {
        NETWORK_TYPE networkType = NETWORK_TYPE.NETWORK_TYPE_UNKNOWN;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                int type = networkInfo.getType();
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                        switch (type) {
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                networkType = NETWORK_TYPE.NETWORK_TYPE_2G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                networkType = NETWORK_TYPE.NETWORK_TYPE_3G;
                                break;
                            default:
                                networkType = NETWORK_TYPE.NETWORK_TYPE_UNKNOWN;
                                break;
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            if (type == TelephonyManager.NETWORK_TYPE_HSPAP) {
                                networkType = NETWORK_TYPE.NETWORK_TYPE_3G;
                            }
                        } else {
                            if (type == TelephonyManager.NETWORK_TYPE_EHRPD) {
                                networkType = NETWORK_TYPE.NETWORK_TYPE_3G;
                            } else if (type == TelephonyManager.NETWORK_TYPE_LTE) {
                                networkType = NETWORK_TYPE.NETWORK_TYPE_4G;
                            }
                        }
                    } else {
                        networkType = NETWORK_TYPE.NETWORK_TYPE_UNKNOWN;
                    }
                } else if (type == ConnectivityManager.TYPE_WIFI){
                    networkType = NETWORK_TYPE.NETWORK_TYPE_WIFI;
                } else {
                    networkType = NETWORK_TYPE.NETWORK_TYPE_UNKNOWN;
                }
            }
        }

        return networkType;
    }

    public static String getNetworkName(Context context) {
        String networkName = "UNKNOWN";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                int type = networkInfo.getType();
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                        switch (type) {
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                networkName = "2G";
                                break;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                networkName = "3G";
                                break;
                            default:
                                networkName = "UNKNOWN";
                                break;
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            if (type == TelephonyManager.NETWORK_TYPE_HSPAP) {
                                networkName = "3G";
                            }
                        } else {
                            if (type == TelephonyManager.NETWORK_TYPE_EHRPD) {
                                networkName = "3G";
                            } else if (type == TelephonyManager.NETWORK_TYPE_LTE) {
                                networkName = "4G";
                            }
                        }
                    } else {
                        networkName = "UNKNOWN";
                    }
                } else if (type == ConnectivityManager.TYPE_WIFI){
                    networkName = "WIFI";
                } else {
                    networkName = "UNKNOWN";
                }
            }
        }

        return networkName;
    }
}
