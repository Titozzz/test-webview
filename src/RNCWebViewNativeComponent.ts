import type { ViewProps } from 'ViewPropTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import {DirectEventHandler,Double, Int32} from 'react-native/Libraries/Types/CodegenTypes';
import {ScrollEvent} from 'react-native/Libraries/Types/CoreEventTypes';

export type WebViewNativeEvent = Readonly<{
  url: string;
  loading: boolean;
  title: string;
  canGoBack: boolean;
  canGoForward: boolean;
  lockIdentifier: Double;
}>
export type WebViewMessageEvent = Readonly<{
  url: string;
  loading: boolean;
  title: string;
  canGoBack: boolean;
  canGoForward: boolean;
  lockIdentifier: Double;
  data: string;
}>
export type WebViewHttpErrorEvent = Readonly<{
  url: string;
  loading: boolean;
  title: string;
  canGoBack: boolean;
  canGoForward: boolean;
  lockIdentifier: Double;
  description: string;
  statusCode: Int32;
}>

export type WebViewErrorEvent = Readonly<{
  url: string;
  loading: boolean;
  title: string;
  canGoBack: boolean;
  canGoForward: boolean;
  lockIdentifier: Double;
  domain?: string;
  code: Int32;
  description: string;
}>

export type WebViewNativeProgressEvent = Readonly< {
  url: string;
  loading: boolean;
  title: string;
  canGoBack: boolean;
  canGoForward: boolean;
  lockIdentifier: Double;
  progress: Double;
}>

export type WebViewNavigationEvent = Readonly< {
  url: string;
  loading: boolean;
  title: string;
  canGoBack: boolean;
  canGoForward: boolean;
  lockIdentifier: Double;
  navigationType:
    | 'click'
    | 'formsubmit'
    | 'backforward'
    | 'reload'
    | 'formresubmit'
    | 'other';
  mainDocumentURL?: string;
}>

export type ShouldStartLoadRequestEvent  = Readonly<{
  url: string;
  loading: boolean;
  title: string;
  canGoBack: boolean;
  canGoForward: boolean;
  lockIdentifier: Double;
  navigationType:
    | 'click'
    | 'formsubmit'
    | 'backforward'
    | 'reload'
    | 'formresubmit'
    | 'other';
  mainDocumentURL?: string;
  isTopFrame: boolean;
}>

type ScrollEvent = Readonly<{
  contentInset: {
    bottom: Double,
    left: Double,
    right: Double,
    top: Double,
  },
  contentOffset: {
    y: Double,
    x: Double,
  },
  contentSize: {
    height: Double,
    width: Double,
  },
  layoutMeasurement: {
    height: Double,
    width: Double,
  },
  targetContentOffset?: {
    y: Double,
    x: Double,
  },
  velocity?: {
    y: Double,
    x: Double,
  },
  zoomScale?: Double,
  responderIgnoreScroll?: boolean,
}>

type WebViewRenderProcessGoneEvent = Readonly<{
  didCrash: boolean;
}>

export interface NativeProps extends ViewProps {
  // Android only
  allowFileAccess?: boolean;
  // Android only
  allowFileAccessFromFileURLs?: boolean;
  // Android only
  allowUniversalAccessFromFileURLs?: boolean;
  // Android only
  allowsFullscreenVideo?: boolean;
  // Android only
  androidHardwareAccelerationDisabled?: boolean;
  // Android only
  androidLayerType?: string;
  applicationNameForUserAgent?: string;
  basicAuthCredential?: Readonly<{
    username: string;
    password: string;
  }>;
  cacheEnabled?: boolean;
  // Android only
  cacheMode?: string;
  // Android only
  domStorageEnabled?: boolean;
  // Android only
  downloadingMessage?: string;
  // Android only
  forceDarkOn?: boolean;
  // Android only
  geolocationEnabled?: boolean;
  hasOnScroll?: boolean;
  incognito?: boolean;
  injectedJavaScript?: string;
  injectedJavaScriptBeforeContentLoaded?: string;
  injectedJavaScriptForMainFrameOnly?: boolean;
  injectedJavaScriptBeforeContentLoadedForMainFrameOnly?: boolean;
  javaScriptCanOpenWindowsAutomatically?: boolean;
  javaScriptEnabled?: boolean;
  // Android only
  lackPermissionToDownloadMessage?: string;
  mediaPlaybackRequiresUserAction?: boolean;
  messagingEnabled: boolean;
  // Android only
  messagingModuleName: string;
  // Android only
  minimumFontSize?: Int32;
  // Android only
  mixedContentMode?: string;
  // Android only
  nestedScrollEnabled?: boolean;
  // Android only
  onContentSizeChange?: DirectEventHandler<WebViewNativeEvent>;
  onLoadingError: DirectEventHandler<WebViewErrorEvent>;
  onLoadingFinish: DirectEventHandler<WebViewNavigationEvent>;
  onLoadingProgress: DirectEventHandler<WebViewNativeProgressEvent>;
  onLoadingStart: DirectEventHandler<WebViewNavigationEvent>;
  onHttpError: DirectEventHandler<WebViewHttpErrorEvent>;
  onMessage: DirectEventHandler<WebViewMessageEvent>;
  // Android only
  onRenderProcessGone?: DirectEventHandler<WebViewRenderProcessGoneEvent>;
  onScroll?: DirectEventHandler<ScrollEvent>;
  onShouldStartLoadWithRequest: DirectEventHandler<ShouldStartLoadRequestEvent>;
  // Android only
  overScrollMode?: string;
  // Android only
  saveFormDataDisabled?: boolean;
  // Android only
  scalesPageToFit?: boolean;
  // Android only
  setBuiltInZoomControls?: boolean;
  // Android only
  setDisplayZoomControls?: boolean;
  // Android only
  setSupportMultipleWindows?: boolean;
  showsHorizontalScrollIndicator?: boolean;
  showsVerticalScrollIndicator?: boolean;
  source: Readonly<{uri: string}>;
  // Android only
  textZoom?: Double;
  // Android only
  thirdPartyCookiesEnabled?: boolean;
  // Android only
  urlPrefixesForDefaultIntent?: readonly string[];
  userAgent?: string;
}

export default codegenNativeComponent<NativeProps>(
  'RNCWebView'
) as HostComponent<NativeProps>;