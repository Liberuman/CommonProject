package com.sxu.commonproject.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.ACache;
import com.sxu.commonproject.baseclass.AVImClientManager;
import com.sxu.commonproject.manager.UserManager;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.ToastUtil;
import com.sxu.commonproject.view.NavigationBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by juhg on 16/3/1.
 */
public class ConversationActivity extends BaseActivity {

    private EditText msgContentEdit;
    private TextView sendText;
    private ListView conversationList;
    private PullToRefreshListView pullToRefreshListView;
    private NavigationBar navigationBar;

    private boolean isSingle;
    //private String userId;
    private String userIcon;
    private String userName;
    private List<AVIMMessage> msgRecordList = new ArrayList<AVIMMessage>();
    private ConversationAdapter msgAdapter;
    private AVIMConversation squareConversation;
    private AVIMConversation singleConversation;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_conversation;
    }

    @Override
    protected void getViews() {
        msgContentEdit = (EditText)findViewById(R.id.msg_content_edit);
        sendText = (TextView)findViewById(R.id.send_text);
        pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.converation_list);
        navigationBar = (NavigationBar)findViewById(R.id.navigationBar);

        CommonApplication.setTypeface(msgContentEdit);
        CommonApplication.setTypeface(sendText);
    }

    public static void enter(Context context, String userId, String userIcon, String userName) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra("isSingle", true);
        intent.putExtra("userId", userId);
        intent.putExtra("userIcon", userIcon);
        intent.putExtra("userName", userName);
        context.startActivity(intent);
    }

    public static void enter(Context context, boolean isSingle, String userId, String userIcon, String userName) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra("isSingle", isSingle);
        intent.putExtra("userId", userId);
        intent.putExtra("userIcon", userIcon);
        intent.putExtra("userName", userName);
        context.startActivity(intent);
    }

    @Override
    protected void initActivity() {
        isSingle = getIntent().getBooleanExtra("isSingle", false);
        //userId = getIntent().getStringExtra("userId");
        userIcon = getIntent().getStringExtra("userIcon");
        userName = getIntent().getStringExtra("userName");

        LogUtil.i("isSingle==" + isSingle + " userName==" + userName);
        pullToRefreshListView.setShowIndicator(false);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        conversationList = pullToRefreshListView.getRefreshableView();
        navigationBar.showReturnIcon().setTitle(userName);
        // 缓存用户头像，便于在消息列表页面展示
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userIcon)) {
            ACache cache = ACache.get(CommonApplication.getInstance());
            cache.put(userName, userIcon);
        }
        if (isSingle) {
            initClient();
        } else {
            String conversationId = getIntent().getStringExtra("conversationId");
            LogUtil.i("conversationId==" + conversationId);
            if (!TextUtils.isEmpty(conversationId)) {
                getSquare(conversationId);
                queryInSquare(conversationId);
            }
        }

        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = msgContentEdit.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    sendMsg(content);
                } else {
                    Toast.makeText(ConversationActivity.this, "消息不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, new CustomMessageHandler());
    }

    /**
     * 发送消息
     * @param content
     */
    private void sendMsg(final String content) {
        final AVIMMessage msg = new AVIMMessage();
        msg.setContent(content);
        msg.setFrom(CommonApplication.userInfo.nick_name);
        // 发送消息
        if (singleConversation != null) {
            singleConversation.sendMessage(msg, new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    if (filterException(e)) {
                        msgContentEdit.setText("");
                        msgRecordList.add(msg);
                        if (msgAdapter == null) {
                            msgAdapter = new ConversationAdapter();
                            conversationList.setAdapter(msgAdapter);
                        } else {
                            msgAdapter.notifyDataSetChanged();
                        }
                        conversationList.smoothScrollToPosition(msgRecordList.size());
                    }
                }
            });
        }
    }

    /**
     * 检查客户端的连接状态，并创建会话
     */
    private void initClient() {
        if (CommonApplication.client != null) {
            LogUtil.i("客户端＝＝" + CommonApplication.client.getClientId());
            // 创建会话
            CommonApplication.client.createConversation(Arrays.asList(userName), userName,
                    null, false, true, new AVIMConversationCreatedCallback() {
                @Override
                public void done(AVIMConversation conversation, AVIMException e) {
                    if (filterException(e)) {
                        singleConversation = conversation;
                        LogUtil.i("会话创建成功" + conversation.getConversationId());
                        setConversation(conversation);
                    } else {
                        LogUtil.i("会话创建失败");
                    }
                }
            });
        } else {
            LogUtil.i("login userName" + UserManager.getInstance(this).getUserName());
            CommonApplication.client = AVIMClient.getInstance(CommonApplication.userInfo.nick_name);
            CommonApplication.client.open(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient client, AVIMException e) {
                    if (filterException(e)) {
                        // 创建会话
                        client.createConversation(Arrays.asList(userName), userName, null, new AVIMConversationCreatedCallback() {
                            @Override
                            public void done(AVIMConversation conversation, AVIMException e) {
                                if (filterException(e)) {
                                    singleConversation = conversation;
                                    ToastUtil.show(ConversationActivity.this, "连接成功");
                                } else {
                                    LogUtil.i("连接失败");
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 获取指定会话的聊天记录
     * @param conversation
     */
    private void setConversation(AVIMConversation conversation) {
        conversation.queryMessages(new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
                if (filterException(e) && list.size() > 0) {
                    LogUtil.i("聊天记录不为空");
                    msgRecordList.addAll(list);
                    if (msgAdapter == null) {
                        msgAdapter = new ConversationAdapter();
                        conversationList.setAdapter(msgAdapter);
                    } else {
                        msgAdapter.notifyDataSetChanged();
                    }
                    conversationList.smoothScrollToPosition(msgRecordList.size());
                } else {
                    LogUtil.i("聊天记录为空");
                }
            }
        });
    }

    /**
     * 异常处理
     * @param e
     * @return
     */
    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace(System.out);
            LogUtil.e(e.getMessage() + e.getLocalizedMessage() + e.getCause().toString());
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据 conversationId 查取本地缓存中的 conversation，如若没有缓存，则返回一个新建的 conversaiton
     */
    private void getSquare(String conversationId) {
        if (TextUtils.isEmpty(conversationId)) {
            throw new IllegalArgumentException("conversationId can not be null");
        }

        AVIMClient client = AVImClientManager.getInstance().getClient();
        squareConversation = client.getConversation(conversationId);
    }

    /**
     * 加入 conversation
     */
    private void joinSquare() {
        squareConversation.join(new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (filterException(e)) {
                    setConversation(squareConversation);
                }
            }
        });
    }

    /**
     * 先查询自己是否已经在该 conversation，如果存在则直接给 chatFragment 赋值，否则先加入，再赋值
     */
    private void queryInSquare(String conversationId) {
        final AVIMClient client = AVImClientManager.getInstance().getClient();
        AVIMConversationQuery conversationQuery = client.getQuery();
        conversationQuery.whereEqualTo("objectId", conversationId);
        conversationQuery.containsMembers(Arrays.asList(AVImClientManager.getInstance().getClientId()));
        conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (filterException(e)) {
                    if (null != list && list.size() > 0) {
                        setConversation(list.get(0));
                    } else {
                        joinSquare();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, new CustomMessageHandler());
    }

    public class CustomMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message,AVIMConversation conversation,AVIMClient client){
            LogUtil.d("onMessage" + message.getContent() + " clientId==" + client.getClientId());
            //message.setFrom(client.getClientId());
            msgRecordList.add(message);
            if (msgAdapter == null) {
                msgAdapter = new ConversationAdapter();
                conversationList.setAdapter(msgAdapter);
            } else {
                msgAdapter.notifyDataSetChanged();
            }
            conversationList.smoothScrollToPosition(msgRecordList.size());
        }

        public void onMessageReceipt(AVIMMessage message,AVIMConversation conversation,AVIMClient client){
            LogUtil.d("onMessageReceipt");
        }
    }

    private class ConversationAdapter extends BaseAdapter {

        /**
         * 收到的消息布局类型
         */
        private final int VIEW_TYPE_OTHER = 0;
        /**
         * 发送的消息布局类型
         */
        private final int VIEW_TYPE_MY = 1;
        /**
         * 布局类型的数量
         */
        private final int VIWE_TYPE_COUNT = 2;

        @Override
        public int getCount() {
            return msgRecordList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return msgRecordList.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return VIWE_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            if (msgRecordList.get(position).getFrom() != null
                    && msgRecordList.get(position).getFrom().equals(CommonApplication.userInfo.nick_name)) {
                return VIEW_TYPE_MY;
            }

            return VIEW_TYPE_OTHER;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OtherViewHolder otherHolder = null;
            MyViewHolder myHolder = null;
            int type = getItemViewType(position);
            if (convertView == null) {
                otherHolder = new OtherViewHolder();
                myHolder = new MyViewHolder();
                switch (type) {
                    case VIEW_TYPE_OTHER:
                        convertView = getLayoutInflater().from(ConversationActivity.this).inflate(R.layout.item_conversation_left_layout, parent, false);
                        otherHolder.icon = (SimpleDraweeView) convertView.findViewById(R.id.user_icon);
                        otherHolder.contentText = (TextView)convertView.findViewById(R.id.content_text);
                        convertView.setTag(otherHolder);
                        break;
                    case VIEW_TYPE_MY:
                        convertView = getLayoutInflater().from(ConversationActivity.this).inflate(R.layout.item_conversation_right_layout, parent, false);
                        myHolder.icon = (SimpleDraweeView) convertView.findViewById(R.id.user_icon);
                        myHolder.contentText = (TextView)convertView.findViewById(R.id.content_text);
                        convertView.setTag(myHolder);
                        break;
                    default:
                        break;
                }
            } else {
                switch (type) {
                    case VIEW_TYPE_OTHER:
                        otherHolder = (OtherViewHolder) convertView.getTag();
                        break;
                    case VIEW_TYPE_MY:
                        myHolder = (MyViewHolder) convertView.getTag();
                        break;
                    default:
                        break;
                }
            }

            if (type == VIEW_TYPE_OTHER) {
                setItemValue(otherHolder, position);
            } else {
                setItemValue(myHolder, position);
            }

            return convertView;
        }

        /**
         * 设置子布局的内容
         * @param viewHolder
         * @param position
         */
        private void setItemValue(ViewHolder viewHolder, int position) {
            viewHolder.contentText.setText(msgRecordList.get(position).getContent());
            CommonApplication.setTypeface(viewHolder.contentText);
            if (msgRecordList.get(position).getFrom() != null && msgRecordList.get(position).getFrom().equals(CommonApplication.userInfo.id)) {
                if (!TextUtils.isEmpty(CommonApplication.userInfo.icon)) {
                    viewHolder.icon.setImageURI(Uri.parse(CommonApplication.userInfo.icon));
                }
            } else {
                if (!TextUtils.isEmpty(userIcon)) {
                    viewHolder.icon.setImageURI(Uri.parse(userIcon));
                }
            }
        }

        private class ViewHolder {
            public SimpleDraweeView icon;
            public TextView contentText;
            public TextView timeText;
        }

        private class OtherViewHolder extends ViewHolder {

        }

        private class MyViewHolder extends ViewHolder  {

        }
    }
}
