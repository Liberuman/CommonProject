package com.sxu.commonproject.manager;

import android.content.Context;

import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.bean.UserBean;
import com.sxu.commonproject.view.PreferenceUtil;

/**
 * Created by juhg on 16/3/8.
 */
public class UserManager {

    private Context context;
    private UserBean.UserItemBean userInfo;

    public static String USER_ID = "userId";
    public static String USER_ICON = "userIcon";
    public static String USER_NAME = "userName";
    public static String USER_TEL_NUMBER = "telNumber";
    public static String USER_SIGN = "userSign";

    private UserManager(Context context) {
        this.context = context;
    }

    public static UserManager getInstance(Context context) {
        return new UserManager(context);
    }

    public void saveUserInfo(UserBean.UserItemBean userInfo) {
        this.userInfo = userInfo;
        CommonApplication.userInfo = userInfo;
        PreferenceUtil.putString(context, USER_ID, userInfo.id);
        PreferenceUtil.putString(context, USER_ICON, userInfo.icon);
        PreferenceUtil.putString(context, USER_NAME, userInfo.nick_name);
        PreferenceUtil.putString(context, USER_TEL_NUMBER, userInfo.tel_number);
        PreferenceUtil.putString(context, USER_SIGN, userInfo.sign);
    }

    public UserBean.UserItemBean getUserInfo() {
        if (userInfo == null) {
            userInfo = new UserBean.UserItemBean();
        }
        userInfo.id = PreferenceUtil.getString(context, USER_ID);
        userInfo.icon = PreferenceUtil.getString(context, USER_ICON);
        userInfo.nick_name = PreferenceUtil.getString(context, USER_NAME);
        userInfo.tel_number = PreferenceUtil.getString(context, USER_TEL_NUMBER);
        userInfo.sign = PreferenceUtil.getString(context, USER_SIGN);

        return userInfo;
    }

    public void clearUserInfo() {
        userInfo = null;
        PreferenceUtil.putString(context, USER_ID, "");
        PreferenceUtil.putString(context, USER_ICON, "");
        PreferenceUtil.putString(context, USER_NAME, "");
        PreferenceUtil.putString(context, USER_TEL_NUMBER, "");
        PreferenceUtil.putString(context, USER_SIGN, "");
    }

    public String getUserId() {
        return PreferenceUtil.getString(context, USER_ID);
    }

    public String getUserName() {
        return PreferenceUtil.getString(context, USER_NAME);
    }

    public String getUserIcon() {
        return PreferenceUtil.getString(context, USER_ICON);
    }
}
