package com.sxu.commonproject.bean;

/**
 * Created by juhg on 16/2/29.
 */
public class SuggestionBean extends BaseProtocolBean {

    public SuggestionItemBean data;

    public static class SuggestionItemBean extends BaseBean {
        public String id;
        public String content;
        public String tel_number;
        public String time;
    }
}
