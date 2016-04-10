package com.sxu.commonproject.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.bean.BaseProtocolBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.view.NavigationBar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by juhg on 16/2/29.
 */
public class SubmitSuggestionActivity extends BaseActivity {

    private EditText contentEdit;
    private EditText telEdit;
    private NavigationBar navigationBar;

    private BaseHttpQuery<BaseProtocolBean> submitQuery;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_submit_suggestion;
    }

    @Override
    protected void getViews() {
        contentEdit = (EditText)findViewById(R.id.content_edit);
        telEdit = (EditText)findViewById(R.id.tel_edit);
        navigationBar = (NavigationBar)findViewById(R.id.navigationBar);

        CommonApplication.setTypeface(contentEdit);
        CommonApplication.setTypeface(telEdit);
    }

    @Override
    protected void initActivity() {
        navigationBar.showReturnIcon().setTitle("意见反馈");
        navigationBar.getRightText("提交").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(contentEdit.getText().toString())) {
                    submitSuggestion();
                } else {
                    Toast.makeText(SubmitSuggestionActivity.this, "反馈内容为空", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void submitSuggestion() {
        submitQuery = new BaseHttpQuery<BaseProtocolBean>(this, BaseProtocolBean.class,
                new BaseHttpQuery.OnQueryFinishListener<BaseProtocolBean>() {
            @Override
            public void onFinish(BaseProtocolBean bean) {
                ToastUtil.show(SubmitSuggestionActivity.this, "提交成功");
                finish();
            }

            @Override
            public void onError(int errCode, String errMsg) {
                ToastUtil.show(SubmitSuggestionActivity.this, "提交失败" + errMsg);
            }
        });

        Map<String, String> params = new HashMap<String, String>();
        params.put("content", contentEdit.getText().toString());
        params.put("tel_number", telEdit.getText().toString());
        submitQuery.doPostQuery(ServerConfig.urlWithSuffix(ServerConfig.SUBMIT_SUGGESTION), params);
    }
}
