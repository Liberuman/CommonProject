package com.sxu.commonproject.bean;

/*******************************************************************************
 * FileName: VersionBean
 * <p/>
 * Description: 版本更新
 * <p/>
 * Author: juhg
 * <p/>
 * Version: v1.0
 * <p/>
 * Date: 16/4/13
 * <p/>
 * Copyright: all rights reserved by zhinanmao.
 *******************************************************************************/
public class VersionBean extends BaseProtocolBean {

    public VersionItemBean data;

    public static class VersionItemBean extends BaseBean {
        public String mustUpdate;
        public String version;
        public int verCode;
        public String desc;
        public String downloadUrl;
    }
}
