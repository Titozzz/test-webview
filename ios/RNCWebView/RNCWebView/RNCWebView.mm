// This guard prevent the code from being compiled in the old architecture
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNCWebView.h"
#import "RNCWebViewImpl.h"

#import <react/renderer/components/RNCWebViewSpec/ComponentDescriptors.h>
#import <react/renderer/components/RNCWebViewSpec/EventEmitters.h>
#import <react/renderer/components/RNCWebViewSpec/Props.h>
#import <react/renderer/components/RNCWebViewSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@interface RNCWebView () <RCTRNCWebViewViewProtocol>

@end

@implementation RNCWebView {
    RNCWebViewImpl * _webView;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<RNCWebViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const RNCWebViewProps>();
    _props = defaultProps;
    _webView = [[RNCWebViewImpl alloc] initWithFrame:frame];
    self.contentView = _webView;
}

return self;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<RNCWebViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<RNCWebViewProps const>(props);
    
    if (oldViewProps.source.uri != newViewProps.source.uri) {
        NSDictionary *dict = @{ @"uri" : [NSString stringWithCString:newViewProps.source.uri.c_str() encoding:[NSString defaultCStringEncoding]]};
        [_webView setSource: dict];
    }

    [super updateProps:props oldProps:oldProps];
}

Class<RCTComponentViewProtocol> RNCWebViewCls(void)
{
return RNCWebView.class;
}

@end
#endif
