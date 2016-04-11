package com.sxu.commonproject.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.BaseCommonAdapter;
import com.sxu.commonproject.baseclass.BaseViewHolder;
import com.sxu.commonproject.bean.NearbyUserBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.FormatUtil;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juhg on 16/3/14.
 */
public class NearByContactActivity extends BaseProgressActivity {

    private PullToRefreshListView nearbyList;

    private BaseHttpQuery<NearbyUserBean> nearbyQuery;
    private BaseCommonAdapter<NearbyUserBean.NearbyUserItemBean> nearbyAdapter;
    private List<NearbyUserBean.NearbyUserItemBean> userList = new ArrayList<NearbyUserBean.NearbyUserItemBean>();

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_nearby_contact;
    }

    @Override
    protected void getViews() {
        nearbyList = (PullToRefreshListView)findViewById(R.id.nearby_list);
    }

    @Override
    protected void initActivity() {
        nearbyList.setShowIndicator(false);
        navigationBar.setTitle("附近的人");
        setNearBYAdapter();

        nearbyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id >= 0) {
                    Intent intent = new Intent(NearByContactActivity.this, UserDetailInfoActivity.class);
                    intent.putExtra("userId", userList.get((int)id).id);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void requestData() {
        nearbyQuery = new BaseHttpQuery<NearbyUserBean>(this, NearbyUserBean.class,
                new BaseHttpQuery.OnQueryFinishListener<NearbyUserBean>() {
            @Override
            public void onFinish(NearbyUserBean bean) {
                if (bean.code == 1 && bean.data != null) {
                    notifyLoadFinish(MSG_LOAD_FINISH);
                    userList.addAll(bean.data);
                } else {
                    notifyLoadFinish(MSG_LOAD_EMPTY);
                }
            }

            @Override
            public void onError(int errCode, String errMsg) {
                LogUtil.i("error:" + errMsg);
                notifyLoadFinish(MSG_LOAD_FAILURE);
            }
        });

        if (CommonApplication.location != null) {
            nearbyQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.GET_NEARBY_USERS,
                    CommonApplication.location.getLongitude(), CommonApplication.location.getLatitude())));
        } else {
            ToastUtil.show(this, "位置获取失败");
            notifyLoadFinish(MSG_LOAD_FAILURE);
        }
    }

    private void setNearBYAdapter() {
        nearbyAdapter = new BaseCommonAdapter<NearbyUserBean.NearbyUserItemBean>(this, userList, R.layout.item_nearby_layout) {
            @Override
            public void convert(BaseViewHolder viewHolder, NearbyUserBean.NearbyUserItemBean data) {
                viewHolder.setText(R.id.nickname_text, data.nick_name);
                viewHolder.setText(R.id.sign_text, data.sign);
                if (!TextUtils.isEmpty(data.distance)) {
                    viewHolder.setText(R.id.distance_text, FormatUtil.getFormatDistance(Float.parseFloat(data.distance)));
                }
                if (!TextUtils.isEmpty(data.icon)) {
                    viewHolder.setImageResource(R.id.user_icon, R.drawable.default_icon, null, data.icon);
                }
            }
        };

        nearbyList.setAdapter(nearbyAdapter);
    }
}
