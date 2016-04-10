package com.sxu.commonproject.fragment;

import android.content.Intent;
import android.location.Location;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sxu.commonproject.R;
import com.sxu.commonproject.activity.ActivityDetailActivity;
import com.sxu.commonproject.activity.UserDetailInfoActivity;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.BaseCommonAdapter;
import com.sxu.commonproject.baseclass.BaseViewHolder;
import com.sxu.commonproject.bean.ActivityBean;
import com.sxu.commonproject.bean.EventBusBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.FormatUtil;
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
public class HomeFragment extends BaseProgressFragment {

    private PullToRefreshListView activityList;
    private BaseHttpQuery<ActivityBean> activityQuery;
    private List<ActivityBean.ActivityItemBean> activityData = new ArrayList<ActivityBean.ActivityItemBean>();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tab_home;
    }

    @Override
    public void getViews() {
        activityList = (PullToRefreshListView)rootView.findViewById(R.id.activity_list);
    }

    @Override
    public void initFragment() {
        activityList.setMode(PullToRefreshBase.Mode.BOTH);
        activityList.setShowIndicator(false);
        setCommonAdapter();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ActivityDetailActivity.class);
                LogUtil.i("id====" + activityData.get((int) id).id);
                intent.putExtra("id", activityData.get((int) id).id);
                startActivity(intent);
            }
        });

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

        double lat = 31.210491;
        double lon = 121.522448;
        double[][] latlon = {
                {31.220491, 121.522448},
                {31.215491, 121.522458},
                {31.510491, 121.523448},
                {32.210491, 121.512448},
                {31.210691, 121.522448},
                {31.210498, 121.532448},
                {31.210401, 121.522548},
        };

        for (int i = 0; i < latlon.length; i++) {
            LogUtil.i("latlon[" + i + "]: distance===" + getDistance(lat, lon, latlon[i][0], latlon[i][1]));
            float[] result = new float[5];
            Location.distanceBetween(lat, lon, latlon[i][0], latlon[i][1], result);
            LogUtil.i("android distance==" + result[0]);
        }


    }

    @Override
    public void requestData() {
        activityQuery = new BaseHttpQuery<ActivityBean>(getActivity(), ActivityBean.class,
                new BaseHttpQuery.OnQueryFinishListener<ActivityBean>() {
            @Override
            public void onFinish(ActivityBean bean) {
                activityList.onRefreshComplete();
                if (bean.data != null && bean.data.size() > 0) {
                    if (currentPage == 1) {
                        if (isFirstLoad) {
                            notifyLoadFinish(MSG_LOAD_FIRST_FINISH);
                        } else {
                            notifyLoadFinish(MSG_LOAD_REFRESH_FINISH);
                        }
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
//                    activityList.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            activityList.onRefreshComplete();
//                        }
//                    }, 500);
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

        activityQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.LATEST_ACTIVITIES, currentPage)));
    }

    @Override
    protected void setCommonAdapter() {
        if (commonAdapter == null) {
            commonAdapter = new BaseCommonAdapter<ActivityBean.ActivityItemBean>(getActivity(), activityData, R.layout.item_activity_layout4) {
                @Override
                public void convert(final BaseViewHolder viewHolder, ActivityBean.ActivityItemBean data) {
                    LogUtil.i("title===" + data.title);
                    viewHolder.setText(R.id.activity_title_text, data.title);
                    if (!TextUtils.isEmpty(data.distance)) {
                        float distance = Float.parseFloat(data.distance);
                        viewHolder.setText(R.id.distance_text, FormatUtil.getFormatDistance(distance));
                    }
                    viewHolder.setText(R.id.poster_name_text, data.user_name);
                    viewHolder.setText(R.id.post_time_text, data.create_time);
                    viewHolder.setImageResource(R.id.user_icon, R.drawable.ic_launcher, null, data.user_icon);
                    final ActivityBean.ActivityItemBean activityInfo = data;
                    viewHolder.getView(R.id.user_icon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), UserDetailInfoActivity.class);
                            intent.putExtra("userId", activityInfo.user_id);
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

    private static final double EARTH_RADIUS = 6372797;
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }

    public static double getDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;

        return s;
    }

}
