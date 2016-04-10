package com.sxu.commonproject.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;

/**
 * Created by juhg on 16/3/8.
 */
public class PromptDialog extends AlertDialog {

    private TextView contentText;
    private TextView confirmText;
    private TextView cancelText;

    public PromptDialog(Context context) {
        super(context);
    }

    public PromptDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_layout);
        contentText = (TextView)findViewById(R.id.content_text);
        confirmText = (TextView)findViewById(R.id.confirm_text);
        cancelText = (TextView)findViewById(R.id.cancel_text);

        CommonApplication.setTypeface(contentText);
        CommonApplication.setTypeface(confirmText);
        CommonApplication.setTypeface(cancelText);

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setContentText(String content) {
        contentText.setText(content);
    }
    public void setConfirmClickListener(View.OnClickListener listener) {
        confirmText.setOnClickListener(listener);
    }
}
