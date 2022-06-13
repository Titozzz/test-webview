import type { ViewProps } from 'ViewPropTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

export interface NativeProps extends ViewProps {
  color: string;
}

export default codegenNativeComponent<NativeProps>(
  'RNCWebView'
) as HostComponent<NativeProps>;