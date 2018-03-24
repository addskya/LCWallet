package com.lc.app;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lc.app.javascript.JavaScriptApi;

/**
 * Created by Orange on 18-3-24.
 * Email:addskya@163.com
 */
public abstract class JsBaseActivity extends BaseActivity {
    private static final String TAG = "JsBaseActivity";
    private WebView mWebView;

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
        mWebView.addJavascriptInterface(new JavaScriptApi(), "handler");

        webSettings.setUserAgentString(webSettings.getUserAgentString());

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i(TAG, "onConsoleMessage:" + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.i(TAG, "onJsAlert message:" + message);
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                Log.i(TAG, "onJsConfirm");
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, JsPromptResult result) {
                Log.i(TAG, "onJsPrompt");
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url,
                                            String message, JsResult result) {
                Log.i(TAG, "onJsBeforeUnload");
                return super.onJsBeforeUnload(view, url, message, result);
            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.i(TAG, "onReceivedTitle");
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                Log.i(TAG, "onReceivedTouchIconUrl");
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                Log.i(TAG, "onCreateWindow");
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                Log.i(TAG, "onShowFileChooser");
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
                Log.i(TAG, "onCloseWindow");
            }

            @Override
            public void onGeolocationPermissionsHidePrompt() {
                super.onGeolocationPermissionsHidePrompt();
                Log.i(TAG, "onGeolocationPermissionsHidePrompt");
            }


            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                Log.i(TAG, "onHideCustomView");
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                Log.i(TAG, "onReceivedIcon");
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onRequestFocus(WebView view) {
                Log.i(TAG, "onRequestFocus");
                super.onRequestFocus(view);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                Log.i(TAG, "onShowCustomView");
                super.onShowCustomView(view, callback);
            }

            @Override
            public boolean onJsTimeout() {
                Log.i(TAG, "onJsTimeout");
                return super.onJsTimeout();
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(TAG, "url:" + url);
                if (!handleUrl(url)) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView webView,
                                           SslErrorHandler sslErrorHandler,
                                           SslError sslError) {
                super.onReceivedSslError(webView, sslErrorHandler, sslError);
            }

            @Override
            public void onReceivedError(WebView view,
                                        WebResourceRequest request,
                                        WebResourceError error) {
                Log.e(TAG, "onReceivedError error:" + error);
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view,
                                            WebResourceRequest request,
                                            WebResourceResponse errorResponse) {
                Log.e(TAG, "onReceivedHttpError request:" + request);
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                Log.i(TAG, "onReceivedClientCertRequest");
                super.onReceivedClientCertRequest(view, request);
            }


            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                Log.i(TAG, "onFormResubmission");
                super.onFormResubmission(view, dontResend, resend);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.i(TAG, "onLoadResource:" + url);
                super.onLoadResource(view, url);
            }


            @Override
            public void onPageFinished(WebView view, String url) {

                Log.i(TAG, "onPageFinished");
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i(TAG, "onPageStarted");
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view,
                                                  HttpAuthHandler handler,
                                                  String host, String realm) {

                Log.i(TAG, "onReceivedHttpAuthRequest");
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }

            @Override
            public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                Log.i(TAG, "onReceivedLoginRequest");
                super.onReceivedLoginRequest(view, realm, account, args);
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {

                Log.i(TAG, "onScaleChanged");
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                Log.i(TAG, "onUnhandledKeyEvent");
                super.onUnhandledKeyEvent(view, event);
            }
        });
    }


    /**
     * 实现对特殊协议的处理:
     * 如: lcf://action=abc
     * 如果是以 lcf的schema,则可以自己实现中转
     *
     * @param url the openable url
     * @return true if the url is openable
     */
    private boolean handleUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "gotoAnyWhere with Empty Url");
            return false;
        }

        if (url.toLowerCase().startsWith("http")) { //startsWith http or https
            return false;
        }

        final Uri uri = Uri.parse(url);

        if (uri == null) {
            return false;
        }

        Intent viewIntent = getBrowsableIntent(uri);
        if (!startActivitySafely(viewIntent)) {
            Log.e(TAG, "Error when handleUrl:" + url);
        }

        return true;
    }

    private Intent getBrowsableIntent(@NonNull Uri uri) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        viewIntent.addCategory(Intent.CATEGORY_DEFAULT);

        viewIntent.setData(uri);
        return viewIntent;
    }

    private boolean startActivitySafely(@NonNull Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            return false;
        }
        return true;
    }

    protected void initWallet() {
        String call = "javascript:initWallet()";
        mWebView.evaluateJavascript(call, null);
    }

    protected void createWallet(@NonNull CharSequence walletName,
                                @NonNull CharSequence password) {
        String call = "javascript:createWallet(\"" + walletName + "\",\"" + password + "\")";
        Log.d(TAG, "execute:" + call);
        mWebView.evaluateJavascript(call, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "value:" + value);
            }
        });
    }
}
