package com.reactnativecommunity.webview;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.viewmanagers.RNCWebViewManagerDelegate;
import com.facebook.react.viewmanagers.RNCWebViewManagerInterface;
import com.facebook.react.views.scroll.ScrollEventType;
import com.reactnativecommunity.webview.events.TopHttpErrorEvent;
import com.reactnativecommunity.webview.events.TopLoadingErrorEvent;
import com.reactnativecommunity.webview.events.TopLoadingFinishEvent;
import com.reactnativecommunity.webview.events.TopLoadingProgressEvent;
import com.reactnativecommunity.webview.events.TopLoadingStartEvent;
import com.reactnativecommunity.webview.events.TopMessageEvent;
import com.reactnativecommunity.webview.events.TopRenderProcessGoneEvent;
import com.reactnativecommunity.webview.events.TopShouldStartLoadWithRequestEvent;

import java.util.Map;

// class RNCWebViewManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & RNCWebViewManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
//     public RNCWebViewManagerDelegate(U viewManager) {
//         super(viewManager);
//     }
//     @Override
//     public void setProperty(T view, String propName, @Nullable Object value) {
//         switch (propName) {
//             case "color":
//                 mViewManager.setColor(view, value == null ? null : (String) value);
//                 break;
//             case "source":
//                 mViewManager.setSource(view, (ReadableMap) value);
//                 break;
//             default:
//                 super.setProperty(view, propName, value);
//         }
//     }
// }

// interface RNCWebViewManagerInterface<T extends View> {
//     void setColor(T view, @Nullable String value);
//     void setSource(T view, @Nullable ReadableMap value);
// }


@ReactModule(name = RNCWebViewManagerImpl.NAME)
public class RNCWebViewManager extends SimpleViewManager<RNCWebView>
        implements RNCWebViewManagerInterface<RNCWebView> {

    private final ViewManagerDelegate<RNCWebView> mDelegate;
    protected RNCWebViewConfig mWebViewConfig;
    protected boolean mAllowsFullscreenVideo = false;
    protected @Nullable String mDownloadingMessage = null;
    protected @Nullable String mLackPermissionToDownloadMessage = null;

    public RNCWebViewManager(ReactApplicationContext context) {
        mWebViewConfig = webView -> {
        };
        mDelegate = new RNCWebViewManagerDelegate(this);
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

    @Override
    protected void addEventEmitters(ThemedReactContext reactContext, RNCWebView view) {
        // Do not register default touch emitter and let WebView implementation handle touches
        // view.setWebViewClient(new RNCWebViewClient());
        Log.e("test", "couccou");
    }

    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        Map export = super.getExportedCustomDirectEventTypeConstants();
        if (export == null) {
            export = MapBuilder.newHashMap();
        }
        // Default events but adding them here explicitly for clarity
        export.put(TopLoadingStartEvent.EVENT_NAME, MapBuilder.of("registrationName", "onLoadingStart"));
        export.put(TopLoadingFinishEvent.EVENT_NAME, MapBuilder.of("registrationName", "onLoadingFinish"));
        export.put(TopLoadingErrorEvent.EVENT_NAME, MapBuilder.of("registrationName", "onLoadingError"));
        export.put(TopMessageEvent.EVENT_NAME, MapBuilder.of("registrationName", "onMessage"));
        // !Default events but adding them here explicitly for clarity

        export.put(TopLoadingProgressEvent.EVENT_NAME, MapBuilder.of("registrationName", "onLoadingProgress"));
        export.put(TopShouldStartLoadWithRequestEvent.EVENT_NAME, MapBuilder.of("registrationName", "onShouldStartLoadWithRequest"));
        export.put(ScrollEventType.getJSEventName(ScrollEventType.SCROLL), MapBuilder.of("registrationName", "onScroll"));
        export.put(TopHttpErrorEvent.EVENT_NAME, MapBuilder.of("registrationName", "onHttpError"));
        export.put(TopRenderProcessGoneEvent.EVENT_NAME, MapBuilder.of("registrationName", "onRenderProcessGone"));
        return export;
    }
}