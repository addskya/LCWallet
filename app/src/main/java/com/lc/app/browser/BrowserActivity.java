package com.lc.app.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lc.app.BaseActivity;
import com.lc.app.R;
import com.lc.app.databinding.ActivityBrowserBinding;
import com.lc.app.javascript.JavaScriptApi;


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
        initWebView();

        String url = "file:///android_asset/api/test.html";
        mWebView.loadUrl(url);
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
        mWebView.addJavascriptInterface(new JavaScriptApi(),"handler");

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
        });
    }

    @Override
    public void onBackPressed() {
        //String call = "javascript:alertMessage(\"Hello Orange\")";
        //String call = "javascript:toastMessage(\"Hello Orange\")";
        String call = "javascript:testRate()";
        mWebView.loadUrl(call);
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
}
