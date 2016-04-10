package com.sxu.commonproject.bean;

import java.util.ArrayList;

/**
 * Created by juhg on 16/3/17.
 */
public class NearbyUserBean extends BaseProtocolBean {

    public ArrayList<NearbyUserItemBean> data;

    public static class NearbyUserItemBean extends BaseBean {
        public String id;
        public String icon;
        public String small_icon;
        public String nick_name;
        public String tel_number;
        public String gender;
        public String sign;
        public String distance;
        public String longitude;
        public String latitude;
    }
}
