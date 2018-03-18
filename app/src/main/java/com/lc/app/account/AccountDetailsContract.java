package com.lc.app.account;

import android.support.annotation.NonNull;

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

        void onAccountVisibleChanged(boolean isChecked);

        void backup(@NonNull Account account);
    }

    interface Presenter extends BasePresenter {

    }
}
