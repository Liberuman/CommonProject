package com.sxu.commonproject.baseclass;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxu.commonproject.app.CommonApplication;

/**
 * Created by juhg on 16/2/18.
 */
public class BaseViewHolder {

    private Context context;
    private int position;
    private View convertView;
    private SparseArray<View> views = new SparseArray<View>();

    public BaseViewHolder(Context context, int layoutId, int position, ViewGroup parent) {
        this.context = context;
        this.position = position;
        this.convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.convertView.setTag(this);
    }

    public static BaseViewHolder get(Context context, int layoutId, int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            return new BaseViewHolder(context, layoutId, position, parent);
        } else {
            BaseViewHolder viewHolder = (BaseViewHolder)convertView.getTag();
            viewHolder.position = position;
            return viewHolder;
        }
    }

    public View getConvertView() {
        return convertView;
    }

    public int getPosition() {
        return position;
    }

    public <T extends View> T getView(int resId) {
        View itemView = views.get(resId);
        if (itemView == null) {
            itemView = convertView.findViewById(resId);
            views.put(resId, itemView);
        }

        return (T)itemView;
    }

    public void setText(int resId, String content) {
        TextView textView = getView(resId);
        textView.setText(content);
        CommonApplication.setTypeface(textView);
    }

    public void setImageResource(int resId, int defaultImageId, ViewGroup.LayoutParams params, String url) {
        ImageView imageView = getView(resId);
        if (imageView != null && params != null) {
            imageView.setLayoutParams(params);
        }
        Glide.with(context)
                .load(url)
                .placeholder(defaultImageId)
                .error(defaultImageId)
                .into(imageView);
    }
}
