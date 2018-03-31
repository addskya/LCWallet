package com.lc.app.export;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lc.app.BaseActivity;
import com.lc.app.R;

/**
 * Created by Orange on 18-3-31.
 * Email:addskya@163.com
 */

public class ExportActivity extends BaseActivity {

    private static final String TAG = "";
    private static final String EXTRA_KEYSTORE =
            "com.lc.app.EXTRA_KEYSTORE";

    /**
     * launch the ExportActivity
     *
     * @param context  the Activity context
     * @param keystore the keystore
     */
    public static void intentTo(@NonNull Context context,
                                @NonNull CharSequence keystore) {
        Intent intent = new Intent(context, ExportActivity.class);
        intent.putExtra(EXTRA_KEYSTORE, keystore);
        context.startActivity(intent);
    }

    private TextView mKeystoreView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        mKeystoreView = findViewById(R.id.keystore);
        parseIntent(getIntent());
    }

    private void parseIntent(Intent intent) {
        if (!intent.hasExtra(EXTRA_KEYSTORE)) {
            finish();
            return;
        }
        String keystore = intent.getStringExtra(EXTRA_KEYSTORE);

        String keystoreWithoutSpit = keystore.replace("\\","");
        CharSequence resultKeystore = keystoreWithoutSpit.subSequence(2,keystoreWithoutSpit.length() - 2);
        mKeystoreView.setText(String.valueOf(resultKeystore));
    }

    // OnClickListener
    public void onBack(View view) {
        finish();
    }

    // OnClickListener
    public void copyKeystore(View view) {
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (manager != null) {
            String keystore = String.valueOf(mKeystoreView.getText());
            ClipData data = ClipData.newPlainText("keystore", keystore);
            manager.setPrimaryClip(data);
            toastMessage(R.string.text_copyed);
        }
    }
}
