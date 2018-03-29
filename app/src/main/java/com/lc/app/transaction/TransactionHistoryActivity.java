package com.lc.app.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lc.app.JsBaseActivity;
import com.lc.app.R;
import com.lc.app.javascript.JsCallback;
import com.lc.app.model.Account;

import java.util.List;

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
        public void onCallback(final int message, final String error, final Object result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (message) {
                        case MESSAGE_TRANSFER_HISTORY: {
                            Log.d(TAG, "error:" + error);
                            Log.d(TAG, "result:" + result);
                            showHistoryResult(error, result);
                            break;
                        }
                    }
                }
            });
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

    private TextView mStatusView;

    private View mSwipeView;

    private RecyclerView mListView;
    private HistoryAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        mStatusView = findViewById(R.id.statusView);
        mSwipeView = findViewById(R.id.swipe);
        mListView = findViewById(R.id.list);
        mAdapter = new HistoryAdapter(getLayoutInflater(), this);
        mListView.setAdapter(mAdapter);
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
        mStatusView.setText(R.string.text_loading);
        showHistoryTransaction(account.getRealAddress());
    }

    @Override
    protected JsCallback getJsCallback() {
        return mCallBack;
    }

    // OnClickListener
    public void onBack(View view) {
        onBackPressed();
    }

    // OnClickListener
    public void onRetry(View view) {
        sendCommand(new Runnable() {
            @Override
            public void run() {
                loadTransactionHistory();
            }
        });
    }

    private void showHistoryResult(String error, Object result) {
        if (!TextUtils.isEmpty(error)) {
            mStatusView.setText(error);
            mStatusView.setVisibility(View.VISIBLE);
            mSwipeView.setVisibility(View.GONE);
            return;
        }

        if (result == null) {
            //No result
            mStatusView.setText(R.string.text_no_transaction_history);
            mStatusView.setVisibility(View.VISIBLE);
            mSwipeView.setVisibility(View.GONE);
        } else {
            mStatusView.setVisibility(View.GONE);
            mSwipeView.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            List<History> list = gson.fromJson(String.valueOf(result),
                    new TypeToken<List<History>>() {
                    }.getType());
            mAdapter.addOrSetData(list, true);
        }
    }
}
