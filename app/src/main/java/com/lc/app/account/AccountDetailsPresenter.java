package com.lc.app.account;

import android.support.annotation.NonNull;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
class AccountDetailsPresenter implements AccountDetailsContract.Presenter {

    private AccountDetailsContract.View mView;


    AccountDetailsPresenter(@NonNull AccountDetailsContract.View view) {
        view.setPresenter(this);
        mView = view;
    }

    @Override
    public void destroy() {
        mView = null;
    }
}
