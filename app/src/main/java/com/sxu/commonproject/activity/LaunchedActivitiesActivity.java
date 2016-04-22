package com.sxu.commonproject.activity;

/**
 * Created by Administrator on 2016/4/9 0009.
 */

import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sxu.commonproject.R;
import com.sxu.commonproject.baseclass.BaseCommonAdapter;
import com.sxu.commonproject.baseclass.BaseViewHolder;
import com.sxu.commonproject.bean.ActivityBean;
import com.sxu.commonproject.bean.EventBusBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

//import com.google.common.eventbus.Subscribe;

/**
 * Created by juhg on 16/2/22.
 */
public class LaunchedActivitiesActivity extends BaseProgressActivity {

    private PullToRefreshListView activityList;
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
        navigationBar.setTitle("已发布的活动");
        setCommonAdapter();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        activityList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentPage = 1;
                activityData.clear();
                requestData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentPage++;
                requestData();
            }
        });
    }

    @Override
    public void requestData() {
        activityQuery = new BaseHttpQuery<ActivityBean>(LaunchedActivitiesActivity.this, ActivityBean.class,
                new BaseHttpQuery.OnQueryFinishListener<ActivityBean>() {
                    @Override
                    public void onFinish(ActivityBean bean) {
                        LogUtil.i("data====" + bean.data);
                        if (activityList != null) {
                            activityList.onRefreshComplete();
                        }
                        if (bean.data != null && bean.data.size() > 0) {
                            if (currentPage == 1) {
                                notifyLoadFinish(MSG_LOAD_FINISH);
                            } else {
                                notifyLoadFinish(MSG_LOAD_MORE_FINISH);
                            }
                            activityData.addAll(bean.data);
                        } else {
                            if (currentPage == 1) {
                                notifyLoadFinish(MSG_LOAD_EMPTY);
                            } else {
                                currentPage--;
                                notifyLoadFinish(MSG_LOAD_NO_MORE);
                            }
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        LogUtil.i("onError" + errMsg);
                        if (activityList != null) {
                            activityList.onRefreshComplete();
                        } else {
                            LogUtil.i("activityList is null");
                        }

                        if (currentPage == 1) {
                            notifyLoadFinish(MSG_LOAD_EMPTY);
                        } else {
                            currentPage--;
                            notifyLoadFinish(MSG_LOAD_NO_MORE);
                        }
                    }
                });

        activityQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.LAUNCHED_ACTIVITIES, currentPage)));
    }

    @Override
    protected void setCommonAdapter() {
        if (commonAdapter == null) {
            commonAdapter = new BaseCommonAdapter<ActivityBean.ActivityItemBean>(LaunchedActivitiesActivity.this,
                    activityData, R.layout.item_launched_layout) {
                @Override
                public void convert(final BaseViewHolder viewHolder, ActivityBean.ActivityItemBean data) {
                    viewHolder.setText(R.id.activity_title_text, data.title);
                    viewHolder.setText(R.id.post_time_text, data.create_time);
                }
            };

            activityList.getRefreshableView().setAdapter(commonAdapter);
        } else {
            commonAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventBusBean.UpdateActivityBean event) {
        if (event.needUpdate) {
            requestData();
        }
    }
}

