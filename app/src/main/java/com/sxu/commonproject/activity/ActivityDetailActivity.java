package com.sxu.commonproject.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.bean.ActivityDetailBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.LogUtil;

/**
 * Created by juhg on 16/2/24.
 */
public class ActivityDetailActivity extends BaseProgressActivity {

    private TextView titleText;
    private TextView nameText;
    private TextView destinationText;
    private TextView commentText;
    private TextView timeText;
    private TextView contactText;

    private BaseHttpQuery<ActivityDetailBean> activityQuery;
    private ActivityDetailBean.ActivityItemBean activityInfo;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_act_detail;
    }

    @Override
    protected void getViews() {
        titleText = (TextView)findViewById(R.id.activity_title_text);
        nameText = (TextView)findViewById(R.id.creator_text);
        destinationText = (TextView)findViewById(R.id.destination_text);
        timeText = (TextView)findViewById(R.id.time_text);
        commentText = (TextView)findViewById(R.id.comment_text);
        contactText = (TextView)findViewById(R.id.contact_text);

        CommonApplication.setTypeface(titleText);
        CommonApplication.setTypeface(nameText);
        CommonApplication.setTypeface(destinationText);
        CommonApplication.setTypeface(commentText);
        CommonApplication.setTypeface(timeText);
        CommonApplication.setTypeface(contactText);

        CommonApplication.setTypeface((TextView)findViewById(R.id.creator_title_text));
        CommonApplication.setTypeface((TextView)findViewById(R.id.destination_title_text));
        CommonApplication.setTypeface((TextView)findViewById(R.id.time_title_text));
        CommonApplication.setTypeface((TextView)findViewById(R.id.comment_title_text));
    }

    @Override
    protected void initActivity() {
        if (activityInfo != null) {
            titleText.setText(activityInfo.title);
            nameText.setText(activityInfo.user_name);
            destinationText.setText(activityInfo.destination);
            commentText.setText(activityInfo.comment);
            timeText.setText(activityInfo.activity_time);

            contactText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonApplication.isLogined) {
                        Intent intent = new Intent(ActivityDetailActivity.this, ConversationActivity.class);
                        intent.putExtra("isSingle", true);
                        intent.putExtra("userId", activityInfo.user_id);
                        intent.putExtra("userName", activityInfo.user_name);
                        startActivity(intent);
                    } else {
                        LoginActivity.enter(ActivityDetailActivity.this, true);
                    }
                }
            });
        }
        navigationBar.setTitle("活动详情");
    }

    @Override
    protected void requestData() {
        activityQuery = new BaseHttpQuery<ActivityDetailBean>(this, ActivityDetailBean.class,
                new BaseHttpQuery.OnQueryFinishListener<ActivityDetailBean>() {
                    @Override
                    public void onFinish(ActivityDetailBean bean) {
                        if (bean.code == 1 && bean.data != null) {
                            notifyLoadFinish(MSG_LOAD_FINISH);
                            activityInfo = bean.data;
                        } else {
                            notifyLoadFinish(MSG_LOAD_EMPTY);
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        notifyLoadFinish(MSG_LOAD_FAILURE);
                    }
                });

        LogUtil.i("receiver id====" + getIntent().getStringExtra("id"));
        activityQuery.doGetQuery(ServerConfig.urlWithSuffix(
                String.format(ServerConfig.SPECIFIC_ACTIVITY, getIntent().getStringExtra("id"))));
    }
}
