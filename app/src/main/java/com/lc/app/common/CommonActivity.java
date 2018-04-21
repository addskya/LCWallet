package com.lc.app.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.lc.app.App;
import com.lc.app.BaseActivity;
import com.lc.app.R;
import com.lc.app.databinding.ActivityCommonBinding;
import com.lc.app.model.CommonEntry;
import com.lc.app.ui.ConfirmDialog;

import java.util.List;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
public class CommonActivity extends BaseActivity implements CommonContract.View {

    private static final String EXTRA_PICK_MODE
            = "com.lc.app.EXTRA_PICK_MODE";

    public static void intentTo(@NonNull Context context) {
        Intent intent = new Intent(context, CommonActivity.class);
        intent.putExtra(EXTRA_PICK_MODE, false);
        context.startActivity(intent);
    }

    public static void intentTo(@NonNull Activity activity,
                                int requestCode) {
        Intent intent = new Intent(activity, CommonActivity.class);
        intent.putExtra(EXTRA_PICK_MODE, true);
        activity.startActivityForResult(intent, requestCode);
    }

    private CommonContract.Presenter mPresenter;
    private CommonAdapter mAdapter;
    private ActivityCommonBinding mBinding;
    private boolean mPickMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_common);
        mBinding.setView(this);

        new CommonPresenter(this);
        mAdapter = new CommonAdapter(getLayoutInflater(), this);
        mBinding.list.setAdapter(mAdapter);
        mPresenter.loadAccount(getCommonPath());
        mPickMode = getIntent().getBooleanExtra(EXTRA_PICK_MODE, false);
    }


    @Override
    public void onTap(@Nullable CommonEntry entry) {
        if (mPickMode) {
            Intent data = new Intent();
            data.putExtra("data", (Parcelable) entry);
            setResult(RESULT_OK, data);
            finish();
        } else {
            AddCommonDialog.intentTo(this, entry,
                    new AddCommonDialog.CallBack() {
                        @Override
                        public void onCallback(CommonEntry entry) {
                            mPresenter.updateAccount(getCommonPath(), entry);
                        }
                    });
        }
    }

    @Override
    public boolean onLongTap(@Nullable final CommonEntry entry) {
        if (mPickMode) {
            return true;
        }

        ConfirmDialog.intentTo(this,
                null,
                getString(R.string.text_delete_address),
                getString(R.string.text_sure),
                getString(R.string.text_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: {
                                mPresenter.deleteAccount(getCommonPath(), entry);
                                break;
                            }
                            case DialogInterface.BUTTON_NEUTRAL:
                            case DialogInterface.BUTTON_NEGATIVE: {
                                break;
                            }
                        }
                    }
                });
        return true;
    }

    @Override
    public void setPresenter(CommonContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onAddAccountStart() {
        showProgressDialog();
    }

    @Override
    public void onAddAccountSuccess(@NonNull CommonEntry entry) {
        mPresenter.loadAccount(getCommonPath());
    }

    @Override
    public void onAddAccountError(Throwable error) {
        showError(error);
        dismissProgressDialog();
    }

    @Override
    public void onAddAccountCompleted() {
        //Nothing
        dismissProgressDialog();
    }

    @Override
    public void onDeleteAccountStart() {
        showProgressDialog();
    }

    @Override
    public void onDeleteAccountSuccess() {
        mPresenter.loadAccount(getCommonPath());
        dismissProgressDialog();
    }

    @Override
    public void onDeleteAccountError(Throwable error) {
        dismissProgressDialog();
        showError(error);
    }

    @Override
    public void onDeleteAccountCompleted() {
        dismissProgressDialog();
    }

    @Override
    public void onUpdateAccountStart() {
        showProgressDialog();
    }

    @Override
    public void onUpdateAccountSuccess(@NonNull CommonEntry entry) {
        mPresenter.loadAccount(getCommonPath());
    }

    @Override
    public void onUpdateAccountError(Throwable error) {
        dismissProgressDialog();
    }

    @Override
    public void onUpdateAccountCompleted() {
        dismissProgressDialog();
    }

    @Override
    public void onLoadAccountStart() {
        showProgressDialog();
    }

    @Override
    public void onLoadAccountSuccess(@Nullable List<CommonEntry> list) {
        mAdapter.clear();
        mAdapter.addData(list);
    }

    @Override
    public void onLoadAccountError(Throwable error) {
        dismissProgressDialog();
        showError(error);
    }

    @Override
    public void onLoadAccountCompleted() {
        dismissProgressDialog();
        if (mAdapter.getDataCount() > 0) {
            mBinding.list.setVisibility(View.VISIBLE);
            mBinding.emptyView.setVisibility(View.GONE);
        } else {
            mBinding.list.setVisibility(View.GONE);
            mBinding.emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void addAccount() {
        AddCommonDialog.intentTo(this, null, new AddCommonDialog.CallBack() {
            @Override
            public void onCallback(CommonEntry entry) {
                mPresenter.addAccount(getCommonPath(), entry);
            }
        });
    }

    private String getCommonPath() {
        return ((App) getApplication()).getCommonFolder();
    }
}
