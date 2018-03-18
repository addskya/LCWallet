package com.lc.app;

import android.app.Application;

import com.lc.app.fresco.FrescoConfig;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FrescoConfig.init(this);
    }
}
