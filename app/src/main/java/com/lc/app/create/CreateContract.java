package com.lc.app.create;

import android.support.annotation.NonNull;

import com.lc.app.BasePresenter;
import com.lc.app.BaseView;
import com.lc.app.model.Account;

import rx.Observable;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public interface CreateContract {

    interface View extends BaseView<Presenter> {

        void onCreateWalletStart();

        void onCreateWalletSuccess(@NonNull Account account);

        void onCreateWalletFailure();

        void onCreateWalletError(Throwable error);

        void onCreateWalletCompleted();


        void back();

        void createWallet();

    }

    interface Presenter extends BasePresenter  {

        void createWallet (@NonNull CharSequence password);
    }

    interface Model {

        Observable<Account> createWallet(@NonNull CharSequence password);
    }
}
