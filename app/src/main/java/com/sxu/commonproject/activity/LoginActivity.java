package com.sxu.commonproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.manager.UserManager;
import com.sxu.commonproject.bean.BaseProtocolBean;
import com.sxu.commonproject.bean.EventBusBean;
import com.sxu.commonproject.bean.UserBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.service.ReceiverMsgService;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.util.VerificationUtil;
import com.sxu.commonproject.view.NavigationBar;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by juhg on 16/3/2.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText telNumberEdit;
    private EditText identityCodeEdit;
    private EditText passwdEdit;
    private TextView sendCodeText;
    private TextView loginText;
    private TextView switchLoginText;
    private LinearLayout identityLayout;
    private LinearLayout passwdLayout;
    private NavigationBar navigationBar;

    private int seconds = 60;
    private boolean flag;
    private boolean isRunning = false;
    private boolean isMobelLogin;
    private BaseHttpQuery<UserBean> loginQuery;
    private BaseHttpQuery<BaseProtocolBean> codeQuery;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    seconds--;
                    if (seconds > 0) {
                        sendCodeText.setEnabled(false);
                        sendCodeText.setText(seconds + "s后重新发送");
                    } else {
                        isRunning = false;
                        seconds = 60;
                        sendCodeText.setEnabled(true);
                        sendCodeText.setText("发送验证码");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void getViews() {
        telNumberEdit = (EditText)findViewById(R.id.telnumber_edit);
        identityCodeEdit = (EditText)findViewById(R.id.identity_code_edit);
        passwdEdit = (EditText)findViewById(R.id.passwd_edit);
        sendCodeText = (TextView)findViewById(R.id.send_code_text);
        loginText = (TextView)findViewById(R.id.login_text);
        switchLoginText = (TextView)findViewById(R.id.switch_login_text);
        identityLayout = (LinearLayout)findViewById(R.id.identity_layout);
        passwdLayout = (LinearLayout)findViewById(R.id.passwd_layout);
        navigationBar = (NavigationBar)findViewById(R.id.navigationBar);

        CommonApplication.setTypeface(telNumberEdit);
        CommonApplication.setTypeface(identityCodeEdit);
        CommonApplication.setTypeface(passwdEdit);
        CommonApplication.setTypeface(sendCodeText);
        CommonApplication.setTypeface(loginText);
        CommonApplication.setTypeface(switchLoginText);
        CommonApplication.setTypeface((TextView) findViewById(R.id.social_contact_login_text));
    }

    @Override
    protected void initActivity() {
        flag = getIntent().getBooleanExtra("flag", false);
        isMobelLogin = getIntent().getBooleanExtra("isMobelLogin", true);
        if (isMobelLogin) {
            navigationBar.showReturnIcon().setTitle("手机验证码登录")
                    .getRightText("注册").setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                }
            });
        } else {
            sendCodeText.setVisibility(View.GONE);
            identityLayout.setVisibility(View.GONE);
            passwdLayout.setVisibility(View.VISIBLE);
            switchLoginText.setText("忘记密码？");
            navigationBar.showReturnIcon().setTitle("账号密码登录")
                    .getRightText("注册").setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                }
            });
        }

        switchLoginText.setOnClickListener(this);
        sendCodeText.setOnClickListener(this);
        loginText.setOnClickListener(this);
    }

    public static void enter(Context context, boolean flag) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("flag", flag);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_code_text:
                getSmsCode();
                isRunning = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isRunning) {
                            try {
                                handler.sendEmptyMessageDelayed(1, 1000);
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                e.printStackTrace(System.out);
                            }
                        }
                    }
                }).start();
                break;
            case R.id.login_text:
                isRunning = false;
                String telNumber = telNumberEdit.getText().toString();
                if (!TextUtils.isEmpty(telNumber)) {
                    if (VerificationUtil.isValidTelNumber(telNumber)) {
                        if (isMobelLogin) {
                            if (!TextUtils.isEmpty(identityCodeEdit.getText().toString())) {
                                login(true);
                            } else {
                                ToastUtil.show(this, "验证码不能为空");
                            }
                        } else {
                            if (!TextUtils.isEmpty(passwdEdit.getText().toString())) {
                                login(false);
                            } else {
                                ToastUtil.show(this, "密码不能为空");
                            }
                        }
                    } else {
                        ToastUtil.show(this, "手机号码格式错误");
                    }
                } else {
                    ToastUtil.show(this, "手机号码不能为空");
                }
                break;
            case R.id.switch_login_text:
                if (isMobelLogin) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("isMobelLogin", false);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, ResetPasswdTelActivity.class));
                }
                break;
            default:
                break;
        }
    }

    private void getSmsCode() {
        codeQuery = new BaseHttpQuery<BaseProtocolBean>(this, BaseProtocolBean.class, new BaseHttpQuery.OnQueryFinishListener<BaseProtocolBean>() {
            @Override
            public void onFinish(BaseProtocolBean bean) {
                if (bean.code == 1) {
                    ToastUtil.show(LoginActivity.this, bean.msg);
                }
            }

            @Override
            public void onError(int errCode, String errMsg) {
                ToastUtil.show(LoginActivity.this, errMsg);
            }
        });

        codeQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.GET_SMS_CODE, telNumberEdit.getText().toString())));
    }

    private void login(boolean isMobelLogin) {
        loginQuery = new BaseHttpQuery<UserBean>(this, UserBean.class,
                new BaseHttpQuery.OnQueryFinishListener<UserBean>() {
            @Override
            public void onFinish(UserBean bean) {
                if (bean.code == 1 && bean.data != null) {
                    UserManager.getInstance(LoginActivity.this).saveUserInfo(bean.data);
                    CommonApplication.isLogined = true;
                    startService(new Intent(LoginActivity.this, ReceiverMsgService.class));
                    EventBus.getDefault().post(new EventBusBean.LoginBean(bean.data));
                    if (!bean.data.has_passwd.equals("0")) {
                        if (!flag) {
                            MainActivity.enter(LoginActivity.this, 3);
                        }
                        finish();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, ResetPasswdActivity.class);
                        intent.putExtra("telNumber", telNumberEdit.getText().toString());
                        intent.putExtra("setPwdFlag", true);
                        startActivity(intent);
                    }
                } else {
                    ToastUtil.show(LoginActivity.this, bean.msg);
                }
            }

            @Override
            public void onError(int errCode, String errMsg) {
                ToastUtil.show(LoginActivity.this, errMsg);
            }
        });

        if (isMobelLogin) {
            loginQuery.doGetQuery(ServerConfig.urlWithSuffix(
                    String.format(ServerConfig.LOGIN_BY_CODE, telNumberEdit.getText().toString(), identityCodeEdit.getText().toString())));
        } else {
            loginQuery.doGetQuery(ServerConfig.urlWithSuffix(
                    String.format(ServerConfig.LOGIN_BY_ACCOUNT, telNumberEdit.getText().toString(), passwdEdit.getText().toString())));
        }
    }

    @Override
    protected void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }
}
