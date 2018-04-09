package com.lc.app.portim;

import android.support.annotation.NonNull;

import com.lc.app.model.Account;
import com.lc.app.utils.WalletUtil;

import rx.Observable;

/**
 * Created by orange on 18-4-9.
 */

class ImportModel implements ImportContract.Model {

    @Override
    public Observable<Boolean> saveWallet(@NonNull String accountFilePath,
                                          @NonNull Account account) {
        return WalletUtil.saveWallet(accountFilePath, account);
    }
}
