package com.sxu.commonproject.bean;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by juhg on 16/2/17.
 */
public abstract class BaseBean implements Serializable {
    public String toJson() {
        return toJson(this);
    }

    public static String toJson(BaseBean bean) {
        if (bean != null) {
            return new Gson().toJson(bean);
        }

        return null;
    }

    public static <T extends BaseBean> T fromJson(String content, Class<T> tClass) {
        if (!TextUtils.isEmpty(content)) {
            return new Gson().fromJson(content, tClass);
        }

        return null;
    }
}
