package com.reactnativecommunity.webview;

import androidx.annotation.Nullable;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.bridge.ReactApplicationContext;
import android.graphics.Color;
import java.util.Map;
import java.util.HashMap;

public class RNCWebViewManager extends SimpleViewManager<RNCWebView> {

    ReactApplicationContext mCallerContext;

    public RNCWebViewManager(ReactApplicationContext reactContext) {
        mCallerContext = reactContext;
    }

    @Override
    public String getName() {
        return RNCWebViewManagerImpl.NAME;
    }

    @Override
    public RNCWebView createViewInstance(ThemedReactContext context) {
        return RNCWebViewManagerImpl.createViewInstance(context);
    }

    @ReactProp(name = "color")
    public void setColor(RNCWebView view, String color) {
        RNCWebViewManagerImpl.setColor(view, color);
    }

}