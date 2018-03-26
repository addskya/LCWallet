package com.lc.app.portim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.Toast;

import com.lc.app.JsBaseActivity;
import com.lc.app.R;
import com.lc.app.ui.PasswordView;

/**
 * Created by Orange on 18-3-26.
 * Email:addskya@163.com
 */

public class ImportActivity extends JsBaseActivity {

    public static void intentTo(@NonNull Activity activity,
                                int requestCode) {
        Intent intent = new Intent(activity, ImportActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_import);
    }

    public void onBack(View view) {
        finish();
    }


    public void importWallet(View view) {
        EditText walletNameView = findViewById(R.id.wallet_name);

        PasswordView password1View = findViewById(R.id.password_1);
        PasswordView password2View = findViewById(R.id.password_2);

        EditText keystoreView = findViewById(R.id.wallet_keystore);

        final CharSequence walletName = walletNameView.getText();
        if (TextUtils.isEmpty(walletName)) {
            return;
        }

        final CharSequence password1 = password1View.getText();
        final CharSequence password2 = password2View.getText();

        if (!TextUtils.equals(password1, password2)) {
            Toast.makeText(this, R.string.error_two_password_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.getTrimmedLength(password1) < 8) {
            // 密码长度不够8位
            Toast.makeText(this, R.string.error_password_short,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        final CharSequence keystore = keystoreView.getText();
        if (TextUtils.isEmpty(keystore)) {
            return;
        }

        showProgressDialog();
        importWallet(walletName, keystore, password1, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                dismissProgressDialog();
                if (TextUtils.isEmpty(value)
                        || "null".equalsIgnoreCase(value)) {
                    // fail
                    return;
                }

                Toast.makeText(ImportActivity.this,
                        R.string.text_wallet_import_success,
                        Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        });
    }
}
