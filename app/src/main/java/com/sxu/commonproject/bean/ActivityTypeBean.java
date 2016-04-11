package com.sxu.commonproject.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by juhg on 16/3/11.
 */
public class ActivityTypeBean extends BaseProtocolBean implements Serializable {

    public ArrayList<ActivityTypeItemBean> data;

    public static class ActivityTypeItemBean extends BaseBean implements Serializable {
        public String id;
        public String icon;
        public String type_name;
    }
}
