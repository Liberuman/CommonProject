package com.sxu.commonproject.activity;

import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.util.AndroidPlatformUtil;
import com.sxu.commonproject.view.NavigationBar;

/**
 * Created by juhg on 16/3/16.
 */
public class AboutActivity extends BaseActivity {

    private TextView appNameText;
    private NavigationBar navigationBar;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected void getViews() {
        appNameText = (TextView)findViewById(R.id.app_name_text);
        navigationBar = (NavigationBar)findViewById(R.id.navigationBar);

        CommonApplication.setTypeface(appNameText);
        CommonApplication.setTypeface((TextView)findViewById(R.id.desc_text));
        CommonApplication.setTypeface((TextView)findViewById(R.id.author_title_text));
        CommonApplication.setTypeface((TextView)findViewById(R.id.author_text));
        CommonApplication.setTypeface((TextView)findViewById(R.id.email_title_text));
        CommonApplication.setTypeface((TextView)findViewById(R.id.email_text));
    }

    @Override
    protected void initActivity() {
        appNameText.setText(getResources().getText(R.string.app_name) + " v" + AndroidPlatformUtil.getVersion(this));
        navigationBar.showReturnIcon().setTitle("关于");
    }
}
