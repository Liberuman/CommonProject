package com.sxu.commonproject.bean;

import com.sxu.commonproject.R;

/**
 * Created by juhg on 16/2/23.
 */
public class ContactBean extends BaseBean {

    public String id;
    public int icon;
    public String nick_name;
    public String content;
    public String time;

    public ContactBean() {
        id = "1";
        icon = R.drawable.ic_launcher;
        nick_name = "Freeman";
        content = "欢迎来到指南猫旅行学院";
        time = "2月22日";
    }

    public ContactBean(String id, int icon, String nick_name, String content, String time) {
        this.id = id;
        this.icon = icon;
        this.nick_name = nick_name;
        this.content = content;
        this.time = time;
    }

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
