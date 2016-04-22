package com.sxu.commonproject.manager;

import android.content.Context;

import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.bean.UserBean;
import com.sxu.commonproject.util.SharePreferenceTag;
import com.sxu.commonproject.view.PreferenceUtil;

/**
 * Created by juhg on 16/3/8.
 */
public class UserManager {

    private Context context;
    private UserBean.UserItemBean userInfo;

    private UserManager(Context context) {
        this.context = context;
    }

    public static UserManager getInstance(Context context) {
        return new UserManager(context);
    }

    public void saveUserInfo(UserBean.UserItemBean userInfo) {
        this.userInfo = userInfo;
        CommonApplication.userInfo = userInfo;
        PreferenceUtil.putString(SharePreferenceTag.USER_ID, userInfo.id);
        PreferenceUtil.putString(SharePreferenceTag.USER_ICON, userInfo.icon);
        PreferenceUtil.putString(SharePreferenceTag.USER_NAME, userInfo.nick_name);
        PreferenceUtil.putString(SharePreferenceTag.USER_TEL_NUMBER, userInfo.tel_number);
        PreferenceUtil.putString(SharePreferenceTag.USER_SIGN, userInfo.signature);
        PreferenceUtil.putString(SharePreferenceTag.TOKEN, userInfo.token);
    }

    public UserBean.UserItemBean getUserInfo() {
        if (userInfo == null) {
            userInfo = new UserBean.UserItemBean();
        }
        userInfo.id = PreferenceUtil.getString(SharePreferenceTag.USER_ID);
        userInfo.icon = PreferenceUtil.getString(SharePreferenceTag.USER_ICON);
        userInfo.nick_name = PreferenceUtil.getString(SharePreferenceTag.USER_NAME);
        userInfo.tel_number = PreferenceUtil.getString(SharePreferenceTag.USER_TEL_NUMBER);
        userInfo.signature = PreferenceUtil.getString(SharePreferenceTag.USER_SIGN);
        userInfo.token = PreferenceUtil.getString(SharePreferenceTag.TOKEN);

        return userInfo;
    }

    public void clearUserInfo() {
        userInfo = null;
        PreferenceUtil.putString(SharePreferenceTag.USER_ID, "");
        PreferenceUtil.putString(SharePreferenceTag.USER_ICON, "");
        PreferenceUtil.putString(SharePreferenceTag.USER_NAME, "");
        PreferenceUtil.putString(SharePreferenceTag.USER_TEL_NUMBER, "");
        PreferenceUtil.putString(SharePreferenceTag.USER_SIGN, "");
        PreferenceUtil.putString(SharePreferenceTag.TOKEN, "");
    }

    public String getUserId() {
        return PreferenceUtil.getString(SharePreferenceTag.USER_ID);
    }

    public String getUserName() {
        return PreferenceUtil.getString(SharePreferenceTag.USER_NAME);
    }

    public String getUserIcon() {
        return PreferenceUtil.getString(SharePreferenceTag.USER_ICON);
    }
}
