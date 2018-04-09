package com.lc.app.javascript;

import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by Orange on 18-3-13.
 * Email:addskya@163.com
 */

public class JavaScriptApi {

    private static final String TAG = "JavaScriptApi";

    private JsCallback mCallBack;

    public JavaScriptApi() {
        this(null);
    }

    public JavaScriptApi(@Nullable JsCallback callback) {
        mCallBack = callback;
    }

    @JavascriptInterface
    public void onCallback_initWallet(String error, String result) {
        Log.i(TAG, "onCallback_initWallet -> error:" + error);
        Log.i(TAG, "onCallback_initWallet -> result:" + result);
        if (mCallBack != null) {
            mCallBack.onCallback(JsCallback.MESSAGE_INIT_WALLET, error, result);
        }
    }


    /**
     * 查询账户余额回调
     *
     * @param error  正常情况下为null,或错误信息
     * @param result 账户余额或Null
     */
    @JavascriptInterface
    public void onCallback_BalanceOf(String error, String result) {
        Log.i(TAG, "onCallback_BalanceOf -> error:" + error);
        Log.i(TAG, "onCallback_BalanceOf -> result:" + result);
        if (mCallBack != null) {
            mCallBack.onCallback(JsCallback.MESSAGE_BALANCE, error, result);
        }
    }

    /**
     * 获取交易费率的回调
     *
     * @param error  正常情况下为null,或错误信息
     * @param result 当前的交易费率,或Null
     */
    @JavascriptInterface
    public void onCallback_getRate(String error, String result) {
        Log.i(TAG, "onCallback_getRate -> error:" + error);
        Log.i(TAG, "onCallback_getRate -> result:" + result);
        if (mCallBack != null) {
            mCallBack.onCallback(JsCallback.MESSAGE_RATE, error, result);
        }
    }

    /**
     * 转账交易回调
     *
     * @param error  正常情况下为null,或错误信息
     * @param result 本次交易的hash值,或null
     */
    @JavascriptInterface
    public void onCallback_transferByFee(String error, String result) {
        Log.i(TAG, "onCallback_transferByFee -> error:" + error);
        Log.i(TAG, "onCallback_transferByFee -> result:" + result);
        if (mCallBack != null) {
            mCallBack.onCallback(JsCallback.MESSAGE_TRANSFER, error, result);
        }
    }

    /**
     * 查询转账历史记录
     *
     * @param error 正常情况下为null,或错误信息
     * @param json  转账的历史记录,使用数组返回,{from,to,value,date}
     */
    @JavascriptInterface
    public void onCallback_historyTransactions(String error, String json) {
        Log.i(TAG, "onCallback_historyTransactions -> error:" + error);
        Log.i(TAG, "onCallback_historyTransactions -> result:" + json);
        if (mCallBack != null) {
            mCallBack.onCallback(JsCallback.MESSAGE_TRANSFER_HISTORY, error, json);
        }
    }
}
