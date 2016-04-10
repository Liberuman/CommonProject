package com.sxu.commonproject.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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
public class ResetPasswdActivity extends BaseActivity {

    private NavigationBar navigationBar;
    private EditText newPasswdEdit;
    private EditText comfirmPasswdEdit;

    private boolean setPwdFlag;
    private String tips;
    private BaseHttpQuery<BaseProtocolBean> resetPasswdQuery;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_reset_passwd;
    }

    @Override
    protected void getViews() {
        newPasswdEdit = (EditText)findViewById(R.id.new_passwd_edit);
        comfirmPasswdEdit = (EditText)findViewById(R.id.confirm_passwd_edit);
        navigationBar = (NavigationBar)findViewById(R.id.navigationBar);

        CommonApplication.setTypeface(newPasswdEdit);
        CommonApplication.setTypeface(comfirmPasswdEdit);
    }

    @Override
    protected void initActivity() {
        setPwdFlag = getIntent().getBooleanExtra("setPwdFlag", false);
        if (setPwdFlag) {
            navigationBar.setTitle("设置密码");
            tips = "密码设置成功";
        } else {
            navigationBar.setTitle("重置密码");
            tips = "密码修改成功";
        }
        navigationBar.showReturnIcon().getRightText("完成").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPasswd = newPasswdEdit.getText().toString();
                String comfirmPasswd = comfirmPasswdEdit.getText().toString();
                if (TextUtils.isEmpty(newPasswd) && TextUtils.isEmpty(comfirmPasswd)) {
                    ToastUtil.show(ResetPasswdActivity.this, "密码不能为空哦~");
                } else {
                    if (!newPasswd.equals(comfirmPasswd)) {
                        ToastUtil.show(ResetPasswdActivity.this, "两次输入的密码不一致哦~");
                    } else {
                        resetPasswd();
                    }
                }
            }
        });
    }

    private void resetPasswd() {
        resetPasswdQuery = new BaseHttpQuery<BaseProtocolBean>(this, BaseProtocolBean.class,
                new BaseHttpQuery.OnQueryFinishListener<BaseProtocolBean>() {
                    @Override
                    public void onFinish(BaseProtocolBean bean) {
                        if (bean.code == 1) {
                            ToastUtil.show(ResetPasswdActivity.this, tips);
                            MainActivity.enter(ResetPasswdActivity.this, 3);
                            finish();
                        } else {
                            ToastUtil.show(ResetPasswdActivity.this, bean.msg);
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        ToastUtil.show(ResetPasswdActivity.this, errMsg);
                    }
                });

        resetPasswdQuery.doGetQuery(ServerConfig.urlWithSuffix(
                String.format(ServerConfig.RESTE_PASSWD, getIntent().getStringExtra("telNumber"), newPasswdEdit.getText().toString())));
    }
}
