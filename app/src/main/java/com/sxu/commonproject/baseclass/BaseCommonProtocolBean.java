package com.sxu.commonproject.baseclass;

import com.sxu.commonproject.bean.BaseProtocolBean;

/**
 * Created by juhg on 16/3/9.
 *
 * 目的：解决插入操作成功后需要返回ID的情况。
 *
 * 原因：使用BaseProtocolBean时无法接受到返回的Data数据，如果自定义相应的Bean，却只有一个字段，所以抽象出了此类。
 */
public class BaseCommonProtocolBean extends BaseProtocolBean {
    public String data;
}
