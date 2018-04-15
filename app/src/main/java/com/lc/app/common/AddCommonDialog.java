package com.lc.app.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lc.app.BaseDialog;
import com.lc.app.R;
import com.lc.app.model.Account;
import com.lc.app.model.CommonEntry;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
class AddCommonDialog extends BaseDialog {

    static void intentTo(@NonNull Context context,
                         @Nullable CommonEntry entry,
                         @Nullable CallBack callBack) {
        AddCommonDialog dialog = new AddCommonDialog(context, entry, callBack);
        dialog.show();
    }

    private AddCommonDialog(@NonNull Context context,
                            @Nullable CommonEntry entry,
                            @Nullable CallBack callBack) {
        super(context);
        mCommonEntry = entry;
        mCallBack = callBack;
    }

    private EditText mName;
    private EditText mAddress;

    private CommonEntry mCommonEntry;
    private CallBack mCallBack;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_add_common, null, false);
        mName = view.findViewById(R.id.account_name);
        mAddress = view.findViewById(R.id.account_address);
        view.findViewById(R.id.commit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = String.valueOf(mName.getText());
                        String address = String.valueOf(mAddress.getText());

                        if (TextUtils.getTrimmedLength(address) != 42) {
                            Toast.makeText(getBaseContext(),
                                    R.string.error_address_invalid,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (mCommonEntry == null) {
                            mCommonEntry = new CommonEntry();
                        }
                        mCommonEntry.setName(name);
                        mCommonEntry.setAddress(address);
                        if (mCallBack != null) {
                            mCallBack.onCallback(mCommonEntry);
                        }
                        dismiss();
                    }
                });
        if (mCommonEntry != null) {
            mName.setText(mCommonEntry.getName());
            mAddress.setText(mCommonEntry.getAddress());
        }

        return view;
    }

    @Override
    protected int getWindowGravity() {
        return Gravity.CENTER;
    }


    interface CallBack {
        void onCallback(CommonEntry entry);
    }
}
