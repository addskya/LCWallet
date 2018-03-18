package com.lc.app.create;

import android.support.annotation.NonNull;

import com.lc.app.model.Account;

import rx.Observable;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public class MockModel implements CreateContract.Model {

    @Override
    public Observable<Account> createWallet(@NonNull CharSequence password) {
        Account account = new Account();
        account.setName("");
        account.setRemain(0);
        account.setNumber("QGDFSGSEGSETQAafa");

        return Observable.just(account);
    }
}
