package com.sxu.commonproject.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by juhg on 16/2/22.
 */
public abstract class BaseFragment extends Fragment {

    public View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getLayoutId() != 0) {
            rootView = inflater.inflate(getLayoutId(), null);
            getViews();
            initFragment();
        }

        return rootView;
    }

    public abstract int getLayoutId();

    public abstract void getViews();

    public abstract void initFragment();
}
