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
 * Created by Orange on 18-3-13.
 * Email:addskya@163.com
 */

public class LoadingDialog extends BaseDialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_loading,
                null, false);
        return view;
    }

}
