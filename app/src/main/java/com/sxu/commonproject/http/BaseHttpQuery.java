package com.sxu.commonproject.http;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.sxu.commonproject.bean.BaseBean;
import com.sxu.commonproject.util.LogUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by juhg on 16/2/17.
 */
public class BaseHttpQuery<T extends BaseBean> {

    private Context context;
    private Class<T> TClass;
    private OnQueryFinishListener queryFinishListener;
    private Handler mainHandler;
    private BaseHttpClient httpClient;

    private final int RESPONSE_CODE_DEFAULT = 100;        // 默认的应答code
    private final int RESPONSE_CODE_EMPTY = 0;            // 数据请求为空
    private final int RESPONSE_CODE_PARSE_ERROR = 2;      // 数据解析错误

    public BaseHttpQuery(Context context, Class<T> TClass, OnQueryFinishListener<T> queryFinishListener) {
        this.context = context;
        this.TClass = TClass;
        this.mainHandler = new Handler(context.getMainLooper());
        this.queryFinishListener = queryFinishListener;
        this.httpClient = BaseHttpClient.getInstance();
    }

    private void asyncGet(String url, Map<String, String> param, Callback responseCallback) {
        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
        // 追加请求的参数
        if (param != null) {
            Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
            while (iterator.hasNext()) {
                uriBuilder.appendQueryParameter(iterator.next().getKey(), iterator.next().getValue());
            }
        }

        String realUrl = uriBuilder.build().toString();
        Request request = new Request.Builder().url(realUrl).build();
        httpClient.newCall(request).enqueue(responseCallback);
    }

    private void asyncPost(String url, Map<String, String> param, Callback responseCallback) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (param != null) {
            Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> item = iterator.next();
                formBuilder.add(item.getKey(), item.getValue());
            }
        }
        FormBody formBody = formBuilder.build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        LogUtil.i("request==" + request.body());
        httpClient.newCall(request).enqueue(responseCallback);
    }

    private void asyncPost(String url, byte[] data, Callback responseCallback) {

    }

    public void doGetQuery(String url) {
        doGetQuery(url, null);
    }

    public void doGetQuery(String url, Map<String, String> param) {
        asyncGet(url, param, new MyCallBack());
    }

    public void doPostQuery(String url, Map<String, String> param) {
        asyncPost(url, param, new MyCallBack());
    }

    public void doPostQuery(String url, byte[] data) {
        asyncPost(url, data, new MyCallBack());
    }

    private class MyCallBack implements Callback {
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final Response respon = response;
            final String content = respon.body().string();
            if (response.isSuccessful()) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(content)) {
                            try {
                                queryFinishListener.onFinish(BaseBean.fromJson(content, TClass));
                            } catch (Exception e) {
                                e.printStackTrace(System.out);
                                queryFinishListener.onError(RESPONSE_CODE_PARSE_ERROR, "数据解析错误");
                            }
                        } else {
                            queryFinishListener.onError(RESPONSE_CODE_EMPTY, "数据请求为空");
                        }
                    }
                });
            } else {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        queryFinishListener.onError(respon.code(), respon.message());
                    }
                });
            }
        }

        @Override
        public void onFailure(Call call, IOException e) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    queryFinishListener.onError(RESPONSE_CODE_DEFAULT, "您好像没有连接网络哦");
                }
            });
        }
    }

    public interface OnQueryFinishListener<T extends BaseBean> {
        public void onFinish(T bean);
        public void onError(int errCode, String errMsg);
    }
}
