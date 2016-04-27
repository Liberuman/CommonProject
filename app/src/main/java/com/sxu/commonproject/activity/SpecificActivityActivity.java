package com.sxu.commonproject.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.BaseCommonAdapter;
import com.sxu.commonproject.baseclass.BaseViewHolder;
import com.sxu.commonproject.bean.ActivityBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.DistanceFormatUtil;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.TimeFormatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juhg on 16/2/24.
 */
public class SpecificActivityActivity extends BaseProgressActivity {

    private PullToRefreshListView activityList;
    private BaseCommonAdapter<ActivityBean.ActivityItemBean> activityAdapter;
    private BaseHttpQuery<ActivityBean> activityQuery;
    private List<ActivityBean.ActivityItemBean> activityData = new ArrayList<ActivityBean.ActivityItemBean>();

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_tab_home;
    }

    @Override
    public void getViews() {
        activityList = (PullToRefreshListView)findViewById(R.id.activity_list);
    }

    @Override
    public void initActivity() {
        activityList.setMode(PullToRefreshBase.Mode.BOTH);
        activityList.setShowIndicator(false);
        navigationBar.setTitle(getIntent().getStringExtra("typeName"));
//        for (int i = 0; i < 20; i++) {
//            activityData.add(new ActivityBean());
//        }
        setActivityAdapter();
        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (CommonApplication.isLogined) {
                    ActivityDetailActivity.enter(SpecificActivityActivity.this, activityData.get((int) id).id, activityData.get((int) id).user_icon);
                } else {
                    LoginActivity.enter(SpecificActivityActivity.this, true);
                }
            }
        });
    }

    @Override
    public void requestData() {
        activityQuery = new BaseHttpQuery<ActivityBean>(this, ActivityBean.class,
                new BaseHttpQuery.OnQueryFinishListener<ActivityBean>() {
                    @Override
                    public void onFinish(ActivityBean bean) {
                        notifyLoadFinish(MSG_LOAD_FINISH);
                        if (bean != null) {
                            LogUtil.i("bean=======" + bean.data );
                            activityData.addAll(bean.data);
                        } else {
                            LogUtil.i("bean is null");
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        LogUtil.i("onError" + errMsg);
                        notifyLoadFinish(MSG_LOAD_EMPTY);
                    }
                });

        activityQuery.doGetQuery(ServerConfig.urlWithSuffix(
                String.format(ServerConfig.SPECIFIC_TYPE_ACTIVITIES, getIntent().getStringExtra("typeId"))));
    }

    private void setActivityAdapter() {
        if (activityData != null) {
            activityAdapter = new BaseCommonAdapter<ActivityBean.ActivityItemBean>(this, activityData, R.layout.item_activity_layout4) {
                @Override
                public void convert(BaseViewHolder viewHolder, ActivityBean.ActivityItemBean data) {
                    LogUtil.i("title===" + data.title);
                    viewHolder.setText(R.id.activity_title_text, data.title);
                    if (!TextUtils.isEmpty(data.distance)) {
                        float distance = Float.parseFloat(data.distance);
                        viewHolder.setText(R.id.distance_text, DistanceFormatUtil.getFormatDistance(distance));
                    }
                    viewHolder.setText(R.id.poster_name_text, data.user_name);
                    viewHolder.setText(R.id.post_time_text, TimeFormatUtil.getTimeDesc(data.create_time));
                    viewHolder.setImageResource(R.id.user_icon, R.drawable.ic_launcher, null, data.user_icon);
                }
            };

            activityList.getRefreshableView().setAdapter(activityAdapter);
        }
    }
}
