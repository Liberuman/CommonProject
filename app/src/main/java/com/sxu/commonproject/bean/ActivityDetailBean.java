package com.sxu.commonproject.bean;

/**
 * Created by juhg on 16/2/22.
 */
public class ActivityDetailBean extends BaseProtocolBean {

    public ActivityItemBean data;

    public static class ActivityItemBean extends BaseBean {
        public String id;
        public String user_id;
        public String title;
        public String user_name;
        public String destination;
        public String distance;
        // 活动的创建时间
        public String create_time;
        // 活动的开始时间
        public String activity_time;
        public String type;
        public String status;
        public String comment;
        public String number_of_person;
    }
}
