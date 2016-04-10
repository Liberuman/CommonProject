package com.sxu.commonproject.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.util.AndroidPlatformUtil;

/**
 * Created by juhg on 16/2/17.
 */
public class NavigationBar extends RelativeLayout implements View.OnClickListener {

    private TextView titleText;
    private TextView positionText;
    private TextView orderText;
    private TextView rightText;
    private ImageView returnIcon;
    private Context context;

    public NavigationBar(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.navigator_layout, this);
        titleText = (TextView) findViewById(R.id.navigationTitle);
        positionText = (TextView)findViewById(R.id.position_text);
        orderText = (TextView)findViewById(R.id.order_text);
        rightText = (TextView)findViewById(R.id.right_text);
        returnIcon = (ImageView) findViewById(R.id.return_icon);

        CommonApplication.setTypeface(positionText);
        CommonApplication.setTypeface(orderText);
        CommonApplication.setTypeface(rightText);

        returnIcon.setOnClickListener(this);
        positionText.setOnClickListener(this);
        orderText.setOnClickListener(this);
    }

    public NavigationBar setTitle(String title) {
        titleText.setText(title);
        CommonApplication.setTypeface(titleText);
        return this;
    }

    public NavigationBar showReturnIcon() {
        //returnIcon.setVisibility(VISIBLE);
        return this;
    }

    public NavigationBar showPositionText() {
        positionText.setVisibility(VISIBLE);
        return this;
    }

    public NavigationBar showOrderText() {
        orderText.setVisibility(VISIBLE);
        return this;
    }

    public NavigationBar hidePositionText() {
        positionText.setVisibility(GONE);
        return this;
    }

    public NavigationBar hideOrderText() {
        orderText.setVisibility(GONE);
        return this;
    }

    public TextView getRightText(String text) {
        rightText.setVisibility(VISIBLE);
        rightText.setText(text);
        return rightText;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_icon:
                ((Activity)context).finish();
                break;
            case R.id.order_text:
                showOrderMenu();
                break;
            case R.id.time_text:
                break;
            case R.id.distance_text:
                break;
            default:
                break;
        }
    }

    private void showOrderMenu() {
        View orderView = LayoutInflater.from(context).inflate(R.layout.view_order_layout, null);
        PopupWindow menuWindow = new PopupWindow(orderView, AndroidPlatformUtil.dpToPx(context, 100),
                AndroidPlatformUtil.dpToPx(context, 90));
        // 获取焦点，避免点击对话框外时出发其他事件
        menuWindow.setFocusable(true);
        menuWindow.setOutsideTouchable(true);
        // PopupWindow默认情况下outsideTouchable无效，设置背景后即可解决此问题
        menuWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.designer_menu_bg));
        menuWindow.showAtLocation(orderText, Gravity.TOP | Gravity.RIGHT,
                AndroidPlatformUtil.dpToPx(context, 12), AndroidPlatformUtil.dpToPx(context, 65));
        if (!menuWindow.isShowing()) {
            menuWindow.showAsDropDown(orderText);
        }

        TextView timeText = (TextView)findViewById(R.id.time_text);
        TextView distanceText = (TextView)findViewById(R.id.distance_text);
        CommonApplication.setTypeface(timeText);
        CommonApplication.setTypeface(distanceText);

        timeText.setOnClickListener(this);
        distanceText.setOnClickListener(this);
    }
}

