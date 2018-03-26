package com.lc.app.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lc.app.model.Account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Orange on 18-3-26.
 * Email:addskya@163.com
 */

public class WalletUtil {

    private static String generateWalletFileName(@NonNull Account account) {
        return account.getWalletName() + ".wallet";
    }

    public static Observable<Boolean> saveWallet(@NonNull String accountFilePath,
                                                 @NonNull Account account) {
        try {
            if (!createFolder(accountFilePath)) {
                throw new IOException("Create account file failed!");
            }
            File parent = new File(accountFilePath);
            File file = new File(parent, generateWalletFileName(account));
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(account);
        } catch (IOException e) {
            return Observable.error(e);
        }

        return Observable.just(Boolean.TRUE);
    }

    private static boolean createFolder(@NonNull String folderName) {
        if (TextUtils.isEmpty(folderName)) {
            throw new IllegalArgumentException("Empty folderName");
        }

        File file = new File(folderName);
        if (file.exists()) {
            return file.isDirectory() || (file.delete() && file.mkdirs());
        } else {
            return file.mkdirs();
        }
    }

    public static Observable<Boolean> deleteWallet(@NonNull String accountFilePath,
                                                   @NonNull Account account) {
        File parent = new File(accountFilePath);
        File file = new File(parent, generateWalletFileName(account));
        return Observable.just(file.delete());
    }


    public static Observable<Boolean> updateWallet(@NonNull String accountFilePath,
                                                   @NonNull Account account) {
        File parent = new File(accountFilePath);
        File file = new File(parent, generateWalletFileName(account));
        if (file.delete()) {
            return saveWallet(accountFilePath, account);
        }
        return Observable.just(false);
    }

    public static Observable<List<Account>> queryWallet(@NonNull String walletFolderPath) {
        List<Account> results = new ArrayList<>();
        File folderFile = new File(walletFolderPath);
        File[] wallets = folderFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".wallet");
            }
        });

        if (wallets != null && wallets.length > 0) {
            for (File f : wallets) {
                Account account = parseAccount(f);
                if (account != null) {
                    results.add(account);
                }
            }
        }

        return Observable.just(results);
    }

    private static Account parseAccount(@NonNull File walletFile) {
        try {
            FileInputStream fis = new FileInputStream(walletFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            if (obj instanceof Account) {
                return (Account) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
