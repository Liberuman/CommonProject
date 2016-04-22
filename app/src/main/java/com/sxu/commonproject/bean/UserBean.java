package com.sxu.commonproject.bean;

/**
 * Created by juhg on 16/3/2.
 */
public class UserBean extends BaseProtocolBean {

    public UserItemBean data;

    public static class UserItemBean extends BaseBean {
        public String id;
        public String icon;
        public String small_icon;
        public String nick_name;
        public String tel_number;
        public String gender;
        public String signature;
        public String passwd;
        public String token;
        public String has_passwd;
        public String longitude;
        public String latitude;
        public String create_time;
    }

}

