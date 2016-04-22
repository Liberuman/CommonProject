package com.sxu.commonproject.bean;

import com.sxu.commonproject.R;

/**
 * Created by juhg on 16/2/23.
 */
public class ContactBean extends BaseBean {

    public String id;
    public String icon;
    public String nick_name;
    public String content;
    public String time;

    @Override
    public boolean equals(Object o) {
        if (id.equals(((ContactBean)o).id)) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "id=" + id + " nick_name=" + nick_name + " content=" + content + " time=" + time;
    }
}
