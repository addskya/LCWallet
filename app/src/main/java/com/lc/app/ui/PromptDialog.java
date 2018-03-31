package com.lc.app.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.lc.app.BaseDialog;
import com.lc.app.R;

/**
 * Created by Orange on 18-3-31.
 * Email:addskya@163.com
 */

public class PromptDialog extends BaseDialog {

    /**
     * launch the PromptDialog
     *
     * @param context  the activity context
     * @param callBack the CallBack when Input
     * @return the dialog instance
     */
    public static BaseDialog intentTo(@NonNull Context context,
                                      @Nullable CallBack callBack) {
        PromptDialog dialog = new PromptDialog(context);
        dialog.setCallBack(callBack);
        return dialog;
    }


    private PromptDialog(@NonNull Context context) {
        super(context);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    private PasswordView mPasswordView;
    private CallBack mCallBack;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_prompt,
                null, false);
        mPasswordView = view.findViewById(R.id.wallet_password);
        view.findViewById(R.id.cancel).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.ok).setOnClickListener(mOnClickListener);
        return view;
    }

    private View.OnClickListener mOnClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.cancel: {
                            if (mCallBack != null) {
                                mCallBack.onCallback(null);
                            }
                            dismiss();
                            break;
                        }

                        case R.id.ok: {
                            if (mCallBack != null) {
                                mCallBack.onCallback(String.valueOf(mPasswordView.getText()));
                            }
                            dismiss();
                            break;
                        }
                    }
                }
            };

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCallBack = null;
    }

    private void setCallBack(@Nullable CallBack callBack) {
        mCallBack = callBack;
    }

    public interface CallBack {

        void onCallback(@Nullable String input);
    }
}
