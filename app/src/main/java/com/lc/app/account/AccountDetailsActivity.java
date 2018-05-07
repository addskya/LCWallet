package com.lc.app.account;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.lc.app.App;
import com.lc.app.DefaultObserver;
import com.lc.app.JsBaseActivity;
import com.lc.app.R;
import com.lc.app.code.QrCodeDialog;
import com.lc.app.databinding.ActivityAccountDetailsBinding;
import com.lc.app.export.ExportActivity;
import com.lc.app.javascript.JsCallback;
import com.lc.app.model.Account;
import com.lc.app.transaction.TransactionActivity;
import com.lc.app.transaction.TransactionHistoryFragment;
import com.lc.app.ui.PromptDialog;
import com.lc.app.utils.WalletUtil;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 */
public class AccountDetailsActivity extends JsBaseActivity implements
        AccountDetailsContract.View {
    private static final String TAG = "AccountDetails";
    private static final String EXTRA_ACCOUNT =
            "com.lc.app.EXTRA_ACCOUNT";

    private static final int REQUEST_CODE_TRANSACTION = 0x20;

    private static final int REQUEST_CODE_TRANSACTION_HISTORY = 0x21;
    private Account mAccount;
    private ActivityAccountDetailsBinding mBinding;
    private AccountDetailsContract.Presenter mPresenter;

    private TransactionHistoryFragment mTransactionFragment;
    private PopupMenu mPopupMenu;
    private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener =
            new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_export: {
                            export(mAccount);
                            break;
                        }
                        case R.id.menu_rename: {
                            rename(mAccount);
                            break;
                        }
                        case R.id.menu_delete: {
                            delete(mAccount);
                            break;
                        }
                        default: {
                            return false;
                        }
                    }
                    return true;
                }
            };

    /**
     * 显示账户详情界面
     *
     * @param context the Activity context
     * @param account the account
     */
    public static void intentTo(@NonNull Activity context,
                                @NonNull Account account,
                                int requestCode) {
        Intent intent = new Intent(context, AccountDetailsActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, (Parcelable) account);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_account_details);

        new AccountDetailsPresenter(this);
        mBinding.setView(this);
        mPopupMenu = new PopupMenu(this, mBinding.more);
        mPopupMenu.getMenuInflater().inflate(R.menu.menu_wallet,
                mPopupMenu.getMenu());
        mPopupMenu.setOnMenuItemClickListener(mOnMenuItemClickListener);
        mTransactionFragment = (TransactionHistoryFragment) getSupportFragmentManager()
                .findFragmentById(R.id.transaction);
        parseIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
    }

    private void parseIntent(@NonNull Intent intent) {
        if (!intent.hasExtra(EXTRA_ACCOUNT)) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        final Account account = intent.getParcelableExtra(EXTRA_ACCOUNT);
        mAccount = account;
        mBinding.setAccount(account);
        mBinding.executePendingBindings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_TRANSACTION: {
                queryBalance();
                break;
            }
        }
    }

    @Override
    public void setPresenter(AccountDetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showQrCode(@NonNull Account account) {
        QrCodeDialog.intentTo(this, account);
    }

    @Override
    public void back() {
        onBackPressed();
    }

    @Override
    public void showMoreOperate() {
        mPopupMenu.show();
    }

    @Override
    public void gotoTransaction() {
        // 如果当前账户的全额为零,禁止发起转账
        Account account = mAccount;
        if (account.getRemain() <= 0) {
            Toast.makeText(this, R.string.error_remain_zero, Toast.LENGTH_SHORT).show();
            return;
        }
        TransactionActivity.intentTo(AccountDetailsActivity.this,
                mAccount,
                REQUEST_CODE_TRANSACTION);
    }

    @Override
    public void onAccountVisibleChanged(boolean isChecked) {
        Account account = mAccount;
        if (account != null) {
            account.setSecret(isChecked);
        }
    }

    @Override
    public void backup(@NonNull Account account) {
        Log.i(TAG, "BackUp:" + account);
    }

    @Override
    public void export(@Nullable final Account account) {
        if (account == null) {
            return;
        }

        PromptDialog.intentTo(this, new PromptDialog.CallBack() {
            @Override
            public void onCallback(@Nullable String input) {
                if (TextUtils.isEmpty(input)) {
                    return;
                }

                final CharSequence walletName = account.getWalletName();
                final CharSequence password = input;
                showProgressDialog(R.string.text_export_wallet_ing);
                exportWallet(walletName, password, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i(TAG, "export value:" + value);
                        dismissProgressDialog();
                        if (TextUtils.isEmpty(value) ||
                                "null".equalsIgnoreCase(value)) {
                            // Export failed.
                            toastMessage(R.string.error_password_invalid);
                            return;
                        }

                        ExportActivity.intentTo(AccountDetailsActivity.this, value);
                    }
                });
            }
        }).show();
    }

    @Override
    public void copyAddress(@Nullable Account account) {
        if (account == null) {
            return;
        }

        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (manager != null) {
            String keystore = String.valueOf(account.getRealAddress());
            ClipData data = ClipData.newPlainText("address", keystore);
            manager.setPrimaryClip(data);
            toastMessage(R.string.text_address_copy);
        }
    }

    private void rename(@Nullable final Account account) {
        if (account == null) {
            return;
        }

        final CharSequence accountName = account.getWalletName();
        RenameWalletDialog.intentTo(this, accountName, new RenameWalletDialog.CallBack() {
            @Override
            public void onCallback(int which, @Nullable final CharSequence input) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                    case DialogInterface.BUTTON_NEUTRAL: {
                        Log.i(TAG, "BUTTON_NEUTRAL:BUTTON_POSITIVE");
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE: {
                        Log.i(TAG, "BUTTON_NEGATIVE:" + input);

                        // 先判断钱包名是否已经存在了
                        existsWallet(accountName, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Boolean exists = Boolean.parseBoolean(value);
                                if (exists) {
                                    toastMessage(R.string.error_wallet_name_exists);
                                } else {
                                    renameWallet(accountName, mAccount.getRealAddress(),
                                            input, new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String value) {
                                                    Boolean renamed = Boolean.valueOf(value);
                                                    if (renamed) {
                                                        toastMessage(R.string.text_rename_success);
                                                        mAccount.setWalletName(String.valueOf(input));
                                                        String accountPath = ((App) getApplication()).getWalletFolder();
                                                        WalletUtil.updateWallet(accountPath, mAccount)
                                                                .subscribe(new DefaultObserver<Boolean>() {
                                                                    @Override
                                                                    public void onNext(Boolean response) {
                                                                        setResult(RESULT_OK);
                                                                    }
                                                                });
                                                    } else {
                                                        toastMessage(R.string.error_rename_failed);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        break;
                    }
                }
            }
        });
    }


    private void delete(@Nullable final Account account) {
        if (account == null) {
            return;
        }

        RemoveWalletDialog.intentTo(this, new RemoveWalletDialog.CallBack() {
            @Override
            public void onCallback(int which, @Nullable CharSequence input) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        Log.i(TAG, "BUTTON_POSITIVE");
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE: {
                        CharSequence walletName = account.getWalletName();
                        CharSequence password = input;
                        CharSequence address = account.getRealAddress();
                        removeWallet(walletName, password, address, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i(TAG, "value:" + value);
                                Boolean deleted = Boolean.valueOf(value);
                                if (deleted) {
                                    String accountPath = ((App) getApplication()).getWalletFolder();
                                    WalletUtil.deleteWallet(accountPath, account)
                                            .subscribeOn(Schedulers.io())
                                            .unsubscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new DefaultObserver<Boolean>() {
                                                @Override
                                                public void onNext(Boolean response) {
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            });
                                } else {
                                    toastMessage(R.string.error_invalid_password);
                                }
                            }
                        });
                        break;
                    }
                    case DialogInterface.BUTTON_NEUTRAL: {
                        Log.i(TAG, "BUTTON_NEUTRAL");
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void onWalletInitCompleted() {
        super.onWalletInitCompleted();
        queryBalance();
        loadTransactionHistory();
    }

    private void queryBalance() {
        sendCommand(new Runnable() {
            @Override
            public void run() {
                String address = mAccount.getRealAddress();
                // 2018.04.21 要求查余额不转圈
                // showProgressDialog();
                balanceOf(address);
            }
        });
    }

    private void loadTransactionHistory() {
        sendCommand(new Runnable() {
            @Override
            public void run() {
                // mStatusView.setText(R.string.text_loading);
                showHistoryTransaction(mAccount.getRealAddress());
            }
        });
    }

    @Override
    protected JsCallback getJsCallback() {
        return new JsCallback() {
            @Override
            public void onCallback(final int message, final String error, final Object result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (message) {
                            case MESSAGE_BALANCE: {
                                try {
                                    float remain = Float.parseFloat(String.valueOf(result));
                                    Account account = mAccount;
                                    if (account != null) {
                                        account.setRemain(remain);
                                    }
                                    mBinding.setAccount(account);
                                    mBinding.executePendingBindings();
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                dismissProgressDialog();
                                break;
                            }
                            case MESSAGE_INIT_WALLET: {
                                onWalletInitResult(error, result);
                                break;
                            }
                            case MESSAGE_TRANSFER_HISTORY: {
                                Log.d(TAG, "error:" + error);
                                Log.d(TAG, "result:" + result);
                                mTransactionFragment.showHistoryResult(error, result);
                                break;
                            }
                        }
                    }
                });
            }
        };
    }

    @Override
    public void refresh() {
        parseIntent(getIntent());
        SwipeRefreshLayout swipeView = findViewById(R.id.swipe);
        swipeView.setRefreshing(false);
        Log.i(TAG, "refresh");
        queryBalance();
        loadTransactionHistory();
    }
}
