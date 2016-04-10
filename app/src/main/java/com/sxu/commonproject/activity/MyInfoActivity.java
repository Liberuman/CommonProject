package com.sxu.commonproject.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.BaseCommonProtocolBean;
import com.sxu.commonproject.bean.EventBusBean;
import com.sxu.commonproject.bean.UserBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.manager.PathManager;
import com.sxu.commonproject.manager.UserManager;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.FastBlurUtil;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.view.ChooseMenuDialog;
import com.sxu.commonproject.view.PromptDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by juhg on 16/3/11.
 */
public class MyInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView genderText;
    private TextView telNumberText;
    private TextView nicknameText;
    private TextView locationText;
    private TextView logoutText;
    private EditText signEdit;
    private ImageView icon;
    private ImageView iconBg;
    private ImageView chooseIcon;
    private LinearLayout iconLayout;
    private LinearLayout genderLayout;
    private LinearLayout signLayout;

    // 照片文件的Uri地址
    private Uri iconUri;
    // 裁减后的图片Uri地址
    private Uri cropImageUri;
    // 0表示男，1表示女
    private int gender = 0;
    private boolean genderIsModified = false;
    private boolean signIsModified = false;
    private Bitmap originBitmap;
    private UserBean.UserItemBean userInfo;
    private BaseHttpQuery<BaseCommonProtocolBean> updateQuery;
    private BaseHttpQuery<BaseCommonProtocolBean> tokenQuery;
    private BaseHttpQuery<BaseCommonProtocolBean> uploadQuery;

    private final int REQUEST_CODE_TAKE_PHOTO = 1;
    private final int REQUEST_CODE_CHOOSE_IMAGE = 2;
    private final int REQUEST_CODE_CROP_IMAGE = 3;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Glide.with(MyInfoActivity.this)
                            .load((String)msg.obj)
                            .placeholder(R.drawable.ic_launcher)
                            .error(R.drawable.ic_launcher)
                            .into(icon);
                    break;
                case 2:
                    ToastUtil.show(MyInfoActivity.this, "头像上传失败");
                    break;
                case 3:
                    if (originBitmap != null) {
                        LogUtil.i("调用高斯");
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
        signEdit = (EditText)findViewById(R.id.sign_text);
        icon = (ImageView)findViewById(R.id.icon);
        iconBg = (ImageView)findViewById(R.id.icon_bg);
        chooseIcon = (ImageView)findViewById(R.id.choose_icon);
        iconLayout = (LinearLayout)findViewById(R.id.icon_layout);
        genderLayout = (LinearLayout)findViewById(R.id.gender_layout);
        signLayout = (LinearLayout)findViewById(R.id.sign_layout);

        CommonApplication.setTypeface(nicknameText);
        CommonApplication.setTypeface(locationText);
        CommonApplication.setTypeface(genderText);
        CommonApplication.setTypeface(telNumberText);
        CommonApplication.setTypeface(signEdit);
        CommonApplication.setTypeface(logoutText);
        CommonApplication.setTypeface((TextView) findViewById(R.id.gender_title_text));
        CommonApplication.setTypeface((TextView) findViewById(R.id.tel_title_text));
        CommonApplication.setTypeface((TextView) findViewById(R.id.sign_title_text));
    }

    @Override
    protected void initActivity() {
        userInfo = (UserBean.UserItemBean)getIntent().getSerializableExtra("userInfo");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, screenWidth*2/3);
        iconBg.setLayoutParams(params);
        iconLayout.setLayoutParams(params);

        if (userInfo != null) {
            if (!TextUtils.isEmpty(userInfo.gender)) {
                gender = Integer.parseInt(userInfo.gender);
            }
            if (gender == 0) {
                genderText.setText("男");
            } else {
                genderText.setText("女");
            }
            telNumberText.setText(userInfo.tel_number);
            signEdit.setText(userInfo.sign);
            if (!TextUtils.isEmpty(userInfo.icon)) {
                Glide.with(this).load(userInfo.icon).asBitmap().into(new SimpleTarget<Bitmap>(screenWidth, screenWidth * 2 / 3) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        icon.setImageBitmap(bitmap);
                        originBitmap = bitmap;
                        handler.sendEmptyMessage(3);
                    }
                });
            }
        }

        signEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                signIsModified = !signIsModified;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        icon.setOnClickListener(this);
        chooseIcon.setOnClickListener(this);
        genderLayout.setOnClickListener(this);
        signLayout.setOnClickListener(this);
        logoutText.setOnClickListener(this);
    }

    private void setBlurImageView(Bitmap originBitmap, int scaleRatio) {
        Matrix matrix = new Matrix();
        matrix.postScale(1 / scaleRatio, 1 / scaleRatio);
        LogUtil.i("width===" + originBitmap.getWidth() + "  height==" + originBitmap.getHeight() + "matrix==");
        //Bitmap scaledBitmap = Bitmap.createBitmap(originBitmap, 0, 0, originBitmap.getWidth(), originBitmap.getHeight(), matrix, true);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap, originBitmap.getWidth() / scaleRatio,
                originBitmap.getHeight() / scaleRatio, false);
        Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, 20, true);

        iconBg.setImageBitmap(blurBitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon:
                final ChooseMenuDialog dialog = new ChooseMenuDialog(this, R.style.ChooseMenuDialogTheme);
                dialog.show();
                dialog.getPhotoText().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file = new File(PathManager.getInstance().getImgCacheDir() + "/temp_icon.jpg");
                        LogUtil.i("filepath==" + file.getPath());
                        iconUri = Uri.fromFile(file);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, iconUri);
                        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
                        dialog.dismiss();
                    }
                });

                dialog.getChooseText().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.gender_layout:
                chooseIcon.setVisibility(View.VISIBLE);
                if (gender == 0) {
                    chooseIcon.setImageResource(R.drawable.gender_man);
                } else {
                    chooseIcon.setImageResource(R.drawable.gender_woman);
                }
                break;
            case R.id.choose_icon:
                gender = ~gender;
                genderIsModified = !genderIsModified;
                if (gender == 0) {
                    genderText.setText("男");
                    chooseIcon.setImageResource(R.drawable.gender_man);
                } else {
                    genderText.setText("女");
                    chooseIcon.setImageResource(R.drawable.gender_woman);
                }
                chooseIcon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
                chooseIcon.setVisibility(View.GONE);
                break;
            case R.id.sign_layout:
//                signEdit.setFocusable(true);
//                signEdit.setFocusableInTouchMode(true);
//                signEdit.requestFocus();
                break;
            case R.id.logout_text:
                final PromptDialog promptDialog = new PromptDialog(this);
                promptDialog.show();
                promptDialog.setContentText("确定要退出登录吗");
                promptDialog.setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonApplication.isLogined = false;
                        UserManager.getInstance(MyInfoActivity.this).clearUserInfo();
                        EventBus.getDefault().post(new EventBusBean.LogoutBean());
                        promptDialog.dismiss();
                        finish();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(intent != null) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_PHOTO:
                    startCropImage(iconUri);
                    break;
                case REQUEST_CODE_CHOOSE_IMAGE:
                    if (intent.getData() != null) {
                        iconUri = intent.getData();
                        startCropImage(iconUri);
                    }
                    break;
                case REQUEST_CODE_CROP_IMAGE:
//                    ProgressDialog.show(this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getToken(getPathByUri(cropImageUri));
                            //uploadImage(getPathByUri(cropImageUri));
                        }
                    }).start();
                    break;
                default:
                    break;

            }
        } else {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO && iconUri != null) {
                startCropImage(iconUri);
            }
        }
    }

    private void updateUserInfo() {
        updateQuery = new BaseHttpQuery<BaseCommonProtocolBean>(this, BaseCommonProtocolBean.class,
                new BaseHttpQuery.OnQueryFinishListener<BaseCommonProtocolBean>() {
            @Override
            public void onFinish(BaseCommonProtocolBean bean) {
                if (bean.code == 1) {
                    userInfo.gender = gender + "";
                    userInfo.sign = signEdit.getText().toString();
                    UserManager.getInstance(MyInfoActivity.this).saveUserInfo(userInfo);
                    ToastUtil.show(MyInfoActivity.this, "信息更新成功");
                } else {
                    ToastUtil.show(MyInfoActivity.this, "信息更新失败");
                }
            }

            @Override
            public void onError(int errCode, String errMsg) {
                ToastUtil.show(MyInfoActivity.this, errMsg);
            }
        });

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", userInfo.id);
        params.put("gender", gender + "");
        params.put("sign", signEdit.getText().toString());
        updateQuery.doPostQuery(ServerConfig.urlWithSuffix(ServerConfig.UPDATE_USER_INFO), params);
    }

    /**
     * 裁减图片操作
     * 注意：
     *   1. 由于Android对应用程序的内存有严格限制，为了防止传送大图片出现OOM，在拍照截图的过程中默认传递的都是缩略图，
     *      这样裁减后的图片非常模糊。而采用URi的方式传递的是大图片，需要注意的是，如果需要传递大图片，那么拍照时需要设置
     *      照片的输出路径，也就是在调用相机时传递MediaStore.EXTRA_OUTPUT参数，同时为了防止某些手机默认传递大图片的
     *      问题（如小米4），在传递大图片时将return-data置为false.
     *
     *   2. 对于某些手机或平板，输入Uri和输出Uri不能相等，否则裁减后的图片大小变为0kb,如平板Teclast P80h.
     * @param uri
     */
    private void startCropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 图片处于可裁剪状态
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        // 裁剪后图片的大小
        //intent.putExtra("outputX", 500);
        //intent.putExtra("outputY", 500);
        //intent.putExtra("scale", true);
        // 以Uri的方式传递照片，如果
        File cropFile = new File(PathManager.getInstance().getImgCacheDir() + "crop_image.jpg");
        cropImageUri = Uri.fromFile(cropFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
        // 裁剪区域的形状为圆形，默认为矩形
        //intent.putExtra("circleCrop", true);
        // 设置输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // return-data=true传递的为缩略图，小米手机默认传递大图，所以会导致onActivityResult调用失败
        intent.putExtra("return-data", false);
        // 关闭人脸识别
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    private void getToken(final String filePath) {
        tokenQuery = new BaseHttpQuery<BaseCommonProtocolBean>(this, BaseCommonProtocolBean.class,
                new BaseHttpQuery.OnQueryFinishListener<BaseCommonProtocolBean>() {
                    @Override
                    public void onFinish(final BaseCommonProtocolBean bean) {
                        if (bean.code == 1 && !TextUtils.isEmpty(bean.data)) {
                            uploadImageToQiniu(filePath, bean.data);
                        } else {
                            LogUtil.i(bean.msg);
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        LogUtil.i("error:" + errMsg);
                    }
                });

        tokenQuery.doGetQuery(ServerConfig.urlWithSuffix(ServerConfig.GET_TOKEN));
    }

    private void uploadImageToQiniu(String filePath, String token) {
        UploadManager uploadManager = new UploadManager();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = "icon_" + sdf.format(new Date());
        uploadManager.put(filePath, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                LogUtil.i("result===" + info.path + "   ==" + info.toString());
                uploadImageToServer(key);
            }
        }, null);
    }

    private void uploadImageToServer(String url) {
        uploadQuery = new BaseHttpQuery<BaseCommonProtocolBean>(this, BaseCommonProtocolBean.class,
                new BaseHttpQuery.OnQueryFinishListener<BaseCommonProtocolBean>() {
                    @Override
                    public void onFinish(BaseCommonProtocolBean bean) {
                        if (bean.code == 1) {
                            ToastUtil.show(MyInfoActivity.this, "头像上传成功");
                            Glide.with(MyInfoActivity.this).
                                    load(bean.data).asBitmap().into(new SimpleTarget<Bitmap>(screenWidth, screenWidth * 2 / 3) {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                    originBitmap = bitmap;
                                    handler.sendEmptyMessage(3);
                                }
                            });
                        } else {
                            ToastUtil.show(MyInfoActivity.this, "头像上传失败");
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        LogUtil.i("error:" + errMsg);
                    }
                });

        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", CommonApplication.userInfo.id);
        params.put("icon", url);
        uploadQuery.doPostQuery(ServerConfig.urlWithSuffix(ServerConfig.UPLOAD_ICON), params);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public String getPathByUri(final Uri uri) {
        String path = null;
        if (uri != null) {
            String scheme = uri.getScheme();
            if (TextUtils.isEmpty(scheme)) {
                path = uri.getPath();
            } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        if (index > -1) {
                            path = cursor.getString(index);
                        }
                    }
                    cursor.close();
                }
            } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
                path = uri.getPath();
            } else {
                /**
                 * Nothing
                 */
            }
        }

        return path;
    }

    @Override
    public void finish() {
        // 退出时保存用户信息
        if (genderIsModified || signIsModified) {
            updateUserInfo();
        }

        super.finish();
    }
}
