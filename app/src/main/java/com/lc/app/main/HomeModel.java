package com.lc.app.main;

import android.support.annotation.NonNull;

import com.lc.app.model.Account;
import com.lc.app.utils.WalletUtil;

import java.util.List;

import rx.Observable;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 */
class HomeModel implements HomeContract.Model {
    @Override
    public Observable<List<Account>> queryAccount(@NonNull String walletFolderPath) {
        return WalletUtil.queryWallet(walletFolderPath);
    }
}
