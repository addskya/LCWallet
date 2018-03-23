package com.lc.app.javascript;

import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by Orange on 18-3-13.
 * Email:addskya@163.com
 */

public class JavaScriptApi {

    private static final String TAG = "JavaScriptApi";

    @JavascriptInterface
    public void toastMessage(String message) {
        Log.i(TAG, "toastMessage:" + message);
    }

    @JavascriptInterface
    public void onSumResult(int result) {
        Log.i(TAG, "onSumResult:" + result);
    }
}
