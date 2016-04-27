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
        public long create_time;
        public int type;
        public String comment;
        public String activity_time;
        public float longitude;
        public float latitude;

        @Override
        public int compareTo(ActivityItemBean another) {
            if (!TextUtils.isEmpty(distance) && !TextUtils.isEmpty(another.distance)
                    && Float.parseFloat(distance) < Float.parseFloat(another.distance)) {
                return -1;
            }

            return 1;
        }
    }
}
