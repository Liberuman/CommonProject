package com.sxu.commonproject.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.BaseCommonAdapter;
import com.sxu.commonproject.bean.BaseBean;
import com.sxu.commonproject.util.AndroidPlatformUtil;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.view.NavigationBar;

/**
 * Created by juhg on 16/2/17.
 */
public abstract class BaseProgressActivity<T extends BaseBean> extends Activity {

    /**
     * 获取屏幕宽度，便于计算View的宽高
     */
    protected int screenWidth;
    /**
     * 当前页码（用于分页）
     */
    protected int currentPage = 1;

    /**
     * 数据请求失败或为空
     */
    protected static final int MSG_LOAD_EMPTY = -1;
    /**
     * 数据请求成功
     */
    protected static final int MSG_LOAD_FINISH = -2;
    /**
     * 加载更多数据
     */
    protected static final int MSG_LOAD_MORE_FINISH = -3;
    /**
     * 刷新页面
     */
    protected static final int MSG_REFRESH_FINISH = -4;
    /**
     * 没有更多数据
     */
    protected static final int MSG_LOAD_NO_MORE = -5;
    /**
     * 没有登录
     */
    protected static final int MSG_NO_LOGIN = -6;
    /**
     * 数据请求失败
     */
    protected static final int MSG_LOAD_FAILURE = -7;

    /**
     * Loading页面的子View
     */
    private TextView resultText;
    private ImageView resultIcon;
    private LinearLayout resultLayout;
    private ProgressBar progressBar;
    private FrameLayout containerLayout;
    protected NavigationBar navigationBar;

    protected BaseCommonAdapter<T> commonAdapter;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handleMsg(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        screenWidth = AndroidPlatformUtil.getScreenWidth(this);
        // 设置Actvity的切换动画
        overridePendingTransition(R.anim.splash_push_left_in, R.anim.splash_push_left_out);
        requestData();
        // 对于启动时进行数据请求的页面，先加载"加载中"页面
        setContentView(R.layout.activity_loading_layout);
        navigationBar = (NavigationBar)findViewById(R.id.navigationBar);
        containerLayout = (FrameLayout)findViewById(R.id.content_container);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        resultLayout = (LinearLayout)findViewById(R.id.result_layout);
        resultIcon = (ImageView)findViewById(R.id.result_icon);
        resultText = (TextView)findViewById(R.id.result_msg_text);
        CommonApplication.setTypeface(resultText);

        navigationBar.setTitle("加载中");
        resultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();
            }
        });
    }


    /**
     * 获取Activity的布局ID
     * @return
     */
    protected abstract int getLayoutResId();

    /**
     * 请求网络数据
     */
    protected void requestData() {

    }

    protected void setCommonAdapter() {

    }
    /**
     * 获取Activity中所有的View
     */
    protected abstract void getViews();

    /**
     * Activity的逻辑实现
     */
    protected abstract void initActivity();

    public void notifyLoadFinish(int what) {
        handler.sendEmptyMessage(what);
    }

    protected void handleMsg(Message msg) {
        progressBar.setVisibility(View.GONE);
        switch (msg.what) {
            case MSG_LOAD_EMPTY:
                resultLayout.setVisibility(View.VISIBLE);
                resultIcon.setImageResource(R.drawable.ic_launcher);
                resultText.setText("没有搜索到结果");
                break;
            case MSG_LOAD_FINISH:
                if (getLayoutResId() != 0) {
                    //containerLayout.removeAllViews();
                    resultLayout.setVisibility(View.GONE);
                    containerLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.abc_fade_in));
                    getLayoutInflater().inflate(getLayoutResId(), containerLayout);
                    //setContentView(getLayoutResId());
                    getViews();
                    initActivity();
                }
                break;
            case MSG_LOAD_FAILURE:
                resultLayout.setVisibility(View.VISIBLE);
                resultIcon.setImageResource(R.drawable.ic_launcher);
                resultText.setText("您好像没有连接网络");
                break;
            case MSG_LOAD_MORE_FINISH:
                if (commonAdapter != null) {
                    commonAdapter.notifyDataSetChanged();
                } else {
                    setCommonAdapter();
                }
                break;
            case MSG_LOAD_NO_MORE:
                ToastUtil.show(this, "没有更多数据啦~");
                break;
            case MSG_NO_LOGIN:
                // 启动登录窗口
                LoginActivity.enter(this, true);
                break;
            default:
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.splash_push_right_in, R.anim.splash_push_right_out);
    }
}
