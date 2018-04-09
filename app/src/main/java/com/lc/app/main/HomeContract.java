package com.lc.app.main;

import android.support.annotation.NonNull;

import com.lc.app.BasePresenter;
import com.lc.app.BaseView;
import com.lc.app.model.Account;

import java.util.List;

import rx.Observable;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 */
public interface HomeContract {

    interface View extends BaseView<Presenter> {

        void refresh();

        void scanQrCode();

        void doOperate();

        void showAccount(@NonNull Account account);

        void showQrCode(@NonNull Account account);


        void onLoadAccountStart();

        void onLoadAccounts(List<Account> accounts, boolean refresh);

        void onLoadAccountError(Throwable error);

        void onLoadAccountCompleted();

        String getAccountRemain();
    }

    interface Presenter extends BasePresenter {

        void loadAccounts(@NonNull String walletFolderPath,
                          boolean refresh);

    }

    interface Model {

        Observable<List<Account>> queryAccount(@NonNull String walletFolderPath);
    }
}
