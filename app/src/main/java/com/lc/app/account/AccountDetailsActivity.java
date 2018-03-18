package com.lc.app.account;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.CheckBox;

import com.lc.app.BaseActivity;
import com.lc.app.R;
import com.lc.app.code.QrCodeDialog;
import com.lc.app.databinding.ActivityAccountDetailsBinding;
import com.lc.app.model.Account;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 */
public class AccountDetailsActivity extends BaseActivity implements
        AccountDetailsContract.View {
    private static final String TAG = "";
    private static final String EXTRA_ACCOUNT =
            "com.lc.app.EXTRA_ACCOUNT";

    /**
     * 显示账户详情界面
     *
     * @param context the Activity context
     * @param account the account
     */
    public static void intentTo(@NonNull Context context,
                                @NonNull Account account) {
        Intent intent = new Intent(context, AccountDetailsActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, account);
        context.startActivity(intent);
    }

    private ActivityAccountDetailsBinding mBinding;
    private AccountDetailsContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_account_details);

        new AccountDetailsPresenter(this);
        mBinding.setView(this);
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

        Account account = intent.getParcelableExtra(EXTRA_ACCOUNT);
        mBinding.setAccount(account);
        mBinding.executePendingBindings();
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

    }

    @Override
    public void onAccountVisibleChanged(boolean isChecked) {
        mBinding.getAccount();

    }

    @Override
    public void backup(@NonNull Account account) {
        Log.i(TAG, "BackUp:" + account);
    }
}
