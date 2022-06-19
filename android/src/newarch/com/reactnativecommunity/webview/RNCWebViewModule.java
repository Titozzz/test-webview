package com.reactnativecommunity.webview;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;

import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static android.app.Activity.RESULT_OK;

@ReactModule(name = RNCWebViewModuleImpl.NAME)
public class RNCWebViewModule extends NativeRNCWebViewSpec  implements ActivityEventListener {
    private ValueCallback<Uri> filePathCallbackLegacy;
    private ValueCallback<Uri[]> filePathCallback;
    private File outputImage;
    private File outputVideo;
    private DownloadManager.Request downloadRequest;

    public RNCWebViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return RNCWebViewModuleImpl.NAME;
    }

    @Override
    public void isFileUploadSupported(final Promise promise) {
        promise.resolve(RNCWebViewModuleImpl.isFileUploadSupported());
    }

    @Override
    public void onShouldStartLoadWithRequestCallback(boolean shouldStart, double lockIdentifier) {
        RNCWebViewModuleImpl.onShouldStartLoadWithRequestCallback(shouldStart, lockIdentifier);
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

        if (filePathCallback == null && filePathCallbackLegacy == null) {
            return;
        }

        boolean imageTaken = false;
        boolean videoTaken = false;

        if (outputImage != null && outputImage.length() > 0) {
            imageTaken = true;
        }
        if (outputVideo != null && outputVideo.length() > 0) {
            videoTaken = true;
        }

        // based off of which button was pressed, we get an activity result and a file
        // the camera activity doesn't properly return the filename* (I think?) so we use
        // this filename instead
        switch (requestCode) {
            case RNCWebViewModuleImpl.PICKER:
                if (resultCode != RESULT_OK) {
                    if (filePathCallback != null) {
                        filePathCallback.onReceiveValue(null);
                    }
                } else {
                    if (imageTaken) {
                        filePathCallback.onReceiveValue(new Uri[]{getOutputUri(outputImage)});
                    } else if (videoTaken) {
                        filePathCallback.onReceiveValue(new Uri[]{getOutputUri(outputVideo)});
                    } else {
                        filePathCallback.onReceiveValue(RNCWebViewModuleImpl.getSelectedFiles(data, resultCode));
                    }
                }
                break;
            case RNCWebViewModuleImpl.PICKER_LEGACY:
                if (resultCode != RESULT_OK) {
                    filePathCallbackLegacy.onReceiveValue(null);
                } else {
                    if (imageTaken) {
                        filePathCallbackLegacy.onReceiveValue(getOutputUri(outputImage));
                    } else if (videoTaken) {
                        filePathCallbackLegacy.onReceiveValue(getOutputUri(outputVideo));
                    } else {
                        filePathCallbackLegacy.onReceiveValue(data.getData());
                    }
                }
                break;

        }

        if (outputImage != null && !imageTaken) {
            outputImage.delete();
        }
        if (outputVideo != null && !videoTaken) {
            outputVideo.delete();
        }

        filePathCallback = null;
        filePathCallbackLegacy = null;
        outputImage = null;
        outputVideo = null;
    }

    public void onNewIntent(Intent intent) {
    }

    public void startPhotoPickerIntent(ValueCallback<Uri> filePathCallback, String acceptType) {
        filePathCallbackLegacy = filePathCallback;
        RNCWebViewModuleImpl.startPhotoPickerIntent(acceptType, getCurrentActivity(), outputImage, outputVideo);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean startPhotoPickerIntent(final ValueCallback<Uri[]> callback, final String[] acceptTypes, final boolean allowMultiple) {
        filePathCallback = callback;

        return RNCWebViewModuleImpl.startPhotoPickerIntent(acceptTypes, allowMultiple, getCurrentActivity(), outputImage, outputVideo);
    }

    public void setDownloadRequest(DownloadManager.Request request) {
        this.downloadRequest = request;
    }

    public void downloadFile(String downloadingMessage) {
        RNCWebViewModuleImpl.downloadFile(downloadingMessage, downloadRequest, getReactApplicationContext());
    }

    public boolean grantFileDownloaderPermissions(String downloadingMessage, String lackPermissionToDownloadMessage) {
        return RNCWebViewModuleImpl.grantFileDownloaderPermissions(downloadingMessage, lackPermissionToDownloadMessage, downloadRequest, getCurrentActivity());
    }

    protected boolean needsCameraPermission() {
        return RNCWebViewModuleImpl.needsCameraPermission(getCurrentActivity());
    }

    private Uri getOutputUri(File capturedFile) {
        return RNCWebViewModuleImpl.getOutputUri(capturedFile, getReactApplicationContext());
    }
}