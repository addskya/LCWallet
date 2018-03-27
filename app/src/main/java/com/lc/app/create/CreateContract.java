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

        void onSaveWalletStart();

        void onSaveWalletSuccess(@NonNull Boolean success,
                                 @NonNull Account account);

        void onSaveWalletError(Throwable error);

        void onSaveWalletCompleted();


        void back();

        void createWallet();
    }

    interface Presenter extends BasePresenter {

        void saveWallet(@NonNull String accountFilePath,
                        @NonNull Account account);
    }

    interface Model {

        Observable<Boolean> saveWallet(@NonNull String accountFilePath,
                                       @NonNull Account account);
    }
}
