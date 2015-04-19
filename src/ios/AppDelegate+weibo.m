//
//  AppDelegate+weibo.m
//
//  Created by Landys on 4/19/15.
//
//

#import "AppDelegate+weibo.h"

#import <objc/runtime.h>
#import "WXApi.h"
#import "CDVWeibo.h"

@implementation AppDelegate (weibo)

// its dangerous to override a method from within a category.
// Instead we will use method swizzling. we set this up in the load call.
+ (void)load {
    Method original, swizzled;
    
    original = class_getInstanceMethod(self, @selector(init));
    swizzled = class_getInstanceMethod(self, @selector(swizzled_weibo_init));
    method_exchangeImplementations(original, swizzled);
}

- (AppDelegate *)swizzled_weibo_init {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(initWeiboApp:) name:@"UIApplicationDidFinishLaunchingNotification" object:nil];
    
    // This actually calls the original init method over in AppDelegate. Equivilent to calling super
    // on an overrided method, this is not recursive, although it appears that way. neat huh?
    return [self swizzled_weibo_init];
}

// This code will be called immediately after application:didFinishLaunchingWithOptions:. We need
// to process notifications in cold-start situations
- (void)initWeiboApp:(NSNotification *)notification {
    NSString* appId = [self.viewController.settings objectForKey:@"weiboappid"];
    NSString* redirectURI = [self.viewController.settings objectForKey:@"weiboredirecturi"];
    if (appId && redirectURI) {
        CDVWeibo* weiboPlugin = [self.viewController getCommandInstance:@"Weibo"];
        [weiboPlugin registerApp:appId redirectURI:redirectURI];
    }
}

@end
