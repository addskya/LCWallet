package com.lc.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lc.app.javascript.JavaScriptApi;
import com.lc.app.javascript.JsCallback;
import com.lc.app.ui.LoadingDialog;
import com.lc.app.ui.StatusDialog;

/**
 * Created by Orange on 18-3-24.
 * Email:addskya@163.com
 */
public abstract class JsBaseActivity extends BaseActivity {
    private static final String TAG = "JsBaseActivity";
    private WebView mWebView;
    private BaseDialog mDialog;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildWebView();
    }

    @Override
    public void finish() {
        super.finish();
        dismissProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
        ViewParent parent = mWebView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mWebView);
        }
        mHandler = null;
    }

    private void buildWebView() {
        Log.d(TAG, "buildWebView");
        mWebView = new WebView(this);
        initWebView(mWebView);
        mWebView.setVisibility(View.VISIBLE);
        String url = "file:///android_asset/api/api.html";
        mWebView.loadUrl(url);
        int width = 100;
        int height = 100;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        getWindow().addContentView(mWebView, params);
        Log.e(TAG, "addWebView");
    }

    @SuppressWarnings("JavascriptInterface")
    private void initWebView(@NonNull WebView webView) {
        final WebSettings webSettings = webView.getSettings();

        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());
        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadWithOverviewMode(false);
        webSettings.setTextZoom(100);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        CookieManager.setAcceptFileSchemeCookies(true);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);

        // Debug Code
        mWebView.addJavascriptInterface(new JavaScriptApi(getJsCallback()), "handler");

        webSettings.setUserAgentString(webSettings.getUserAgentString());

        mWebView.setWebChromeClient(new WebChromeClient() {
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished:" + url);
                initWallet();
            }
        });
    }

    protected JsCallback getJsCallback() {
        return null;
    }

    protected void sendCommand(@NonNull Runnable action) {
        mHandler.postDelayed(action, 200);
    }

    private void initWallet() {
        String call = "javascript:initWallet()";
        Log.d(TAG, "initWallet execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                onWalletInitCompleted();
            }
        });
    }

    /**
     * 钱包初始化完毕,可以执行其他任务
     */
    protected void onWalletInitCompleted() {
        Log.d(TAG, "onWalletInitCompleted");
    }

    /**
     * create wallet
     *
     * @param walletName the wallet name
     * @param password   the wallet password
     * @param callback   the callback after create wallet
     */
    protected void createWallet(@NonNull CharSequence walletName,
                                @NonNull CharSequence password,
                                @Nullable ValueCallback<String> callback) {
        String call = "javascript:createWallet(\"" + walletName + "\",\"" + password + "\")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    @SuppressWarnings("nouse")
    protected void loadWallet(@NonNull CharSequence walletName,
                              @NonNull CharSequence password,
                              @NonNull CharSequence address,
                              @Nullable ValueCallback<String> callback) {
        String call = "javascript:loadWallet(\"" + walletName + "\",\"" + password + "\"," + address + ")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    /**
     * export the Wallet
     *
     * @param walletName the wallet name
     * @param password   the wallet password
     * @param callback   the callback after export wallet
     */
    protected void exportWallet(@NonNull CharSequence walletName,
                                @NonNull CharSequence password,
                                @Nullable ValueCallback<String> callback) {
        String call = "javascript:exportWallet(\"" + walletName + "\",\"" + password + "\")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    /**
     * import wallet
     *
     * @param walletName the wallet name
     * @param keystore   the wallet keystore
     * @param password   the wallet password
     * @param callback   the callback after import wallet
     */
    protected void importWallet(@NonNull CharSequence walletName,
                                @NonNull CharSequence keystore,
                                @NonNull CharSequence password,
                                @Nullable ValueCallback<String> callback) {
        String call = "javascript:importWallet(\"" + walletName + "\"," + keystore + ",\"" + password + "\")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    protected void loadWallet(@Nullable ValueCallback<String> callback) {
        String call = "javascript:getWalletInfo()";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    /**
     * query the current rate for transfer
     *
     * @param callback the callback
     */
    protected void getRate(@Nullable ValueCallback<String> callback) {
        String call = "javascript:getRate()";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    /**
     * query the balance of the wallet
     *
     * @param address the wallet address
     */
    protected void balanceOf(@NonNull CharSequence address) {
        address = "\"0x" + address + "\"";
        String call = "javascript:balanceOf(" + address + ")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, null);
    }

    /**
     * transaction to other wallet
     *
     * @param walletName     the execute wallet name
     * @param password       the execute wallet password
     * @param executeAccount the execute account
     * @param toAccount      to which account
     * @param amount         the transfer amount
     */
    protected void transferByFee(@NonNull CharSequence walletName,
                                 @NonNull CharSequence password,
                                 @NonNull CharSequence executeAccount,
                                 @NonNull CharSequence toAccount,
                                 @NonNull float amount,
                                 @Nullable ValueCallback<String> callback) {
        executeAccount = "\"0x" + executeAccount + "\"";
        toAccount = "0x" + toAccount + "";

        String call = "javascript:transferByFee("
                + "\"" + walletName + "\","
                + "\"" + password + "\","
                + executeAccount + ","
                + "\"" + toAccount + "\","
                + amount + ")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    /**
     * 查询账户所有交易记录
     *
     * @param target 目标账户
     */
    protected void showHistoryTransaction(String target) {
        target = "\"0x" + target + "\"";
        String call = "javascript:showHistoryTransaction(" + target + ")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, null);
    }

    protected void showProgressDialog() {
        if (mDialog == null) {
            mDialog = new LoadingDialog(this);
        }
        mDialog.show();
    }

    protected void dismissProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }


    protected void showProgressDialog(@StringRes int status) {
        dismissProgressDialog();
        if (mDialog == null) {
            mDialog = new StatusDialog(this, status);
        }
        mDialog.show();
    }

    @NonNull
    protected String getWalletFolder() {
        return ((App) getApplication()).getWalletFolder();
    }

    private final class ValueCallbackWrapper<T> implements ValueCallback<T> {

        private ValueCallback<T> mBaseCallback;

        ValueCallbackWrapper(@Nullable ValueCallback<T> base) {
            mBaseCallback = base;
        }

        @Override
        public void onReceiveValue(T value) {
            Log.i(TAG, "onReceiveValue:" + value);
            if (mBaseCallback != null) {
                mBaseCallback.onReceiveValue(value);
            }
        }
    }
}
