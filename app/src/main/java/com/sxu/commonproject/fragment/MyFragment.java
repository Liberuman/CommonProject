package com.sxu.commonproject.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.memory.BitmapPool;
import com.sxu.commonproject.R;
import com.sxu.commonproject.activity.AboutActivity;
import com.sxu.commonproject.activity.LaunchActivityActivity;
import com.sxu.commonproject.activity.LaunchedActivitiesActivity;
import com.sxu.commonproject.activity.LoginActivity;
import com.sxu.commonproject.activity.MyInfoActivity;
import com.sxu.commonproject.activity.SubmitSuggestionActivity;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.DownloadProgressListener;
import com.sxu.commonproject.baseclass.FileDownload;
import com.sxu.commonproject.baseclass.FileDownloaderManager;
import com.sxu.commonproject.bean.VersionBean;
import com.sxu.commonproject.manager.PathManager;
import com.sxu.commonproject.manager.UserManager;
import com.sxu.commonproject.bean.EventBusBean;
import com.sxu.commonproject.manager.VersionUpdateManager;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.view.ShareDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by juhg on 16/2/22.
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {

    private TextView nicknameText;
    private SimpleDraweeView loginIcon;
    private LinearLayout launchActivityLayout;
    private LinearLayout launchedActivityLayout;
    private LinearLayout suggestionLayout;
    private LinearLayout shareLayout;
    private LinearLayout appraiseLayout;
    private LinearLayout versionLayout;
    private LinearLayout aboutLayout;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tab_my;
    }

    @Override
    public void getViews() {
        nicknameText = (TextView)rootView.findViewById(R.id.nickname_text);
        loginIcon = (SimpleDraweeView)rootView.findViewById(R.id.login_icon);
        launchActivityLayout = (LinearLayout)rootView.findViewById(R.id.launch_activity_layout);
        launchedActivityLayout = (LinearLayout)rootView.findViewById(R.id.launched_activity_layout);
        suggestionLayout = (LinearLayout)rootView.findViewById(R.id.suggestion_layout);
        shareLayout = (LinearLayout)rootView.findViewById(R.id.share_layout);
        appraiseLayout = (LinearLayout)rootView.findViewById(R.id.appraise_layout);
        versionLayout = (LinearLayout)rootView.findViewById(R.id.version_layout);
        aboutLayout = (LinearLayout)rootView.findViewById(R.id.about_layout);

        CommonApplication.setTypeface(nicknameText);
        CommonApplication.setTypeface((TextView)rootView.findViewById(R.id.launch_text));
        CommonApplication.setTypeface((TextView)rootView.findViewById(R.id.launched_text));
        CommonApplication.setTypeface((TextView)rootView.findViewById(R.id.suggestion_text));
        CommonApplication.setTypeface((TextView)rootView.findViewById(R.id.share_text));
        CommonApplication.setTypeface((TextView)rootView.findViewById(R.id.appraise_text));
        CommonApplication.setTypeface((TextView)rootView.findViewById(R.id.version_text));
        CommonApplication.setTypeface((TextView)rootView.findViewById(R.id.about_text));
    }

    @Override
    public void initFragment() {
        if (CommonApplication.isLogined) {
            nicknameText.setText(CommonApplication.userInfo.nick_name);
            if (!TextUtils.isEmpty(CommonApplication.userInfo.icon)) {
                loginIcon.setImageURI(Uri.parse(CommonApplication.userInfo.icon));
            } else {
                loginIcon.setImageResource(R.drawable.login_icon);
            }
        }

        loginIcon.setOnClickListener(this);
        launchActivityLayout.setOnClickListener(this);
        launchedActivityLayout.setOnClickListener(this);
        suggestionLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        appraiseLayout.setOnClickListener(this);
        versionLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_icon:
                if (CommonApplication.isLogined) {
                    Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                    intent.putExtra("userInfo", UserManager.getInstance(getActivity()).getUserInfo());
                    startActivity(intent);
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.launch_activity_layout:
                if (!CommonApplication.isLogined) {
                    ToastUtil.show(getActivity(), "请先登录");
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LaunchActivityActivity.class));
                }
                break;
            case R.id.launched_activity_layout:
                if (!CommonApplication.isLogined) {
                    ToastUtil.show(getActivity(), "请先登录");
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LaunchedActivitiesActivity.class));
                }
                break;
            case R.id.suggestion_layout:
                startActivity(new Intent(getActivity(), SubmitSuggestionActivity.class));
                break;
            case R.id.share_layout:
                ShareDialog dialog = new ShareDialog(getActivity());
                dialog.show();
                break;
            case R.id.appraise_layout:
                try {
                    String address = "market://details?id=" + getActivity().getPackageName();
                    Intent marketIntent = new Intent("android.intent.action.VIEW");
                    marketIntent.setData(Uri.parse(address));
                    startActivity(marketIntent);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
                break;
            case R.id.version_layout:
                VersionUpdateManager updateManager = new VersionUpdateManager(getActivity());
                updateManager.checkVersion();
                break;
            case R.id.about_layout:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusBean.LoginBean event) {
        if (event.userInfo != null) {
            CommonApplication.isLogined = true;
            nicknameText.setText(event.userInfo.nick_name);
            LogUtil.i("icon===" + event.userInfo.icon);
            if (!TextUtils.isEmpty(event.userInfo.icon)) {
                loginIcon.setImageURI(Uri.parse(event.userInfo.icon));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusBean.LogoutBean event) {
        nicknameText.setText("请点击登录");
        loginIcon.setImageResource(R.drawable.login_icon);
    }
}
