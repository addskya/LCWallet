package com.lc.app.common;

import android.support.annotation.NonNull;

import com.lc.app.model.CommonEntry;
import com.lc.app.utils.CommonUtils;

import java.io.IOException;
import java.util.List;

import rx.Observable;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
class CommonModel implements CommonContract.Model {

    @Override
    public Observable<Boolean> addAccount(
            @NonNull String filePath,
            @NonNull CommonEntry entry) {
        try {
            CommonUtils.saveAccount(filePath, entry);
        } catch (IOException e) {
            return Observable.error(e);
        }

        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> deleteAccount(
            @NonNull String filePath,
            @NonNull CommonEntry entry) {
        try {
            CommonUtils.deleteAccount(filePath, entry);
        } catch (IOException e) {
            return Observable.error(e);
        }
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> updateAccount(
            @NonNull String filePath,
            @NonNull CommonEntry entry) {
        try {
            CommonUtils.updateAccount(filePath, entry);
        } catch (IOException e) {
            return Observable.error(e);
        }
        return Observable.just(true);
    }

    @Override
    public Observable<List<CommonEntry>> loadAccount(@NonNull String filePath) {
        try {
            return Observable.just(CommonUtils.loadAccount(filePath));
        } catch (IOException | ClassNotFoundException e) {
            return Observable.error(e);
        }
    }
}
