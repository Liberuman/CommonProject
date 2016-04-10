package com.sxu.commonproject.activity;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.BaseCommonProtocolBean;
import com.sxu.commonproject.bean.EventBusBean;
import com.sxu.commonproject.bean.UserBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.util.VerificationUtil;
import com.sxu.commonproject.view.NavigationBar;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by juhg on 16/3/2.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText telNumberEdit;
    private EditText identityCodeEdit;
    private EditText passwdEdit;
    private TextView sendCodeText;
    private TextView registerText;
    private NavigationBar navigationBar;

    private String realCode;
    private String telNumber;
    private String inputCode;
    private String passwd;
    private int seconds = 60;
    private boolean isRunning = false;
    private BaseHttpQuery<BaseCommonProtocolBean> codeQuery;
    private BaseHttpQuery<UserBean> registerQuery;
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
        return R.layout.activity_register;
    }

    @Override
    protected void getViews() {
        telNumberEdit = (EditText)findViewById(R.id.telnumber_edit);
        identityCodeEdit = (EditText)findViewById(R.id.identity_code_edit);
        passwdEdit = (EditText)findViewById(R.id.passwd_edit);
        sendCodeText = (TextView)findViewById(R.id.send_code_text);
        registerText = (TextView)findViewById(R.id.register_text);
        navigationBar = (NavigationBar)findViewById(R.id.navigationBar);

        CommonApplication.setTypeface(telNumberEdit);
        CommonApplication.setTypeface(identityCodeEdit);
        CommonApplication.setTypeface(passwdEdit);
        CommonApplication.setTypeface(sendCodeText);
        CommonApplication.setTypeface(registerText);
    }

    @Override
    protected void initActivity() {
        navigationBar.showReturnIcon().setTitle("注册");

        sendCodeText.setOnClickListener(this);
        registerText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_code_text:
                telNumber = telNumberEdit.getText().toString();
                if (TextUtils.isEmpty(telNumber)) {
                    ToastUtil.show(this, "电话号码不能为空");
                } else if (!VerificationUtil.isValidTelNumber(telNumber)) {
                    ToastUtil.show(this, "电话号码格式错误");
                } else {
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
                    getSmsCode();
                }
                break;
            case R.id.register_text:
                telNumber = telNumberEdit.getText().toString();
                inputCode = identityCodeEdit.getText().toString();
                passwd = passwdEdit.getText().toString();
                if (TextUtils.isEmpty(telNumber)) {
                    ToastUtil.show(this, "电话号码不能为空");
                } else if (!VerificationUtil.isValidTelNumber(telNumber)) {
                    ToastUtil.show(this, "电话号码格式错误");
                } else if (TextUtils.isEmpty(inputCode)){
                    ToastUtil.show(this, "验证码不能为空");
                } else if (!TextUtils.isEmpty(realCode) && !inputCode.equals(realCode)) {
                    ToastUtil.show(this, "验证码错误");
                } else if (TextUtils.isEmpty(passwd)) {
                    ToastUtil.show(this, "密码不能为空");
                } else {
                    register();
                }
                break;
            default:
                break;
        }
    }

    private void getSmsCode() {
        codeQuery = new BaseHttpQuery<BaseCommonProtocolBean>(this, BaseCommonProtocolBean.class, new BaseHttpQuery.OnQueryFinishListener<BaseCommonProtocolBean>() {
            @Override
            public void onFinish(BaseCommonProtocolBean bean) {
                if (bean.code == 1 && !TextUtils.isEmpty(bean.data)) {
                    realCode = bean.data;
                    ToastUtil.show(RegisterActivity.this, bean.msg);
                }
            }

            @Override
            public void onError(int errCode, String errMsg) {
                ToastUtil.show(RegisterActivity.this, errMsg);
            }
        });

        codeQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.GET_SMS_CODE, telNumberEdit.getText().toString())));
    }

    private void register() {
        registerQuery = new BaseHttpQuery<UserBean>(this, UserBean.class, new BaseHttpQuery.OnQueryFinishListener<UserBean>() {
            @Override
            public void onFinish(UserBean bean) {
                if (bean.code == 1 && bean.data != null) {
                    EventBus.getDefault().post(new EventBusBean.LoginBean(bean.data));
                    MainActivity.enter(RegisterActivity.this, 3);
                    finish();
                }
                ToastUtil.show(RegisterActivity.this, bean.msg);
            }

            @Override
            public void onError(int errCode, String errMsg) {
                ToastUtil.show(RegisterActivity.this, errMsg);
            }
        });

        registerQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.REGISTER, telNumber, passwd)));
    }

    @Override
    protected void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }
}
