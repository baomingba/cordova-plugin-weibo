#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>
#import "WeiboSDK.h"

@interface CDVWeibo : CDVPlugin <WeiboSDKDelegate>

- (void)login:(CDVInvokedUrlCommand*)command;
- (void)isInstalled:(CDVInvokedUrlCommand*)command;

- (void)registerApp:(NSString*)appId redirectURI:(NSString*)redirectURI;

@end