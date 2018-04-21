package com.lc.app.account;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.lc.app.BaseDialog;
import com.lc.app.R;
import com.lc.app.ui.PasswordView;

/**
 * Created by Orange on 18-4-21.
 * Email:addskya@163.com
 */
public class RenameWalletDialog extends BaseDialog {

    public static void intentTo(@NonNull Context context,
                                @Nullable CharSequence accountName,
                                @Nullable CallBack callBack) {
        RenameWalletDialog dialog = new RenameWalletDialog(context);
        dialog.setAccountName(accountName);
        dialog.setCallBack(callBack);
        dialog.show();
    }

    private RenameWalletDialog(@NonNull Context context) {
        super(context);
    }

    private CharSequence mAccountName;
    private CallBack mCallback;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_rename_wallet, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        final EditText nameView = view.findViewById(R.id.name_view);
        View cancel = view.findViewById(R.id.cancel);
        View confirm = view.findViewById(R.id.confirm);
        nameView.setText(mAccountName);
        Selection.setSelection(nameView.getText(), mAccountName.length(), mAccountName.length());

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
                CharSequence newName = nameView.getText();
                if (TextUtils.isEmpty(newName)) {
                    return;
                }

                if (mCallback != null) {
                    mCallback.onCallback(BUTTON_NEGATIVE, newName);
                }
                dismiss();
            }
        });
    }

    private void setAccountName(@Nullable CharSequence accountName) {
        mAccountName = accountName;
    }

    private void setCallBack(@Nullable CallBack callBack) {
        mCallback = callBack;
    }

    public interface CallBack {
        void onCallback(int which, @Nullable CharSequence input);
    }
}
