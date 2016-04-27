package com.sxu.commonproject.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.bean.UserBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.FastBlurUtil;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.ToastUtil;

/**
 * Created by juhg on 16/3/11.
 */
public class UserDetailInfoActivity extends BaseProgressActivity {

    private TextView genderText;
    private TextView telNumberText;
    private TextView nicknameText;
    private TextView locationText;
    private TextView logoutText;
    private TextView sendMsgText;
    private EditText signEdit;
    private ImageView icon;
    private ImageView iconBg;
    private LinearLayout iconLayout;

    // 0表示男，1表示女
    private int gender = 0;
    private Bitmap originBitmap;
    private UserBean.UserItemBean userInfo;
    private BaseHttpQuery<UserBean> userInfoQuery;
    // 图片的高宽比

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (originBitmap != null) {
                        setBlurImageView(originBitmap, 4);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_my_info;
    }

    @Override
    protected void getViews() {
        nicknameText = (TextView)findViewById(R.id.nickname_text);
        locationText = (TextView)findViewById(R.id.location_text);
        genderText = (TextView)findViewById(R.id.gender_text);
        telNumberText = (TextView)findViewById(R.id.tel_text);
        logoutText = (TextView)findViewById(R.id.logout_text);
        sendMsgText = (TextView)findViewById(R.id.send_msg_text);
        signEdit = (EditText)findViewById(R.id.sign_text);
        icon = (ImageView)findViewById(R.id.icon);
        iconBg = (ImageView)findViewById(R.id.icon_bg);
        iconLayout = (LinearLayout)findViewById(R.id.icon_layout);

        CommonApplication.setTypeface(nicknameText);
        CommonApplication.setTypeface(locationText);
        CommonApplication.setTypeface(genderText);
        CommonApplication.setTypeface(telNumberText);
        CommonApplication.setTypeface(signEdit);
        CommonApplication.setTypeface(logoutText);
        CommonApplication.setTypeface(sendMsgText);
        CommonApplication.setTypeface((TextView) findViewById(R.id.gender_title_text));
        CommonApplication.setTypeface((TextView) findViewById(R.id.tel_title_text));
        CommonApplication.setTypeface((TextView) findViewById(R.id.sign_title_text));
    }

    @Override
    protected void initActivity() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, screenWidth*2/3);
        iconBg.setLayoutParams(params);
        iconLayout.setLayoutParams(params);
        signEdit.setEnabled(false);
        logoutText.setVisibility(View.GONE);
        sendMsgText.setVisibility(View.VISIBLE);
        navigationBar.setVisibility(View.GONE);

        if (userInfo != null) {
            nicknameText.setText(userInfo.nick_name);
            if (!TextUtils.isEmpty(userInfo.gender)) {
                gender = Integer.parseInt(userInfo.gender);
            }
            if (gender == 0) {
                genderText.setText("男");
            } else {
                genderText.setText("女");
            }
            telNumberText.setText(userInfo.tel_number);
            signEdit.setText(userInfo.signature);
            if (!TextUtils.isEmpty(userInfo.icon)) {
                loadIcon(Uri.parse(userInfo.icon));
            } else {
                setBlurImageView(((BitmapDrawable)getResources().getDrawable(R.drawable.default_icon)).getBitmap(), 4);
            }
        }

        sendMsgText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonApplication.isLogined) {
                    ConversationActivity.enter(UserDetailInfoActivity.this, userInfo.id, userInfo.icon, userInfo.nick_name);
                } else {
                    ToastUtil.show(UserDetailInfoActivity.this, "请先登录");
                    startActivity(new Intent(UserDetailInfoActivity.this, LoginActivity.class));
                }
            }
        });
    }

    @Override
    protected void requestData() {
        userInfoQuery = new BaseHttpQuery<UserBean>(this, UserBean.class,
                new BaseHttpQuery.OnQueryFinishListener<UserBean>() {
                    @Override
                    public void onFinish(UserBean bean) {
                        if (bean.code == 1 && bean.data != null) {
                            userInfo = bean.data;
                            notifyLoadFinish(MSG_LOAD_FINISH);
                        } else {
                            notifyLoadFinish(MSG_LOAD_EMPTY);
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        notifyLoadFinish(MSG_LOAD_FAILURE);
                    }
                });
        LogUtil.i("请求数据" + getIntent().getStringExtra("userId"));
        userInfoQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.GET_USER_INFO, getIntent().getStringExtra("userId"))));
    }

    public void loadIcon(Uri uri) {
        DataSubscriber dataSubscriber = new BaseDataSubscriber<CloseableReference<CloseableBitmap>>() {
            @Override
            public void onNewResultImpl(DataSource<CloseableReference<CloseableBitmap>> dataSource) {
                if (dataSource.isFinished()) {
                    CloseableReference<CloseableBitmap> imageReference = dataSource.getResult();
                    if (imageReference != null) {
                        final CloseableReference<CloseableBitmap> closeableReference = imageReference.clone();
                        try {
                            CloseableBitmap closeableBitmap = closeableReference.get();
                            Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
                            if (bitmap != null && !bitmap.isRecycled()) {
                                originBitmap = bitmap;
                                icon.setImageBitmap(bitmap);
                                handler.sendEmptyMessage(1);
                            }
                        } finally {
                            imageReference.close();
                            closeableReference.close();
                        }
                    }
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                LogUtil.e(dataSource.getFailureCause().getMessage());
            }
        };

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).build();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(request, this);
        dataSource.subscribe(dataSubscriber, CallerThreadExecutor.getInstance());
    }

    private void setBlurImageView(Bitmap originBitmap, int scaleRatio) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap, originBitmap.getWidth() / scaleRatio,
                originBitmap.getHeight() / scaleRatio, false);
        Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, 20, true);

        iconBg.setImageBitmap(blurBitmap);
    }
}
