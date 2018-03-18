package com.lc.app.main;

import com.lc.app.model.Account;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 */
public class MockModel implements HomeContract.Model {
    @Override
    public Observable<List<Account>> queryAccount() {
        List<Account> results = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            Account a = new Account();
            a.setName("第" + i + "个账户");
            a.setNumber("123456789");
            a.setRemain(100.1F + i);
            results.add(a);
        }

        return Observable.just(results);
    }
}
