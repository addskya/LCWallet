package com.lc.app.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lc.app.BasePresenter;
import com.lc.app.BaseView;
import com.lc.app.model.Account;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public interface AccountDetailsContract {

    interface View extends BaseView<Presenter> {

        void showQrCode(@NonNull Account account);

        void back();

        void showMoreOperate();

        void gotoTransaction();

        void onAccountVisibleChanged(boolean isChecked);

        void backup(@NonNull Account account);

        void export(@Nullable Account account);

        void copyAddress(@Nullable Account account);
    }

    interface Presenter extends BasePresenter {

    }
}
