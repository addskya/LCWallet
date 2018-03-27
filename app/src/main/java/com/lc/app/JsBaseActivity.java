package com.lc.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

/**
 * Created by Orange on 18-3-24.
 * Email:addskya@163.com
 */
public abstract class JsBaseActivity extends BaseActivity {
    private static final String TAG = "JsBaseActivity";
    private WebView mWebView;
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildWebView();
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

    @SuppressLint("JavascriptInterface")
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
        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptEnabled(true);
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
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100) {
                    onWebViewLoadCompleted();
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
        });
    }

    protected JsCallback getJsCallback() {
        return null;
    }

    /**
     * WebView 显示加载完毕,
     * 可以调用javascript接口了
     */
    protected void onWebViewLoadCompleted() {
        initWallet();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
        ViewParent parent = mWebView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mWebView);
        }
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

    }

    /**
     * @param walletName
     * @param password
     * @param callback
     */
    protected void createWallet(@NonNull CharSequence walletName,
                                @NonNull CharSequence password,
                                @Nullable ValueCallback<String> callback) {
        initWallet();
        String call = "javascript:createWallet(\"" + walletName + "\",\"" + password + "\")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    protected void loadWallet(@NonNull CharSequence walletName,
                              @NonNull CharSequence password,
                              @NonNull CharSequence address,
                              @Nullable ValueCallback<String> callback) {
        initWallet();
        String call = "javascript:loadWallet(\"" + walletName + "\",\"" + password + "\"," + address + ")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    /**
     * export the Wallet
     *
     * @param walletName
     * @param password
     * @param callback
     */
    protected void exportWallet(@NonNull CharSequence walletName,
                                @NonNull CharSequence password,
                                @Nullable ValueCallback<String> callback) {
        initWallet();
        String call = "javascript:exportWallet(\"" + walletName + "\",\"" + password + "\")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    protected void importWallet(@NonNull CharSequence walletName,
                                @NonNull CharSequence keystore,
                                @NonNull CharSequence password,
                                @Nullable ValueCallback<String> callback) {
        initWallet();
        String call = "javascript:importWallet(\"" + walletName + "\",\"" + keystore + "\",\"" + password + "\")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    protected void getRate(@Nullable ValueCallback<String> callback) {
        initWallet();
        String call = "javascript:getRate()";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallbackWrapper<>(callback));
    }

    protected void balanceOf(@NonNull CharSequence address) {
        initWallet();
        String call = "javascript:balanceOf(\"" + address + "\")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, null);
    }

    // transferByFee(walletName, password, executeAccount, toAccount ,amount)
    protected void transferByFee(@NonNull CharSequence walletName,
                                 @NonNull CharSequence password,
                                 @NonNull CharSequence executeAccount,
                                 @NonNull CharSequence toAccount,
                                 @NonNull float amount) {
        initWallet();
        String call = "javascript:transferByFee("
                + "\"" + walletName + "\","
                + "\"" + password + "\","
                + "\"" + executeAccount + "\","
                + "\"" + toAccount + "\","
                + "\"" + amount + "\")";
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
