package com.lc.app;

import android.app.Application;

import com.lc.app.fresco.FrescoConfig;

import java.io.File;
import java.util.UUID;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public class App extends Application {

    private static final String WALLET_FOLDER_NAME = "wallet";

    @Override
    public void onCreate() {
        super.onCreate();
        FrescoConfig.init(this);
    }

    public String getWalletFolder() {
        return new File(getFilesDir(), WALLET_FOLDER_NAME).getAbsolutePath();
    }

}
