package com.sxu.commonproject.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.sxu.commonproject.R;
import com.sxu.commonproject.activity.ConversationActivity;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.manager.UserManager;
import com.sxu.commonproject.bean.ContactBean;
import com.sxu.commonproject.bean.EventBusBean;
import com.sxu.commonproject.bean.UserBean;
import com.sxu.commonproject.http.BaseHttpQuery;
import com.sxu.commonproject.protocol.ServerConfig;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by juhg on 16/3/9.
 */
public class ReceiverMsgService extends Service {

    private BaseHttpQuery<UserBean> userInfoQuery;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, new CustomMessageHandler());
        loginLeanCloud();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void loginLeanCloud() {
        if (!TextUtils.isEmpty(UserManager.getInstance(this).getUserName())) {
            if (CommonApplication.client == null) {
                CommonApplication.client = AVIMClient.getInstance(CommonApplication.userInfo.nick_name);
            }
            LogUtil.i("客户端==" + CommonApplication.client.getClientId());
            CommonApplication.client.open(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient client, AVIMException e) {
                    if (e != null) {
                        e.printStackTrace(System.out);
                    } else {
                        LogUtil.i("客户端 连接成功==" + client.getClientId());
                    }
                }
            });
        }
    }

    public class CustomMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            LogUtil.d("onMessage" + message.getContent() + conversation.getName() + " clientId==" + client.getClientId() + " from==" + message.getFrom());
            showNotification(message.getFrom(), message.getContent());
            getUserInfo(client.getClientId(), message.getContent());
            EventBus.getDefault().post(new EventBusBean.UpdateConversation(message));
        }

        public void onMessageReceipt(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            LogUtil.d("onMessageReceipt");
        }
    }

    private void showNotification(String clientId, String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("userName", clientId);
        intent.putExtra("isSingle", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("来自" + clientId + "的新消息")
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0, notification);
        LogUtil.i("显示通知消息");
    }

    private void getUserInfo(String userId, final String content) {
        userInfoQuery = new BaseHttpQuery<UserBean>(this, UserBean.class, new BaseHttpQuery.OnQueryFinishListener<UserBean>() {
            @Override
            public void onFinish(UserBean bean) {
                if (bean.code == 1 && bean.data != null) {
                    // 更新消息列表页面
                    //ContactBean contact = new ContactBean(bean.data.id, R.drawable.ic_launcher, bean.data.nick_name,
                    //content, getCurrentTime());
                    //EventBus.getDefault().post(new EventBusBean.UpdateMsgList(contact));
                } else {
                    ToastUtil.show(ReceiverMsgService.this, bean.msg);
                }
            }

            @Override
            public void onError(int errCode, String errMsg) {
                LogUtil.i(errMsg);
            }
        });

        userInfoQuery.doGetQuery(ServerConfig.urlWithSuffix(String.format(ServerConfig.GET_USER_INFO, userId)));
    }

    private String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
}
