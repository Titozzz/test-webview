import type { ViewProps } from 'ViewPropTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import {DirectEventHandler} from 'react-native/Libraries/Types/CodegenTypes';

export interface NativeProps extends ViewProps {
  color: string;
  source: Readonly<{
    uri: string;
  }>
  onLoadingProgress: DirectEventHandler<Readonly<{
    data: string;
  }>>;
  onLoadingFinish: DirectEventHandler<Readonly<{
    data: string;
  }>>;
}

export default codegenNativeComponent<NativeProps>(
  'RNCWebView'
) as HostComponent<NativeProps>;