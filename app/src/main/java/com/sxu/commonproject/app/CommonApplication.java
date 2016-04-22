package com.sxu.commonproject.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
//import com.avos.avoscloud.AVOSCloud;
//import com.avos.avoscloud.im.v2.AVIMClient;
//import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.umeng.socialize.PlatformConfig;
import com.sxu.commonproject.manager.UserManager;
import com.sxu.commonproject.bean.UserBean;
import com.sxu.commonproject.service.LocationService;
import com.sxu.commonproject.service.ReceiverMsgService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by juhg on 16/2/17.
 */
public class CommonApplication extends Application {

    private static Context context;
    private static Typeface typeface;
    public static boolean isLogined = false;
    public static UserBean.UserItemBean userInfo;
    public static AVIMClient client;
    public static AMapLocation location;
    public static Map<String, AVIMConversation> conversationMap = new HashMap<String, AVIMConversation>();

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        initGlide();
        initUmengShare();
        AVOSCloud.initialize(this, "uUn9uI5ABWJrjTfaLB3bCffD-gzGzoHsz", "50bTpIIB5Vs0kjOUsOY9RmFm");
        AVOSCloud.setDebugLogEnabled(true);

        typeface = Typeface.createFromAsset(getAssets(), "fzlt.ttf");
        userInfo = UserManager.getInstance(this).getUserInfo();
        if (!TextUtils.isEmpty(userInfo.token)) {
            isLogined = true;
            startService(new Intent(this, ReceiverMsgService.class));
        }
        startService(new Intent(this, LocationService.class));
    }

    public static Context getInstance() {
        return context;
    }
    /**
     * 更换Glide网络库
     */
    private void initGlide() {
        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient()));
    }

    public static void setTypeface(TextView textView) {
        if (typeface != null) {
            textView.setTypeface(typeface);
        }
    }

    private void initUmengShare() {
        PlatformConfig.setWeixin("wxa523383662f16855", "cef59149b7db02f27a49a54c1b30ecc9");
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
    }
}
