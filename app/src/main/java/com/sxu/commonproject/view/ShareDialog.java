package com.sxu.commonproject.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;
import com.sxu.commonproject.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juhg on 16/2/29.
 */
public class ShareDialog extends AlertDialog {

    private Context context;
    private EditText shareContentEdit;
    private TextView cancelText;
    private GridView shareGrid;
    private List<ShareBean> shareTypeList = new ArrayList<ShareBean>();

    public ShareDialog(Context context) {
        super(context);
        this.context = context;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_share);
        shareContentEdit = (EditText)findViewById(R.id.share_content_edit);
        cancelText = (TextView)findViewById(R.id.cancel_text);
        shareGrid = (GridView)findViewById(R.id.share_grid);

        CommonApplication.setTypeface(shareContentEdit);
        CommonApplication.setTypeface(cancelText);
        CommonApplication.setTypeface((TextView)findViewById(R.id.share_text));

        initData();
        shareGrid.setAdapter(new ShareGridAdapter());
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        shareGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UMImage image = new UMImage(context, "http://7xrqzm.com1.z0.glb.clouddn.com/icon_20160315173836");
                ShareAction shareAction = new ShareAction((Activity)context);
                shareAction.withText(shareContentEdit.getText().toString()).withMedia(image)
                        .withTargetUrl("http://dev.umeng.com/social/android/detail-share#1").setCallback(umShareListener);
                switch (position) {
                    case 0:
                        shareAction.setPlatform(SHARE_MEDIA.WEIXIN);
                        break;
                    case 1:
                        shareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
                        break;
                    case 2:
                        shareAction.setPlatform(SHARE_MEDIA.QQ);
                        break;
                    case 3:
                        shareAction.setPlatform(SHARE_MEDIA.SINA);
                        break;
                    default:
                        break;
                }

                shareAction.share();
                dismiss();
            }
        });
    }

    private void initData() {
        shareTypeList.add(new ShareBean(R.drawable.weixin_icon, "微信"));
        shareTypeList.add(new ShareBean(R.drawable.wechat_moment_icon, "朋友圈"));
        shareTypeList.add(new ShareBean(R.drawable.login_qq_icon, "QQ"));
        shareTypeList.add(new ShareBean(R.drawable.sina_weibo_icon, "微博"));
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            LogUtil.i("分享成功了");
            dismiss();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            LogUtil.i("分享失败");
            dismiss();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            LogUtil.i("分享取消了");
        }
    };

    private class ShareGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return shareTypeList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return shareTypeList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_type_layout, parent, false);
            }
            TextView descText = (TextView)convertView.findViewById(R.id.type_desc_text);
            ImageView icon = (ImageView)convertView.findViewById(R.id.type_icon);
            CommonApplication.setTypeface(descText);
            descText.setText(shareTypeList.get(position).desc);
            icon.setImageResource(shareTypeList.get(position).icon);

            return convertView;
        }
    }

    private class ShareBean {
        private int icon;
        private String desc;

        public ShareBean(int icon, String desc) {
            this.icon = icon;
            this.desc = desc;
        }
    }
}
