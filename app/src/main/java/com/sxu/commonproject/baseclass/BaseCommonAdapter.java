package com.sxu.commonproject.baseclass;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sxu.commonproject.bean.BaseBean;

import java.util.List;

/**
 * Created by juhg on 16/2/18.
 */
public abstract class BaseCommonAdapter<T extends BaseBean> extends BaseAdapter {

    private Context context;
    private List<T> data;
    private int layoutId;

    public BaseCommonAdapter(Context context, List<T> data, int layoutId) {
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder viewHolder = getViewHolder(position, convertView, parent);
        convert(viewHolder, getItem(position));
        return viewHolder.getConvertView();
    }

    public abstract void convert(BaseViewHolder viewHolder, T data);

    private BaseViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        return BaseViewHolder.get(context,layoutId, position, convertView, parent);
    }
}
