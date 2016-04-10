package com.sxu.commonproject.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.AVImClientManager;
import com.sxu.commonproject.manager.UserManager;
import com.sxu.commonproject.util.AndroidPlatformUtil;
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
    private String userId;
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

    @Override
    protected void initActivity() {
        isSingle = getIntent().getBooleanExtra("isSingle", false);
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");
        LogUtil.i("isSingle==" + isSingle + " userId==" + userId + " userName==" + userName);
        pullToRefreshListView.setShowIndicator(false);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        conversationList = pullToRefreshListView.getRefreshableView();
        navigationBar.showReturnIcon().setTitle(userName);
        if (isSingle) {
            initClient(userId);
        } else {
            String conversationId = getIntent().getStringExtra("conversationId");
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

    private void sendMsg(final String content) {
        AVIMMessage msg = new AVIMMessage();
        msg.setContent(content);
        msgRecordList.add(msg);
        if (msgAdapter == null) {
            msgAdapter = new ConversationAdapter();
            conversationList.setAdapter(msgAdapter);
        } else {
            msgAdapter.notifyDataSetChanged();
        }
        conversationList.smoothScrollToPosition(msgRecordList.size());
        msgContentEdit.setText("");
        //msg.setText(content);
        // 发送消息
        if (singleConversation != null) {
            singleConversation.sendMessage(msg, new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    if (e == null) {
                        LogUtil.i("发送成功！" + content);
                    }
                }
            });
        }
    }

    public static void enter(Context context, String userId, String userName, boolean isSingle) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("isSingle", isSingle);
        context.startActivity(intent);
    }

    private void initClient(final String userId) {
        if (CommonApplication.client != null) {
            // 获取聊天记录
            getSingleConversation(userId);
            // 创建对话
            CommonApplication.client.createConversation(Arrays.asList(userId), userName, null, false, true, new AVIMConversationCreatedCallback() {
                @Override
                public void done(AVIMConversation conversation, AVIMException e) {
                    if (filterException(e)) {
                        singleConversation = conversation;
                        CommonApplication.conversationMap.put(userId, conversation);
                        LogUtil.i("连接成功" + conversation.getConversationId());
                    }
                }
            });
        } else {
            CommonApplication.client = AVIMClient.getInstance(UserManager.getInstance(this).getUserId());
            CommonApplication.client.open(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient client, AVIMException e) {
                    if (filterException(e)) {
                        getSingleConversation(userId);
                        // 创建对话
                        client.createConversation(Arrays.asList(userId), userName, null, new AVIMConversationCreatedCallback() {
                            @Override
                            public void done(AVIMConversation conversation, AVIMException e) {
                                if (filterException(e)) {
                                    singleConversation = conversation;
                                    ToastUtil.show(ConversationActivity.this, "连接成功");
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void getSingleConversation(final String userId) {
        //final AVIMClient client = AVImClientManager.getInstance().getClient();
        /*AVIMConversationQuery conversationQuery = CommonApplication.client.getQuery();
        conversationQuery.withMembers(Arrays.asList(userId), true);
        conversationQuery.whereEqualTo("customConversationType", 1);
        conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (filterException(e)) {
                    //注意：此处仍有漏洞，如果获取了多个 conversation，默认取第一个
                    if (null != list && list.size() > 0) {
                        setConversation(list.get(0));
                    } else {
                        HashMap<String, Object> attributes = new HashMap<String, Object>();
                        attributes.put("customConversationType", 1);
                        CommonApplication.client.createConversation(Arrays.asList(userId), null, attributes, false, new AVIMConversationCreatedCallback() {
                            @Override
                            public void done(AVIMConversation avimConversation, AVIMException e) {
                                setConversation(avimConversation);
                            }
                        });
                    }
                }
            }
        });*/

        if (CommonApplication.conversationMap.get(userId) != null) {
            setConversation(CommonApplication.conversationMap.get(userId));
        }
    }

    private void setConversation(AVIMConversation conversation) {
        conversation.queryMessages(new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
                if (filterException(e) && list.size() > 0) {
                    msgRecordList.addAll(list);
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

    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
            ToastUtil.show(this, e.getMessage() + e.getLocalizedMessage());
            LogUtil.i(e.getMessage() + e.getLocalizedMessage() + e.getCause().toString());
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
            message.setFrom(client.getClientId());
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().from(ConversationActivity.this).inflate(R.layout.item_conversation_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.contentText = (TextView)convertView.findViewById(R.id.content_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)viewHolder.contentText.getLayoutParams();
            int padding = AndroidPlatformUtil.dpToPx(ConversationActivity.this, 5);

            if (msgRecordList.get(position).getFrom() != null && msgRecordList.get(position).getFrom().equals("")) {
                params.gravity = Gravity.RIGHT;
                viewHolder.contentText.setBackgroundResource(R.drawable.a86);
                viewHolder.contentText.setPadding(padding, padding, padding*2, padding);
            } else {
                params.gravity = Gravity.LEFT;
                viewHolder.contentText.setBackgroundResource(R.drawable.a84);
                //viewHolder.contentText.setPadding(padding*2, padding, padding, padding);
            }
            viewHolder.contentText.setLayoutParams(params);
            viewHolder.contentText.setText(msgRecordList.get(position).getContent());
            CommonApplication.setTypeface(viewHolder.contentText);

            return convertView;
        }

        private class ViewHolder {
            public ImageView icon;
            public TextView contentText;
            private TextView timeText;
        }
    }

    @Override
    public void finish() {
        //startService(new Intent(this, ReceiverMsgService.class));
        super.finish();
    }
}
