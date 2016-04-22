package com.sxu.commonproject.activity;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.AndroidPlatformUtil;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by juhg on 15/9/30.
 */
public class NewUserGuideActivity extends BaseActivity {

    private ImageView entryIcon;
    private ViewPager viewPager;
    private boolean mIsFirst, mIsJump;
    private View[] indicator = new View[3];
    private int[] guideImage = new int[]{
            R.drawable.splash_bg,
            R.drawable.splash_bg,
            R.drawable.splash_bg,
    };

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_guide_layout;
    }

    @Override
    protected void getViews() {
        entryIcon = (ImageView) findViewById(R.id.entry_icon);
        viewPager = (ViewPager) findViewById(R.id.guide_viewpager);
        indicator[0] = findViewById(R.id.indicator0);
        indicator[1] = findViewById(R.id.indicator1);
        indicator[2] = findViewById(R.id.indicator2);
    }

    @Override
    protected void initActivity() {
        viewPager.setOffscreenPageLimit(5);
        entryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TelephonyManager tm = (TelephonyManager) NewUserGuideActivity.this.getSystemService(TELEPHONY_SERVICE);//得到手机管理者
                //PhoneInfo pi = new PhoneInfo(android.os.Build.BRAND, android.os.Build.VERSION.RELEASE, tm.getSubscriberId(), 2);
                //sendCommit(pi);
                sendCommit();
            }
        });

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return guideImage.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object obj) {
                return view == (View) obj;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(NewUserGuideActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageResource(guideImage[position]);
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ImageView imageView = new ImageView(NewUserGuideActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageResource(guideImage[position]);
                container.removeView(imageView);
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
                if (position == guideImage.length - 1) {
                    if (!mIsFirst && !mIsJump) {
                        mIsJump = true;
                        //跳转去首页
                        TelephonyManager tm = (TelephonyManager) NewUserGuideActivity.this.getSystemService(TELEPHONY_SERVICE);//得到手机管理者
                        //PhoneInfo pi = new PhoneInfo(android.os.Build.BRAND, android.os.Build.VERSION.RELEASE, tm.getSubscriberId(), 2);
                        //sendCommit(pi);
                        sendCommit();
                    } else {
                        mIsFirst = false;
                    }
                } else {
                    mIsFirst = true;
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == guideImage.length - 1) {
                    entryIcon.setVisibility(View.VISIBLE);
                } else {
                    entryIcon.setVisibility(View.GONE);
                }
                selectCurrentDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void selectCurrentDot(int position) {
        for (int i = 0; i < indicator.length; i++) {
            if (i == position) {
                indicator[i].setBackgroundResource(R.drawable.indicator_selected);
            } else {
                indicator[i].setBackgroundResource(R.drawable.indicator_unselected);
            }
        }
    }

    //用post方提交数据给法服务器
    private void sendCommit() {
        // http post commit
//        ZnmHttpQuery<PhoneInfo> customQuery = new ZnmHttpQuery<PhoneInfo>(this, PhoneInfo.class, null);
//        Map<String, String> reqMap = new HashMap<String, String>();
//        reqMap.put("phoneType", reqBean.phonedevice);
//        reqMap.put("version", reqBean.version);
//        reqMap.put("deviceToken", reqBean.solesign);
//        reqMap.put("imei", AndroidPlatformUtil.getDeviceID(this));
//        reqMap.put("android_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
//        reqMap.put("v", AndroidPlatformUtil.getAppVersionName(this));
//        reqMap.put("refer", SourceConfig.getSourceId(this));
//        reqMap.put("mac_addr", AndroidPlatformUtil.getMacAddress(this));
//        reqMap.put("factory", URLEncoder.encode(Build.MANUFACTURER));
//        reqMap.put("plant", reqBean.plant + "");
//
//        customQuery.doPostQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.PHONE_INFO,
//                ZnmApplication.getXGToken())), reqMap);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}