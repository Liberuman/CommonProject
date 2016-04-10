package com.sxu.commonproject.bean;

/**
 * Created by juhg on 16/2/22.
 */
public class ActivityItemBean extends BaseBean {

    public int icon;
    public String desc;

    public ActivityItemBean(int icon, String desc) {
        this.icon = icon;
        this.desc = desc;
    }
}
