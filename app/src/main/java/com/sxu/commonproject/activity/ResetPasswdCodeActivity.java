package com.sxu.commonproject.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.bean.BaseProtocolBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.view.NavigationBar;

/**
 * Created by juhg on 16/3/4.
 */
public class ResetPasswdCodeActivity extends BaseActivity {

    private NavigationBar navigationBar;
    private EditText codeEdit;
    private BaseHttpQuery<BaseProtocolBean> vertifyCodeQuery;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_reset_passwd_code;
    }

    @Override
    protected void getViews() {
        codeEdit = (EditText)findViewById(R.id.code_edit);
        navigationBar = (NavigationBar)findViewById(R.id.navigationBar);

        CommonApplication.setTypeface(codeEdit);
        CommonApplication.setTypeface((TextView) findViewById(R.id.tips_text));
    }

    @Override
    protected void initActivity() {
        navigationBar.showReturnIcon().setTitle("重置密码")
                .getRightText("下一步").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeEdit.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.show(ResetPasswdCodeActivity.this, "验证码不能为空哦~");
                } else {
                    vertifyCode();
                }
            }
        });
    }

    private void vertifyCode() {
        vertifyCodeQuery = new BaseHttpQuery<BaseProtocolBean>(this, BaseProtocolBean.class,
                new BaseHttpQuery.OnQueryFinishListener<BaseProtocolBean>() {
                    @Override
                    public void onFinish(BaseProtocolBean bean) {
                        if (bean.code == 1) {
                            Intent intent = new Intent(ResetPasswdCodeActivity.this, ResetPasswdActivity.class);
                            intent.putExtra("telNumber", getIntent().getStringExtra("telNumber"));
                            startActivity(intent);
                        } else {
                            ToastUtil.show(ResetPasswdCodeActivity.this, bean.msg);
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        ToastUtil.show(ResetPasswdCodeActivity.this, errMsg);
                    }
                });

        vertifyCodeQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(
                ServerConfig.VERTIFY_CODE, getIntent().getStringExtra("telNumber"), codeEdit.getText().toString())));
    }
}
