package com.reactnativecommunity.webview;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.common.build.ReactBuildConfig;
import com.facebook.react.uimanager.ThemedReactContext;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

public class RNCWebViewManagerImpl {

    public static final String NAME = "RNCWebView";
    private static final String TAG = "RNCWebViewManagerImpl";

    protected static final String HTML_ENCODING = "UTF-8";
    protected static final String HTML_MIME_TYPE = "text/html";
    protected static final String JAVASCRIPT_INTERFACE = "ReactNativeWebView";
    protected static final String HTTP_METHOD_POST = "POST";
    // Use `webView.loadUrl("about:blank")` to reliably reset the view
    // state and release page resources (including any running JavaScript).
    protected static final String BLANK_URL = "about:blank";

    protected static final String DEFAULT_DOWNLOADING_MESSAGE = "Downloading";
    protected static final String DEFAULT_LACK_PERMISSION_TO_DOWNLOAD_MESSAGE =
            "Cannot download files as permission was denied. Please provide permission to write to storage, in order to download files.";

    public static RNCWebView createViewInstance(ThemedReactContext reactContext, @Nullable RNCWebViewConfig webViewConfig, @Nullable String downloadingMessage, @Nullable String lackPermissionToDownloadMessage, boolean allowsFullscreenVideo) {
        RNCWebView webView = new RNCWebView(reactContext);
        setupWebChromeClient(reactContext, webView, allowsFullscreenVideo);
        reactContext.addLifecycleEventListener(webView);
        webViewConfig.configWebView(webView);
        WebSettings settings = webView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setSupportMultipleWindows(true);
    
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          settings.setAllowFileAccessFromFileURLs(false);
          setAllowUniversalAccessFromFileURLs(webView, false);
        }
        setMixedContentMode(webView, "never");
    
        // Fixes broken full-screen modals/galleries due to body height being 0.
        webView.setLayoutParams(
          new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
    
        if (ReactBuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
          WebView.setWebContentsDebuggingEnabled(true);
        }
        // webView.setDownloadListener(new DownloadListener() {
        //     public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        //       webView.setIgnoreErrFailedForThisURL(url);
      
        //       RNCWebViewModule module = getModule(reactContext);
      
        //       DownloadManager.Request request;
        //       try {
        //         request = new DownloadManager.Request(Uri.parse(url));
        //       } catch (IllegalArgumentException e) {
        //         Log.w(TAG, "Unsupported URI, aborting download", e);
        //         return;
        //       }
      
        //       String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        //       String downloadMessage = "Downloading " + fileName;
      
        //       //Attempt to add cookie, if it exists
        //       URL urlObj = null;
        //       try {
        //         urlObj = new URL(url);
        //         String baseUrl = urlObj.getProtocol() + "://" + urlObj.getHost();
        //         String cookie = CookieManager.getInstance().getCookie(baseUrl);
        //         request.addRequestHeader("Cookie", cookie);
        //       } catch (MalformedURLException e) {
        //         Log.w(TAG, "Error getting cookie for DownloadManager", e);
        //       }
      
        //       //Finish setting up request
        //       request.addRequestHeader("User-Agent", userAgent);
        //       request.setTitle(fileName);
        //       request.setDescription(downloadMessage);
        //       request.allowScanningByMediaScanner();
        //       request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //       request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
      
        //       module.setDownloadRequest(request);
      
        //       if (module.grantFileDownloaderPermissions(getDownloadingMessageOrDefault(downloadingMessage), getLackPermissionToDownloadMessageOrDefault(lackPermissionToDownloadMessage))) {
        //         module.downloadFile(getDownloadingMessageOrDefault(downloadingMessage));
        //       }
        //     }
        //   });
      
        return webView;
    }

    protected static void setupWebChromeClient(ThemedReactContext reactContext, RNCWebView webView, boolean allowsFullscreenVideo) {
        Activity activity = reactContext.getCurrentActivity();

        if (allowsFullscreenVideo && activity != null) {
            int initialRequestedOrientation = activity.getRequestedOrientation();

            RNCWebChromeClient webChromeClient = new RNCWebChromeClient(reactContext, webView) {
                @Override
                public Bitmap getDefaultVideoPoster() {
                    return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                }

                @Override
                public void onShowCustomView(View view, CustomViewCallback callback) {
                    if (mVideoView != null) {
                        callback.onCustomViewHidden();
                        return;
                    }

                    mVideoView = view;
                    mCustomViewCallback = callback;

                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mVideoView.setSystemUiVisibility(FULLSCREEN_SYSTEM_UI_VISIBILITY);
                        activity.getWindow().setFlags(
                                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        );
                    }

                    mVideoView.setBackgroundColor(Color.BLACK);

                    // Since RN's Modals interfere with the View hierarchy
                    // we will decide which View to hide if the hierarchy
                    // does not match (i.e., the WebView is within a Modal)
                    // NOTE: We could use `mWebView.getRootView()` instead of `getRootView()`
                    // but that breaks the Modal's styles and layout, so we need this to render
                    // in the main View hierarchy regardless
                    ViewGroup rootView = getRootView();
                    rootView.addView(mVideoView, FULLSCREEN_LAYOUT_PARAMS);

                    // Different root views, we are in a Modal
                    if (rootView.getRootView() != mWebView.getRootView()) {
                        mWebView.getRootView().setVisibility(View.GONE);
                    } else {
                        // Same view hierarchy (no Modal), just hide the WebView then
                        mWebView.setVisibility(View.GONE);
                    }

                    mReactContext.addLifecycleEventListener(this);
                }

                @Override
                public void onHideCustomView() {
                    if (mVideoView == null) {
                        return;
                    }

                    // Same logic as above
                    ViewGroup rootView = getRootView();

                    if (rootView.getRootView() != mWebView.getRootView()) {
                        mWebView.getRootView().setVisibility(View.VISIBLE);
                    } else {
                        // Same view hierarchy (no Modal)
                        mWebView.setVisibility(View.VISIBLE);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    }

                    rootView.removeView(mVideoView);
                    mCustomViewCallback.onCustomViewHidden();

                    mVideoView = null;
                    mCustomViewCallback = null;

                    activity.setRequestedOrientation(initialRequestedOrientation);

                    mReactContext.removeLifecycleEventListener(this);
                }
            };

            webView.setWebChromeClient(webChromeClient);
        } else {
            RNCWebChromeClient webChromeClient = (RNCWebChromeClient) webView.getWebChromeClient();
            if (webChromeClient != null) {
                webChromeClient.onHideCustomView();
            }

            webChromeClient = new RNCWebChromeClient(reactContext, webView) {
                @Override
                public Bitmap getDefaultVideoPoster() {
                    return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                }
            };

            webView.setWebChromeClient(webChromeClient);
        }
    }

    // public static RNCWebViewModule getModule(ThemedReactContext reactContext) {
    //     return reactContext.getNativeModule(RNCWebViewModule.class);
    // }

    public static void setMixedContentMode(WebView view, @Nullable String mixedContentMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mixedContentMode == null || "never".equals(mixedContentMode)) {
                view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
            } else if ("always".equals(mixedContentMode)) {
                view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            } else if ("compatibility".equals(mixedContentMode)) {
                view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            }
        }
    }

    public static void setAllowUniversalAccessFromFileURLs(WebView view, boolean allow) {
        view.getSettings().setAllowUniversalAccessFromFileURLs(allow);
    }

    private static String getDownloadingMessageOrDefault(@Nullable String downloadingMessage) {
        return downloadingMessage == null ? DEFAULT_DOWNLOADING_MESSAGE : downloadingMessage;
    }

    private static String getLackPermissionToDownloadMessageOrDefault(@Nullable String lackPermissionToDownloadMessage) {
        return lackPermissionToDownloadMessage == null ? DEFAULT_LACK_PERMISSION_TO_DOWNLOAD_MESSAGE : lackPermissionToDownloadMessage;
    }

    public static void setColor(RNCWebView view, String color) {
        view.setBackgroundColor(Color.parseColor(color));
    }

    public static void setSource(RNCWebView view, ReadableMap source) {
        if (source != null) {
            if (source.hasKey("html")) {
                String html = source.getString("html");
                String baseUrl = source.hasKey("baseUrl") ? source.getString("baseUrl") : "";
                view.loadDataWithBaseURL(baseUrl, html, HTML_MIME_TYPE, HTML_ENCODING, null);
                return;
            }
            if (source.hasKey("uri")) {
                String url = source.getString("uri");
                String previousUrl = view.getUrl();
                if (previousUrl != null && previousUrl.equals(url)) {
                    return;
                }
                if (source.hasKey("method")) {
                    String method = source.getString("method");
                    if (method.equalsIgnoreCase(HTTP_METHOD_POST)) {
                        byte[] postData = null;
                        if (source.hasKey("body")) {
                            String body = source.getString("body");
                            try {
                                postData = body.getBytes("UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                postData = body.getBytes();
                            }
                        }
                        if (postData == null) {
                            postData = new byte[0];
                        }
                        view.postUrl(url, postData);
                        return;
                    }
                }
                HashMap<String, String> headerMap = new HashMap<>();
                if (source.hasKey("headers")) {
                    ReadableMap headers = source.getMap("headers");
                    ReadableMapKeySetIterator iter = headers.keySetIterator();
                    while (iter.hasNextKey()) {
                        String key = iter.nextKey();
                        if ("user-agent".equals(key.toLowerCase(Locale.ENGLISH))) {
                            if (view.getSettings() != null) {
                                view.getSettings().setUserAgentString(headers.getString(key));
                            }
                        } else {
                            headerMap.put(key, headers.getString(key));
                        }
                    }
                }
                view.loadUrl(url, headerMap);
                return;
            }
        }
        view.loadUrl(BLANK_URL);
    }
}