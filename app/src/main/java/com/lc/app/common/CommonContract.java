package com.lc.app.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lc.app.BasePresenter;
import com.lc.app.BaseView;
import com.lc.app.model.Account;
import com.lc.app.model.CommonEntry;

import java.util.List;

import rx.Observable;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
public interface CommonContract {

    interface View extends BaseView<Presenter> {

        void onAddAccountStart();

        void onAddAccountSuccess(@NonNull CommonEntry entry);

        void onAddAccountError(Throwable error);

        void onAddAccountCompleted();


        void onDeleteAccountStart();

        void onDeleteAccountSuccess();

        void onDeleteAccountError(Throwable error);

        void onDeleteAccountCompleted();

        void onUpdateAccountStart();

        void onUpdateAccountSuccess(@NonNull CommonEntry entry);

        void onUpdateAccountError(Throwable error);

        void onUpdateAccountCompleted();

        void onLoadAccountStart();

        void onLoadAccountSuccess(@Nullable List<CommonEntry> list);

        void onLoadAccountError(Throwable error);

        void onLoadAccountCompleted();

        void onTap(@Nullable CommonEntry entry);

        boolean onLongTap(@Nullable CommonEntry entry);

        // Click
        void addAccount();

        void onBack();
    }


    interface Presenter extends BasePresenter {


        void addAccount(@NonNull String filePath,
                        @NonNull CommonEntry entry);

        void deleteAccount(@NonNull String filePath,
                           @NonNull CommonEntry entry);

        void updateAccount(@NonNull String filePath,
                           @NonNull CommonEntry entry);

        void loadAccount(@NonNull String path);
    }

    interface Model {

        Observable<Boolean> addAccount(
                @NonNull String filePath,
                @NonNull CommonEntry entry);

        Observable<Boolean> deleteAccount(
                @NonNull String filePath,
                @NonNull CommonEntry entry);

        Observable<Boolean> updateAccount(
                @NonNull String filePath,
                @NonNull CommonEntry entry);


        Observable<List<CommonEntry>> loadAccount(@NonNull String filePath);
    }
}
