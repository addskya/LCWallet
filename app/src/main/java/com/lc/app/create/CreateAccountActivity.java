package com.lc.app.create;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.lc.app.BaseActivity;
import com.lc.app.BaseDialog;
import com.lc.app.JsBaseActivity;
import com.lc.app.R;
import com.lc.app.databinding.ActivityCreateAccountBinding;
import com.lc.app.model.Account;
import com.lc.app.ui.LoadingDialog;


/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public class CreateAccountActivity extends JsBaseActivity implements CreateContract.View {

    private static final String TAG = "CreateAccountActivity";

    /**
     * Create a Account
     *
     * @param activity    the host activity
     * @param requestCode the requestCode
     */
    public static void intentTo(@NonNull Activity activity,
                                int requestCode) {
        Intent intent = new Intent(activity, CreateAccountActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    private ActivityCreateAccountBinding mBinding;
    private CreateContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_create_account);
        new CreatePresenter(this);
        mBinding.setView(this);
        mBinding.executePendingBindings();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mPresenter = null;
        mLoadDialog = null;
    }

    @Override
    public void setPresenter(CreateContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private BaseDialog mLoadDialog;

    @Override
    public void onCreateWalletStart() {
        mLoadDialog = new LoadingDialog(this);
        mLoadDialog.show();
    }

    @Override
    public void onCreateWalletSuccess(@NonNull Account account) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onCreateWalletFailure() {

    }

    @Override
    public void onCreateWalletError(Throwable error) {
        if (mLoadDialog != null) {
            mLoadDialog.dismiss();
        }
    }

    @Override
    public void onCreateWalletCompleted() {
        if (mLoadDialog != null) {
            mLoadDialog.dismiss();
        }
    }

    @Override
    public void back() {
        onBackPressed();
    }

    @Override
    public void createWallet() {
        //
        CharSequence password1 = mBinding.password1.getText();
        CharSequence password2 = mBinding.password2.getText();

        if (!TextUtils.equals(password1, password2)) {
            Toast.makeText(this, R.string.error_two_password_invalid,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.getTrimmedLength(password1) < 8) {
            // 密码长度不够8位
            Toast.makeText(this, R.string.error_password_short,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String walletName = "Android20180324";

        initWallet();
        createWallet(walletName, password1);

        // Create Wallet
        // mPresenter.createWallet(password1);
    }
}
