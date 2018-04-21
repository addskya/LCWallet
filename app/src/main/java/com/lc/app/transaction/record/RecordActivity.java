package com.lc.app.transaction.record;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lc.app.BaseActivity;
import com.lc.app.R;
import com.lc.app.databinding.ActivityRecordBinding;
import com.lc.app.transaction.History;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
public class RecordActivity extends BaseActivity implements RecordContract.View {
    private static final String TAG = "RecordActivity";

    private static final String EXTRA_DATA
            = "com.lc.app.EXTRA_DATA";

    public static void intent(@NonNull Context context,
                              @NonNull History history) {
        Intent intent = new Intent(context, RecordActivity.class);
        intent.putExtra(EXTRA_DATA, history);
        context.startActivity(intent);
    }

    private ActivityRecordBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_record);
        mBinding.setView(this);

        parseIntent(getIntent());
    }

    private void parseIntent(@NonNull Intent intent) {
        History history = intent.getParcelableExtra(EXTRA_DATA);
        mBinding.setHistory(history);
        mBinding.executePendingBindings();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
    }

    @Override
    public void setPresenter(RecordContract.Presenter presenter) {

    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void copy(History history) {
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (manager != null) {
            String keystore = String.valueOf(history.getTo());
            ClipData data = ClipData.newPlainText("to", keystore);
            manager.setPrimaryClip(data);
            toastMessage(R.string.text_address_copy);
        }
    }

    @Override
    public String getFormat(float value) {
        return getString(R.string.format_float, value);
    }
}
