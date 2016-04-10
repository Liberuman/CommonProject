package com.sxu.commonproject.bean;

/**
 * Created by juhg on 16/3/3.
 */
public class EventBusBean {

    public static class UpdateActivityBean {

        public boolean needUpdate;

        public UpdateActivityBean(boolean needUpdate) {
            this.needUpdate = needUpdate;
        }
    }

    public static class LoginBean {

        public UserBean.UserItemBean userInfo;

        public LoginBean(UserBean.UserItemBean userInfo) {
            this.userInfo = userInfo;
        }
    }

    public static class LogoutBean {

    }

    public static class UpdateMsgList {

        public ContactBean contactInfo;

        public UpdateMsgList(ContactBean contactInfo) {
            this.contactInfo = contactInfo;
        }
    }
}
