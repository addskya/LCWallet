package com.lc.app.main;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.widget.PopupMenu;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lc.app.BaseAdapter;
import com.lc.app.JsBaseActivity;
import com.lc.app.R;
import com.lc.app.account.AccountDetailsActivity;
import com.lc.app.code.QrCodeActivity;
import com.lc.app.code.QrCodeDialog;
import com.lc.app.create.CreateAccountActivity;
import com.lc.app.databinding.ActivityHomeBinding;
import com.lc.app.javascript.JsCallback;
import com.lc.app.model.Account;
import com.lc.app.portim.ImportActivity;

import java.util.List;
import java.util.Stack;

public class HomeActivity extends JsBaseActivity implements HomeContract.View {

    private static final String TAG = "HomeActivity";
    private static final int REQUEST_CODE_CREATE_ACCOUNT = 0x10;
    private static final int REQUEST_CODE_IMPORT = 0x11;
    private static final int REQUEST_CODE_TRANSLATE = 0x12;
    private static final int REQUEST_CODE_SCAN_QR_CODE = IntentIntegrator.REQUEST_CODE;
    // private HomeContract.Presenter mPresenter;
    private BaseAdapter<Account, HomeContract.View> mAdapter;
    private ActivityHomeBinding mBinding;
    private PopupMenu mPopupMenu;
    private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener =
            new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_create_account: {
                            CreateAccountActivity.intentTo(
                                    HomeActivity.this, REQUEST_CODE_CREATE_ACCOUNT);
                            break;
                        }
                        case R.id.menu_import_account: {
                            ImportActivity.intentTo(HomeActivity.this, REQUEST_CODE_IMPORT);
                            break;
                        }
                        case R.id.menu_verify_account: {

                            break;
                        }
                        case R.id.menu_settings: {

                            break;
                        }
                    }
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_home);
        // new HomePresenter(this);
        mBinding.setView(this);
        mBinding.executePendingBindings();

        mPopupMenu = new PopupMenu(this, mBinding.operate);
        mPopupMenu.getMenuInflater().inflate(R.menu.menu_home,
                mPopupMenu.getMenu());
        mPopupMenu.setOnMenuItemClickListener(mOnMenuItemClickListener);


        mAdapter = new AccountAdapter(getLayoutInflater(), this);
        mBinding.account.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CREATE_ACCOUNT:
            case REQUEST_CODE_IMPORT: {
                refresh();
                break;
            }
            case REQUEST_CODE_SCAN_QR_CODE: {
                IntentResult result = IntentIntegrator.parseActivityResult(
                        requestCode, resultCode, data);
                String content = result.getContents();
                Log.i(TAG, "content:" + content);
                break;
            }
            case REQUEST_CODE_TRANSLATE: {
                List<Account> accounts = mAdapter.getDatas();
                loadBalanceOf(accounts);
                break;
            }
            default: {
                Log.w(TAG, "Unknown requestCode:" + requestCode);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOnMenuItemClickListener = null;
        mQueryBalance.clear();
        mQueryBalance = null;
    }

    @Override
    protected void onWalletInitCompleted() {
        super.onWalletInitCompleted();
        sendCommand(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
    }

    @Override
    public void refresh() {
        loadWallet(new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.i(TAG,"value:" + value);
                if (TextUtils.isEmpty(value)
                        || "null".equalsIgnoreCase(value)) {
                    return;
                }
                value = value.substring(1,value.length() - 1);
                List<Account> list = new Gson().fromJson(
                        value.replace("\\",""),new TypeToken<List<Account>>(){}.getType());
                dismissProgressDialog();
                mBinding.swipe.setRefreshing(false);
                onLoadAccounts(list, true);
            }
        });
    }

    @Override
    public void scanQrCode() {
        String[] permission = {
                Manifest.permission.CAMERA,
        };

        requestPermissions(permission, new Runnable() {
            @Override
            public void run() {
                QrCodeActivity.intentTo(HomeActivity.this,
                        REQUEST_CODE_SCAN_QR_CODE);
            }
        });
    }

    @Override
    public void doOperate() {
        mPopupMenu.show();
    }

    @Override
    public void showAccount(@NonNull Account account) {
        AccountDetailsActivity.intentTo(this, account, REQUEST_CODE_TRANSLATE);
    }

    @Override
    public void showQrCode(@NonNull Account account) {
        QrCodeDialog.intentTo(this, account);
    }

    /**
     * 计算所有钱包余额之和
     *
     * @return 所有钱包余额之和
     */
    @Override
    public String getAccountRemain() {
        List<Account> accounts = mAdapter.getDatas();
        float accountRemain = 0;
        for (Account a : accounts) {
            accountRemain += a.getRemain();
        }
        return String.valueOf(accountRemain);
    }

    @Override
    public void onLoadAccountStart() {
        showProgressDialog();
    }

    @Override
    public void onLoadAccounts(List<Account> accounts, boolean refresh) {
        mAdapter.addOrSetData(accounts, refresh);
        loadBalanceOf(accounts);
    }

    @Override
    public void onLoadAccountError(Throwable error) {
        toastMessage(R.string.error_load_wallet);
        mBinding.swipe.setRefreshing(false);
        dismissProgressDialog();
    }

    @Override
    public void onLoadAccountCompleted() {
        mBinding.swipe.setRefreshing(false);
        dismissProgressDialog();
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
                                    if (mQueryBalanceAccount != null) {
                                        mQueryBalanceAccount.setRemain(remain);
                                        mAdapter.notifyDataSetChanged();
                                        startQueryBalance();
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                });
            }
        };
    }

    private Account mQueryBalanceAccount;

    private Stack<Account> mQueryBalance = new Stack<>();


    private void loadBalanceOf(@Nullable List<Account> accounts) {
        if (accounts == null || accounts.size() == 0) {
            return;
        }
        for (Account account : accounts) {
            if (account == null) {
                continue;
            }

            mQueryBalance.push(account);
        }
        startQueryBalance();
    }

    private void startQueryBalance() {
        if (!mQueryBalance.empty()) {
            Account account = mQueryBalance.pop();
            String address = account.getRealAddress();
            if (!TextUtils.isEmpty(address)) {
                mQueryBalanceAccount = account;
                balanceOf(address);
            } else {
                mBinding.swipe.setRefreshing(false);
                dismissProgressDialog();
            }
        }
    }
}
