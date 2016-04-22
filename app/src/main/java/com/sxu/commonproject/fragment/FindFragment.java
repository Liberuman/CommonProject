package com.sxu.commonproject.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.activity.SpecificActivityActivity;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.ACache;
import com.sxu.commonproject.baseclass.BaseCommonAdapter;
import com.sxu.commonproject.baseclass.BaseViewHolder;
import com.sxu.commonproject.bean.ActivityTypeBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.ToastUtil;

import java.util.List;

/**
 * Created by juhg on 16/2/22.
 */

/*************************************************************************
 * FileName: FindFragment
 *
 * Description: 主页查找页面
 *
 * Author: Freeman
 *
 * Version: v1.0
 *
 * Date: 16/2/22
 **************************************************************************/


/**
 * FileName: FindFragment
 *
 * Description: 主页查找页面
 *
 * Author: Freeman
 *
 * Version: v3.0
 *
 * Date: 16/2/22
 */
public class FindFragment extends BaseProgressFragment {

    private GridView gridView;

    private List<ActivityTypeBean.ActivityTypeItemBean> itemData;
    private BaseHttpQuery<ActivityTypeBean> typeQuery;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tab_find;
    }

    @Override
    public void getViews() {
        gridView = (GridView)rootView.findViewById(R.id.activity_type_grid);
    }

    @Override
    public void initFragment() {
        setCommonAdapter();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SpecificActivityActivity.class);
                intent.putExtra("typeName", itemData.get(position).type_name);
                intent.putExtra("typeId", itemData.get(position).id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void requestData() {
        typeQuery = new BaseHttpQuery<ActivityTypeBean>(getActivity(), ActivityTypeBean.class,
                new BaseHttpQuery.OnQueryFinishListener<ActivityTypeBean>() {
                    @Override
                    public void onFinish(ActivityTypeBean bean) {
                        if (bean.code == 1 && bean.data != null && bean.data.size() > 0) {
                            // 缓存活动类型信息
                            ACache cache = ACache.get(CommonApplication.getInstance());
                            cache.put("activityType", bean.data);
                            notifyLoadFinish(MSG_LOAD_FINISH);
                            itemData = bean.data;
                        } else {
                            notifyLoadFinish(MSG_LOAD_EMPTY);
                            ToastUtil.show(getActivity(), bean.msg);
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        notifyLoadFinish(MSG_LOAD_FAILURE);
                        ToastUtil.show(getActivity(),errMsg);
                    }
                });

        typeQuery.doGetQuery(ServerConfig.urlWithSuffix(ServerConfig.ALL_ACTIVITY_TYPE));
    }

    @Override
    protected void setCommonAdapter() {
        if (itemData != null && itemData.size() > 0) {
            commonAdapter = new BaseCommonAdapter<ActivityTypeBean.ActivityTypeItemBean>(getActivity(), itemData, R.layout.item_type_layout) {
                @Override
                public void convert(BaseViewHolder viewHolder, ActivityTypeBean.ActivityTypeItemBean data) {
                    viewHolder.setText(R.id.type_desc_text, data.type_name);
                    viewHolder.setImageResource(R.id.type_icon, R.drawable.ic_launcher, null, data.icon);
                }
            };

            gridView.setAdapter(commonAdapter);
        }
    }
}
