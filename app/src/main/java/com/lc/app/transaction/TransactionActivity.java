package com.lc.app.transaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lc.app.JsBaseActivity;
import com.lc.app.R;
import com.lc.app.javascript.JsCallback;
import com.lc.app.model.Account;
import com.lc.app.ui.PasswordView;

/**
 * Created by Orange on 18-3-27.
 * Email:addskya@163.com
 */

public class TransactionActivity extends JsBaseActivity {

    private static final String TAG = "TransactionActivity";
    private static final String EXTRA_ACCOUNT =
            "com.lc.app.EXTRA_ACCOUNT";
    private Account mAccount;
    private float mCurrentRate = -100;
    private JsCallback mCallBack = new JsCallback() {
        @Override
        public void onCallback(int message, String error, Object result) {
            Log.i(TAG, "onCallBack:" + message);
            Log.i(TAG, "onCallBack:" + error);
            Log.i(TAG, "onCallBack:" + result);
            switch (message) {
                // 查询费率
                case MESSAGE_RATE: {
                    // result 表示费率
                    try {
                        mCurrentRate = Float.valueOf(String.valueOf(result));
                        updateCurrentRate(mCurrentRate);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        updateCurrentRateError();
                    }
                    break;
                }

                // 查询余额
                case MESSAGE_BALANCE: {
                    try {
                        float remain = Float.parseFloat(String.valueOf(result));
                        mAccount.setRemain(remain);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                // 转账结果
                case MESSAGE_TRANSFER: {

                    break;
                }
            }
        }
    };

    public static void intentTo(@NonNull Activity activity,
                                @NonNull Account account,
                                int requestCode) {
        Intent intent = new Intent(activity, TransactionActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, (Parcelable) account);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_transaction);
        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_ACCOUNT)) {
            finish();
            return;
        }

        mAccount = intent.getParcelableExtra(EXTRA_ACCOUNT);
    }

    @Override
    protected void onWalletInitCompleted() {
        if (mCurrentRate < 0) {
            getRate(null);

            balanceOf(mAccount.getAddress());
        }
    }

    @Override
    protected JsCallback getJsCallback() {
        return mCallBack;
    }

    private void updateCurrentRate(float rate) {
        TextView rateView = findViewById(R.id.rate);
        String rateText = getString(R.string.text_fee, rate);
        rateView.setText(rateText);
    }

    private void updateCurrentRateError() {
        TextView rateView = findViewById(R.id.rate);
        String rateText = getString(R.string.text_fee_error);
        rateView.setText(rateText);
    }

    public void onBack(View back) {
        onBackPressed();
    }

    public void onTransaction(View back) {
        PasswordView passwordView = findViewById(R.id.password_1);
        if (!TextUtils.equals(mAccount.getPassword(), passwordView.getText())) {
            toastMessage(R.string.error_password_invalid);
            return;
        }

        EditText toAccountView = findViewById(R.id.toAddress);
        if (TextUtils.isEmpty(toAccountView.getText())) {
            return;
        }

        EditText amountView = findViewById(R.id.amount);
        if (TextUtils.isEmpty(amountView.getText())) {
            return;
        }

        float amount = -1;
        try {
            amount = Float.valueOf(String.valueOf(amountView.getText()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            toastMessage(R.string.error_transfer_amount_invalid);
            return;
        }

        CharSequence walletName = mAccount.getWalletName();
        CharSequence password = mAccount.getPassword();
        CharSequence executeAccount = mAccount.getAddress();
        CharSequence toAccount = toAccountView.getText();
        if (amount <= 0) {
            return;
        }

        transferByFee(walletName, password, executeAccount, toAccount, amount);
    }
}
