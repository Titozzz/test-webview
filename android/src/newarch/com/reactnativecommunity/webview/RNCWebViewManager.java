package com.reactnativecommunity.webview;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReactApplicationContext;
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
        return RNCWebViewManagerImpl.createViewInstance(context);
    }

    @Override
    @ReactProp(name = "color")
    public void setColor(RNCWebView view, @Nullable String color) {
        RNCWebViewManagerImpl.setColor(view, color);
    }
}