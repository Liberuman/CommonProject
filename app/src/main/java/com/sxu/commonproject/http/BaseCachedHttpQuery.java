package com.sxu.commonproject.http;

import android.content.Context;
import android.text.TextUtils;

import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.ACache;
import com.sxu.commonproject.bean.BaseBean;
import com.sxu.commonproject.bean.BaseProtocolBean;
import com.sxu.commonproject.util.MD5Util;
import com.sxu.commonproject.util.NetworkUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/*******************************************************************************
 * FileName: BaseCachedHttpQuery
 * <p/>
 * Description: 带缓存的Http请求
 * <p/>
 * Author: juhg
 * <p/>
 * Version: v1.0
 * <p/>
 * Date: 16/4/14
 * <p/>
 * Copyright: all rights reserved by zhinanmao.
 *******************************************************************************/
public class BaseCachedHttpQuery<T extends BaseBean> extends BaseHttpQuery {

    private ACache cache;
    private String url;

    public BaseCachedHttpQuery(Context context, Class<T> TClass, BaseHttpQuery.OnQueryFinishListener<T> queryFinishListener) {
        super(context, TClass, queryFinishListener);
        cache = ACache.get(CommonApplication.getInstance());
    }

    @Override
    public void doGetQuery(String url) {
        this.url = url;
        if (!NetworkUtil.isValidWifiNetwork(CommonApplication.getInstance())) {
            String content = cache.getAsString(MD5Util.toMD5(replaceUrl(url)));
            if (!TextUtils.isEmpty(content)) {
                queryFinishListener.onFinish(BaseBean.fromJson(content, TClass));
            } else {
                queryFinishListener.onError(RESPONSE_CODE_DEFAULT, "您好像没有连接网络哦");
            }
        } else {
            super.doGetQuery(url, null, new MyCallBack());
        }
    }

    private class MyCallBack implements Callback {
        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            final String content = response.body().string();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    String cacheData = cache.getAsString(MD5Util.toMD5(replaceUrl(url)));
                    if (!TextUtils.isEmpty(content)) {
                        try {
                            queryResult = BaseBean.fromJson(content, TClass);
                            if (queryResult instanceof BaseProtocolBean) {
                                if (((BaseProtocolBean) queryResult).code == 1) {
                                    queryFinishListener.onFinish(queryResult);
                                    cache.put(MD5Util.toMD5(replaceUrl(url)), content, 2 * ACache.TIME_DAY);
                                } else {
                                    queryFinishListener.onError(((BaseProtocolBean) queryResult).code, ((BaseProtocolBean) queryResult).msg);
                                }
                            } else {
                                queryFinishListener.onError(RESPONSE_CODE_PARSE_ERROR, "Model对象没有继承BaseProtocolBean");
                            }
                        } catch (Exception e) {
                            e.printStackTrace(System.out);
                            if (!TextUtils.isEmpty(cacheData)) {
                                queryFinishListener.onFinish(BaseBean.fromJson(content, TClass));
                            } else {
                                queryFinishListener.onError(RESPONSE_CODE_PARSE_ERROR, "数据解析错误");
                            }
                        }
                    } else {
                        if (!TextUtils.isEmpty(cacheData)) {
                            queryFinishListener.onFinish(BaseBean.fromJson(content, TClass));
                        } else {
                            queryFinishListener.onError(RESPONSE_CODE_EMPTY, "服务器返回数据为空");
                        }
                    }
                }
            });
        }

        @Override
        public void onFailure(Call call, IOException e) {
            final String content = cache.getAsString(MD5Util.toMD5(replaceUrl(url)));
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(content)) {
                        queryFinishListener.onFinish(BaseBean.fromJson(content, TClass));
                    } else {
                        queryFinishListener.onError(RESPONSE_CODE_DEFAULT, "您好像没有连接网络哦");
                    }
                }
            });
        }
    }

    String replaceUrl(String str){
        return str.replaceAll("&network=.*?&", "&").replaceAll("&carrier=.*?&", "&").replaceAll("&sign=.*", "");
    }
}
