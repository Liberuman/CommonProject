package com.sxu.commonproject.manager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.sxu.commonproject.R;
import com.sxu.commonproject.activity.MainActivity;
import com.sxu.commonproject.baseclass.DownloadProgressListener;
import com.sxu.commonproject.baseclass.FileDownloaderManager;
import com.sxu.commonproject.bean.VersionBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.AndroidPlatformUtil;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.NetworkUtil;
import com.sxu.commonproject.util.SharePreferenceTag;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.view.PreferenceUtil;
import com.sxu.commonproject.view.PromptDialog;

import java.io.File;

/*******************************************************************************
 * FileName: VersionUpdateManager
 * <p/>
 * Description:
 * <p/>
 * Author: juhg
 * <p/>
 * Version: v1.0
 * <p/>
 * Date: 16/4/13
 * <p/>
 *******************************************************************************/
public class VersionUpdateManager {

    private Context context;
    public VersionUpdateManager(Context context) {
        this.context = context;
    }

    public void checkVersion() {
        BaseHttpQuery<VersionBean> versionQuery = new BaseHttpQuery<>(context, VersionBean.class,
                new BaseHttpQuery.OnQueryFinishListener<VersionBean>() {
                    @Override
                    public void onFinish(VersionBean bean) {
                        if (bean.data != null) {
                            if (bean.data.verCode > AndroidPlatformUtil.getVersionCode(context)) {
                                if (NetworkUtil.isValidWifiNetwork(context)) {
                                    updateVersion(bean.data);
                                } else {
                                    if (context instanceof MainActivity) {
                                        final VersionBean.VersionItemBean versionInfo = bean.data;
                                        final PromptDialog promptDialog = new PromptDialog(context);
                                        promptDialog.show();
                                        promptDialog.setContentText("发现新版本，是否现在安装");
                                        promptDialog.setConfirmClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                promptDialog.dismiss();
                                                updateVersion(versionInfo);
                                            }
                                        });
                                    }
                                }
                            } else {
                                if (context instanceof MainActivity) {
                                    ToastUtil.show(context, "已经是最新版本了");
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(int errCode, String errMsg) {
                        //ToastUtil.show(context, errMsg);
                    }
                });

        if (PreferenceUtil.getBoolean(SharePreferenceTag.HAS_NEW_VERSION, false)
                && !TextUtils.isEmpty(PreferenceUtil.getString(SharePreferenceTag.NEW_VERSION_PATH))) {
            final PromptDialog promptDialog = new PromptDialog(context);
            promptDialog.show();
            promptDialog.setContentText("发现新版本，是否现在安装");
            promptDialog.setConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    promptDialog.dismiss();
                    PreferenceUtil.putBoolean(SharePreferenceTag.HAS_NEW_VERSION, false);
                    PreferenceUtil.putString(SharePreferenceTag.NEW_VERSION_PATH, "");
                    launchNewVersion(PreferenceUtil.getString(SharePreferenceTag.NEW_VERSION_PATH));
                }
            });
        } else {
            versionQuery.doGetQuery(ServerConfig.urlWithSuffix(ServerConfig.CHECK_VERSION));
        }
    }

    public void updateVersion(final VersionBean.VersionItemBean versionInfo) {
        if (context instanceof MainActivity) {
            final PromptDialog promptDialog = new PromptDialog(context);
            promptDialog.show();
            promptDialog.setContentText("点击确定下载新版本");
            promptDialog.setConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    promptDialog.dismiss();
                    downloadNewVersion(versionInfo, true);
                }
            });
        } else {
            downloadNewVersion(versionInfo, false);
        }
    }

    private void downloadNewVersion(VersionBean.VersionItemBean versionInfo, final boolean showProgressBar) {

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = new Notification(R.drawable.ic_launcher, "下载中", System.currentTimeMillis());
        if (showProgressBar) {
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.contentView = new RemoteViews(context.getPackageName(), R.layout.download_layout);
            notification.contentView.setProgressBar(R.id.progressBar, 100, 0, false);
            notificationManager.notify(0, notification);
        }

        FileDownloaderManager downloaderManager = FileDownloaderManager.getInstance();
        downloaderManager.download(context, versionInfo.downloadUrl, versionInfo.version,
                PathManager.getInstance().getApksDir(), new DownloadProgressListener() {
                    @Override
                    public void onDownloadSize(int size, int totalSize) {
                        if (showProgressBar) {
                            LogUtil.i("current size==" + (100.0f * size / totalSize));
                            notification.contentView.setProgressBar(R.id.progressBar, totalSize, size, false);
                            notification.contentView.setTextViewText(R.id.percentage_text, ((int)(size * 100.0f / totalSize)) + "%");
                            notificationManager.notify(0, notification);
                        }
                    }

                    @Override
                    public void onDownloadFinish(String fileName) {
                        if (showProgressBar) {
                            notification.contentView.setTextViewText(R.id.download_text, "下载完成");
                            notification.contentView.setTextViewText(R.id.percentage_text, "100%");
                            Uri uri = Uri.fromFile(new File(fileName));
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
                            notification.contentIntent = PendingIntent.getActivity(context, (int) SystemClock.uptimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            notificationManager.notify(0, notification);
                            launchNewVersion(fileName);
                        } else {
                            PreferenceUtil.putBoolean(SharePreferenceTag.HAS_NEW_VERSION, true);
                            PreferenceUtil.putString(SharePreferenceTag.NEW_VERSION_PATH, fileName);
                        }
                    }

                    @Override
                    public void onDownLoadFail() {
                        LogUtil.i("downloadFail");
                    }
                });
    }

    private void launchNewVersion(String newVersionPath) {
        Uri uri = Uri.fromFile(new File(newVersionPath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity)context).finish();
        } else if (context instanceof Service){
            ((Service) context).stopSelf();
        } else {
            /**
             * Nothing
             */
        }
    }
}
