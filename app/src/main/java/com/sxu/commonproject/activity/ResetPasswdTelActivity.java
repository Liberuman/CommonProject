package com.sxu.commonproject.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.bean.BaseProtocolBean;
import com.sxu.commonproject.bean.UserBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.util.VerificationUtil;
import com.sxu.commonproject.view.NavigationBar;

/**
 * Created by juhg on 16/3/4.
 */
public class ResetPasswdTelActivity extends BaseActivity {

    private NavigationBar navigationBar;
    private EditText telnumberEdit;

    private BaseHttpQuery<UserBean> vertifyTelQuery;
    private BaseHttpQuery<BaseProtocolBean> codeQuery;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_reset_passwd_tel;
    }

    @Override
    protected void getViews() {
        telnumberEdit = (EditText)findViewById(R.id.tel_number_edit);
        navigationBar = (NavigationBar)findViewById(R.id.navigationBar);

        CommonApplication.setTypeface(telnumberEdit);
        CommonApplication.setTypeface((TextView)findViewById(R.id.tips_text));
    }

    @Override
    protected void initActivity() {
        navigationBar.showReturnIcon().setTitle("重置密码")
                .getRightText("下一步").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telNumber = telnumberEdit.getText().toString();
                if (TextUtils.isEmpty(telNumber)) {
                    ToastUtil.show(ResetPasswdTelActivity.this, "手机号码不能为空哦~");
                } else {
                    if (!VerificationUtil.isValidTelNumber(telNumber)) {
                        ToastUtil.show(ResetPasswdTelActivity.this, "手机号码格式不对哦~");
                    } else {
                        vertifyTelnumber();
                    }
                }
            }
        });
    }

    private void vertifyTelnumber() {
        vertifyTelQuery = new BaseHttpQuery<UserBean>(this, UserBean.class,
                new BaseHttpQuery.OnQueryFinishListener<UserBean>() {
                    @Override
                    public void onFinish(UserBean bean) {
                        if (bean.code == 1 && bean.data != null) {
                            getSmsCode();
                        } else {
                            ToastUtil.show(ResetPasswdTelActivity.this, bean.msg);
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        ToastUtil.show(ResetPasswdTelActivity.this, errMsg);
                    }
                });

        vertifyTelQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(
                ServerConfig.VERTIFY_TELNUMBER, telnumberEdit.getText().toString())));
    }

    private void getSmsCode() {
        codeQuery = new BaseHttpQuery<BaseProtocolBean>(this, BaseProtocolBean.class, new BaseHttpQuery.OnQueryFinishListener<BaseProtocolBean>() {
            @Override
            public void onFinish(BaseProtocolBean bean) {
                if (bean.code == 1) {
                    Intent intent = new Intent(ResetPasswdTelActivity.this, ResetPasswdCodeActivity.class);
                    intent.putExtra("telNumber", telnumberEdit.getText().toString());
                    startActivity(intent);
                }
            }

            @Override
            public void onError(int errCode, String errMsg) {
                ToastUtil.show(ResetPasswdTelActivity.this, errMsg);
            }
        });

        codeQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.GET_SMS_CODE, telnumberEdit.getText().toString())));
    }

}
