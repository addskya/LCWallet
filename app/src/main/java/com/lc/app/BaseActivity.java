package com.lc.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import com.lc.app.ui.LoadingDialog;
import com.lc.app.ui.StatusDialog;


/**
 * Created by Orange on 18-2-26.
 * Email:chenghe.zhang@ck-telecom.com
 * 基Activity
 */

public abstract class BaseActivity extends PermissionsActivity {
    private static final String TAG = "BaseActivity";
    private BaseDialog mDialog;

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

    protected void showProgressDialog() {
        if (mDialog == null) {
            mDialog = new LoadingDialog(this);
        }
        mDialog.show();
    }

    protected void dismissProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }


    protected void showProgressDialog(@StringRes int status) {
        dismissProgressDialog();
        if (mDialog == null) {
            mDialog = new StatusDialog(this, status);
        }
        mDialog.show();
    }

    protected void showError(Throwable error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }
}
