package com.lc.app.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lc.app.BaseActivity;
import com.lc.app.R;
import com.lc.app.databinding.ActivityBrowserBinding;
import com.lc.app.javascript.JavaScriptApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by Orange on 18-3-13.
 * Email:addskya@163.com
 */

public class BrowserActivity extends BaseActivity {

    private static final String TAG = "BrowserActivity";
    private WebView mWebView;
    private ActivityBrowserBinding mBinding;

    public static void intentTo(@NonNull Activity activity) {
        Intent intent = new Intent(activity, BrowserActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_browser);
        mWebView = mBinding.webView;
        disableSecrut();
        initWebView();

        String url = "file:///android_asset/api/test-wt.html";
        mWebView.loadUrl(url);
    }

    private void disableSecrut() {
        try {
            Class<?> clazz = mWebView.getSettings().getClass();
            Method method = clazz.getMethod(
                    "setAllowUniversalAccessFromFileURLs", boolean.class);
            if (method != null) {
                method.invoke(mWebView.getSettings(), true);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setTextZoom(100);
        webSettings.setLoadsImagesAutomatically(true);

        // Debug Code
        mWebView.addJavascriptInterface(new JavaScriptApi(), "handler");

        webSettings.setUserAgentString(webSettings.getUserAgentString());

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url,
                                        String userAgent,
                                        String contentDisposition,
                                        String mimetype,
                                        long contentLength) {
                startActivitySafely(getBrowsableIntent(Uri.parse(url)));
            }
        });


        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

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
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler,
                                           SslError error) {
                Log.e(TAG, "onReceivedSslError:" + error);
                super.onReceivedSslError(view, handler, error);
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
        });
    }
/**
 * String call = "javascript:testRate()";
 *        mWebView.loadUrl(call);
 *
 * */

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
}
