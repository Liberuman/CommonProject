package com.sxu.commonproject.util;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by juhg on 16/3/2.
 */
public class ToastUtil {

    public static void show(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
