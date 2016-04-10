package com.sxu.commonproject.bean;

import java.util.ArrayList;

/**
 * Created by juhg on 15/11/27.
 */
public class DesignerListBean extends BaseProtocolBean {

    public DesignerListDataBean data;

    public static class DesignerListDataBean extends BaseBean {
        public ArrayList<DesignerAdBean> intro_imgs;
        public ArrayList<SpecificDesignerItemBean> list;
    }

    public static class SpecificDesignerItemBean extends BaseBean {
        public String user_id;
        public String real_name;
        public String tag_text;
        public String user_icon;
        public String user_title;
        public String news_title;
        public String news_content;
    }

    public static class DesignerAdBean extends BaseBean {
        public String url;
        public String img;
    }
}
