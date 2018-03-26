package com.lc.app.create;

import android.support.annotation.NonNull;

import com.lc.app.model.Account;
import com.lc.app.utils.WalletUtil;

import rx.Observable;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
class CreateModel implements CreateContract.Model {

    @Override
    public Observable<Boolean> saveWallet(@NonNull String accountFilePath,
                                          @NonNull Account account) {
        return WalletUtil.saveWallet(accountFilePath, account);
    }
}
