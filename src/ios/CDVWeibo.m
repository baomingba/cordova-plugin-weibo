#include <sys/types.h>
#include <sys/sysctl.h>

#import <Cordova/CDV.h>
#import <Cordova/CDVViewController.h>

#import "CDVWeibo.h"

@implementation CDVWeibo {
    NSString* _appId;
    NSString* _redirectURI;
    NSString* _currentCallbackId;
}

#pragma mark "Public"

- (void)registerApp:(NSString*)appId redirectURI:(NSString*)redirectURI {
    _appId = appId;
    _redirectURI = redirectURI;
    
    [WeiboSDK enableDebugMode:YES];
    
    [WeiboSDK registerApp:_appId];
}

- (void)login:(CDVInvokedUrlCommand*)command {
    NSLog(@"Weibo login...");
    _currentCallbackId = [NSString stringWithString:command.callbackId];
    
    WBAuthorizeRequest* request = [WBAuthorizeRequest request];
    request.redirectURI = _redirectURI;
    request.scope = @"all";
    request.userInfo = @{@"SSO_From": @"CDVViewController"};
    [WeiboSDK sendRequest:request];
}

- (void)isInstalled:(CDVInvokedUrlCommand*)command {
    CDVPluginResult *result;
    
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:[WeiboSDK isWeiboAppInstalled]];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

#pragma mark "WeiboSDKDelegate"

- (void)didReceiveWeiboRequest:(WBBaseRequest*)request {
    // do nothing.
}

- (void)didReceiveWeiboResponse:(WBBaseResponse*)response {
    NSLog(@"%@", response.debugDescription);

    if ([response isKindOfClass:WBAuthorizeResponse.class]) {
        if(response.statusCode == 0) {
            NSDictionary *info=[NSDictionary dictionaryWithObjectsAndKeys:[(WBAuthorizeResponse *)response userID],@"uid",[(WBAuthorizeResponse*)response accessToken],@"token" , nil];

            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:info];
            [self.commandDelegate sendPluginResult:result callbackId:_currentCallbackId];
        }
        else {
            // error
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
            [self.commandDelegate sendPluginResult:result callbackId:_currentCallbackId];
        }
        
        _currentCallbackId = nil;
    }
}

#pragma mark "CDVPlugin Overrides"

- (void)handleOpenURL:(NSNotification*)notification {
    NSURL* url = [notification object];
    
    if ([url isKindOfClass:[NSURL class]]) {
        [WeiboSDK handleOpenURL:url delegate:self];
    }
}

@end