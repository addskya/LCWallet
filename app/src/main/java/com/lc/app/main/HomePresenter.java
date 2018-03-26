package com.lc.app.main;

import android.support.annotation.NonNull;

import com.lc.app.DefaultObserver;
import com.lc.app.model.Account;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 */
class HomePresenter implements HomeContract.Presenter {

    private static final String TAG = "HomePresenter";

    private HomeContract.View mView;
    private HomeContract.Model mData;

    HomePresenter(@NonNull HomeContract.View view) {
        view.setPresenter(this);
        mView = view;
        mData = new HomeModel();
    }

    @Override
    public void loadAccounts(@NonNull String walletFolderPath,
                             final boolean refresh) {
        mData.queryAccount(walletFolderPath)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<List<Account>>() {

                    @Override
                    public void onStart() {
                        if (mView.isAdded()) {
                            mView.onLoadAccountStart();
                        }
                    }

                    @Override
                    public void onNext(List<Account> response) {
                        if (mView.isAdded()) {
                            mView.onLoadAccounts(response, refresh);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView.isAdded()) {
                            mView.onLoadAccountError(e);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (mView.isAdded()) {
                            mView.onLoadAccountCompleted();
                        }
                    }
                });
    }

    @Override
    public void destroy() {
        mView = null;
    }
}
