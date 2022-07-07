#import <React/RCTViewManager.h>
#import "RNCWebViewImpl.h"

@interface RNCWebViewManager : RCTViewManager
@end

@implementation RNCWebViewManager

RCT_EXPORT_MODULE(RNCWebView)

- (UIView *)view
{
return [[RNCWebViewImpl alloc] init];
}

RCT_CUSTOM_VIEW_PROPERTY(color, NSString, UIView)
{
[view setBackgroundColor:[self hexStringToColor:json]];
}
RCT_EXPORT_VIEW_PROPERTY(source, NSDictionary)

- hexStringToColor:(NSString *)stringToConvert
{
NSString *noHashString = [stringToConvert stringByReplacingOccurrencesOfString:@"#" withString:@""];
NSScanner *stringScanner = [NSScanner scannerWithString:noHashString];

unsigned hex;
if (![stringScanner scanHexInt:&hex]) return nil;
int r = (hex >> 16) & 0xFF;
int g = (hex >> 8) & 0xFF;
int b = (hex) & 0xFF;

return [UIColor colorWithRed:r / 255.0f green:g / 255.0f blue:b / 255.0f alpha:1.0f];
}

@end
