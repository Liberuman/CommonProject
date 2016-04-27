package com.sxu.commonproject.activity;

/**
 * Created by Administrator on 2016/4/9 0009.
 */

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.BaseCommonAdapter;
import com.sxu.commonproject.baseclass.BaseCommonProtocolBean;
import com.sxu.commonproject.baseclass.BaseViewHolder;
import com.sxu.commonproject.bean.ActivityBean;
import com.sxu.commonproject.bean.EventBusBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.AndroidPlatformUtil;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.TimeFormatUtil;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.view.PromptDialog;

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

    private float startX = 0;
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
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        activityList.setMode(PullToRefreshBase.Mode.BOTH);
        activityList.setShowIndicator(false);
        navigationBar.setTitle("已发布的活动");
        setCommonAdapter();

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

        activityQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.LAUNCHED_ACTIVITIES,
                CommonApplication.userInfo.id, currentPage)));
    }

    @Override
    protected void setCommonAdapter() {
        if (commonAdapter == null) {
            commonAdapter = new BaseCommonAdapter<ActivityBean.ActivityItemBean>(LaunchedActivitiesActivity.this,
                    activityData, R.layout.item_launched_layout) {
                @Override
                public void convert(final BaseViewHolder viewHolder, ActivityBean.ActivityItemBean data) {
                    viewHolder.setText(R.id.activity_title_text, data.title);
                    viewHolder.setText(R.id.post_time_text, TimeFormatUtil.getTimeDesc(data.create_time));
                    LinearLayout contentLayout = viewHolder.getView(R.id.content_layout);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentLayout.getLayoutParams();
                    params.width = AndroidPlatformUtil.getScreenWidth(LaunchedActivitiesActivity.this);
                    contentLayout.setLayoutParams(params);

                    TextView deleteText = viewHolder.getView(R.id.delete_text);
                    final ActivityBean.ActivityItemBean activityInfo = data;
                    deleteText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final PromptDialog dialog = new PromptDialog(LaunchedActivitiesActivity.this);
                            dialog.show();
                            dialog.setContentText("您确定要删除吗?");
                            dialog.setConfirmClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteActivityInfo(activityInfo.id, viewHolder.getPosition());
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                    contentLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(LaunchedActivitiesActivity.this, LaunchActivityActivity.class);
                            intent.putExtra("activityInfo", activityInfo);
                            startActivity(intent);
                        }
                    });
                }
            };

            activityList.getRefreshableView().setAdapter(commonAdapter);
        } else {
            commonAdapter.notifyDataSetChanged();
        }
    }

    private void deleteActivityInfo(String id, final int position) {
        BaseHttpQuery<BaseCommonProtocolBean> activityQuery = new BaseHttpQuery<>(this, BaseCommonProtocolBean.class,
                new BaseHttpQuery.OnQueryFinishListener<BaseCommonProtocolBean>() {
            @Override
            public void onFinish(BaseCommonProtocolBean bean) {
                if (bean.data != null && bean.code == 1) {
                    activityData.remove(position);
                    commonAdapter.notifyDataSetChanged();
                    ToastUtil.show(LaunchedActivitiesActivity.this, "活动已删除");
                    EventBus.getDefault().post(new EventBusBean.UpdateActivityBean(true));
                } else {
                    LogUtil.i("活动删除失败" + bean.msg);
                }
            }

            @Override
            public void onError(int errCode, String errMsg) {
                LogUtil.i("活动删除失败" + errMsg);
            }
        });

        if (!TextUtils.isEmpty(id)) {
            activityQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.DEL_ACTIVITY, id)));
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

