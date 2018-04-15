package com.lc.app.common;

import android.support.annotation.NonNull;

import com.lc.app.DefaultObserver;
import com.lc.app.model.CommonEntry;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
class CommonPresenter implements CommonContract.Presenter {

    private CommonContract.View mView;
    private CommonContract.Model mData;

    CommonPresenter(@NonNull CommonContract.View view) {
        view.setPresenter(this);
        mView = view;
        mData = new CommonModel();
    }

    @Override
    public void addAccount(@NonNull String filePath,
                           @NonNull final CommonEntry entry) {
        mData.addAccount(filePath, entry)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Boolean>() {

                    @Override
                    public void onStart() {
                        if (mView.isAdded()) {
                            mView.onAddAccountStart();
                        }
                    }

                    @Override
                    public void onNext(Boolean response) {
                        if (mView.isAdded()) {
                            mView.onAddAccountSuccess(entry);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView.isAdded()) {
                            mView.onAddAccountError(e);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (mView.isAdded()) {
                            mView.onAddAccountCompleted();
                        }
                    }
                });
    }

    @Override
    public void deleteAccount(@NonNull String filePath,
                              @NonNull CommonEntry entry) {
        mData.deleteAccount(filePath, entry)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Boolean>() {

                    @Override
                    public void onStart() {
                        if (mView.isAdded()) {
                            mView.onDeleteAccountStart();
                        }
                    }

                    @Override
                    public void onNext(Boolean response) {
                        if (mView.isAdded()) {
                            mView.onDeleteAccountSuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView.isAdded()) {
                            mView.onDeleteAccountError(e);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (mView.isAdded()) {
                            mView.onDeleteAccountCompleted();
                        }
                    }
                });
    }

    @Override
    public void updateAccount(@NonNull String filePath,
                              @NonNull final CommonEntry entry) {
        mData.updateAccount(filePath, entry)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Boolean>() {

                    @Override
                    public void onStart() {
                        if (mView.isAdded()) {
                            mView.onUpdateAccountStart();
                        }
                    }

                    @Override
                    public void onNext(Boolean response) {
                        if (mView.isAdded()) {
                            mView.onUpdateAccountSuccess(entry);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView.isAdded()) {
                            mView.onUpdateAccountError(e);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (mView.isAdded()) {
                            mView.onUpdateAccountCompleted();
                        }
                    }
                });
    }

    @Override
    public void loadAccount(@NonNull String filePath) {
        mData.loadAccount(filePath)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<List<CommonEntry>>() {

                    @Override
                    public void onStart() {
                        if (mView.isAdded()) {
                            mView.onLoadAccountStart();
                        }
                    }

                    @Override
                    public void onNext(List<CommonEntry> list) {
                        if (mView.isAdded()) {
                            mView.onLoadAccountSuccess(list);
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
        mData = null;
        mView = null;
    }
}
