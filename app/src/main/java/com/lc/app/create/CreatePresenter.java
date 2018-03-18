package com.lc.app.create;

import android.support.annotation.NonNull;

import com.lc.app.DefaultObserver;
import com.lc.app.model.Account;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public class CreatePresenter implements CreateContract.Presenter {
    private static final String TAG = "";

    private CreateContract.View mView;
    private CreateContract.Model mData;

    CreatePresenter(@NonNull CreateContract.View view) {
        view.setPresenter(this);
        mView = view;
        mData = new MockModel();
    }


    @Override
    public void destroy() {
        mView = null;
        mData = null;
    }

    @Override
    public void createWallet(@NonNull CharSequence password) {
        mData.createWallet(password)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Account>(){

                    @Override
                    public void onStart() {
                        if (mView.isAdded()) {
                            mView.onCreateWalletStart();
                        }
                    }

                    @Override
                    public void onNext(Account account) {
                        if (mView.isAdded()) {
                            mView.onCreateWalletSuccess(account);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView.isAdded()) {
                            mView.onCreateWalletError(e);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (mView.isAdded()) {
                            mView.onCreateWalletCompleted();
                        }
                    }
                });
    }
}
