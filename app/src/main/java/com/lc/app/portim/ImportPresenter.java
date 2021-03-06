package com.lc.app.portim;

import android.support.annotation.NonNull;

import com.lc.app.DefaultObserver;
import com.lc.app.model.Account;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
class ImportPresenter implements ImportContract.Presenter {
    private static final String TAG = "";

    private ImportContract.View mView;
    private ImportContract.Model mData;

    ImportPresenter(@NonNull ImportContract.View view) {
        view.setPresenter(this);
        mView = view;
        mData = new ImportModel();
    }


    @Override
    public void destroy() {
        mView = null;
        mData = null;
    }

    @Override
    public void saveWallet(@NonNull String accountFilePath,
                           @NonNull final Account account) {
        mData.saveWallet(accountFilePath, account)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Boolean>() {

                    @Override
                    public void onStart() {
                        if (mView.isAdded()) {
                            mView.onSaveWalletStart();
                        }
                    }

                    @Override
                    public void onNext(Boolean success) {
                        if (mView.isAdded()) {
                            mView.onSaveWalletSuccess(success, account);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView.isAdded()) {
                            mView.onSaveWalletError(e);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (mView.isAdded()) {
                            mView.onSaveWalletCompleted();
                        }
                    }
                });
    }
}
