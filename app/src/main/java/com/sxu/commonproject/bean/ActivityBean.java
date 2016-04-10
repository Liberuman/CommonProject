package com.sxu.commonproject.bean;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by juhg on 16/2/22.
 */
public class ActivityBean extends BaseProtocolBean {

    public ArrayList<ActivityItemBean> data;

    public static class ActivityItemBean extends BaseBean implements Comparable<ActivityItemBean>{
        public String id;
        public String user_id;
        public String title;
        public String user_icon;
        public String user_name;
        public String destination;
        public String distance;
        public String create_time;
        public String type;
        public String status;
        public String number_of_person;

        @Override
        public int compareTo(ActivityItemBean another) {
            if (!TextUtils.isEmpty(distance) && !TextUtils.isEmpty(another.distance)
                    && Float.parseFloat(distance) < Float.parseFloat(another.distance)) {
                return -1;
            }

            return 1;
        }
    }



//    public ActivityBean() {
//        title = "周六去华夏公园打篮球";
//        user_name = "Freeman";
//        distance = "2公里";
//        create_time = "2016－2-20";
//    }
}
