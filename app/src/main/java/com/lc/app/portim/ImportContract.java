package com.lc.app.portim;

import android.support.annotation.NonNull;

import com.lc.app.BasePresenter;
import com.lc.app.BaseView;
import com.lc.app.model.Account;

import rx.Observable;

/**
 * Created by orange on 18-4-9.
 */

public interface ImportContract {

    interface View extends BaseView<Presenter> {

        void onSaveWalletStart();

        void onSaveWalletSuccess(@NonNull Boolean success,
                                 @NonNull Account account);

        void onSaveWalletError(Throwable error);

        void onSaveWalletCompleted();
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
