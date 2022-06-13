import { requireNativeComponent } from 'react-native'

const isFabricEnabled = global.nativeFabricUIManager != null;

const webView = isFabricEnabled ?
    require("./WebViewNativeComponent").default :
    requireNativeComponent("RNCWebView")

export default webView;