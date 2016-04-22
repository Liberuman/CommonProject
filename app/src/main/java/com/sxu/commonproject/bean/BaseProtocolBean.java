package com.sxu.commonproject.bean;

import java.io.Serializable;

/**
 * Created by juhg on 16/2/18.
 */
public class BaseProtocolBean extends BaseBean implements Serializable {
    /**
     * 1表示请求成功， 0表示数据为空， -1表示未登录
     */
    public int code;
    public String msg;
}
