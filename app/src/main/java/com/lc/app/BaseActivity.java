package com.lc.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by Orange on 18-2-26.
 * Email:chenghe.zhang@ck-telecom.com
 * 基Activity
 */

public abstract class BaseActivity extends PermissionsActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }


    /**
     * Whether or NOT the Activity has finished.
     *
     * @return true if the Activity is NOT finish
     */
    public boolean isAdded() {
        return !isFinishing();
    }

    protected void toastMessage(@StringRes int resId) {
        toastMessage(getString(resId));
    }

    protected void toastMessage(@NonNull CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
