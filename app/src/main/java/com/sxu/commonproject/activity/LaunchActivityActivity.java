package com.sxu.commonproject.activity;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.ACache;
import com.sxu.commonproject.baseclass.BaseCommonAdapter;
import com.sxu.commonproject.baseclass.BaseViewHolder;
import com.sxu.commonproject.bean.ActivityTypeBean;
import com.sxu.commonproject.manager.UserManager;
import com.sxu.commonproject.bean.BaseProtocolBean;
import com.sxu.commonproject.bean.EventBusBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.AndroidPlatformUtil;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.view.NavigationBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by juhg on 16/2/24.
 */
public class LaunchActivityActivity extends BaseActivity implements View.OnClickListener {

    private NavigationBar navigationBar;
    private TextView launchText;
    private TextView typeText;
    private EditText activityTitleEdit;
    private EditText destinationEdit;
    private EditText timeEdit;
    private EditText commentEdit;
    private ImageView openIcon;
    private PopupWindow popupWindow;

    private boolean isShowing = false;
    private String typeId;
    private BaseHttpQuery<BaseProtocolBean> launchQuest;
    private ArrayList<ActivityTypeBean.ActivityTypeItemBean> activityTypeList;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_act_launch;
    }

    @Override
    protected void getViews() {
        navigationBar = (NavigationBar)findViewById(R.id.navigationBar);
        launchText = (TextView)findViewById(R.id.launch_text);
        typeText = (TextView)findViewById(R.id.activity_type_text);
        activityTitleEdit = (EditText)findViewById(R.id.activity_title_edit);
        destinationEdit = (EditText)findViewById(R.id.activity_destination_edit);
        timeEdit = (EditText)findViewById(R.id.activity_time_edit);
        commentEdit = (EditText)findViewById(R.id.activity_comment_edit);
        openIcon = (ImageView)findViewById(R.id.open_icon);

        CommonApplication.setTypeface(launchText);
        CommonApplication.setTypeface(typeText);
        CommonApplication.setTypeface(activityTitleEdit);
        CommonApplication.setTypeface(destinationEdit);
        CommonApplication.setTypeface(timeEdit);
        CommonApplication.setTypeface(commentEdit);
    }

    @Override
    protected void initActivity() {
        navigationBar.showReturnIcon().setTitle("发布活动");
        activityTypeList = (ArrayList<ActivityTypeBean.ActivityTypeItemBean>)
                ACache.get(CommonApplication.getInstance()).getAsObject("activityType");

        launchText.setOnClickListener(this);
        typeText.setOnClickListener(this);

        navigationBar.getRightText("发布").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity();
            }
        });
    }

    private void launchActivity() {
        launchQuest = new BaseHttpQuery<BaseProtocolBean>(this, BaseProtocolBean.class,
                new BaseHttpQuery.OnQueryFinishListener<BaseProtocolBean>() {
            @Override
            public void onFinish(BaseProtocolBean bean) {
                ToastUtil.show(LaunchActivityActivity.this, "活动发布成功");
                EventBus.getDefault().post(new EventBusBean.UpdateActivityBean(true));
                MainActivity.enter(LaunchActivityActivity.this, 0);
                finish();
            }

            @Override
            public void onError(int errCode, String errMsg) {
                ToastUtil.show(LaunchActivityActivity.this, "活动发布失败" + errMsg);
            }
        });

        Map<String, String> params = new HashMap<String, String>();
        params.put("title", activityTitleEdit.getText().toString());
        params.put("user_id", UserManager.getInstance(this).getUserId());
        params.put("user_name", UserManager.getInstance(this).getUserName());
        params.put("user_icon", CommonApplication.userInfo.icon);
        params.put("destination", destinationEdit.getText().toString());
        params.put("distance", "1.5");
        params.put("type", typeId+"");
        params.put("status", "1");
        params.put("time", timeEdit.getText().toString());
        if (!TextUtils.isEmpty(CommonApplication.userInfo.icon)) {
            params.put("user_icon", CommonApplication.userInfo.icon);
        } else {
            params.put("user_icon", "");
        }
        if (!TextUtils.isEmpty(commentEdit.getText().toString())) {
            params.put("comment", commentEdit.getText().toString());
        } else {
            params.put("comment", "");
        }
        LogUtil.i("param===" + params.toString());
        if (CommonApplication.isLogined) {
            if (isValidActivityInfo()) {
                launchQuest.doPostQuery(ServerConfig.urlWithSuffix(ServerConfig.ADD_ACTIVITY), params);
            }
        } else {
            ToastUtil.show(this, "请登录后再发布活动");
        }
    }

    private boolean isValidActivityInfo() {
        if (TextUtils.isEmpty(activityTitleEdit.getText().toString())) {
            ToastUtil.show(this, "活动标题不能为空");
            return false;
        } else if (TextUtils.isEmpty(activityTitleEdit.getText().toString())) {
            ToastUtil.show(this, "目的地不能为空");
            return false;
        } else if (TextUtils.isEmpty(activityTitleEdit.getText().toString())) {
            ToastUtil.show(this, "活动时间不能为空");
            return false;
        } else {
            /**
             * Nothing
             */
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.launch_text:
                launchActivity();
                break;
            case R.id.activity_type_text:
                chooseActivityType();
                break;
            default:
                break;
        }
    }

    private void chooseActivityType() {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(typeText.getWindowToken(), 0);
        ListView typeList = new ListView(this);
        typeList.setBackgroundResource(R.drawable.activity_info_edit_bg);
        typeList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (activityTypeList != null && activityTypeList.size() > 0) {
            typeList.setAdapter(new BaseCommonAdapter<ActivityTypeBean.ActivityTypeItemBean>(LaunchActivityActivity.this, activityTypeList,
                    R.layout.item_activity_type_layout) {
                @Override
                public void convert(BaseViewHolder viewHolder, ActivityTypeBean.ActivityTypeItemBean data) {
                    viewHolder.setText(R.id.type_name_text, data.type_name);
                }
            });

            popupWindow = new MyPopupWindow(typeList, AndroidPlatformUtil.getScreenWidth(this)
                    - AndroidPlatformUtil.dpToPx(this, 30), AndroidPlatformUtil.dpToPx(this, 180));
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_info_edit_bg));
            popupWindow.showAsDropDown(typeText, 0, 0);
            openIcon.setImageResource(R.drawable.arrow_up_icon);

            typeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    typeId = activityTypeList.get(position).id;
                    typeText.setText(activityTypeList.get(position).type_name);
                    popupWindow.dismiss();
                }
            });
        }
    }

    public class MyPopupWindow extends PopupWindow {

        public MyPopupWindow(View contentView, int width, int height) {
            super(contentView, width, height);
        }

        @Override
        public void dismiss() {
            openIcon.setImageResource(R.drawable.arrow_down_icon);
            super.dismiss();
        }
    }
}
