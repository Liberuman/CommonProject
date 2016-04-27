package com.sxu.commonproject.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.sxu.commonproject.R;
import com.sxu.commonproject.activity.ConversationActivity;
import com.sxu.commonproject.activity.LoginActivity;
import com.sxu.commonproject.activity.NearByContactActivity;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.baseclass.ACache;
import com.sxu.commonproject.baseclass.BaseCommonAdapter;
import com.sxu.commonproject.baseclass.BaseViewHolder;
import com.sxu.commonproject.bean.ContactBean;
import com.sxu.commonproject.bean.EventBusBean;
import com.sxu.commonproject.util.LogUtil;
import com.sxu.commonproject.util.TimeFormatUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by juhg on 16/2/22.
 */
public class MsgFragment extends BaseFragment {

    private TextView tipsText;
    private ListView contactList;
    private List<ContactBean> contactData = new ArrayList<ContactBean>();
    private BaseCommonAdapter<ContactBean> contactAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tab_msg;
    }

    @Override
    public void getViews() {
        tipsText = (TextView)rootView.findViewById(R.id.tips_text);
        contactList = (ListView)rootView.findViewById(R.id.contact_list);
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.msg_list_header_view, null);
        contactList.addHeaderView(headerView);

        CommonApplication.setTypeface(tipsText);
        CommonApplication.setTypeface((TextView) headerView.findViewById(R.id.nearby_text));
    }

    @Override
    public void initFragment() {
        getLatestContact();
        setContactAdapter();
        EventBus.getDefault().register(this);

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id >= 0) {
                    Intent intent = new Intent(getActivity(), ConversationActivity.class);
                    intent.putExtra("userId", contactData.get((int) id).id);
                    intent.putExtra("userName", contactData.get((int) id).nick_name);
                    intent.putExtra("isSingle", true);
                    startActivity(intent);
                    ConversationActivity.enter(getActivity(), contactData.get((int) id).id,
                            contactData.get((int) id).icon, contactData.get((int) id).nick_name);
                } else {
                    if (CommonApplication.isLogined) {
                        startActivity(new Intent(getActivity(), NearByContactActivity.class));
                    } else {
                        LoginActivity.enter(getActivity(), true);
                    }
                }
            }
        });
    }

    private void getLatestContact() {
        if (CommonApplication.client == null && !TextUtils.isEmpty(CommonApplication.userInfo.nick_name)) {
            CommonApplication.client = AVIMClient.getInstance(CommonApplication.userInfo.nick_name);
        }

        if (CommonApplication.client != null) {
            CommonApplication.client.open(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient client, AVIMException e) {
                    if (e == null) {
                        //登录成功
                        AVIMConversationQuery query = client.getQuery();
                        query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
                        query.findInBackground(new AVIMConversationQueryCallback() {
                            @Override
                            public void done(final List<AVIMConversation> conversationList, AVIMException e) {
                                if (e == null && conversationList != null) {
                                    LogUtil.i("size===记录==" + conversationList.size());
                                    for (int i = 0; i < conversationList.size(); i++) {
                                        final int index = i;
                                        conversationList.get(i).getLastMessage(new AVIMSingleMessageQueryCallback() {
                                            @Override
                                            public void done(AVIMMessage avimMessage, AVIMException e) {
                                                if (e == null) {
                                                    ContactBean itemContact = new ContactBean();
                                                    itemContact.nick_name = conversationList.get(index).getName();
                                                    // 获取缓存的用户头像
                                                    ACache cache = ACache.get(CommonApplication.getInstance());
                                                    itemContact.icon = cache.getAsString(itemContact.nick_name);
                                                    itemContact.content = avimMessage.getContent();
                                                    itemContact.time = TimeFormatUtil.getTimeDesc(
                                                            conversationList.get(index).getUpdatedAt().getTime() / 1000);
                                                    contactData.add(itemContact);
                                                    LogUtil.i("nickName=" + itemContact.nick_name + " time==" + conversationList.get(index).getUpdatedAt().getTime());
                                                    setContactAdapter();
                                                } else {
                                                    e.printStackTrace(System.out);
                                                    setContactAdapter();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void setContactAdapter() {
        if (contactData.size() > 0) {
            tipsText.setVisibility(View.GONE);
        } else {
            tipsText.setVisibility(View.VISIBLE);
        }

        if (contactAdapter == null) {
            contactAdapter = new BaseCommonAdapter<ContactBean>(getActivity(), contactData, R.layout.item_msg_layout) {
                @Override
                public void convert(BaseViewHolder viewHolder, ContactBean data) {
                    viewHolder.setImageResource(R.id.icon, R.drawable.default_icon, null, data.icon);
                    viewHolder.setText(R.id.nickname_text, data.nick_name);
                    viewHolder.setText(R.id.msg_content_text, data.content);
                    viewHolder.setText(R.id.time_text, TimeFormatUtil.getTimeDesc(TimeFormatUtil.strTimeToLong(data.time)));
                }
            };
            contactList.setAdapter(contactAdapter);
        } else {
            contactAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvnet(EventBusBean.UpdateMsgList event) {
        boolean isFound = false;
        if (event.contactInfo != null) {
            LogUtil.i("info=====" + event.contactInfo.toString());
            for (int i = 0; i < contactData.size(); i++) {
                if (contactData.get(i).id.equals(event.contactInfo.id)) {
                    // 接收到同一联系人的消息时更新内容和时间
                    contactData.get(i).content = event.contactInfo.content;
                    contactData.get(i).time = event.contactInfo.time;
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                contactData.add(event.contactInfo);
            }
            contactAdapter.notifyDataSetChanged();
            tipsText.setVisibility(View.GONE);
        }
    }

    private void removeDupItem(List<ContactBean> list) {
        HashSet<ContactBean> hashSet = new HashSet<ContactBean>(list);
        list.clear();
        list.addAll(hashSet);
    }
}
