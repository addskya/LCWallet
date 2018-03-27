package com.lc.app.main;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.PopupMenu;

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
import com.lc.app.model.Account;
import com.lc.app.portim.ImportActivity;

import java.util.List;

public class HomeActivity extends JsBaseActivity implements HomeContract.View {

    private static final String TAG = "HomeActivity";
    private static final int REQUEST_CODE_CREATE_ACCOUNT = 0x10;
    private static final int REQUEST_CODE_IMPORT = 0x11;
    private static final int REQUEST_CODE_SCAN_QR_CODE = IntentIntegrator.REQUEST_CODE;
    private HomeContract.Presenter mPresenter;
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
        new HomePresenter(this);
        mBinding.setView(this);
        mBinding.executePendingBindings();

        mPopupMenu = new PopupMenu(this, mBinding.operate);
        mPopupMenu.getMenuInflater().inflate(R.menu.menu_home,
                mPopupMenu.getMenu());
        mPopupMenu.setOnMenuItemClickListener(mOnMenuItemClickListener);


        mAdapter = new AccountAdapter(getLayoutInflater(), this);
        mBinding.account.setAdapter(mAdapter);

        mPresenter.loadAccounts(getWalletFolder(), false);
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
            case REQUEST_CODE_CREATE_ACCOUNT: {
                mPresenter.loadAccounts(getWalletFolder(), true);
                break;
            }
            case REQUEST_CODE_SCAN_QR_CODE: {
                IntentResult result = IntentIntegrator.parseActivityResult(
                        requestCode, resultCode, data);
                String content = result.getContents();
                Log.i(TAG, "content:" + content);
                break;
            }
            case REQUEST_CODE_IMPORT: {
                mPresenter.loadAccounts(getWalletFolder(), true);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mOnMenuItemClickListener = null;
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refresh() {
        mPresenter.loadAccounts(getWalletFolder(), true);
    }

    @Override
    public void scanQrCode() {
        String[] permission = {
                Manifest.permission.CAMERA,
        };

        requestPermissions(permission, new Runnable() {
            @Override
            public void run() {
                QrCodeActivity.intentTo(HomeActivity.this, REQUEST_CODE_SCAN_QR_CODE);
            }
        });
    }

    @Override
    public void doOperate() {
        mPopupMenu.show();
    }

    @Override
    public void showAccount(@NonNull Account account) {
        AccountDetailsActivity.intentTo(this, account);
    }

    @Override
    public void showQrCode(@NonNull Account account) {
        QrCodeDialog.intentTo(this, account);
    }

    @Override
    public float getAccountRemain() {
        List<Account> accounts = mAdapter.getDatas();
        int accountRemain = 0;
        for (Account a : accounts) {
            accountRemain += a.getRemain();
        }
        return accountRemain;
    }

    @Override
    public void onLoadAccountStart() {
        showProgressDialog();
    }

    @Override
    public void onLoadAccounts(List<Account> accounts, boolean refresh) {
        mAdapter.addOrSetData(accounts, refresh);
    }

    @Override
    public void onLoadAccountError(Throwable error) {
        mBinding.swipe.setRefreshing(false);
        dismissProgressDialog();
    }

    @Override
    public void onLoadAccountCompleted() {
        mBinding.swipe.setRefreshing(false);
        dismissProgressDialog();
    }
}
