package com.reactnativecommunity.webview;

import androidx.annotation.Nullable;
import com.facebook.react.uimanager.ThemedReactContext;
import android.graphics.Color;

public class RNCWebViewManagerImpl {

    public static final String NAME = "RNCWebView";

    public static RNCWebView createViewInstance(ThemedReactContext context) {
        return new RNCWebView(context);
    }

    public static void setColor(RNCWebView view, String color) {
        view.setBackgroundColor(Color.parseColor(color));
    }

}