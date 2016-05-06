package com.sxu.commonproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sxu.commonproject.bean.EventBusBean;
import com.sxu.commonproject.manager.VersionUpdateManager;
import com.sxu.commonproject.service.LocationService;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.SharePreferenceTag;
import com.sxu.commonproject.util.TimeFormatUtil;
import com.sxu.commonproject.view.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/*******************************************************************************
 * FileName: SplashActivity
 * <p/>
 * Description:
 * <p/>
 * Author: juhg
 * <p/>
 * Version: v1.0
 * <p/>
 * Date: 16/4/13
 *******************************************************************************/
public class SplashActivity extends Activity {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, LocationService.class));
        // 检查版本更新
        VersionUpdateManager updateManager = new VersionUpdateManager(this);
        updateManager.checkVersion();
        handler.sendEmptyMessageDelayed(1, 2000);
    }

//    private void checkEntryUserGuideOrMain() {
//        if (PreferenceUtil.getBoolean(SharePreferenceTag.IS_NEW_USER, true)) {
//            PreferenceUtil.putBoolean(SharePreferenceTag.IS_NEW_USER, false);
//            startActivity(new Intent(this, NewUserGuideActivity.class));
//        } else {
//            startActivity(new Intent(this, MainActivity.class));
//        }
//
//        finish();
//    }
}
