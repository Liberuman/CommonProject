package com.sxu.commonproject.http;

import okhttp3.OkHttpClient;

/**
 * Created by juhg on 16/2/17.
 */
public class BaseHttpClient extends OkHttpClient {

    private BaseHttpClient() {

    }

    public static BaseHttpClient getInstance() {
        return SingletonHolder.instance;
    }

    public static class SingletonHolder {
        private static final BaseHttpClient instance = new BaseHttpClient();
    }
}
