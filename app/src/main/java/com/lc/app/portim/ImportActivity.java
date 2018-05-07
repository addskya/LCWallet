package com.lc.app.portim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.EditText;

import com.lc.app.JsBaseActivity;
import com.lc.app.R;
import com.lc.app.model.Account;
import com.lc.app.ui.PasswordView;

/**
 * Created by Orange on 18-3-26.
 * Email:addskya@163.com
 * 导入钱包UI
 */

public class ImportActivity extends JsBaseActivity implements ImportContract.View {

    public static void intentTo(@NonNull Activity activity,
                                int requestCode) {
        Intent intent = new Intent(activity, ImportActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    private static final String TAG = "ImportActivity";

    private static final String KEY_ACCOUNT =
            "com.lc.app.EXTRA_ACCOUNT";

    private static Intent packResult(@NonNull Account account) {
        Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT, (Parcelable) account);
        return intent;
    }

    private ImportContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_import);
        new ImportPresenter(this);
    }

    public void onBack(View view) {
        finish();
    }

    // OnClickListener
    public void importWallet(View view) {
        EditText walletNameView = findViewById(R.id.wallet_name);

        PasswordView password1View = findViewById(R.id.password_1);
        // PasswordView password2View = findViewById(R.id.password_2);

        EditText keystoreView = findViewById(R.id.wallet_keystore);

        final CharSequence walletName = walletNameView.getText();
        if (TextUtils.isEmpty(walletName)
                || TextUtils.getTrimmedLength(walletName) < getResources().getInteger(R.integer.min_wallet_name_length)) {
            toastMessage(R.string.error_wallet_name_short);
            return;
        }

        final CharSequence password1 = password1View.getText();
        // final CharSequence password2 = password2View.getText();

        /*if (!TextUtils.equals(password1, password2)) {
            toastMessage(R.string.error_two_password_invalid);
            return;
        }*/

        if (TextUtils.getTrimmedLength(password1) <
                getResources().getInteger(R.integer.min_password_length)) {
            // 密码长度不够8位
            toastMessage(R.string.error_password_short);
            return;
        }

        final CharSequence keystore = keystoreView.getText();
        if (TextUtils.isEmpty(keystore)) {
            toastMessage(R.string.error_keystore_invalid);
            return;
        }

        // 先判断钱包名是否已经存在了
        existsWallet(walletName, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Boolean exists = Boolean.parseBoolean(value);
                if (exists) {
                    toastMessage(R.string.error_wallet_name_exists);
                } else {
                    showProgressDialog(R.string.text_import_wallet_ing);
                    importWallet(walletName, "'[" + keystore + "]'", password1, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            dismissProgressDialog();
                            if (TextUtils.isEmpty(value)
                                    || "null".equalsIgnoreCase(value)) {
                                toastMessage(R.string.error_import_wallet_fail);
                                return;
                            }

                            toastMessage(R.string.text_wallet_import_success);
                            dismissProgressDialog();
                            Account account = new Account();
                            account.setWalletName(String.valueOf(walletName));
                            account.setKeystore(String.valueOf(keystore));
                            account.setPassword(String.valueOf(password1));

                            StringBuilder sb = new StringBuilder(value);
                            if (value.startsWith("\"")) {
                                sb.deleteCharAt(0);
                            }
                            if (value.endsWith("\"")) {
                                sb.deleteCharAt(sb.length() - 1);
                            }
                            account.setAddress(sb.toString());
                            // 导入钱包,无余额,无交易流水
                            account.setRemain(0);
                            account.setTransactionHistoryJson(null);

                            mPresenter.saveWallet(getWalletFolder(), account);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void setPresenter(ImportContract.Presenter presenter) {
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
}
