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

export interface NativeProps extends ViewProps {
  cacheEnabled?: boolean;
  incognito?: boolean;
  injectedJavaScript?: string;
  injectedJavaScriptBeforeContentLoaded?: string;
  injectedJavaScriptForMainFrameOnly?: boolean;
  injectedJavaScriptBeforeContentLoadedForMainFrameOnly?: boolean;
  javaScriptCanOpenWindowsAutomatically?: boolean;
  javaScriptEnabled?: boolean;
  mediaPlaybackRequiresUserAction?: boolean;
  messagingEnabled: boolean;
  messagingModuleName: string;

  hasOnScroll?: boolean;
  onScroll?: DirectEventHandler<ScrollEvent>;
  onLoadingError: DirectEventHandler<WebViewErrorEvent>;
  onLoadingFinish: DirectEventHandler<WebViewNavigationEvent>;
  onLoadingProgress: DirectEventHandler<WebViewNativeProgressEvent>;
  onLoadingStart: DirectEventHandler<WebViewNavigationEvent>;
  onHttpError: DirectEventHandler<WebViewHttpErrorEvent>;
  onMessage: DirectEventHandler<WebViewMessageEvent>;
  onShouldStartLoadWithRequest: DirectEventHandler<ShouldStartLoadRequestEvent>;

  showsHorizontalScrollIndicator?: boolean;
  showsVerticalScrollIndicator?: boolean;
  // TODO: find a better way to type this.
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  source: Readonly<{uri: string}>;
  userAgent?: string;
  /**
   * Append to the existing user-agent. Overridden if `userAgent` is set.
   */
  applicationNameForUserAgent?: string;
  basicAuthCredential?: Readonly<{
    username: string;
    password: string;
  }>;}

export default codegenNativeComponent<NativeProps>(
  'RNCWebView'
) as HostComponent<NativeProps>;