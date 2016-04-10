package com.sxu.commonproject.protocol;

/**
 * Created by juhg on 16/2/26.
 */
public class ServerConfig {

    // 获取最新的活动列表
    public static String LATEST_ACTIVITIES = "ActivityController/get_latest_activities?page=%s";
    // 获取指定类型的活动列表
    public static String SPECIFIC_TYPE_ACTIVITIES = "ActivityController/get_specific_type_activities?type=%s";
    // 获取指定的活动信息
    public static String SPECIFIC_ACTIVITY = "ActivityController/get_specific_activity?id=%s";
    // 添加新的活动
    public static String ADD_ACTIVITY = "ActivityController/add_activity";
    // 删除指定活动
    public static String DEL_ACTIVITY = "ActivityController/del_activity";
    // 更新活动信息
    public static String UPDATE_ACTIVITY = "ActivityController/update_activity";
    // 获取所有的活动类型
    public static String ALL_ACTIVITY_TYPE = "ActivityController/get_all_activity_type";
    // 提交意见
    public static String SUBMIT_SUGGESTION = "ActivityController/submit_suggestion";
    // 手机号码登录
    public static String LOGIN_BY_CODE = "UserController/login_by_code?tel_number=%s&code=%s";
    // 账号登录
    public static String LOGIN_BY_ACCOUNT = "UserController/login_by_account?tel_number=%s&passwd=%s";
    // 获取短信验证码
    public static String GET_SMS_CODE = "UserController/get_sms_code?tel_number=%s";
    // 获取短信验证码
    public static String REGISTER = "UserController/register?tel_number=%s&passwd=%s";
    // 验证手机号码
    public static String VERTIFY_TELNUMBER = "UserController/vertify_telnumber?tel_number=%s";
    // 验证验证码
    public static String VERTIFY_CODE = "UserController/vertify_code?tel_number=%s&code=%s";
    // 修改密码
    public static String RESTE_PASSWD = "UserController/reset_passwd?tel_number=%s&new_passwd=%s";
    // 获取指定用户信息
    public static String GET_USER_INFO = "UserController/get_user_info?uid=%s";
    // 获取图片上传Token
    public static String GET_TOKEN = "UserController/get_token";
    // 上传用户头像
    public static String UPLOAD_ICON = "UserController/modify_user_icon";
    // 更新用户信息
    public static String UPDATE_USER_INFO = "UserController/update_user_info";
    // 获取附近的人
    public static String GET_NEARBY_USERS = "UserController/get_nearby_users?lng=%s&lat=%s";

    public static String getServerAddress() {
        return "http://139.196.153.190/";
    }

    public static String urlWithSuffix(String suffix) {
        return getServerAddress() + suffix;
    }
}
