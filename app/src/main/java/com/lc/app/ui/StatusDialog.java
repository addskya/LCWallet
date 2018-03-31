package com.lc.app.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lc.app.BaseDialog;
import com.lc.app.R;

/**
 * Created by Orange on 18-3-31.
 * Email:addskya@163.com
 */

public class StatusDialog  extends BaseDialog {

    public StatusDialog(@NonNull Context context ,
                        @StringRes int resId) {
        super(context);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        mStatusMessageId = resId;
    }

    private int mStatusMessageId;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_status,
                null, false);
        TextView statusView = view.findViewById(R.id.status);
        if (mStatusMessageId != 0) {
            statusView.setText(mStatusMessageId);
            statusView.setVisibility(View.VISIBLE);
        }
        return view;
    }

}
