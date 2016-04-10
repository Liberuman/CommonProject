package com.sxu.commonproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.activity.LoginActivity;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.BaseCommonAdapter;
import com.sxu.commonproject.bean.BaseBean;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.ToastUtil;

/**
 * Created by juhg on 16/2/25.
 */
public abstract class BaseProgressFragment<T extends BaseBean> extends Fragment {

    public View rootView;
    public View loadingLayout;
    public LinearLayout resultLayout;
    public ProgressBar progressBar;
    public FrameLayout containerLayout;

    protected int currentPage = 1;
    // 用于区分第一次加载和刷新加载
    protected boolean isFirstLoad = true;
    protected BaseCommonAdapter<T> commonAdapter;
    /**
     * 数据请求失败或为空
     */
    protected final int MSG_LOAD_EMPTY = -1;
    /**
     * 数据请求成功
     */
    protected final int MSG_LOAD_FIRST_FINISH = -2;
    /**
     * 数据请求成功
     */
    protected final int MSG_LOAD_REFRESH_FINISH = -3;
    /**
     * 加载更多数据
     */
    protected final int MSG_LOAD_MORE_FINISH = -4;
    /**
     * 刷新页面
     */
    protected final int MSG_REFRESH_FINISH = -5;
    /**
     * 没有更多数据
     */
    protected final int MSG_LOAD_NO_MORE = -6;
    /**
     * 没有登录
     */
    protected final int MSG_NO_LOGIN = -7;
    /**
     * 数据请求失败
     */
    protected final int MSG_LOAD_FAILURE = -8;


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handleMsg(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requestData();
        if (getLayoutId() != 0) {
            rootView = inflater.inflate(getLayoutId(), null);
        }
        View loadingLayout = inflater.inflate(R.layout.fragment_loading_layout, null);
        resultLayout = (LinearLayout)loadingLayout.findViewById(R.id.result_layout);
        progressBar = (ProgressBar)loadingLayout.findViewById(R.id.progressbar);
        containerLayout = (FrameLayout)loadingLayout.findViewById(R.id.content_container);
        CommonApplication.setTypeface((TextView)loadingLayout.findViewById(R.id.result_msg_text));

        return loadingLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getViews();
        resultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();
            }
        });
    }

    public abstract void requestData();

    public abstract int getLayoutId();

    public abstract void getViews();

    public abstract void initFragment();

    public void notifyLoadFinish(int what) {
        handler.sendEmptyMessage(what);
    }

    protected void setCommonAdapter() {

    }

    protected void handleMsg(Message msg) {
        LogUtil.i("=========handleMsg");
        progressBar.setVisibility(View.GONE);
        switch (msg.what) {
            case MSG_LOAD_EMPTY:
                resultLayout.setVisibility(View.VISIBLE);
                //resultIcon.setImageResource(R.drawable.ic_launcher);
                //resultText.setText("没有搜索到结果");
                break;
            case MSG_LOAD_FIRST_FINISH:
                isFirstLoad = false;
                resultLayout.setVisibility(View.GONE);
                if (rootView != null) {
                    containerLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_in));
                    containerLayout.addView(rootView);
                    initFragment();
                }
                break;
            case MSG_LOAD_REFRESH_FINISH:
                resultLayout.setVisibility(View.GONE);
                initFragment();
                break;
            case MSG_LOAD_FAILURE:
                resultLayout.setVisibility(View.VISIBLE);
                //resultIcon.setImageResource(R.drawable.ic_launcher);
                //resultText.setText("您好像没有连接网络");
                break;
            case MSG_LOAD_MORE_FINISH:
                resultLayout.setVisibility(View.GONE);
                if (commonAdapter == null) {
                    setCommonAdapter();
                } else {
                    commonAdapter.notifyDataSetChanged();
                }
                break;
            case MSG_LOAD_NO_MORE:
                resultLayout.setVisibility(View.GONE);
                ToastUtil.show(getActivity(), "没有更多数据啦~");
                break;
            case MSG_NO_LOGIN:
                resultLayout.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), LoginActivity.class));
                // 启动登录窗口
                break;
            default:
                break;
        }
    }
}
