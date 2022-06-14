package com.reactnativecommunity.webview;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.viewmanagers.RNCWebViewManagerDelegate;
import com.facebook.react.viewmanagers.RNCWebViewManagerInterface;

@ReactModule(name = RNCWebViewManagerImpl.NAME)
public class RNCWebViewManager extends SimpleViewManager<RNCWebView>
        implements RNCWebViewManagerInterface<RNCWebView> {

    private final ViewManagerDelegate<RNCWebView> mDelegate;
    protected RNCWebViewConfig mWebViewConfig;
    protected boolean mAllowsFullscreenVideo = false;
    protected @Nullable String mDownloadingMessage = null;
    protected @Nullable String mLackPermissionToDownloadMessage = null;

    public RNCWebViewManager(ReactApplicationContext context) {
        mDelegate = new RNCWebViewManagerDelegate<>(this);
    }

    @Nullable
    @Override
    protected ViewManagerDelegate<RNCWebView> getDelegate() {
        return mDelegate;
    }

    @NonNull
    @Override
    public String getName() {
        String test = RNCWebViewManagerImpl.NAME;
        return RNCWebViewManagerImpl.NAME;
    }

    @NonNull
    @Override
    protected RNCWebView createViewInstance(@NonNull ThemedReactContext context) {
        return RNCWebViewManagerImpl.createViewInstance(context, mWebViewConfig, mDownloadingMessage, mLackPermissionToDownloadMessage, mAllowsFullscreenVideo);
    }

    @Override
    @ReactProp(name = "color")
    public void setColor(RNCWebView view, @Nullable String color) {
        RNCWebViewManagerImpl.setColor(view, color);
    }

    @Override
    @ReactProp(name = "source")
    public void setSource(RNCWebView view, @Nullable ReadableMap value) {
        RNCWebViewManagerImpl.setSource(view, value);
    }
}