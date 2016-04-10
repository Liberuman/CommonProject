package com.sxu.commonproject.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sxu.commonproject.util.LogUtil;

/**
 * Created by juhg on 16/3/8.
 */
public class FitSystemWindowLayout extends LinearLayout {

    public FitSystemWindowLayout(Context context) {
        super(context);
    }

    public FitSystemWindowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitSystemWindowLayout(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        LogUtil.i("top====" + insets.top);
        insets.top = 0;
        return super.fitSystemWindows(insets);
    }
}
