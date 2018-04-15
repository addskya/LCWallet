package com.lc.app.transaction;

import android.support.annotation.Nullable;

import com.lc.app.BasePresenter;
import com.lc.app.BaseView;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
public interface TransactionHistoryContract {

    interface View extends BaseView<Presenter> {

        void showHistory(@Nullable History history);
    }

    interface Presenter extends BasePresenter {}
}
