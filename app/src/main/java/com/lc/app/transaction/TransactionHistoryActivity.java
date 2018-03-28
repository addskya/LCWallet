package com.lc.app.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lc.app.JsBaseActivity;
import com.lc.app.R;
import com.lc.app.javascript.JsCallback;
import com.lc.app.model.Account;

/**
 * Created by Orange on 18-3-28.
 * Email:addskya@163.com
 */

public class TransactionHistoryActivity extends JsBaseActivity {

    private static final String TAG = "TransactionHistory";

    private static final String EXTRA_ACCOUNT =
            "com.lc.app.EXTRA_ACCOUNT";
    private boolean mInitCalled;
    private JsCallback mCallBack = new JsCallback() {
        @Override
        public void onCallback(int message, String error, Object result) {
            switch (message) {
                case MESSAGE_TRANSFER_HISTORY: {
                    Log.d(TAG, "error:" + error);
                    Log.d(TAG, "result:" + result);
                    break;
                }
            }
        }
    };

    /**
     * 启动转账历史查询UI
     *
     * @param context the activity context
     * @param account the account
     */
    public static void intentTo(@NonNull Context context,
                                @NonNull Account account) {
        Intent intent = new Intent(context, TransactionHistoryActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, (Parcelable) account);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
    }

    @Override
    protected void onWalletInitCompleted() {
        super.onWalletInitCompleted();
        if (mInitCalled) {
            return;
        }
        mInitCalled = true;

        sendCommand(new Runnable() {
            @Override
            public void run() {
                loadTransactionHistory();
            }
        });
    }

    private void loadTransactionHistory() {
        Intent intent = getIntent();
        Account account = intent.getParcelableExtra(EXTRA_ACCOUNT);
        showHistoryTransaction(account.getRealAddress());
    }

    @Override
    protected JsCallback getJsCallback() {
        return mCallBack;
    }
}
