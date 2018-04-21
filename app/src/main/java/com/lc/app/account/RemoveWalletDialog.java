package com.lc.app.account;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.lc.app.BaseDialog;
import com.lc.app.R;
import com.lc.app.ui.PasswordView;

/**
 * Created by Orange on 18-4-21.
 * Email:addskya@163.com
 */
public class RemoveWalletDialog extends BaseDialog {

    public static void intentTo(@NonNull Context context,
                                @Nullable CallBack callBack) {
        RemoveWalletDialog dialog = new RemoveWalletDialog(context);
        dialog.setCallBack(callBack);
        dialog.show();
    }

    private RemoveWalletDialog(@NonNull Context context) {
        super(context);
    }

    private CallBack mCallback;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_remove_wallet, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        final PasswordView passwordView = view.findViewById(R.id.password_view);
        View cancel = view.findViewById(R.id.cancel);
        View confirm = view.findViewById(R.id.confirm);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onCallback(BUTTON_POSITIVE, null);
                }
                dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence password = passwordView.getText();
                if (TextUtils.isEmpty(password)) {
                    return;
                }

                if (mCallback != null) {
                    mCallback.onCallback(BUTTON_NEGATIVE, password);
                }
                dismiss();
            }
        });
    }

    private void setCallBack(@Nullable CallBack callBack) {
        mCallback = callBack;
    }

    public interface CallBack {
        void onCallback(int which, @Nullable CharSequence input);
    }
}
