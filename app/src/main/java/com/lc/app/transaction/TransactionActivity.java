package com.lc.app.transaction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lc.app.DefaultTextWatcher;
import com.lc.app.JsBaseActivity;
import com.lc.app.R;
import com.lc.app.code.QrCodeActivity;
import com.lc.app.javascript.JsCallback;
import com.lc.app.model.Account;
import com.lc.app.ui.PromptDialog;

/**
 * Created by Orange on 18-3-27.
 * Email:addskya@163.com
 */

public class TransactionActivity extends JsBaseActivity {

    private static final String TAG = "TransactionActivity";
    private static final String EXTRA_ACCOUNT =
            "com.lc.app.EXTRA_ACCOUNT";

    private static final int REQUEST_CODE_SCAN_QR_CODE = IntentIntegrator.REQUEST_CODE;
    private Account mAccount;
    private float mCurrentRate = -100;
    private float mWalletAmount;
    private EditText mTransferAmountView;
    private JsCallback mCallBack = new JsCallback() {
        @Override
        public void onCallback(final int message, final String error, final Object result) {
            Log.i(TAG, "onCallBack:" + message);
            Log.i(TAG, "onCallBack:" + error);
            Log.i(TAG, "onCallBack:" + result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                                mWalletAmount = remain;
                                TextView walletAmount = findViewById(R.id.wallet_amount);
                                walletAmount.setText(getString(R.string.text_wallet_amount, remain));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case MESSAGE_INIT_WALLET: {
                            onWalletInitResult(error, result);
                            break;
                        }

                        // 转账结果
                        /*case MESSAGE_TRANSFER: {
                            if (TextUtils.isEmpty(error)) {
                                toastMessage(R.string.text_transaction_success);
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                toastMessage(R.string.error_password_invalid);
                            }
                            dismissProgressDialog();
                            break;
                        }*/
                    }
                }
            });
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

        mTransferAmountView = findViewById(R.id.amount);
        mTransferAmountView.addTextChangedListener(new DefaultTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float transferAmount = Float.valueOf(String.valueOf(s));
                    float feeAmount = mCurrentRate * 0.01f * transferAmount;
                    float maxTransferAmount = mWalletAmount - feeAmount;
                    if (transferAmount > maxTransferAmount) {
                        mTransferAmountView.removeTextChangedListener(this);
                        mTransferAmountView.setText(
                                String.valueOf(Math.min(transferAmount, maxTransferAmount)));
                        mTransferAmountView.addTextChangedListener(this);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onPickAddress(View view) {
        String[] permission = {
                Manifest.permission.CAMERA,
        };

        requestPermissions(permission, new Runnable() {
            @Override
            public void run() {
                QrCodeActivity.intentTo(TransactionActivity.this,
                        REQUEST_CODE_SCAN_QR_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_SCAN_QR_CODE: {
                IntentResult result = IntentIntegrator.parseActivityResult(
                        requestCode, resultCode, data);
                String content = result.getContents();
                Log.i(TAG, "content:" + content);
                EditText toAddressView = findViewById(R.id.toAddress);
                toAddressView.setText(content);
                break;
            }
        }
    }

    @Override
    protected void onWalletInitCompleted() {
        super.onWalletInitCompleted();
        sendCommand(new Runnable() {
            @Override
            public void run() {
                if (mCurrentRate < 0) {
                    getRate(null);

                    balanceOf(mAccount.getRealAddress());
                }
            }
        });
    }

    @Override
    protected JsCallback getJsCallback() {
        return mCallBack;
    }

    private void updateCurrentRate(float rate) {
        TextView rateView = findViewById(R.id.rate);
        String rateText = getString(R.string.text_fee, rate);
        rateView.setText(rateText + "%");
        mTransferAmountView.setEnabled(true);
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
        /*PasswordView passwordView = findViewById(R.id.password_1);
        if (TextUtils.getTrimmedLength(passwordView.getText()) <
                getResources().getInteger(R.integer.min_password_length)) {
            toastMessage(R.string.error_password_invalid);
            return;
        }*/

        final EditText toAccountView = findViewById(R.id.toAddress);
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

        final float transferAmount = amount;

        PromptDialog.intentTo(this, new PromptDialog.CallBack() {
            @Override
            public void onCallback(@Nullable String input) {
                if (!TextUtils.isEmpty(input)) {
                    final CharSequence walletName = mAccount.getWalletName();
                    final CharSequence password = input;
                    final CharSequence executeAccount = mAccount.getRealAddress();
                    final CharSequence toAccount = toAccountView.getText();
                    String toAccountString = String.valueOf(toAccount).toLowerCase();
                    if (toAccountString.startsWith("0x")) {
                        toAccountString = toAccountString.substring(2);
                    }

                    if (transferAmount <= 0) {
                        return;
                    }
                    showProgressDialog(R.string.text_transaction_ing);
                    transferByFee(walletName,
                            password,
                            executeAccount,
                            toAccountString,
                            transferAmount,
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    Log.i(TAG,"value:" + value);
                                    Boolean success = Boolean.parseBoolean(value);
                                    if (!success) {
                                        toastMessage(R.string.error_password_invalid);
                                    } else {
                                        toastMessage(R.string.text_transaction_success);
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                    dismissProgressDialog();
                                }
                            });
                }
            }
        }).show();


    }
}
