package com.reactnativecommunity.webview

import android.app.DownloadManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.common.build.ReactBuildConfig
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class RNCWebViewManagerImpl(context: ReactApplicationContext) {
    companion object {
        const val NAME = "RNCWebView"
    }

    private val TAG = "RNCWebViewManagerImpl"
    private var mWebViewConfig: RNCWebViewConfig = RNCWebViewConfig { webView: WebView? -> }
    private var mAllowsFullscreenVideo = false
    private var mDownloadingMessage: String? = null
    private var mLackPermissionToDownloadMessage: String? = null

    private var mUserAgent: String? = null
    private var mUserAgentWithApplicationName: String? = null
    private val HTML_ENCODING = "UTF-8"
    private val HTML_MIME_TYPE = "text/html"
    private val JAVASCRIPT_INTERFACE = "ReactNativeWebView"
    private val HTTP_METHOD_POST = "POST"

    // Use `webView.loadUrl("about:blank")` to reliably reset the view
    // state and release page resources (including any running JavaScript).
    private val BLANK_URL = "about:blank"

    private val DEFAULT_DOWNLOADING_MESSAGE = "Downloading"
    private val DEFAULT_LACK_PERMISSION_TO_DOWNLOAD_MESSAGE =
        "Cannot download files as permission was denied. Please provide permission to write to storage, in order to download files."


    private var mContext: ReactApplicationContext = context

    fun createViewInstance(): RNCWebView? {
        val webView = RNCWebView(mContext)
        setupWebChromeClient(mContext, webView, mAllowsFullscreenVideo)
        mContext.addLifecycleEventListener(webView)
        mWebViewConfig.configWebView(webView)
        val settings = webView.settings
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        settings.allowFileAccessFromFileURLs = false
        setAllowUniversalAccessFromFileURLs(webView, false)
        setMixedContentMode(webView, "never")

        // Fixes broken full-screen modals/galleries due to body height being 0.
        webView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        if (ReactBuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        webView.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            webView.setIgnoreErrFailedForThisURL(url)
            val module = getModule() ?: return@DownloadListener
            val request: DownloadManager.Request = try {
                DownloadManager.Request(Uri.parse(url))
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "Unsupported URI, aborting download", e)
                return@DownloadListener
            }
            val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
            val downloadMessage = "Downloading $fileName"

            //Attempt to add cookie, if it exists
            var urlObj: URL? = null
            try {
                urlObj = URL(url)
                val baseUrl = urlObj.protocol + "://" + urlObj.host
                val cookie = CookieManager.getInstance().getCookie(baseUrl)
                request.addRequestHeader("Cookie", cookie)
            } catch (e: MalformedURLException) {
                Log.w(TAG, "Error getting cookie for DownloadManager", e)
            }

            //Finish setting up request
            request.addRequestHeader("User-Agent", userAgent)
            request.setTitle(fileName)
            request.setDescription(downloadMessage)
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            module.setDownloadRequest(request)
            if (module.grantFileDownloaderPermissions(
                    getDownloadingMessageOrDefault(),
                    getLackPermissionToDownloadMessageOrDefault()
                )
            ) {
                module.downloadFile(
                    getDownloadingMessageOrDefault()
                )
            }
        })
        return webView
    }

    private fun setupWebChromeClient(
        reactContext: ReactApplicationContext,
        webView: RNCWebView,
        allowsFullscreenVideo: Boolean
    ) {
        val activity = reactContext.currentActivity
        if (allowsFullscreenVideo && activity != null) {
            val initialRequestedOrientation = activity.requestedOrientation
            val webChromeClient: RNCWebChromeClient =
                object : RNCWebChromeClient(reactContext, webView) {
                    override fun getDefaultVideoPoster(): Bitmap? {
                        return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
                    }

                    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                        if (mVideoView != null) {
                            callback.onCustomViewHidden()
                            return
                        }
                        mVideoView = view
                        mCustomViewCallback = callback
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        mVideoView.systemUiVisibility = FULLSCREEN_SYSTEM_UI_VISIBILITY
                        activity.window.setFlags(
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        )
                        mVideoView.setBackgroundColor(Color.BLACK)

                        // Since RN's Modals interfere with the View hierarchy
                        // we will decide which View to hide if the hierarchy
                        // does not match (i.e., the WebView is within a Modal)
                        // NOTE: We could use `mWebView.getRootView()` instead of `getRootView()`
                        // but that breaks the Modal's styles and layout, so we need this to render
                        // in the main View hierarchy regardless
                        val rootView = rootView
                        rootView.addView(mVideoView, FULLSCREEN_LAYOUT_PARAMS)

                        // Different root views, we are in a Modal
                        if (rootView.rootView !== mWebView.rootView) {
                            mWebView.rootView.visibility = View.GONE
                        } else {
                            // Same view hierarchy (no Modal), just hide the WebView then
                            mWebView.visibility = View.GONE
                        }
                        mReactContext.addLifecycleEventListener(this)
                    }

                    override fun onHideCustomView() {
                        if (mVideoView == null) {
                            return
                        }

                        // Same logic as above
                        val rootView = rootView
                        if (rootView.rootView !== mWebView.rootView) {
                            mWebView.rootView.visibility = View.VISIBLE
                        } else {
                            // Same view hierarchy (no Modal)
                            mWebView.visibility = View.VISIBLE
                        }
                        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                        rootView.removeView(mVideoView)
                        mCustomViewCallback.onCustomViewHidden()
                        mVideoView = null
                        mCustomViewCallback = null
                        activity.requestedOrientation = initialRequestedOrientation
                        mReactContext.removeLifecycleEventListener(this)
                    }
                }
            webView.webChromeClient = webChromeClient
        } else {
            var webChromeClient = webView.webChromeClient as RNCWebChromeClient?
            webChromeClient?.onHideCustomView()
            webChromeClient = object : RNCWebChromeClient(reactContext, webView) {
                override fun getDefaultVideoPoster(): Bitmap? {
                    return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
                }
            }
            webView.webChromeClient = webChromeClient
        }
    }

    fun setUserAgent(view: WebView, userAgent: String?) {
        mUserAgent = userAgent
        setUserAgentString(view)
    }

    fun setApplicationNameForUserAgent(view: WebView, applicationName: String?) {
        when {
            applicationName != null -> {
                val defaultUserAgent = WebSettings.getDefaultUserAgent(view.context)
                mUserAgentWithApplicationName = "$defaultUserAgent $applicationName"
            }
            else -> {
                mUserAgentWithApplicationName = null
            }
        }
        setUserAgentString(view)
    }

    private fun setUserAgentString(view: WebView) {
        when {
            mUserAgent != null -> {
                view.settings.userAgentString = mUserAgent
            }
            mUserAgentWithApplicationName != null -> {
                view.settings.userAgentString = mUserAgentWithApplicationName
            }
            else -> {
                view.settings.userAgentString = WebSettings.getDefaultUserAgent(view.context)
            }
        }
    }

    fun setBasicAuthCredential(view: WebView, credential: ReadableMap?) {
        var basicAuthCredential: RNCBasicAuthCredential? = null
        if (credential != null) {
            if (credential.hasKey("username") && credential.hasKey("password")) {
                val username = credential.getString("username")
                val password = credential.getString("password")
                basicAuthCredential = RNCBasicAuthCredential(username, password)
            }
        }
        (view as RNCWebView).setBasicAuthCredential(basicAuthCredential)
    }

    fun getModule(): RNCWebViewModule? {
        return mContext.getNativeModule(RNCWebViewModule::class.java)
    }

    fun setMixedContentMode(view: WebView, mixedContentMode: String?) {
        if (mixedContentMode == null || "never" == mixedContentMode) {
            view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        } else if ("always" == mixedContentMode) {
            view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        } else if ("compatibility" == mixedContentMode) {
            view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
    }

    fun setAllowUniversalAccessFromFileURLs(view: WebView, allow: Boolean) {
        view.settings.allowUniversalAccessFromFileURLs = allow
    }

    private fun getDownloadingMessageOrDefault(): String? {
        return mDownloadingMessage ?: DEFAULT_DOWNLOADING_MESSAGE
    }

    private fun getLackPermissionToDownloadMessageOrDefault(): String? {
        return mLackPermissionToDownloadMessage
            ?: DEFAULT_LACK_PERMISSION_TO_DOWNLOAD_MESSAGE
    }

    fun setSource(view: RNCWebView, source: ReadableMap?) {
        if (source != null) {
            if (source.hasKey("html")) {
                val html = source.getString("html")
                val baseUrl = if (source.hasKey("baseUrl")) source.getString("baseUrl") else ""
                view.loadDataWithBaseURL(
                    baseUrl,
                    html!!,
                    HTML_MIME_TYPE,
                    HTML_ENCODING,
                    null
                )
                return
            }
            if (source.hasKey("uri")) {
                val url = source.getString("uri")
                val previousUrl = view.url
                if (previousUrl != null && previousUrl == url) {
                    return
                }
                if (source.hasKey("method")) {
                    val method = source.getString("method")
                    if (method.equals(HTTP_METHOD_POST, ignoreCase = true)) {
                        var postData: ByteArray? = null
                        if (source.hasKey("body")) {
                            val body = source.getString("body")
                            postData = try {
                                body!!.toByteArray(charset("UTF-8"))
                            } catch (e: UnsupportedEncodingException) {
                                body!!.toByteArray()
                            }
                        }
                        if (postData == null) {
                            postData = ByteArray(0)
                        }
                        view.postUrl(url!!, postData)
                        return
                    }
                }
                val headerMap = HashMap<String, String?>()
                if (source.hasKey("headers")) {
                    val headers = source.getMap("headers")
                    val iter = headers!!.keySetIterator()
                    while (iter.hasNextKey()) {
                        val key = iter.nextKey()
                        if ("user-agent" == key.lowercase(Locale.ENGLISH)) {
                            view.settings.userAgentString = headers.getString(key)
                        } else {
                            headerMap[key] = headers.getString(key)
                        }
                    }
                }
                view.loadUrl(url!!, headerMap)
                return
            }
        }
        view.loadUrl(BLANK_URL)
    }

    fun setMessagingModuleName(view: RNCWebView, value: String) {
        view.messagingModuleName = value
    }

    fun setCacheEnabled(view: RNCWebView, enabled: Boolean) {
        if (enabled) {
            val ctx: Context? = view.context
            if (ctx != null) {
                view.settings.setAppCachePath(ctx.cacheDir.absolutePath)
                view.settings.cacheMode = WebSettings.LOAD_DEFAULT
                view.settings.setAppCacheEnabled(true)
            }
        } else {
            view.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            view.settings.setAppCacheEnabled(false)
        }
    }

    fun setIncognito(view: RNCWebView, enabled: Boolean) {
        // Don't do anything when incognito is disabled
        if (!enabled) {
            return;
        }

        // Remove all previous cookies
        CookieManager.getInstance().removeAllCookies(null);

        // Disable caching
        view.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        view.settings.setAppCacheEnabled(false)
        view.clearHistory();
        view.clearCache(true);

        // No form data or autofill enabled
        view.clearFormData();
        view.settings.savePassword = false;
        view.settings.saveFormData = false;
    }

    fun setInjectedJavaScript(view: RNCWebView, injectedJavaScript: String) {
        view.injectedJS = injectedJavaScript
    }

    fun setInjectedJavaScriptBeforeContentLoaded(view: RNCWebView, value: String) {
        view.injectedJSBeforeContentLoaded = value
    }

    fun setInjectedJavaScriptForMainFrameOnly(view: RNCWebView, value: Boolean) {
        view.injectedJavaScriptForMainFrameOnly = value
    }

    fun setInjectedJavaScriptBeforeContentLoadedForMainFrameOnly(view: RNCWebView, value: Boolean) {
        view.injectedJavaScriptBeforeContentLoadedForMainFrameOnly = value
    }

    fun setJavaScriptCanOpenWindowsAutomatically(view: RNCWebView, value: Boolean) {
        view.settings.javaScriptCanOpenWindowsAutomatically = value
    }

    fun setShowsVerticalScrollIndicator(view: RNCWebView, value: Boolean) {
        view.isVerticalScrollBarEnabled = value
    }

    fun setShowsHorizontalScrollIndicator(view: RNCWebView, value: Boolean) {
        view.isHorizontalScrollBarEnabled = value
    }

    fun setMessagingEnabled(view: RNCWebView, value: Boolean) {
        view.setMessagingEnabled(value)
    }

    fun setMediaPlaybackRequiresUserAction(view: RNCWebView, value: Boolean) {
        view.settings.mediaPlaybackRequiresUserGesture = value
    }

    fun setHasScrollEvent(view: RNCWebView, value: Boolean) {
        view.setHasScrollEvent(value)
    }

    fun setJavaScriptEnabled(view: RNCWebView, enabled: Boolean) {
        view.settings.javaScriptEnabled = enabled
    }

}