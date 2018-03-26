package com.lc.app.create;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ValueCallback;
import android.widget.Toast;

import com.lc.app.App;
import com.lc.app.JsBaseActivity;
import com.lc.app.R;
import com.lc.app.databinding.ActivityCreateAccountBinding;
import com.lc.app.model.Account;


/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public class CreateAccountActivity extends JsBaseActivity implements CreateContract.View {

    private static final String TAG = "CreateAccountActivity";
    private static final String KEY_ACCOUNT =
            "com.lc.app.EXTRA_ACCOUNT";

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

    private static Intent packResult(@NonNull Account account) {
        Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT, (Parcelable) account);
        return intent;
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
    }

    @Override
    public void setPresenter(CreateContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onSaveWalletStart() {
        showProgressDialog();
    }

    @Override
    public void onSaveWalletSuccess(@NonNull Boolean success,
                                    @NonNull Account account) {
        setResult(RESULT_OK, packResult(account));
    }

    @Override
    public void onSaveWalletError(Throwable error) {
        Log.e(TAG, "onSaveWalletError", error);
        dismissProgressDialog();
    }

    @Override
    public void onSaveWalletCompleted() {
        dismissProgressDialog();
        finish();
    }

    @Override
    public void back() {
        onBackPressed();
    }

    @Override
    public void createWallet() {
        final CharSequence password1 = mBinding.password1.getText();
        final CharSequence password2 = mBinding.password2.getText();

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

        final String walletName = generateWalletName();

        showProgressDialog();
        createWallet(walletName, password1, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                dismissProgressDialog();
                if (TextUtils.isEmpty(value)
                        || "null".equalsIgnoreCase(value)) {
                    Toast.makeText(CreateAccountActivity.this,
                            R.string.text_create_wallet_failed,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 成功时,返回钱包内部账户地址信息,失败时为空
                Log.i(TAG, "onReceiveValue:" + value);
                Account account = new Account();
                account.setWalletName(walletName);
                account.setPassword(String.valueOf(password1));
                account.setAddress(value);
                mPresenter.saveWallet(getWalletFolder(), account);
            }
        });
    }

    private String generateWalletName() {
        return ((App) getApplication()).generateWalletName();
    }
}
