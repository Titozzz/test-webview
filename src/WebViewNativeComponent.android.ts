import { requireNativeComponent } from 'react-native'

const isFabricEnabled = global.nativeFabricUIManager != null;

const webView = true ?
    require("./RNCWebViewNativeComponent").default :
    requireNativeComponent("RNCWebView")

export default webView;