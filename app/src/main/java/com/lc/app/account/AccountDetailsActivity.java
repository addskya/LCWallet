package com.lc.app.account;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.widget.PopupMenu;

import com.lc.app.DefaultObserver;
import com.lc.app.JsBaseActivity;
import com.lc.app.R;
import com.lc.app.code.QrCodeDialog;
import com.lc.app.databinding.ActivityAccountDetailsBinding;
import com.lc.app.model.Account;
import com.lc.app.transaction.TransactionActivity;
import com.lc.app.transaction.TransactionHistoryActivity;
import com.lc.app.utils.WalletUtil;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 */
public class AccountDetailsActivity extends JsBaseActivity implements
        AccountDetailsContract.View {
    private static final String TAG = "";
    private static final String EXTRA_ACCOUNT =
            "com.lc.app.EXTRA_ACCOUNT";

    private static final int REQUEST_CODE_TRANSACTION = 0x20;

    private static final int REQUEST_CODE_TRANSACTION_HISTORY = 0x21;
    private ActivityAccountDetailsBinding mBinding;
    private AccountDetailsContract.Presenter mPresenter;
    private PopupMenu mPopupMenu;
    private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener =
            new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_transaction: {
                            TransactionActivity.intentTo(AccountDetailsActivity.this,
                                    mBinding.getAccount(),
                                    REQUEST_CODE_TRANSACTION);
                            return true;
                        }

                        case R.id.menu_transaction_history: {
                            TransactionHistoryActivity.intentTo(AccountDetailsActivity.this,
                                    mBinding.getAccount());
                            return true;
                        }
                        default: {
                            return false;
                        }
                    }
                }
            };

    /**
     * 显示账户详情界面
     *
     * @param context the Activity context
     * @param account the account
     */
    public static void intentTo(@NonNull Context context,
                                @NonNull Account account) {
        Intent intent = new Intent(context, AccountDetailsActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, (Parcelable) account);
        context.startActivity(intent);
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
        mBinding.setAccount(account);
        mBinding.executePendingBindings();

        final CharSequence walletName = account.getWalletName();
        final CharSequence password = account.getPassword();
        final CharSequence address = account.getRealAddress();
        final CharSequence keystore = account.getKeystore();

        if (TextUtils.isEmpty(keystore) && false) {
            showProgressDialog();
            loadWallet(walletName, password, address, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    dismissProgressDialog();
                    if (TextUtils.isEmpty(value)
                            || "null".equalsIgnoreCase(value)) {
                        return;
                    }
                    account.setKeystore(value);
                    WalletUtil.updateWallet(getWalletFolder(), account)
                            .subscribeOn(Schedulers.io())
                            .unsubscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DefaultObserver<Boolean>() {
                                @Override
                                public void onStart() {
                                    showProgressDialog();
                                }

                                @Override
                                public void onNext(Boolean response) {
                                    super.onNext(response);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    dismissProgressDialog();
                                    e.printStackTrace();
                                }

                                @Override
                                public void onCompleted() {
                                    dismissProgressDialog();
                                }
                            });
                }
            });
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
    public void onAccountVisibleChanged(boolean isChecked) {
        Account account = mBinding.getAccount();
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

        final CharSequence walletName = account.getWalletName();
        final CharSequence password = account.getPassword();
        showProgressDialog();
        exportWallet(walletName, password, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.i(TAG, "export value:" + value);
                dismissProgressDialog();
                if (TextUtils.isEmpty(value) ||
                        "null".equalsIgnoreCase(value)) {
                    // Export failed.
                    toastMessage(R.string.text_export_failed);
                    return;
                }

                account.setKeystore(value);
                WalletUtil.updateWallet(getWalletFolder(), account)
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DefaultObserver<Boolean>() {

                        });
                // Copy the keystore to ClipBoradManager

                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (manager != null) {
                    ClipData data = ClipData.newPlainText("keystore", value);
                    manager.setPrimaryClip(data);
                    toastMessage(R.string.text_copyed);
                }
            }
        });
    }
}
